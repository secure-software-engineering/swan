package de.fraunhofer.iem.swan.model.toolkit;

import ai.libs.jaicore.ml.classification.loss.dataset.EClassificationPerformanceMeasure;
import ai.libs.jaicore.ml.core.dataset.serialization.ArffDatasetAdapter;
import ai.libs.jaicore.ml.core.evaluation.evaluator.SupervisedLearnerExecutor;
import ai.libs.jaicore.ml.core.filter.SplitterUtil;
import ai.libs.jaicore.ml.weka.classification.learner.IWekaClassifier;
import ai.libs.mlplan.weka.MLPlanWekaBuilder;
import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.features.WekaFeatureSet;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.util.Util;
import org.api4.java.ai.ml.classification.singlelabel.evaluation.ISingleLabelClassification;
import org.api4.java.ai.ml.core.dataset.serialization.DatasetDeserializationFailedException;
import org.api4.java.ai.ml.core.dataset.splitter.SplitFailedException;
import org.api4.java.ai.ml.core.dataset.supervised.ILabeledDataset;
import org.api4.java.ai.ml.core.dataset.supervised.ILabeledInstance;
import org.api4.java.ai.ml.core.evaluation.execution.ILearnerRunReport;
import org.api4.java.ai.ml.core.evaluation.execution.LearnerExecutionFailedException;
import org.api4.java.algorithm.Timeout;
import org.api4.java.algorithm.exceptions.AlgorithmException;
import org.api4.java.algorithm.exceptions.AlgorithmExecutionCanceledException;
import org.api4.java.algorithm.exceptions.AlgorithmTimeoutedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Oshando Johnson on 27.09.20
 */
public class MLPlan {

    private static final Logger LOGGER = LoggerFactory.getLogger(MLPlan.class);
    private final int ITERATIONS = 1;
    private WekaFeatureSet featureSet;
    private SwanOptions swanOptions;
    long start;

    public MLPlan(WekaFeatureSet features, SwanOptions options) {
        this.featureSet = features;
        swanOptions = options;
    }

    /**
     * Trains and evaluates the model with the given training data and specified classification mode.
     *
     * @return Hashmap containing the name of the classifier and it's F-Measure
     */
    public HashMap<String, HashMap<String, String>> trainModel() {

        switch (ModelEvaluator.Phase.valueOf(swanOptions.getPhase().toUpperCase())) {
            case VALIDATE:

                evaluateData(Util.exportInstancesToArff(featureSet.getTrainInstances().get("sanitizer"), "mlplan"));
                return null;
            case PREDICT:
        }
        return null;
    }

    public HashMap<String, ArrayList<Double>> evaluateData(String arffFilePath) {

        start = System.currentTimeMillis();

        try {

            ArffDatasetAdapter arffDatasetAdapter = new ArffDatasetAdapter();
            ILabeledDataset<ILabeledInstance> dataset = arffDatasetAdapter.readDataset(new File(arffFilePath));

            List<ILabeledDataset<?>> split = SplitterUtil.getLabelStratifiedTrainTestSplit(dataset, new Random(42), .7);
            LOGGER.info("Data read. Time to create dataset object was {}ms", System.currentTimeMillis() - start);

            getClassifier(split);

        } catch (InterruptedException | DatasetDeserializationFailedException | SplitFailedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Returns trained clssifier
     *
     * @param trainingSet training set
     * @return trained classifier
     */
    public Classifier getClassifier(List<ILabeledDataset<?>> trainingSet) {

        IWekaClassifier optimizedClassifier = null;

        try {
            /* initialize mlplan with a tiny search space, and let it run for 30 seconds */
            ai.libs.mlplan.core.MLPlan<IWekaClassifier> mlPlan = new MLPlanWekaBuilder()
                    .withNumCpus(4)//Set to about 12 on the server
                    .withSeed(35467463)
                    //set default timeout
                    .withTimeOut(new Timeout(60, TimeUnit.SECONDS))
                    .withDataset(trainingSet.get(0))
                    /*.withCandidateEvaluationTimeOut(new Timeout(5, TimeUnit.SECONDS))
                    .withPortionOfDataReservedForSelection(0.0)//ignore selection phase
                    .withPerformanceMeasureForSearchPhase(EClassificationPerformanceMeasure.F1_WITH_1_POSITIVE)//use F1
                    .withMCCVBasedCandidateEvaluationInSearchPhase(1, .7)*/
                    .build();
            mlPlan.setLoggerName("mlplan-swan");

            optimizedClassifier = mlPlan.call();

            long trainTime = (int) (System.currentTimeMillis() - start) / 1000;
            LOGGER.info("Finished build of the classifier. Training time was {}s.", trainTime);
            LOGGER.info("Chosen model is: {}", mlPlan.getSelectedClassifier());

            /* evaluate solution produced by mlplan */
            SupervisedLearnerExecutor executor = new SupervisedLearnerExecutor();
            ILearnerRunReport report = executor.execute(optimizedClassifier, trainingSet.get(1));
            LOGGER.info("F-measure for ML-Plan Solution: {}",
                    EClassificationPerformanceMeasure.F1_WITH_1_POSITIVE.loss(report.getPredictionDiffList().getCastedView(Integer.class, ISingleLabelClassification.class)));

        } catch (IOException | AlgorithmTimeoutedException | InterruptedException | AlgorithmException |
                 AlgorithmExecutionCanceledException e) {
            e.printStackTrace();
        } catch (LearnerExecutionFailedException e) {
            throw new RuntimeException(e);
        }
        return optimizedClassifier.getClassifier();
    }
}