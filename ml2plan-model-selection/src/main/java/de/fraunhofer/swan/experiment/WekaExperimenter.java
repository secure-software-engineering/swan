package de.fraunhofer.swan.experiment;

import ai.libs.jaicore.db.IDatabaseConfig;
import ai.libs.jaicore.experiments.*;
import ai.libs.jaicore.experiments.databasehandle.ExperimenterMySQLHandle;
import ai.libs.jaicore.experiments.exceptions.ExperimentAlreadyExistsInDatabaseException;
import ai.libs.jaicore.experiments.exceptions.ExperimentDBInteractionFailedException;
import ai.libs.jaicore.experiments.exceptions.IllegalExperimentSetupException;
import weka.classifiers.Evaluation;
import org.aeonbits.owner.ConfigFactory;
import org.api4.java.algorithm.exceptions.AlgorithmExecutionCanceledException;
import org.api4.java.algorithm.exceptions.AlgorithmTimeoutedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WekaExperimenter {

    private static final Logger logger = LoggerFactory.getLogger(WekaExperimenter.class);

    public static void main(final String[] args) {

        IExperimentSetConfig expConfig = (IExperimentSetConfig) ConfigFactory
                .create(IExperimentSetConfig.class)

                .loadPropertiesFromFile(new File("/home/oshando/IdeaProjects/swan/ml2plan-model-selection/configs-weka/experiments.cnf"));

        IDatabaseConfig dbConfig = (IDatabaseConfig) ConfigFactory
                .create(IDatabaseConfig.class)
                .loadPropertiesFromFile(new File("/home/oshando/IdeaProjects/swan/ml2plan-model-selection/configs-weka/db.properties"));
        ExperimenterMySQLHandle handle = new ExperimenterMySQLHandle(dbConfig);

        ExperimentDatabasePreparer preparer = new ExperimentDatabasePreparer(expConfig, handle);
        try {
            preparer.synchronizeExperiments();
        } catch (ExperimentDBInteractionFailedException | IllegalExperimentSetupException |
                 AlgorithmTimeoutedException | InterruptedException | AlgorithmExecutionCanceledException |
                 ExperimentAlreadyExistsInDatabaseException e) {
            logger.error("Couldn't synchronize experiment table.", e);
        }

        IExperimentSetEvaluator evaluator =
                (ExperimentDBEntry experimentEntry, IExperimentIntermediateResultProcessor processor) -> {
                    Experiment experiment = experimentEntry.getExperiment();
                    Map<String, String> keyFields = experiment.getValuesOfKeyFields();

                    // gather experiment key values:
                    String dataset = keyFields.get("datasets");
                    int seed = Integer.parseInt(keyFields.get("seeds"));

                    try {
                        // Load dataset
                        ConverterUtils.DataSource source = new ConverterUtils.DataSource(dataset);
                        Instances data = source.getDataSet();
                        data.setClassIndex(data.numAttributes() - 1);
                        data.randomize(new Random(seed));

                        SMO svm = new SMO();

                        Evaluation eval = new Evaluation(data);
                        eval.crossValidateModel(svm, data, 10, new Random(seed));

                        String info = dataset.substring(dataset.lastIndexOf("/")) + " " + eval.toClassDetailsString();

                        double[] lblPrecision = {eval.precision(0), eval.precision(1)};
                        double[] lblRecall = {eval.recall(0), eval.recall(1)};
                        double[] lblHarmonic = {eval.fMeasure(0), eval.fMeasure(1)};

                        //Macro calculates for each class individually and then takes the average of these values.
                        double avgPrecision = Arrays.stream(lblPrecision).average().getAsDouble();
                        double avgRecall = Arrays.stream(lblRecall).average().getAsDouble();

                        double avgFMeasure = eval.unweightedMacroFmeasure();

                        //It calculates the precision globally by counting the total true positives and false positives.
                        double microFMeasure = eval.unweightedMicroFmeasure();

                        // submit the results:
                        Map<String, Object> experimentResults = new HashMap<>();
                        experimentResults.put("info", info);

                        experimentResults.put("lblPrecision", Arrays.toString(lblPrecision));
                        experimentResults.put("lblRecall", Arrays.toString(lblRecall));
                        experimentResults.put("lblHarmonic", Arrays.toString(lblHarmonic));

                        experimentResults.put("avgPrecision", avgPrecision);
                        experimentResults.put("avgRecall", avgRecall);
                        experimentResults.put("avgFMeasure", avgFMeasure);

                        experimentResults.put("microFMeasure", microFMeasure);
                        System.out.println(experimentResults);
                        processor.processResults(experimentResults);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
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