package de.fraunhofer.swan;

import ai.libs.jaicore.ml.classification.loss.dataset.EClassificationPerformanceMeasure;
import ai.libs.jaicore.ml.classification.multilabel.dataset.IMekaInstances;
import ai.libs.jaicore.ml.classification.multilabel.dataset.MekaInstances;
import ai.libs.jaicore.ml.classification.multilabel.evaluation.loss.InstanceWiseF1;
import ai.libs.jaicore.ml.classification.multilabel.learner.IMekaClassifier;
import ai.libs.jaicore.ml.core.evaluation.evaluator.SupervisedLearnerExecutor;
import ai.libs.jaicore.ml.core.filter.SplitterUtil;
import ai.libs.jaicore.ml.weka.classification.learner.IWekaClassifier;
import ai.libs.jaicore.ml.weka.dataset.IWekaInstances;
import ai.libs.jaicore.ml.weka.dataset.WekaInstances;
import ai.libs.mlplan.core.MLPlan;
import ai.libs.mlplan.meka.ML2PlanMekaBuilder;
import ai.libs.mlplan.weka.MLPlanWekaBuilder;
import meka.core.MLUtils;
import org.api4.java.ai.ml.classification.multilabel.evaluation.IMultiLabelClassification;
import org.api4.java.ai.ml.classification.singlelabel.evaluation.ISingleLabelClassification;
import org.api4.java.ai.ml.core.dataset.supervised.ILabeledDataset;
import org.api4.java.ai.ml.core.evaluation.execution.ILearnerRunReport;
import org.api4.java.algorithm.Timeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.io.FileReader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Oshando Johnson on 27.09.20
 */
public class ModelSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelSelector.class.getName());

    public static void main(String[] args) throws Exception {

        LOGGER.info("Args: TOOLKIT {}, CPU {}, Dataset {}, Duration {}", args[0], args[1], args[2], args[3]);

        if (args[0].contains("weka"))
            selectWekaModel(Integer.parseInt(args[1]), args[2], Long.parseLong(args[3]));
        else
            selectMekaModel(Integer.parseInt(args[1]), args[2], Long.parseLong(args[3]));
    }

    /**
     * Run ML2-Plan to select multi-label model.
     *
     * @param arffFile path to ARFF dataset file
     * @throws Exception
     */
    public static void selectMekaModel(int cpu, String arffFile, long duration) throws Exception {

        Instances instances = new Instances(new FileReader(arffFile));
        LOGGER.info("Loaded {} instances from {}", instances.numInstances(), arffFile);
        //Prepare instances and split into train and test datasets
        MLUtils.prepareData(instances);
        IMekaInstances dataset = new MekaInstances(instances);
        List<ILabeledDataset<?>> split = SplitterUtil.getSimpleTrainTestSplit(dataset, new Random(0), .7);
        LOGGER.info("Loading {} instances from {}", instances.numInstances(), arffFile);

        // Initialize ML2-Plan
        MLPlan<IMekaClassifier> mlplan = new ML2PlanMekaBuilder()
                .withNumCpus(cpu)
                .withTimeOut(new Timeout(duration, TimeUnit.MINUTES))
                .withDataset(split.get(0)).build();

        try {

            //Evaluate ML2-Plan solution with test set
            IMekaClassifier classifier = mlplan.call();

            SupervisedLearnerExecutor executor = new SupervisedLearnerExecutor();
            ILearnerRunReport report = executor.execute(classifier, split.get(1));
            LOGGER.info("Model error Rate {}", new InstanceWiseF1()
                    .loss(report.getPredictionDiffList().getCastedView(int[].class, IMultiLabelClassification.class)));

        } catch (NoSuchElementException e) {

            LOGGER.error("Building the classifier failed: {}", e.getMessage());
        }
    }

    /**
     * Run ML2-Plan to select multi-label model.
     *
     * @param arffFile path to ARFF dataset file
     * @throws Exception
     */
    public static void selectWekaModel(int cpu, String arffFile, long duration) throws Exception {

        Instances instances = new Instances(new FileReader(arffFile));
        LOGGER.info("Loaded {} instances from {}", instances.numInstances(), arffFile);
        //Prepare instances and split into train and test datasets

        MLUtils.prepareData(instances);
        IWekaInstances dataset = new WekaInstances(instances);
        List<ILabeledDataset<?>> split = SplitterUtil.getSimpleTrainTestSplit(dataset, new Random(0), .7);
        LOGGER.info("Loading {} instances from {}", instances.numInstances(), arffFile);

        // Initialize ML2-Plan
        MLPlan<IWekaClassifier> mlplan = new MLPlanWekaBuilder()
                .withNumCpus(cpu)
                .withTimeOut(new Timeout(duration, TimeUnit.MINUTES))
                .withDataset(split.get(0)).build();

        try {

            //Evaluate ML-Plan solution with test set
            IWekaClassifier classifier = mlplan.call();

            LOGGER.info("Chosen model is: {}", (mlplan.getSelectedClassifier()));
            LOGGER.info("Chosen WEKA model is: {}", (classifier.getClassifier()));

            /* evaluate solution produced by mlplan */
            SupervisedLearnerExecutor executor = new SupervisedLearnerExecutor();
            ILearnerRunReport report = executor.execute(classifier, split.get(1));
            LOGGER.info("Error Rate of the solution produced by ML-Plan: {}. Internally believed error was {}",
                    EClassificationPerformanceMeasure.ERRORRATE.loss(report.getPredictionDiffList()
                            .getCastedView(Integer.class, ISingleLabelClassification.class)),
                    mlplan.getInternalValidationErrorOfSelectedClassifier());
        } catch (NoSuchElementException e) {

            LOGGER.error("Building the classifier failed: {}", e.getMessage());
        }
    }
}
