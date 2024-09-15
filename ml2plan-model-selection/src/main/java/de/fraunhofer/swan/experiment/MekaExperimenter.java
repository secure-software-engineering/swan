package de.fraunhofer.swan.experiment;

import ai.libs.jaicore.db.IDatabaseConfig;
import ai.libs.jaicore.experiments.*;
import ai.libs.jaicore.experiments.databasehandle.ExperimenterMySQLHandle;
import ai.libs.jaicore.experiments.exceptions.ExperimentAlreadyExistsInDatabaseException;
import ai.libs.jaicore.experiments.exceptions.ExperimentDBInteractionFailedException;
import ai.libs.jaicore.experiments.exceptions.IllegalExperimentSetupException;
import meka.classifiers.multilabel.MultiLabelClassifier;
import meka.core.MLUtils;
import meka.core.Result;
import org.aeonbits.owner.ConfigFactory;
import org.api4.java.algorithm.exceptions.AlgorithmExecutionCanceledException;
import org.api4.java.algorithm.exceptions.AlgorithmTimeoutedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.util.*;

public class MekaExperimenter {

    private static final Logger logger = LoggerFactory.getLogger(MekaExperimenter.class);

    public static void main(final String[] args) throws ExperimentDBInteractionFailedException, AlgorithmTimeoutedException, IllegalExperimentSetupException, ExperimentAlreadyExistsInDatabaseException, InterruptedException, AlgorithmExecutionCanceledException {

        IExperimentSetConfig expConfig = (IExperimentSetConfig) ConfigFactory
                .create(IExperimentSetConfig.class)
                .loadPropertiesFromFile(new File("/home/oshando/IdeaProjects/swan/ml2plan-model-selection/configs/experiments.cnf"));

        IDatabaseConfig dbConfig = (IDatabaseConfig) ConfigFactory
                .create(IDatabaseConfig.class)
                .loadPropertiesFromFile(new File("/home/oshando/IdeaProjects/swan/ml2plan-model-selection/configs/db.properties"));
        ExperimenterMySQLHandle handle = new ExperimenterMySQLHandle(dbConfig);

       /*   try {
            handle.setup(expConfig);
        } catch (ExperimentDBInteractionFailedException e) {
            logger.error("Couldn't setup the sql handle.", e);
            System.exit(1);
        }*/

        ExperimentDatabasePreparer preparer = new ExperimentDatabasePreparer(expConfig, handle);
        try {
            preparer.synchronizeExperiments();
        } catch (ExperimentDBInteractionFailedException | IllegalExperimentSetupException |
                 AlgorithmTimeoutedException | InterruptedException | AlgorithmExecutionCanceledException |
                 ExperimentAlreadyExistsInDatabaseException e) {
            logger.error("Couldn't synchronize experiment table.", e);
            //  System.exit(1);
        }

        IExperimentSetEvaluator evaluator =
                (ExperimentDBEntry experimentEntry, IExperimentIntermediateResultProcessor processor) -> {
                    Experiment experiment = experimentEntry.getExperiment();
                    Map<String, String> keyFields = experiment.getValuesOfKeyFields();

                    // gather experiment key values:
                    String dataset = keyFields.get("datasets");
                    String classifierName = keyFields.get("classifiers");
                    String[] classifierOptions = keyFields.get("options").split(",");
                    int seed = Integer.parseInt(keyFields.get("seeds"));

                    try {
                        // Load dataset
                        ConverterUtils.DataSource source = new ConverterUtils.DataSource(dataset);
                        Instances data = source.getDataSet();
                        data.randomize(new Random(seed));
                        MLUtils.prepareData(data);


                        //create the classifier
                        MultiLabelClassifier classifier = (MultiLabelClassifier) Class.forName(classifierName)
                                .getDeclaredConstructor().newInstance();
                        if (classifierOptions.length > 1)
                            classifier.setOptions(classifierOptions);

                        Result result = meka.classifiers.multilabel.Evaluation.cvModel(classifier, data, 10, "PCutL", "7");

                        String info = result.info.toString();

                        double[] lblPrecision = (double[]) result.getMeasurement("Precision (per label)");
                        double[] lblRecall = (double[]) result.getMeasurement("Recall (per label)");
                        double[] lblHarmonic = (double[]) result.getMeasurement("Harmonic (per label)");

                        //Macro calculates for each class individually and then takes the average of these values.
                        double avgPrecision = (double) result.getMeasurement("Macro Precision");
                        double avgRecall = (double) result.getMeasurement("Macro Recall");

                        //It calculates the precision globally by counting the total true positives and false positives.
                        double microPrecision = (double) result.getMeasurement("Micro Precision");
                        double microRecall = (double) result.getMeasurement("Micro Recall");

                        // submit the results:
                        Map<String, Object> experimentResults = new HashMap<>();
                        experimentResults.put("info", info);
                        experimentResults.put("lblPrecision", Arrays.toString(lblPrecision));
                        experimentResults.put("lblRecall", Arrays.toString(lblRecall));
                        experimentResults.put("lblHarmonic", Arrays.toString(lblHarmonic));

                        experimentResults.put("avgPrecision", avgPrecision);
                        experimentResults.put("avgRecall", avgRecall);

                        experimentResults.put("microPrecision", microPrecision);
                        experimentResults.put("microRecall", microRecall);

                        processor.processResults(experimentResults);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

/*
                    // Load dataset
                    ConverterUtils.DataSource source = null;
                    try {
                        source = new ConverterUtils.DataSource("/home/oshando/IdeaProjects/swan/swan-cmd/src/main/resources/dataset/meka/meka-code.arff");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Instances data = null;
                    try {
                        data = source.getDataSet();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // Set the class indices (assumes the last n columns are the labels)
                    try {
                        MLUtils.prepareData(data);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // Choose a classifier
                    EnsembleML classifier = new EnsembleML();

                    // Evaluate the classifier using cross-validation
                    Evaluation eval = null;
                    try {
                        eval = new Evaluation(data);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        eval.crossValidateModel(classifier, data, 10, data.getRandomNumberGenerator(1));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // Output evaluation results
                    try {
                        System.out.println(eval.toClassDetailsString());


                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }




                  /*  ArffLoader loader = new ArffLoader();
                    try {
                        loader.setSource(new File("/home/oshando/IdeaProjects/swan/swan-cmd/src/main/resources/dataset/meka/meka-code.arff"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Instances data = null;
                    try {
                        data = loader.getDataSet();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Train-test split (70:30)
                    int trainSize = (int) Math.round(data.size() * 0.7);
                    int testSize = data.size() - trainSize;
                    Instances trainData = new Instances(data, 0, trainSize);
                    Instances testData = new Instances(data, trainSize, testSize);

                    /* create the classifier
                    Map<String, Object> results = new HashMap<>();
                    long timeStartTraining = System.currentTimeMillis();
                    Classifier classifier = null;
                    try {
                        classifier = (Classifier) Class.forName(classifierName).newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    try{
                        classifier.buildClassifier(trainData);
                        Evaluation evaluation = new Evaluation(testData);
                        evaluation.evaluateModel(classifier, testData);
                        results.put("accuracy", evaluation.pctCorrect()/100);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    results.put("traintime", System.currentTimeMillis() - timeStartTraining);
*/


                    // glue to the client code:
                    //  someUserFunction(a1, a2, a3, b1, b2, c);
                   /* int traintime = 0;
                    float accuracy = 569;

                    // submit the results:
                    Map<String, Object> result = new HashMap<>();
                    result.put("traintime", traintime);
                    result.put("accuracy", accuracy);

                    processor.processResults(result);*/
                };

        try {
            ExperimentRunner runner = new ExperimentRunner(expConfig, evaluator, handle);
            runner.sequentiallyConductExperiments(-1);
        } catch (ExperimentDBInteractionFailedException | InterruptedException e) {
            logger.error("Error trying to run experiments.", e);
            System.exit(1);
        }
    }
}