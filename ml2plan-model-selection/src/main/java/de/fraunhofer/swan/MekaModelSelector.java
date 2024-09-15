package de.fraunhofer.swan;

import ai.libs.jaicore.ml.classification.multilabel.dataset.IMekaInstances;
import ai.libs.jaicore.ml.classification.multilabel.dataset.MekaInstances;
import ai.libs.jaicore.ml.classification.multilabel.evaluation.loss.InstanceWiseF1;
import ai.libs.jaicore.ml.classification.multilabel.learner.IMekaClassifier;
import ai.libs.jaicore.ml.core.evaluation.evaluator.SupervisedLearnerExecutor;
import ai.libs.jaicore.ml.core.filter.SplitterUtil;
import ai.libs.mlplan.core.MLPlan;
import ai.libs.mlplan.meka.ML2PlanMekaBuilder;
import meka.core.MLUtils;
import org.api4.java.ai.ml.classification.multilabel.evaluation.IMultiLabelClassification;
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
public class MekaModelSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(MekaModelSelector.class.getName());

    public static void main(String[] args) throws Exception {

        evaluateModel("/swan-cmd/src/main/resources/dataset/meka/meka-code.arff");
    }

    /**
     * Run ML2-Plan to select multi-label model.
     * @param arffFile path to ARFF dataset file
     * @throws Exception
     */
    public static void evaluateModel(String arffFile) throws Exception {

        Instances instances = new Instances(new FileReader(arffFile));

        //Prepare instances and split into train and test datasets
        MLUtils.prepareData(instances);
        IMekaInstances dataset = new MekaInstances(instances);
        List<ILabeledDataset<?>> split = SplitterUtil.getSimpleTrainTestSplit(dataset, new Random(0), .7);
        LOGGER.info("Loading {} instances from {}", instances.numInstances(), arffFile);

        // Initialize ML2-Plan
        MLPlan<IMekaClassifier> mlplan = new ML2PlanMekaBuilder()
                .withNumCpus(4)
                .withTimeOut(new Timeout(100, TimeUnit.SECONDS))
                .withDataset(split.get(0)).build();

        try {

            //Evaluate ML2-Plan solution with test set
            IMekaClassifier classifier  = mlplan.call();

            SupervisedLearnerExecutor executor = new SupervisedLearnerExecutor();
            ILearnerRunReport report = executor.execute(classifier, split.get(1));
            LOGGER.info("Model error Rate {}", new InstanceWiseF1()
                    .loss(report.getPredictionDiffList().getCastedView(int[].class, IMultiLabelClassification.class)));

        } catch (NoSuchElementException e) {

            LOGGER.error("Building the classifier failed: {}", e.getMessage());
        }
    }
}
