package de.fraunhofer.iem.swan;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.FeatureSetSelector;
import de.fraunhofer.iem.swan.features.IFeatureSet;
import de.fraunhofer.iem.swan.io.dataset.Dataset;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.io.dataset.SrmListUtils;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.soot.Soot;
import de.fraunhofer.iem.swan.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Runner for SWAN
 *
 * @author Lisa Nguyen Quang Do
 */

public class SwanPipeline {

    private static final Logger logger = LoggerFactory.getLogger(SwanPipeline.class);
    public static SwanOptions options;
    private ModelEvaluator modelEvaluator;

    public SwanPipeline(SwanOptions options) {
        SwanPipeline.options = options;
    }

    /**
     * Executes the analysis and can also be called from outside by clients.
     *
     * @throws IOException In case an error occurs during the preparation
     *                     or execution of the analysis.
     */
    public void run() throws IOException, InterruptedException {

        long startAnalysisTime = System.currentTimeMillis();

        //Run Soot
        Soot soot = new Soot(options.getTrainDataDir(), options.getTestDataDir());

        // Load methods in training dataset
        Dataset dataset = new Dataset();
        dataset.setTrain(SrmListUtils.importFile(options.getDatasetJson()));

        if (!options.getTrainDataDir().isEmpty())
            soot.cleanupList(dataset.getTrain());

        logger.info("Loaded {} training methods, distribution={}", dataset.getTrainMethods().size(), Util.countCategories(dataset.getTrainMethods()));

        //Load methods from the test set
        dataset.setTest(new SrmList(options.getTestDataDir()));
        Set<Method> testMethods = soot.loadMethods(dataset.getTest().getTestClasses());
        dataset.getTest().setMethods(testMethods);
        logger.info("Loaded {} methods from {}", testMethods.size(), options.getTestDataDir());

        //Initialize and populate features
        FeatureSetSelector featureSetSelector = new FeatureSetSelector();
        IFeatureSet featureSet = featureSetSelector.select(dataset, options);

        //Train and evaluate model for SRM and CWE categories
        modelEvaluator = new ModelEvaluator(featureSet, options, dataset.getTestMethods());
        modelEvaluator.trainModel();

        long analysisTime = System.currentTimeMillis() - startAnalysisTime;
        logger.info("Total runtime {} minutes", analysisTime / 60000);
    }

    public ModelEvaluator getModelEvaluator() {
        return modelEvaluator;
    }
}