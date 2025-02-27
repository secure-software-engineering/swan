package de.fraunhofer.swan.experiment;

import ai.libs.jaicore.db.IDatabaseConfig;
import ai.libs.jaicore.experiments.*;
import ai.libs.jaicore.experiments.databasehandle.ExperimenterMySQLHandle;
import ai.libs.jaicore.experiments.exceptions.ExperimentAlreadyExistsInDatabaseException;
import ai.libs.jaicore.experiments.exceptions.ExperimentDBInteractionFailedException;
import ai.libs.jaicore.experiments.exceptions.IllegalExperimentSetupException;
import meka.classifiers.multilabel.Evaluation;
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

    public static void main(final String[] args) {

        IExperimentSetConfig expConfig = (IExperimentSetConfig) ConfigFactory
                .create(IExperimentSetConfig.class)

                .loadPropertiesFromFile(new File(".../swan/ml2plan-model-selection/configs/experiments.cnf"));

        IDatabaseConfig dbConfig = (IDatabaseConfig) ConfigFactory
                .create(IDatabaseConfig.class)
                .loadPropertiesFromFile(new File(".../swan/ml2plan-model-selection/configs/db.properties"));
        ExperimenterMySQLHandle handle = new ExperimenterMySQLHandle(dbConfig);

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
                    String options = keyFields.get("options");
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
                        classifier.setOptions(options.split("\\s+"));

                        Result result = Evaluation.cvModel(classifier, data, 10, "PCutL", "7");

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