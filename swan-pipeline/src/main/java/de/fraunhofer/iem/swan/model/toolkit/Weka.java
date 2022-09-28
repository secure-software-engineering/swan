package de.fraunhofer.iem.swan.model.toolkit;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.WekaFeatureSet;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.model.MonteCarloValidator;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.MultiFilter;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Finds possible sources and sinks in a given set of system methods using a
 * probabilistic algorithm trained on a previously annotated sample set.
 *
 * @author Steven Arzt, Lisa Nguyen Quang Do, Goran Piskachev
 */
public class Weka {

    private WekaFeatureSet features;
    private SwanOptions options;
    private Set<Method> methods;
    private static final Logger logger = LoggerFactory.getLogger(Weka.class);
    private HashMap<String, ArrayList<Category>> predictions;
    private HashMap<String, HashMap<String, ArrayList<Double>>> results;
    private DecimalFormat df = new DecimalFormat("####0.000");
    private Classifier bestClass;

    public Weka(WekaFeatureSet features, SwanOptions options, Set<Method> methods) {
        this.features = features;
        this.options = options;
        this.methods = methods;
        predictions = new HashMap<>();


        results = new HashMap<>();

        if (options.isPredictPhase())
            for (Method method : features.getDataset().getTestMethods())
                predictions.put(method.getArffSafeSignature(), new ArrayList<>());
    }

    /**
     * Trains and evaluates the model with the given training data and specified classification mode.
     *
     * @return Hashmap containing the name of the classifier and it's F-Measure
     */
    public SrmList trainModel() {

        switch (ModelEvaluator.Phase.valueOf(options.getPhase().toUpperCase())) {
            case VALIDATE:

                //Phase 1: classify SRM classes
                logger.info("Performing {}-fold cross-validation for {} using WEKA", options.getIterations(), options.getSrmClasses());
                for (String srm : options.getSrmClasses())
                    crossValidateModel(features.getTrainInstances().get(srm));

                //Filter methods from CWE instances that were not classified into one of the SRM classes
                //Phase 2: classify CWE classes
                logger.info("Performing {}-fold cross-validation for {} using WEKA", options.getIterations(), options.getCweClasses());
                for (String cwe : options.getCweClasses())
                    crossValidateModel(features.getTrainInstances().get(cwe));

                // TreeMap to store values of HashMap
                TreeMap<String, HashMap<String, ArrayList<Double>>> sorted
                        = new TreeMap<>(results);

                // Display the TreeMap which is naturally sorted
                for (Map.Entry<String, HashMap<String, ArrayList<Double>>> entry :
                        sorted.entrySet())

                    return null;
            case PREDICT:

                logger.info("Predicting {} for TEST dataset using WEKA", options.getSrmClasses());
                for (String srm : options.getSrmClasses()) {
                    predictModel(srm);
                }

                for (Method method : methods) {
                    for (Category category : predictions.get(method.getArffSafeSignature())) {
                        method.addCategory(category);
                    }
                }
                return new SrmList(methods);
        }
        return null;
    }

    public void predictModel(String srm) {
        Pair<String, Double> bestClassifier = crossValidateModel(features.getTrainInstances().get(srm));

        try {

            Classifier classifier = bestClass;
            classifier.buildClassifier(features.getTrainInstances().get(srm));

            Evaluation eval = new Evaluation(features.getTestInstances().get(srm));
            eval.evaluateModel(classifier, features.getTestInstances().get(srm));

            for (Instance instance : features.getTestInstances().get(srm)) {

                double prediction = eval.evaluateModelOnce(classifier, instance);
                if (prediction > 0) {
                    predictions.get(instance.stringValue(features.getTestInstances().get(srm).attribute("id").index()))
                            .add(Category.valueOf(srm.toUpperCase()));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public Pair<String, Double> crossValidateModel(Instances instances) {

        String category = instances.attribute(instances.numAttributes() - 1).name();
        instances.setClass(instances.attribute(instances.numAttributes() - 1));

        LinkedHashMap<String, HashMap<String, String>> fMeasure = new LinkedHashMap<>();

        List<Classifier> classifiers = new ArrayList<>();
        classifiers.add(new BayesNet());
        classifiers.add(new NaiveBayes());
        classifiers.add(new J48());
        classifiers.add(new SMO());
        classifiers.add(new JRip());
        classifiers.add(new DecisionStump());
        classifiers.add(new Logistic());

        Pair<String, Double> bestClassifier = new Pair<>("", 0.0);
        List<Pair> classifierSummary = new ArrayList<>();
        //For each classifier, evaluate its performance on the instances

        HashMap<String, ArrayList<Double>> measure = new HashMap<>();
        for (Classifier classifier : classifiers) {

            MonteCarloValidator evaluator = new MonteCarloValidator();
            evaluator.monteCarloValidate(instances, classifier, options.getTrainTestSplit(), options.getIterations());

            for (String key : evaluator.getFMeasure().keySet()) {

                double averageFMeasure = evaluator.getFMeasure().get(key).stream().mapToDouble(a -> a).average().getAsDouble();
                double averagePrecision = evaluator.getPrecision().get(key).stream().mapToDouble(a -> a).average().getAsDouble();
                double averageRecall = evaluator.getRecall().get(key).stream().mapToDouble(a -> a).average().getAsDouble();

                Pair summary = new Pair<>(classifier.getClass().getName(), Double.parseDouble(df.format(averageFMeasure)));
                classifierSummary.add(summary);

                if (averageFMeasure > bestClassifier.getValue()) {
                    bestClassifier = summary;
                    bestClass = classifier;
                }

                if (category.contains("authentication")) {

                    if (!results.containsKey(category + "-" + key)) {
                        HashMap<String, ArrayList<Double>> m = new HashMap<>();
                        m.put(classifier.getClass().getSimpleName(), evaluator.getFMeasure().get(key));
                        results.put(category + "-" + key, m);
                    } else {
                        results.get(category + "-" + key).put(classifier.getClass().getSimpleName(), evaluator.getFMeasure().get(key));
                    }
                } else
                    measure.put(classifier.getClass().getSimpleName(), evaluator.getFMeasure().get(key));

                logger.debug("{} Average F-measure ({}), Precision ({}) and Recall ({}) for {}({}) ", classifier.getClass().getSimpleName(), averageFMeasure,averagePrecision, averageRecall, category, key);
            }
            if (!category.contains("authentication"))
                results.put(category, measure);
        }
        logger.info("Selecting {} model for {}, evaluated classifiers={}", bestClassifier.getKey(), category, classifierSummary);

        return bestClassifier;
    }

    /**
     * Applies the Weka filters to the instances.
     *
     * @param instances instane set
     * @param filters   array of filters
     * @return instances with filter applied
     */
    public Instances applyFilter(Instances instances, MultiFilter filters) {

        try {
            filters.setInputFormat(instances);
            return Filter.useFilter(instances, filters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}