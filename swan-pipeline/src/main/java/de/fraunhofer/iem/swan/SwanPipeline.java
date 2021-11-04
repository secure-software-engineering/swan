package de.fraunhofer.iem.swan;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.Features;
import de.fraunhofer.iem.swan.features.InstancesHandler;
import de.fraunhofer.iem.swan.features.code.FeatureHandler;
import de.fraunhofer.iem.swan.features.code.soot.Loader;
import de.fraunhofer.iem.swan.features.doc.DocFeatureHandler;
import de.fraunhofer.iem.swan.io.dataset.Parser;
import de.fraunhofer.iem.swan.io.dataset.Writer;
import de.fraunhofer.iem.swan.model.Learner;
import de.fraunhofer.iem.swan.util.SwanConfig;
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

    /**
     * Executes the analysis and can also be called from outside by lients.
     *
     * @param options options to run the analysis
     * @throws IOException          In case an error occurs during the preparation
     *                              or execution of the analysis.
     */
    public void run(SwanOptions options) throws IOException, InterruptedException {

        long startAnalysisTime = System.currentTimeMillis();

        // Cache the list of classes and the CP.
        Set<String> testClasses = Util.getAllClassesFromDirectory(options.getTestData());
        String testCp = Util.buildCP(options.getTestData());

        logger.info("Loading train data from {}", options.getTrainData());
        String trainingCp = Util.buildCP(options.getTrainData());

        // Cache the methods from the training set.
        parser = new Parser(trainingCp);
        parser.loadTrainingSet(Collections.singleton(options.getDatasetJson()));
        logger.info("{} training methods, distribution={}",
                parser.methods().size(), Util.countCategories(parser.methods(), false));

        //Remove methods that do not have method doc comments
        parser.removeUndocumentedMethods();
        logger.info("Remove undocumented training methods. Remaining {}, distribution={}",
                parser.methods().size(), Util.countCategories(parser.methods(), true));

        // Cache the methods from the testing set.
        logger.info("Loading test data from {}", options.getTestData());
        loader = new Loader(testCp);
        loader.loadTestSet(testClasses, parser.methods());

        // Cache the features.
        logger.info("Loading feature instances");
        featureHandler = new FeatureHandler(trainingCp + System.getProperty("path.separator") + testCp);
        featureHandler.initializeFeatures();

        Set<Method> methods = new HashSet<>();

        for (Method method : parser.methods()) {
            List<String> words = Arrays.asList(StringUtils.split(method.getJavadoc().getMethodComment(), " "));
            if (method.getJavadoc().getMethodComment().length() > 0 || words.size() > 1
            ) {
                //  if (method.getDiscovery().contentEquals("manual")) {
                methods.add(method);
                //if(method.getCategoriesTrained().contains(Category.AUTHENTICATION_NEUTRAL))
                //   System.out.println(method.getJavaSignature());
            }
        }

        //Populate SWAN feature attributes
        docFeatureHandler = null;
        InstancesHandler.FeatureSet feature = InstancesHandler.FeatureSet.valueOf(options.getFeatureSet());

        new Features();
        switch (feature) {
            case SWANDOC_MANUAL:
            case SWAN_SWANDOC_MANUAL:

                docFeatureHandler = new DocFeatureHandler(methods);
                docFeatureHandler.initialiseManualFeatureSet();
                docFeatureHandler.evaluateManualFeatureData();
                break;
            case SWANDOC_WORD_EMBEDDING:
            case SWAN_SWANDOC_WORD_EMBEDDING:

                docFeatureHandler = new DocFeatureHandler(methods);
                docFeatureHandler.initialiseAutomaticFeatureSet();
                docFeatureHandler.evaluateAutomaticFeatureData();
                break;
        }

        // Prepare classifier.
        logger.info("Preparing classifier");
        writer = new Writer(loader.methods());
        learner = new Learner(writer);

        Learner.Mode learnerMode = Learner.Mode.valueOf(options.getLearningMode());

        /*
            FIRST PHASE - binary classification for each of the categories.
            (1) Classify: source, sink, sanitizer,
            auth-no-change, auth-unsafe-state, auth-safe-state
            (2) Classify: relevant
         */

        //Store predictions for each classifier and iteration.
        predictions = new HashMap<>();
        for (int x = 0; x < 10; x++)
            predictions.put(Integer.toString(x), new HashSet<>());

        runClassEvaluation(options.getSrmClasses(), feature, learnerMode );

        // Save data from last classification.
        loader.resetMethods();

        // Cache the methods from the second test set.
        loader.pruneNone();

        /*
            SECOND PHASE - binary classification for each of the CWE categories.
            (1) Classify: cwe78, cwe079, cwe089, cwe306, cwe601, cwe862, cwe863
         */
        runClassEvaluation(options.getCweClasses(), feature, learnerMode);

        SwanConfig swanConfig = new SwanConfig();
        Properties config = swanConfig.getConfig();
        String fileName = config.getProperty("output_file_name");

        String outputFile = options.getOutputDir() + File.separator + fileName + ".json";
        logger.info("Writing results to {}", outputFile);
        writer.printResultsJSON(loader.methods(), outputFile);

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