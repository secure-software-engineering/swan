package de.fraunhofer.iem.swan.model;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.CSV;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Oshando Johnson on 02.09.20
 */
public class MonteCarloValidator {

    private ArrayList<String> predictions;
    private HashMap<String, ArrayList<Double>> fMeasure;

    public MonteCarloValidator() {
        predictions = new ArrayList<>();
        fMeasure = new HashMap<>();
    }

    public ArrayList<String> getPredictions() {
        return predictions;
    }

    public HashMap<String, ArrayList<Double>> getFMeasure() {
        return fMeasure;
    }

    /**
     * Evaluates instances using Monte Carlo Cross Evaluation.
     *
     * @param instances       instance set
     * @param classifier      classifier to model creation
     * @param trainPercentage percentage of instances for train set
     * @param iterations      number of evaluation iterations
     */
    public void monteCarloValidate(Instances instances, Classifier classifier, double trainPercentage, int iterations) {

        for (int i = 0; i < iterations; i++) {

            int trainSize = (int) Math.round(instances.numInstances() * trainPercentage);
            int testSize = instances.numInstances() - trainSize;

            instances.randomize(new Random(1337 + i * 11));
            instances.stratify(10);

            Instances trainInstances = new Instances(instances, 0, trainSize);
            Instances testInstances = new Instances(instances, trainSize, testSize);

            evaluate(classifier, trainInstances, testInstances);
        }
    }

    public ArrayList<String> evaluate(Classifier classifier, Instances trainInstances, Instances testInstances) {

        Evaluation eval = null;
        try {

            classifier.buildClassifier(trainInstances);

            eval = new Evaluation(testInstances);

            AbstractOutput abstractOutput = new CSV();
            abstractOutput.setBuffer(new StringBuffer());
            abstractOutput.setHeader(testInstances);
            abstractOutput.setAttributes(Integer.toString(testInstances.numAttributes() - 1));

            eval.evaluateModel(classifier, testInstances, abstractOutput);

            //Obtain all predictions and extract method signatures
            String[] output = abstractOutput.getBuffer().toString().split("\n");

            for (String result : output) {
                String[] entry = result.split(",");

                if (!entry[2].contains("0")) {
                    predictions.add(entry[5].replace("'", ""));
                }
            }
            updateResultSet(testInstances, eval);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictions;
    }

    public void updateResultSet(Instances instances, Evaluation eval) {

        for (int c = 0; c < instances.numClasses(); c++) {

            String currentClass = instances.classAttribute().value(c);

            if (!currentClass.contentEquals("0")) {
                if (!fMeasure.containsKey(currentClass))
                    fMeasure.put(currentClass, new ArrayList<>(Collections.singletonList(eval.fMeasure(c))));
                else {
                    fMeasure.get(currentClass).add(eval.fMeasure(c));
                }
            }
        }
    }
}