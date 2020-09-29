package de.fraunhofer.iem.swan.model;

import de.fraunhofer.iem.swan.io.Writer;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.NominalToBinary;

import java.io.IOException;
import java.util.*;

/**
 * Finds possible sources and sinks in a given set of system methods using a
 * probabilistic algorithm trained on a previously annotated sample set.
 *
 * @author Steven Arzt, Lisa Nguyen Quang Do, Goran Piskachev
 */
public class Learner {

    private final static String WEKA_LEARNER_ALL = "SMO";

    public enum LEARN_MODE {
        MANUAL,
        AUTOMATIC
    }

    public enum EVAL_MODE {
        CLASS,
        RELEVANCE
    }

    private final boolean CROSS_EVALUATE = true;
    private final boolean CLASSIFY = false;

    private final int CROSS_EVALUATE_ITERATIONS = 10;

    private final Writer writer;


    public Learner(Writer writer) {
        this.writer = writer;

    }

    /**
     * Trains and evaluates the model with the given training data and specified classification mode.
     *
     * @param instances instances
     * @return Hashmap containing the name of the classifier and it's F-Measure
     */
    public HashMap<String, HashMap<String, String>> trainModel(Instances instances, LEARN_MODE learnMode) {

        switch (learnMode) {

            case MANUAL:
                return runManualEvaluation(instances);

            case AUTOMATIC:
                return runAutomaticEvaluation(instances);
            default:
                return null;
        }
    }


    public HashMap<String, HashMap<String, String>> runAutomaticEvaluation(Instances instances) {

        MLPlanExecutor mlPlanExecutor = new MLPlanExecutor();
        LinkedHashMap<String, HashMap<String, String>> fMeasure = new LinkedHashMap<>();

        try {
            HashMap<String, String>  f = mlPlanExecutor.run(instances);
            fMeasure.put(mlPlanExecutor.getFilteredClassifier().getClass().getSimpleName(),f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fMeasure;
    }

    /**
     *
     * @param instances
     * @return
     */
    public HashMap<String, HashMap<String, String>> runManualEvaluation(Instances instances) {

        LinkedHashMap<String, HashMap<String, String>> fMeasure = new LinkedHashMap<>();

        // Cross evaluation
        if (CROSS_EVALUATE) {

            List<Classifier> classifiers = new ArrayList<>();
            classifiers.add(new BayesNet());
            classifiers.add(new NaiveBayes());
            classifiers.add(new J48());
            classifiers.add(new SMO());
            classifiers.add(new JRip());
            classifiers.add(new DecisionStump());
            classifiers.add(new Logistic());

            HashSet<String> classes = new HashSet<>();
            //For each classifier, evaluate its performance on the instances
            for (Classifier classifier : classifiers) {

                HashMap<String, String> results = evaluateModel(classifier, instances);
                fMeasure.put(classifier.getClass().getSimpleName(), results);
            }
        }

        // LinkedHashMap<String, HashMap> fMeasure
        for (Object srm : fMeasure.get("Logistic").keySet()) {
            System.out.println("---" + srm.toString() + "---");
            for (String c : fMeasure.keySet()) {

                String measures = fMeasure.get(c).get(srm.toString()).toString();
                measures = measures.replace(".", ",").substring(0, measures.lastIndexOf(";"));
                System.out.println(measures);
            }

            //    System.out.println(srm.toString());
            //   System.out.println(fMeasure.get(srm));
        }

        Runtime.getRuntime().gc();

        return fMeasure;
    }

    /**
     * Creates and evaluates model using specified classifier and training instances.
     *
     * @param classifier WEKA classifier
     * @param instances  training instances
     */
    public HashMap<String, String> evaluateModel(Classifier classifier, Instances instances) {

        //Apply filters to dataset
        MultiFilter filters = new MultiFilter();
        filters.setFilters(new Filter[]{new NominalToBinary()});
        //  instances = applyFilter(instances, filters);

        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setClassifier(classifier);

        ModelEvaluator evaluator = new ModelEvaluator();
        evaluator.monteCarloValidate(instances, filteredClassifier, 0.8, CROSS_EVALUATE_ITERATIONS);
        //evaluator.crossEvaluate(instances, filteredClassifier, CROSS_EVALUATE_ITERATIONS, CROSS_EVALUATE_FOLDS);

        exportPredictions(evaluator.getPredictions());

        return evaluator.getfMeasure();
    }

    public void exportPredictions(ArrayList<AbstractOutput> predictions) {

        for (AbstractOutput prediction : predictions) {

            //System.out.println("PRE: " + prediction.getBuffer());
        }
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


    public double round(double val, int decimals) {
        val = val * (10 * decimals);
        val = Math.round(val);
        return val / (10 * decimals);
    }
}