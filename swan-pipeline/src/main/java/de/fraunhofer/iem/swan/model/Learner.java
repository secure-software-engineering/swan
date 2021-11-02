package de.fraunhofer.iem.swan.model;

import de.fraunhofer.iem.swan.features.InstancesHandler;
import de.fraunhofer.iem.swan.io.dataset.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
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
public class Learner {

    public enum LEARN_MODE {
        MANUAL,
        AUTOMATIC
    }

    public enum EVAL_MODE {
        CLASS,
        RELEVANCE
    }

    private final int CROSS_EVALUATE_ITERATIONS = 10;
    private static final Logger logger = LoggerFactory.getLogger(Learner.class);

    public Learner(Writer writer) {

    }

    /**
     * Trains and evaluates the model with the given training data and specified classification mode.
     *
     * @param instancesHandlers list of InstanceHandlers
     * @return Hashmap containing the name of the classifier and it's F-Measure
     */
    public HashMap<String, HashMap<String, String>> trainModel(ArrayList<InstancesHandler> instancesHandlers, LEARN_MODE learnMode) {

        switch (learnMode) {

            case MANUAL:
                return runManualEvaluation(instancesHandlers);
            case AUTOMATIC:
                return runAutomaticEvaluation(instancesHandlers);
            default:
                return null;
        }
    }

    /**
     * Run AutoML training and evaluation on instances.
     *
     * @param instancesHandlers list of instances
     * @return
     */
    public HashMap<String, HashMap<String, String>> runAutomaticEvaluation(ArrayList<InstancesHandler> instancesHandlers) {

        LinkedHashMap<String, HashMap<String, String>> fMeasure = new LinkedHashMap<>();

        for (InstancesHandler instancesHandler : instancesHandlers) {
            MLPlanExecutor mlPlanExecutor = new MLPlanExecutor();
            fMeasure.put("ML-Plan", mlPlanExecutor.evaluateDataset(instancesHandler));
        }

        outputFMeasure(fMeasure);
        return fMeasure;
    }

    /**
     * @param instancesHandlers
     * @return
     */
    public HashMap<String, HashMap<String, String>> runManualEvaluation(ArrayList<InstancesHandler> instancesHandlers) {

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

            ModelEvaluator evaluator = new ModelEvaluator();
            evaluator.monteCarloValidate(instancesHandlers, classifier, 0.7, CROSS_EVALUATE_ITERATIONS);
            //evaluator.crossValidate(instancesHandlers.get(0).getInstances(), classifier, 10, CROSS_EVALUATE_ITERATIONS);

            exportPredictions(evaluator.getPredictions());

            fMeasure.put(classifier.getClass().getSimpleName(), evaluator.getFMeasure());
        }

        outputFMeasure(fMeasure);
        Runtime.getRuntime().gc();

        return fMeasure;
    }

    public void outputFMeasure(LinkedHashMap<String, HashMap<String, String>> fMeasure) {

        String value = fMeasure.keySet().toArray()[0].toString();

        for (Object srm : fMeasure.get(value).keySet()) {
            logger.info("Classification complete for {}", srm.toString());
            for (String c : fMeasure.keySet()) {

                String measures = fMeasure.get(c).get(srm.toString());
                measures = measures.replace(".", ",").substring(0, measures.lastIndexOf(";"));
                logger.info("{} classification results using {}: {}", srm, c, measures);
            }
        }
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