package de.fraunhofer.iem.swan;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.features.FeaturesHandler;
import de.fraunhofer.iem.swan.features.code.soot.SourceFileLoader;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.io.dataset.SrmListUtils;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Runner for SWAN
 *
 * @author Lisa Nguyen Quang Do
 */

public class SwanPipeline {

    private Learner learner;
    private Loader loader;
    private Parser parser;
    private FeatureHandler featureHandler;
    private Writer writer;
    // Configuration tags for debugging

    private static final Logger logger = LoggerFactory.getLogger(SwanPipeline.class);
    private DocFeatureHandler docFeatureHandler;
    public static HashMap<String, HashSet<String>> predictions;

    public static SwanOptions options;

    public SwanPipeline(SwanOptions options) {
        SwanPipeline.options = options;
    }

    /**
     * Executes the analysis and can also be called from outside by lients.
     *
     * @throws IOException          In case an error occurs during the preparation
     *                              or execution of the analysis.
     */
    public void run() throws IOException, InterruptedException {

        long startAnalysisTime = System.currentTimeMillis();

        // Load methods in training dataset
        SrmList dataset = SrmListUtils.importFile(options.getDatasetJson(), options.getTrainDataDir());
        logger.info("Loaded {} training methods, distribution={}", dataset.getMethods().size(), Util.countCategories(dataset.getMethods()));

        //Load methods from the test set
        logger.info("Loading test JARs in {}", options.getTestDataDir());
        SourceFileLoader testDataset = new SourceFileLoader(options.getTestDataDir());
        testDataset.load(dataset.getMethods());

        //Initialize and populate features
        FeaturesHandler featuresHandler = new FeaturesHandler(dataset, testDataset, options);
        featuresHandler.createFeatures();

        // Cache the methods from the second test set.
        loader.pruneNone();

        /*
            SECOND PHASE - binary classification for each of the CWE categories.
            (1) Classify: cwe78, cwe079, cwe089, cwe306, cwe601, cwe862, cwe863
         */
        runClassEvaluation(options.getCweClasses(), feature, learnerMode);

        String outputFile = options.getOutputDir() + File.separator + "swan-srm-cwe-list.json";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(outputFile), dataset);
        logger.info("SRM/CWE list exported to {}", outputFile);

        long analysisTime = System.currentTimeMillis() - startAnalysisTime;
        logger.info("Total runtime {} mins", analysisTime / 60000);
    }

    public void runClassEvaluation(List<String> classes, InstancesHandler.FeatureSet featureSet, Learner.Mode learnerMode) {

        for (String cat : classes) {

            HashSet<Category> categories;

            if (cat.contentEquals("authentication"))
                categories = new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION_NEUTRAL, Category.NONE));
            else
                categories = new HashSet<>(Arrays.asList(Category.fromText(cat), Category.NONE));

            runClassifier(categories, Learner.EVAL_MODE.CLASS, featureSet, learnerMode);
        }
    }


    private double runClassifier(HashSet<Category> categories, Learner.EVAL_MODE eval_mode, InstancesHandler.FeatureSet featureSet, Learner.Mode learnerMode) {
        parser.resetMethods();
        loader.resetMethods();

        logger.info("Starting classification for {}", categories.toString());
        long startAnalysisTime;

        if (categories.stream().anyMatch(Category::isCwe)) {

            ArrayList<InstancesHandler> instancesHandlers = new ArrayList<>();
            for (String iteration : predictions.keySet()) {

                if (predictions.get(iteration).size() == 0)
                    continue;

                HashSet<Method> methods = new HashSet<>();

                for (Method method : parser.getMethods()) {
                    if (predictions.get(iteration).contains(method.getArffSafeSignature()))
                        methods.add(method);
                }

                InstancesHandler instancesHandler = new InstancesHandler();
                instancesHandler.createInstances(methods, featureHandler.features(), docFeatureHandler, categories, featureSet);

                instancesHandlers.add(instancesHandler);
            }

            startAnalysisTime = System.currentTimeMillis();
            learner.trainModel(instancesHandlers, learnerMode);

        } else {
            InstancesHandler instancesHandler = new InstancesHandler();
            instancesHandler.createInstances(parser.getMethods(), featureHandler.features(), docFeatureHandler, categories, featureSet);
            startAnalysisTime = System.currentTimeMillis();

            ArrayList<InstancesHandler> instancesHandlers = new ArrayList<>();
            instancesHandlers.add(instancesHandler);
            learner.trainModel(instancesHandlers, learnerMode);
        }

        long analysisTime = System.currentTimeMillis() - startAnalysisTime;
        logger.info("Total time for classification {}ms", analysisTime);

        return 0.0;
    }
}