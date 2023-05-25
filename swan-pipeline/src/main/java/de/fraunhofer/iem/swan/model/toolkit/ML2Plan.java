package de.fraunhofer.iem.swan.model.toolkit;

import ai.libs.jaicore.ml.classification.multilabel.dataset.IMekaInstances;
import ai.libs.jaicore.ml.classification.multilabel.dataset.MekaInstances;
import ai.libs.jaicore.ml.classification.multilabel.evaluation.loss.InstanceWiseF1;
import ai.libs.jaicore.ml.classification.multilabel.learner.IMekaClassifier;
import ai.libs.jaicore.ml.core.evaluation.evaluator.SupervisedLearnerExecutor;
import ai.libs.jaicore.ml.core.filter.SplitterUtil;
import ai.libs.mlplan.core.MLPlan;
import ai.libs.mlplan.meka.ML2PlanMekaBuilder;
import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.features.MekaFeatureSet;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import meka.core.MLUtils;
import org.api4.java.ai.ml.classification.multilabel.evaluation.IMultiLabelClassification;
import org.api4.java.ai.ml.core.dataset.supervised.ILabeledDataset;
import org.api4.java.ai.ml.core.evaluation.execution.ILearnerRunReport;
import org.api4.java.algorithm.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Oshando Johnson on 27.09.20
 */
public class ML2Plan {

    private static final Logger LOGGER = LoggerFactory.getLogger(ML2Plan.class);
    private MekaFeatureSet featureSet;
    private SwanOptions swanOptions;

    public ML2Plan(MekaFeatureSet features, SwanOptions options) {
        this.featureSet = features;
        swanOptions = options;
    }

    /**
     * Trains and evaluates the model with the given training data and specified classification mode.
     *
     * @return Hashmap containing the name of the classifier and it's F-Measure
     */
    public SrmList trainModel() {

        switch (ModelEvaluator.Phase.valueOf(swanOptions.getPhase().toUpperCase())) {
            case VALIDATE:

                try {
                    crossValidate(featureSet.getTrainInstances().get("meka"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            case PREDICT:
        }
        return null;
    }

    public void crossValidate(Instances instances) throws Exception {

        //Reformat relation name to meet ML-Plan requirements: 'Example_Dataset: -C 3 -split-percentage 50'
        instances.setRelationName(instances.relationName().replace("-meka.", " -meka."));

        //Prepare instances and split into train and test datasets
        MLUtils.prepareData(instances);
        IMekaInstances dataset = new MekaInstances(instances);
        List<ILabeledDataset<?>> split = SplitterUtil.getSimpleTrainTestSplit(dataset, new Random(0), .7);

        // Initialize ML-Plan
        MLPlan<IMekaClassifier> mlplan = new ML2PlanMekaBuilder()
                .withNumCpus(4)
                .withTimeOut(new Timeout(300, TimeUnit.SECONDS))
                .withDataset(split.get(0)).build();

        mlplan.setLoggerName("ml2plan");

        try {
            long start = System.currentTimeMillis();
            IMekaClassifier optimizedClassifier = mlplan.call();
            long trainTime = (int) (System.currentTimeMillis() - start) / 1000;
            LOGGER.info("Classifier built in {}s", trainTime);

            //Cross-validate classifier produced ML2-Plan
            SupervisedLearnerExecutor executor = new SupervisedLearnerExecutor();
            ILearnerRunReport report = executor.execute(optimizedClassifier, split.get(1));
            LOGGER.info("Model error Rate{}", new InstanceWiseF1().loss(report.getPredictionDiffList().getCastedView(int[].class, IMultiLabelClassification.class)));

        } catch (NoSuchElementException e) {

            LOGGER.error("Building the classifier failed: {}", e.getMessage());
        }
    }
}
