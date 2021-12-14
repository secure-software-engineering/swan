package de.fraunhofer.iem.swan.model;

import de.fraunhofer.iem.swan.util.Util;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.CSV;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Oshando Johnson on 02.09.20
 */
public class MonteCarloValidator {

    private ArrayList<AbstractOutput> predictions;
    private HashMap<String, String> fMeasure;

    public MonteCarloValidator() {
        predictions = new ArrayList<>();
    }

    public ArrayList<AbstractOutput> getPredictions() {
        return predictions;
    }

    public HashMap<String, String> getFMeasure() {
        return fMeasure;
    }

    /**
     * Evaluates instances using Monte Carlo Cross Evaluation.
     *
     * @param instances       instance set
     * @param classifier      classifier to model creation
     * @param trainPercentage percentage of instances for train set
     * @param iterations      number of evaluation iterations
     * @return average F-score for iterations
     */
    public HashMap<String, String> monteCarloValidate(Instances instances, Classifier classifier, double trainPercentage, int iterations) {

        initializeResultSet(instances);

        for (int i = 0; i < iterations; i++) {
            Util.exportInstancesToArff(instances);
            evaluateIteration(instances, classifier, trainPercentage, i);
        }
        return fMeasure;
    }

    public void evaluateIteration(Instances instances, Classifier classifier, double trainPercentage, int iteration) {

        int trainSize = (int) Math.round(instances.numInstances() * trainPercentage);
        int testSize = instances.numInstances() - trainSize;

        instances.randomize(new Random(1337 + iteration * 11));
        instances.stratify(10);

        Instances trainInstances = new Instances(instances, 0, trainSize);
        Instances testInstances = new Instances(instances, trainSize, testSize);

        evaluate(classifier, trainInstances, testInstances, iteration);
    }

    public void evaluate(Classifier classifier, Instances trainInstances, Instances testInstances, int iteration) {

        Evaluation eval = null;
        try {

            classifier.buildClassifier(trainInstances);

            eval = new Evaluation(testInstances);

            AbstractOutput abstractOutput = new CSV();
            abstractOutput.setBuffer(new StringBuffer());
            abstractOutput.setHeader(testInstances);
            abstractOutput.setAttributes(Integer.toString(testInstances.numAttributes() - 1));

            eval.evaluateModel(classifier, testInstances, abstractOutput);

            String[] predictions = abstractOutput.getBuffer().toString().split("\n");

            for (String result : predictions) {
                String[] entry = result.split(",");

                if (entry[2].contains("source") || entry[2].contains("sink") || entry[2].contains("sanitizer")
                        || entry[2].contains("auth")) {

                    String method = entry[5].replace("'", "");

                    // System.out.println(method);
                    // SwanPipeline.predictions.get(Integer.toString(iteration)).add(method);
                }
            }

            //get class name
            String className = "";
            for (int x = 0; x < testInstances.attribute("class").numValues(); x++) {

                if (!testInstances.attribute("class").value(x).contains("none")) {
                    className = testInstances.attribute("class").value(x);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateResultSet(testInstances, eval);
    }

    public void initializeResultSet(Instances instances) {
        fMeasure = new HashMap<>();

        for (int x = 0; x < instances.numClasses(); x++) {

            if (!instances.classAttribute().value(x).contentEquals("none")) {
                fMeasure.put(instances.classAttribute().value(x), "");
            }
        }
    }

    public void updateResultSet(Instances instances, Evaluation eval) {

        for (int x = 0; x < instances.numClasses(); x++) {

            if (!instances.classAttribute().value(x).contentEquals("none")) {

                String current = fMeasure.get(instances.classAttribute().value(x));
                current += eval.fMeasure(x) + ";";

                fMeasure.replace(instances.classAttribute().value(x), current.replace("NaN", "0"));
            }
        }
    }
}