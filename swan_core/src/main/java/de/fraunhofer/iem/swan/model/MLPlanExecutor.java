package de.fraunhofer.iem.swan.model;

import ai.libs.jaicore.ml.classification.loss.dataset.EClassificationPerformanceMeasure;
import ai.libs.jaicore.ml.core.dataset.serialization.ArffDatasetAdapter;
import ai.libs.jaicore.ml.core.evaluation.evaluator.SupervisedLearnerExecutor;
import ai.libs.jaicore.ml.core.filter.SplitterUtil;
import ai.libs.jaicore.ml.weka.classification.learner.IWekaClassifier;
import ai.libs.mlplan.core.MLPlan;
import ai.libs.mlplan.multiclass.wekamlplan.MLPlanWekaBuilder;
import org.api4.java.ai.ml.classification.singlelabel.evaluation.ISingleLabelClassification;
import org.api4.java.ai.ml.core.dataset.serialization.DatasetDeserializationFailedException;
import org.api4.java.ai.ml.core.dataset.splitter.SplitFailedException;
import org.api4.java.ai.ml.core.dataset.supervised.ILabeledDataset;
import org.api4.java.ai.ml.core.evaluation.execution.ILearnerRunReport;
import org.api4.java.ai.ml.core.evaluation.execution.LearnerExecutionFailedException;
import org.api4.java.algorithm.Timeout;
import org.api4.java.algorithm.exceptions.AlgorithmException;
import org.api4.java.algorithm.exceptions.AlgorithmExecutionCanceledException;
import org.api4.java.algorithm.exceptions.AlgorithmTimeoutedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Oshando Johnson on 27.09.20
 */
public class MLPlanExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MLPlanExecutor.class);
    private final int ITERATIONS = 10;

    public MLPlanExecutor() {

    }

    /**
     * Run ML-Plan using the provided path to the ARFF file.
     *
     * @param arffFilePath file path for ARFF file
     */
    public void evaluateDataset(String arffFilePath) {

        long start = System.currentTimeMillis();

        //Initialize dataset using ARFF file path
        ILabeledDataset<?> dataset = null;
        try {
            dataset = ArffDatasetAdapter.readDataset(new File(arffFilePath));
        } catch (DatasetDeserializationFailedException e) {
            e.printStackTrace();
        }

        //For each iteration, create a new train-test-split and run ML-Plan
        for (int iteration = 0; iteration < ITERATIONS; iteration++) {

            try {
                List<ILabeledDataset<?>> split = SplitterUtil.getLabelStratifiedTrainTestSplit(dataset, new Random(1337 + (iteration * 11)), 0.7);
                LOGGER.info("Data read. Time to create dataset object was {}ms", System.currentTimeMillis() - start);

                IWekaClassifier optimizedClassifier = getClassifier(split.get(0));

                /* evaluate solution produced by mlplan */
                SupervisedLearnerExecutor executor = new SupervisedLearnerExecutor();
                ILearnerRunReport report = executor.execute(optimizedClassifier, split.get(0), split.get(1));

                System.out.println(report.getPredictionDiffList().getPredictionsAsList().toString());

                LOGGER.info("Error Rate of the solution produced by ML-Plan: {}. ",
                        EClassificationPerformanceMeasure.F1_WITH_1_POSITIVE.loss(report.getPredictionDiffList().getCastedView(Integer.class, ISingleLabelClassification.class)));

            } catch (SplitFailedException | InterruptedException | LearnerExecutionFailedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * SReturns trained clssifier
     * @param trainingSet training set
     * @return trained classifier
     */
    public IWekaClassifier getClassifier(ILabeledDataset<?> trainingSet) {

        IWekaClassifier optimizedClassifier = null;
        /* initialize mlplan with a tiny search space, and let it run for 30 seconds */

        try {
            MLPlan<IWekaClassifier> mlPlan = new MLPlanWekaBuilder()
                    .withNumCpus(8)//Set to about 12 on the server
                    .withSeed(35467463)
                    //set default timeout
                    .withTimeOut(new Timeout(30, TimeUnit.SECONDS))
                    .withDataset(trainingSet)
                    .withPortionOfDataReservedForSelection(0.0)//ignore selection phase
                    .withPerformanceMeasureForSearchPhase(EClassificationPerformanceMeasure.F1_WITH_1_POSITIVE)//use F1
                    .withMCCVBasedCandidateEvaluationInSearchPhase(10, .7)
                    .build();

            mlPlan.setLoggerName("testedalgorithm");

            long start = System.currentTimeMillis();

            optimizedClassifier = mlPlan.call();

            long trainTime = (int) (System.currentTimeMillis() - start) / 1000;
            LOGGER.info("Finished build of the classifier. Training time was {}s.", trainTime);
            LOGGER.info("Chosen model is: {}", (mlPlan.getSelectedClassifier()));
            LOGGER.info("Internally believed error was {}", mlPlan.getInternalValidationErrorOfSelectedClassifier());

        } catch (IOException | AlgorithmTimeoutedException | InterruptedException | AlgorithmException | AlgorithmExecutionCanceledException e) {
            e.printStackTrace();
        }
        return optimizedClassifier;
    }

    public static void main(String[] args) {

        String file = "/Users/oshando/Projects/thesis/03-code/swan/swan_core/src/main/resources/waveform.arff";

        MLPlanExecutor mlPlan = new MLPlanExecutor();
        mlPlan.evaluateDataset(file);
    }
}
