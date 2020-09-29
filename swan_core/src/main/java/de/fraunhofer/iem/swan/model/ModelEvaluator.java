package de.fraunhofer.iem.swan.model;


import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.CSV;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Oshando Johnson on 02.09.20
 */
public class ModelEvaluator {

    private ArrayList<AbstractOutput> predictions;
    private HashMap<String, String> fMeasure;

    public ModelEvaluator() {
        predictions = new ArrayList<>();
    }

    public ArrayList<AbstractOutput> getPredictions() {
        return predictions;
    }

    public HashMap<String, String> getfMeasure() {
        return fMeasure;
    }

    /**
     * Evaluates instances using Monte Carlo Cross Evaluation.
     *
     * @param instances          instance set
     * @param filteredClassifier classifier to model creation
     * @param trainPercentage    percentage of instances for train set
     * @param iterations         number of evaluation iterations
     * @return average F-score for iterations
     */
    public HashMap<String, String> monteCarloValidate(Instances instances, FilteredClassifier filteredClassifier, double trainPercentage, int iterations) {

        initializeResultSet(instances);

        for (int i = 0; i < iterations; i++) {

            //System.out.println("----" + filteredClassifier.getClassifier().getClass().getSimpleName() + " Iteration #" + i + "----");
            StringBuffer stringBuffer = new StringBuffer();
            Evaluation eval = null;

            //Instances percentage split
            int trainSize = (int) Math.round(instances.numInstances() * trainPercentage);
            int testSize = instances.numInstances() - trainSize;

            //System.out.println("Split: " + trainSize + "/" + testSize);
            instances.randomize(new Random(1337 + i * 11));

            Instances trainInstances = new Instances(instances, 0, trainSize);
            Instances testInstances = new Instances(instances, trainSize, testSize);

            try {

                filteredClassifier.buildClassifier(trainInstances);

                eval = new Evaluation(testInstances);

                AbstractOutput abstractOutput = new CSV();
                abstractOutput.setBuffer(new StringBuffer());
                abstractOutput.setHeader(testInstances);
                abstractOutput.setAttributes("last");

                eval.evaluateModel(filteredClassifier, testInstances, abstractOutput);
             //   System.out.println(abstractOutput.getBuffer());
                predictions.add(abstractOutput);


                //System.out.println(eval.toClassDetailsString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            updateResultSet(instances, eval);
        }
        return fMeasure;
    }

    /**
     * Evaluates instances using Cross Evaluation.
     *
     * @param instances          instance set
     * @param filteredClassifier classifier to model creation
     * @param iterations         number of evaluation iterations
     * @return average F-score for iterations
     */
    public HashMap<String, String> crossValidate(Instances instances, FilteredClassifier filteredClassifier, int iterations, int folds) {

        initializeResultSet(instances);

        for (int i = 0; i < iterations; i++) {

            Evaluation eval = null;
            StringBuffer stringBuffer = new StringBuffer();

            try {
                eval = new Evaluation(instances);
                eval.crossValidateModel(filteredClassifier, instances, folds
                        , new Random(1337 + i * 11),
                        stringBuffer, new Range(Integer.toString(instances.numAttributes() - 1)),
                        true);
                //System.out.println(stringBuffer.toString());
                System.out.println(eval.toClassDetailsString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateResultSet(instances, eval);
        }
        return fMeasure;
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

                fMeasure.replace(instances.classAttribute().value(x), current.replace("NaN","0"));
            }
        }
    }
}