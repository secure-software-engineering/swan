package de.fraunhofer.iem.swan;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.DocFeatureHandler;
import de.fraunhofer.iem.swan.doc.features.automatic.AutomaticFeatureHandler;
import de.fraunhofer.iem.swan.doc.features.automatic.DocCommentVector;
import de.fraunhofer.iem.swan.doc.features.manual.ManualFeaturesHandler;
import de.fraunhofer.iem.swan.doc.util.Utils;
import de.fraunhofer.iem.swan.features.FeatureHandler;
import de.fraunhofer.iem.swan.io.FileUtility;
import de.fraunhofer.iem.swan.io.Loader;
import de.fraunhofer.iem.swan.io.Parser;
import de.fraunhofer.iem.swan.io.Writer;
import de.fraunhofer.iem.swan.model.InstancesHandler;
import de.fraunhofer.iem.swan.model.Learner;
import de.fraunhofer.iem.swan.util.SwanConfig;
import de.fraunhofer.iem.swan.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Runner for SWAN
 *
 * @author Lisa Nguyen Quang Do
 */

public class Main {

    private Learner learner;
    private Loader loader;
    private Parser parser;
    private FeatureHandler featureHandler;
    private String outputPath;
    private Writer writer;

    // Configuration tags for debugging
    private static final boolean runSources = true;
    private static final boolean runSinks = true;
    private static final boolean runSanitizers = true;
    private static final boolean runAuthentications = true;
    private static final boolean runRelevant = true;
    private static final boolean runCwes = true;

    private static final boolean runOAT = false; // run one at a time analysis
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final InstancesHandler.INSTANCE_SET INSTANCE_TYPE = InstancesHandler.INSTANCE_SET.SWAN_SWANDOC_MANUAL;
    private static final Learner.LEARN_MODE LEARNING_MODE = Learner.LEARN_MODE.MANUAL;

    public static String INPUT = "/Users/oshando/Projects/thesis/03-code/swandoc/src/main/resources/training-jars";
    public static String JAVADOC_OUTPUT = "/Users/oshando/Projects/thesis/03-code/training-docs";
    public static String TRAINING_SET = "/Users/oshando/Projects/thesis/03-code/swandoc/src/main/resources/training-set-javadoc.json";


    DocFeatureHandler docFeatureHandler;

    public static void main(String[] args) {

        try {
            if (args.length != 4) {
                System.err.println("");
                System.err.println(
                        "Usage: java de.fraunhofer.iem.swan.Main <source-dir> <train-sourcecode> <train-json> <output-dir>\n");
                System.err.println("<source-dir>:\tDirectory with all JAR files or source code of the Test Data.");
                System.err.println("\t\tThis is the actual user library being evaluated.\n");
                System.err.println(
                        "<train-sourcecode>: Directory with all JAR Files or source code of the Train Data to learn from");
                System.err.println(
                        "\t\tNote: This can be set to \"internal\" without quotes, to use the internal train sourcecode that is bundled in this jar.\n");
                System.err
                        .println("<train-json>: Path to the train data file (JSON), which includes method signatures.");
                System.err.println(
                        "\t\tNote: This can be set to \"internal\" (without quotes), to use the internal train data file that is bundled in this jar.\n");
                System.err.println("<output-dir>:\tDirectory where the output should be written.\n");
                return;
            }

            // Get configuration options from command line arguments.
            String sourceDir = args[0];
            String trainSourceCode = args[1].equals("internal") ? null : args[1];
            String trainJson = args[2].equals("internal") ? null : args[2];
            String outputDir = args[3];

            Main main = new Main();
            main.run(sourceDir, trainSourceCode, trainJson, outputDir);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method executes the analysis and can also be called from outside by
     * clients. It uses the builtin training data.
     *
     * @param sourceDir This is the actual user library being evaluated.
     * @param outputDir Directory where the output should be written.
     * @throws IOException          In case an error occurs during the preparation
     *                              or execution of the analysis.
     * @throws InterruptedException
     */
    public void run(String sourceDir, String outputDir) throws IOException, InterruptedException {
        run(sourceDir, null, null, outputDir);
    }

    /**
     * This method executes the analysis and can also be called from outside by
     * clients.
     *
     * @param sourceDir       This is the actual user library being evaluated.
     * @param trainSourceCode Directory with all JAR Files or source code of the
     *                        Train Data to learn from. If this is
     *                        <code>null</code>, then the builtin traindata is used.
     * @param trainJson       Path to the train data file (JSON), which includes
     *                        method signatures.If this is <code>null</code>, then
     *                        the builtin json file is used.
     * @param outputDir       Directory where the output should be written.
     * @throws IOException          In case an error occurs during the preparation
     *                              or execution of the analysis.
     * @throws InterruptedException
     */
    public void run(String sourceDir, String trainSourceCode, String trainJson, String outputDir)
            throws IOException, InterruptedException {

        // This helper object keeps track of created temporary directories and files to
        // to be deleted before exiting the application.
        FileUtility fileUtility = new FileUtility();

        if (trainJson == null) {
            trainJson = fileUtility.getResourceFile("/input/TrainDataMethods/configurationmethods.json")
                    .getAbsolutePath();
        }

        if (trainSourceCode == null) {
            trainSourceCode = fileUtility.getResourceDirectory("/input/TrainDataLibs").getAbsolutePath();
        }

        try {

            internalRun(sourceDir, trainSourceCode, trainJson, outputDir);

        } finally {
            // Delete temporary files and folders that have been created.
            fileUtility.dispose();
        }
    }

    private void internalRun(String sourceDir, String trainSourceCode, String trainJson, String outputDir)
            throws IOException, InterruptedException {

        long startAnalysisTime = System.currentTimeMillis();

        int iterations = 0;
        if (runOAT)
            iterations = 206; // number of features //TODO: improve code: better borders here.

        // for OAT analysis. Each feature is disabled once.
        for (int i = 0; i <= iterations; i++) {
            if (i == 0)
                logger.info("Running with all features.");
            else {
                logger.info("Running without " + i + "th feature");
            }

            // Cache the list of classes and the CP.
            Set<String> testClasses = Util.getAllClassesFromDirectory(sourceDir);
            String testCp = Util.buildCP(sourceDir);

            logger.info("Loading train data from {}", trainSourceCode);
            String trainingCp = Util.buildCP(trainSourceCode);
            outputPath = outputDir;

            // Cache the methods from the training set.
            parser = new Parser(trainingCp);
            parser.loadTrainingSet(Collections.singleton(trainJson));
            logger.info("{} training methods, distribution={}",
                    parser.methods().size(), Utils.countCategories(parser.methods(), false));

            //Remove methods that do not have method doc comments
            parser.removeUndocumentedMethods();
            logger.info("Remove undocumented training methods. Remaining {}, distribution={}",
                    parser.methods().size(), Utils.countCategories(parser.methods(), false));

            // Cache the methods from the testing set.
            logger.info("Loading test data from {}", sourceDir);
            loader = new Loader(testCp);
            loader.loadTestSet(testClasses, parser.methods());

            // Cache the features.
            logger.info("Loading feature instances");
            featureHandler = new FeatureHandler(trainingCp + System.getProperty("path.separator") + testCp);
            featureHandler.initializeFeatures(i); // use 0 for all feature instances

            //Populate SWAN feature attributes
            docFeatureHandler = null;
            switch (INSTANCE_TYPE) {
                case SWANDOC_MANUAL:
                case SWAN_SWANDOC_MANUAL:

                    docFeatureHandler = new DocFeatureHandler(parser.getMethods());
                    docFeatureHandler.initialiseManualFeatureSet();
                    docFeatureHandler.evaluateManualFeatureData();
                    break;
                case SWANDOC_AUTOMATIC:
                case SWAN_SWANDOC_AUTOMATIC:

                    docFeatureHandler = new DocFeatureHandler(parser.getMethods());
                    docFeatureHandler.initialiseAutomaticFeatureSet();
                    docFeatureHandler.evaluateAutomaticFeatureData();
                    break;
            }

            // Prepare classifier.
            logger.info("Preparing classifier");
            writer = new Writer(loader.methods());
            learner = new Learner(writer);

            /*
                FIRST PHASE - binary classification for each of the categories.
                (1) Classify: source, sink, sanitizer,
                auth-no-change, auth-unsafe-state, auth-safe-state
                (2) Classify: relevant
             */
            runClassEvaluation(false);

            // Save data from last classification.
            loader.resetMethods();

            // Cache the methods from the second test set.
            loader.pruneNone();

            /*
                SECOND PHASE - binary classification for each of the CWE categories.
                (1) Classify: cwe78, cwe079, cwe089, cwe306, cwe601, cwe862, cwe863
             */
            runClassEvaluation(true);

            SwanConfig swanConfig = new SwanConfig();
            Properties config = swanConfig.getConfig();
            String fileName = config.getProperty("output_file_name");

            String outputFile = outputDir + File.separator + fileName + ".json";
            logger.info("Writing results to {}", outputFile);
            writer.printResultsJSON(loader.methods(), outputFile);

            long analysisTime = System.currentTimeMillis() - startAnalysisTime;
            logger.info("Total runtime {} mins", analysisTime/60000);
        }
    }

    public void runClassEvaluation(boolean forCwe) throws IOException, InterruptedException {

        if (forCwe) {

            // Run classifications for all CWEs in JSON file.
            if (runCwes) {
                for (String cweId : parser.cwe()) {
                    // if (cweId.toLowerCase().contains("cwe306"))
                    runClassifier(
                            new HashSet<>(Arrays.asList(Category.getCategoryForCWE(cweId), Category.NONE)),
                            Learner.EVAL_MODE.CLASS);
                }
            }
        } else {

            if (runSources) {
                runClassifier(new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)), Learner.EVAL_MODE.CLASS);
            }

            if (runSinks) {
                runClassifier(new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)), Learner.EVAL_MODE.CLASS);
            }

            if (runSanitizers) {
                runClassifier(new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)),
                        Learner.EVAL_MODE.CLASS);
            }

            if (runAuthentications) {
                runClassifier(
                        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,
                                Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION_NEUTRAL, Category.NONE)),
                        Learner.EVAL_MODE.CLASS);
            }

            if (runRelevant) {
                runClassifier(new HashSet<>(Arrays.asList(Category.RELEVANT, Category.NONE)), Learner.EVAL_MODE.RELEVANCE);
            }
        }
    }


    private double runClassifier(HashSet<Category> categories, Learner.EVAL_MODE eval_mode) {
        parser.resetMethods();
        loader.resetMethods();

        logger.info("Starting classification for {}", categories.toString());

        InstancesHandler instancesHandler = new InstancesHandler();
        Instances instances = instancesHandler.createInstances(parser.getMethods(), featureHandler.features(), docFeatureHandler, categories, INSTANCE_TYPE);
        long startAnalysisTime = System.currentTimeMillis();

        learner.trainModel(instances, LEARNING_MODE);

        long analysisTime = System.currentTimeMillis() - startAnalysisTime;
        logger.info("Total time for classification {}ms", analysisTime);

        return 0.0;
    }
}