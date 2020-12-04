package de.fraunhofer.iem.swan.model;


import de.fraunhofer.iem.swan.Main;
import de.fraunhofer.iem.swan.features.InstancesHandler;
import de.fraunhofer.iem.swan.util.Util;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.CSV;
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

    public HashMap<String, String> getFMeasure() {
        return fMeasure;
    }

    /**
     * Evaluates instances using Monte Carlo Cross Evaluation.
     *
     * @param instancesHandlers instance set
     * @param classifier        classifier to model creation
     * @param trainPercentage   percentage of instances for train set
     * @param iterations        number of evaluation iterations
     * @return average F-score for iterations
     */
    public HashMap<String, String> monteCarloValidate(ArrayList<InstancesHandler> instancesHandlers, Classifier classifier, double trainPercentage, int iterations) {

        initializeResultSet(instancesHandlers.get(0).getInstances());

        if (instancesHandlers.size() == 1) {
            for (int i = 0; i < iterations; i++) {
                Util.exportInstancesToArff(instancesHandlers.get(0).getInstances());
                evaluateIteration(instancesHandlers.get(0).getInstances(), classifier, trainPercentage, i);
            }

        } else {

            for (InstancesHandler instancesHandler : instancesHandlers) {
                Util.exportInstancesToArff(instancesHandlers.get(0).getInstances());
                evaluateIteration(instancesHandler.getInstances(), classifier, trainPercentage, instancesHandlers.indexOf(instancesHandler));
            }
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


        /*try {
            String arffFilePath = Util.exportInstancesToArff(instances);

            //Initialize dataset using ARFF file path
            ILabeledDataset<?> dataset = ArffDatasetAdapter.readDataset(new File(arffFilePath));
            List<ILabeledDataset<?>> split = SplitterUtil.getLabelStratifiedTrainTestSplit(dataset, new Random(1337 + (iteration * 11)), 0.7);


            String trainPath = "/Users/oshando/Projects/thesis/03-code/swan/swan_core/swan-out/mlplan/train-methods-dataset.arff";
            ArffDatasetAdapter.serializeDataset(new File(trainPath), split.get(0));
            ArffLoader trainLoader = new ArffLoader();
            trainLoader.setFile(new File(trainPath));
            Instances trainInstances = trainLoader.getDataSet();
            trainInstances.setClassIndex(trainInstances.numAttributes() - 1);


            String testPath = "/Users/oshando/Projects/thesis/03-code/swan/swan_core/swan-out/mlplan/test-methods-dataset.arff";
            ArffDatasetAdapter.serializeDataset(new File(testPath), split.get(1));
            ArffLoader testLoader = new ArffLoader();
            testLoader.setFile(new File(testPath));
            Instances testInstances = testLoader.getDataSet();
            testInstances.setClassIndex(testInstances.numAttributes() - 1);


        } catch (Exception e) {

        }*/
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

            System.out.println(eval.toClassDetailsString());

            String[] predictions = abstractOutput.getBuffer().toString().split("\n");

            ArrayList<String> methods = new ArrayList<>();

            for (String result : predictions) {
                String[] entry = result.split(",");

                if (entry[2].contains("source") || entry[2].contains("sink") || entry[2].contains("sanitizer")
                        || entry[2].contains("auth")) {

                    String method = entry[5].replace("'", "");
                    Main.predictions.get(Integer.toString(iteration)).add(method);
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

            String iter = classifier.getClass().getSimpleName() + ";" + className + ";" + iteration;

        } catch (Exception e) {
            e.printStackTrace();
        }
        updateResultSet(testInstances, eval);
    }


    /**
     * Evaluates instances using Cross Evaluation.
     *
     * @param instances  instance set
     * @param classifier classifier to model creation
     * @param iterations number of evaluation iterations
     * @return average F-score for iterations
     */
    public HashMap<String, String> crossValidate(Instances instances, Classifier classifier, int iterations, int folds) {

        initializeResultSet(instances);

        for (int i = 0; i < iterations; i++) {

            Evaluation eval = null;
            StringBuffer stringBuffer = new StringBuffer();

            try {
                eval = new Evaluation(instances);
                eval.crossValidateModel(classifier, instances, folds
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

                fMeasure.replace(instances.classAttribute().value(x), current.replace("NaN", "0"));
            }
        }
    }
}