package de.fraunhofer.iem.swan.model.toolkit;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.features.WekaFeatureSet;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.model.MonteCarloValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.MultiFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Finds possible sources and sinks in a given set of system methods using a
 * probabilistic algorithm trained on a previously annotated sample set.
 *
 * @author Steven Arzt, Lisa Nguyen Quang Do, Goran Piskachev
 */
public class Weka {

    private WekaFeatureSet features;
    private SwanOptions options;
    private static final Logger logger = LoggerFactory.getLogger(ModelEvaluator.class);

    public Weka(WekaFeatureSet features, SwanOptions options) {
        this.features = features;
        this.options = options;
    }

    /**
     * Trains and evaluates the model with the given training data and specified classification mode.
     *
     * @return Hashmap containing the name of the classifier and it's F-Measure
     */
    public HashMap<String, HashMap<String, String>> trainModel() {

        switch (ModelEvaluator.Phase.valueOf(options.getPhase().toUpperCase())) {
            case VALIDATE:

                //Phase 1: classify SRM classes
                for (String srm : options.getSrmClasses())
                    runManualEvaluation(features.getTrainInstances().get(srm));

                //Filter methods from CWE instances that were not classified into one of the SRM classes
                //Phase 2: classify CWE classes
                for (String cwe : options.getCweClasses())
                    runManualEvaluation(features.getTrainInstances().get(cwe));

                return null;
            case PREDICT:

        }
        return null;
    }

    /**
     * @return
     */
    public HashMap<String, HashMap<String, String>> runManualEvaluation(Instances instances) {

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

        //For each classifier, evaluate its performance on the instances
        for (Classifier classifier : classifiers) {

            MonteCarloValidator evaluator = new MonteCarloValidator();
            evaluator.monteCarloValidate(instances, classifier, options.getTrainTestSplit(), options.getIterations());

            for (String key : evaluator.getFMeasure().keySet()) {

                logger.info("Average F-measure for {}({}) using {}: {}, {}", category, key, classifier.getClass().getSimpleName(),
                        evaluator.getFMeasure().get(key).stream().mapToDouble(a -> a).average().getAsDouble(), evaluator.getFMeasure().get(key));
            }
        }
        return fMeasure;
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