package de.fraunhofer.iem.swan.model;

import ai.libs.jaicore.logging.LoggerUtil;
import ai.libs.jaicore.ml.weka.dataset.WekaInstances;
import ai.libs.mlplan.multiclass.wekamlplan.MLPlanWekaBuilder;
import de.fraunhofer.iem.swan.util.Util;
import org.api4.java.ai.ml.core.dataset.serialization.DatasetDeserializationFailedException;
import org.api4.java.ai.ml.core.dataset.splitter.SplitFailedException;
import org.api4.java.algorithm.exceptions.AlgorithmException;
import org.api4.java.algorithm.exceptions.AlgorithmExecutionCanceledException;
import org.api4.java.algorithm.exceptions.AlgorithmTimeoutedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;

import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * @author Oshando Johnson on 27.09.20
 */
public class MLPlanExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MLPlanExecutor.class);
    FilteredClassifier filteredClassifier;

    public MLPlanExecutor() {
        this.filteredClassifier = new FilteredClassifier();
    }

    /**
     * Runs ML-Plan with the given instance set.
     * @param instances training instances
     * @return hashmap containing F1 measures
     */
    public HashMap<String, String> run(Instances instances) {

        long start = System.currentTimeMillis();

        instances.setClassIndex(instances.numAttributes() - 1);

        HashMap<String, String> results = new HashMap<>();

        //Find classifier using ML-Plan
        try {

            Classifier classifier = new MLPlanWekaBuilder()
                    .withDataset(new WekaInstances(instances))
                    .withPortionOfDataReservedForSelection(.8)
                    //.withNumCpus(4)
                    //.withTimeOut(new Timeout(300, TimeUnit.SECONDS))
                    .withMCCVBasedCandidateEvaluationInSearchPhase(10, .8)
                    .build()
                    .call()
                    .getClassifier();

            long trainTime = (int) (System.currentTimeMillis() - start) / 1000;
            filteredClassifier.setClassifier(classifier);
            LOGGER.info("Finished build of the classifier. Training time was {}s.", trainTime);

            LOGGER.info("Chosen model is: {}", (filteredClassifier.getClassifier().getClass().getSimpleName()));
        } catch (IOException | AlgorithmTimeoutedException | InterruptedException | AlgorithmException | AlgorithmExecutionCanceledException e) {
            e.printStackTrace();
        }

        //Evaluate classifier using MCCV
        try {

            /* evaluate solution produced by mlplan */
            ModelEvaluator modelEvaluator = new ModelEvaluator();
            results = modelEvaluator.monteCarloValidate(instances, filteredClassifier, 0.8, 10);

         //Output evaluation results
            for(String category: results.keySet()){
                System.out.println("---" + category + "---");
                System.out.println(filteredClassifier.getClassifier().getClass().getSimpleName()+ ";"+
                        results.get(category).replace(".", ",").substring(0, results.get(category).lastIndexOf(";")));
            }

            long totalTime = (int) (System.currentTimeMillis() - start) / 1000;
            LOGGER.info("ML-Plan execution completed. Total time {}s.", totalTime);
            return modelEvaluator.getfMeasure();
        } catch (NoSuchElementException e) {
            LOGGER.error("Building the classifier failed: {}", LoggerUtil.getExceptionInfo(e));
        }
        return results;
    }

    /**
     * Returns classifier selected by ML-Plan.
     * @return WEKA classifier
     */
    public FilteredClassifier getClassifier() {
        return filteredClassifier;
    }

    public static void main(String[] args) throws DatasetDeserializationFailedException, IOException, InterruptedException, SplitFailedException, AlgorithmExecutionCanceledException, AlgorithmTimeoutedException, AlgorithmException {

        Instances dataset = Util.loadArffFile("/Users/oshando/Projects/thesis/03-code/swan/swan_core/swan-out/weka/Train_sanitizer_none.arff");
        //Instances dataset = Util.loadArffFile("/Users/oshando/Projects/thesis/03-code/swan/swan_core/src/main/resources/waveform.arff");

        MLPlanExecutor mlPlan = new MLPlanExecutor();
        mlPlan.run(dataset);
    }
}
