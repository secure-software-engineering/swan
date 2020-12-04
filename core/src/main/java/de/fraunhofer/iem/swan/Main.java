package de.fraunhofer.iem.swan;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.doc.DocFeatureHandler;
import de.fraunhofer.iem.swan.features.code.FeatureHandler;
import de.fraunhofer.iem.swan.io.FileUtility;
import de.fraunhofer.iem.swan.features.code.soot.Loader;
import de.fraunhofer.iem.swan.io.dataset.Parser;
import de.fraunhofer.iem.swan.io.dataset.Writer;
import de.fraunhofer.iem.swan.features.InstancesHandler;
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

public class Main {

    /**
     * Instance set refers to the configuration that should be used when running SWAN.
     * Available configurations are described in {@link InstancesHandler.INSTANCE_SET}
     */
    private static final InstancesHandler.INSTANCE_SET INSTANCE_TYPE = InstancesHandler.INSTANCE_SET.SWAN;

    /**
     * Sets the learning mode from {@link Learner.LEARN_MODE} that should be used when
     * running SWAN.
     */
    private static final Learner.LEARN_MODE LEARNING_MODE = Learner.LEARN_MODE.MANUAL;

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
    private static final boolean runRelevant = false;
    private static final boolean runCwes = true;

    private static final boolean runOAT = false; // run one at a time analysis
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private DocFeatureHandler docFeatureHandler;

    public static HashMap<String, HashSet<String>> predictions;

    public static void main(String[] args) {

        try {
            if (args.length != 4) {
                System.err.println("");
                System.err.println(
                        "Usage: java de.fraunhofer.iem.swan.Main <test-source> <train-source> <dataset-json> <output-dir>\n");
                System.err.println("<test-source>:\tDirectory with all JAR files or source code of the Test Data.");
                System.err.println("\t\tThis is the actual user library being evaluated.\n");
                System.err.println(
                        "<train-source>: Directory with all JAR Files or source code of the Train Data to learn from");
                System.err.println(
                        "\t\tNote: This can be set to \"internal\" without quotes, to use the internal train sourcecode that is bundled in this jar.\n");
                System.err
                        .println("<dataset-json>: Path to the dataset file (JSON), which includes method signatures.");
                System.err.println(
                        "\t\tNote: This can be set to \"internal\" (without quotes), to use the internal dataset file that is bundled in this jar.\n");
                System.err.println("<output-dir>:\tDirectory where the output should be written.\n");
                return;
            }

            // Get configuration options from command line arguments.
            String testSourceDir = args[0];
            String trainSourceDir = args[1].equals("internal") ? null : args[1];
            String datasetFile = args[2].equals("internal") ? null : args[2];
            String outputDir = args[3];

            Main main = new Main();
            main.run(testSourceDir, trainSourceDir, datasetFile, outputDir);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method executes the analysis and can also be called from outside by
     * clients. It uses the builtin training data.
     *
     * @param testSoure This is the actual user library being evaluated.
     * @param outputDir Directory where the output should be written.
     * @throws IOException          In case an error occurs during the preparation
     *                              or execution of the analysis.
     * @throws InterruptedException
     */
    public void run(String testSoure, String outputDir) throws IOException, InterruptedException {
        run(testSoure, null, null, outputDir);
    }

    /**
     * This method executes the analysis and can also be called from outside by
     * clients.
     *
     * @param testSource  This is the actual user library being evaluated.
     * @param trainSource Directory with all JAR Files or source code of the
     *                    Train Data to learn from. If this is
     *                    <code>null</code>, then the builtin traindata is used.
     * @param datasetFile Path to the train data file (JSON), which includes
     *                    method signatures.If this is <code>null</code>, then
     *                    the builtin json file is used.
     * @param outputDir   Directory where the output should be written.
     * @throws IOException          In case an error occurs during the preparation
     *                              or execution of the analysis.
     * @throws InterruptedException
     */
    public void run(String testSource, String trainSource, String datasetFile, String outputDir)
            throws IOException, InterruptedException {

        // This helper object keeps track of created temporary directories and files to
        // to be deleted before exiting the application.
        FileUtility fileUtility = new FileUtility();

        if (datasetFile == null) {
            datasetFile = fileUtility.getResourceFile("/input/Dataset/data-set-with-doc-comments")
                    .getAbsolutePath();
        }

        if (trainSource == null) {
            trainSource = fileUtility.getResourceDirectory("/input/TrainDataLibs").getAbsolutePath();
        }

        try {

            internalRun(testSource, trainSource, datasetFile, outputDir);

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
                    parser.methods().size(), Util.countCategories(parser.methods(), false));

            //Remove methods that do not have method doc comments
            parser.removeUndocumentedMethods();
            logger.info("Remove undocumented training methods. Remaining {}, distribution={}",
                    parser.methods().size(), Util.countCategories(parser.methods(), true));

            // Cache the methods from the testing set.
            logger.info("Loading test data from {}", sourceDir);
            loader = new Loader(testCp);
            loader.loadTestSet(testClasses, parser.methods());

            // Cache the features.
            logger.info("Loading feature instances");
            featureHandler = new FeatureHandler(trainingCp + System.getProperty("path.separator") + testCp);
            featureHandler.initializeFeatures(i); // use 0 for all feature instances

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
            switch (INSTANCE_TYPE) {
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

            /*
                FIRST PHASE - binary classification for each of the categories.
                (1) Classify: source, sink, sanitizer,
                auth-no-change, auth-unsafe-state, auth-safe-state
                (2) Classify: relevant
             */

            //Store predictions for each classifier and iteration.
            predictions = new HashMap<>();
            for (int x = 0; x < 10; x++)
                predictions.put(Integer.toString(x), new HashSet<String>());

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
            logger.info("Total runtime {} mins", analysisTime / 60000);
        }
    }

    public void runClassEvaluation(boolean forCwe) {

        if (forCwe) {

            // Run classifications for all CWEs in JSON file.
            if (runCwes) {

                for (String cweId : parser.cwe()) {
                    // if (cweId.toLowerCase().contains("cwe306"))
                    runClassifier(
                            new HashSet<>(Arrays.asList(Category.getCategoryForCWE(cweId), Category.NONE)),
                            Learner.EVAL_MODE.CLASS, true);
                }
            }
        } else {

            if (runSources) {
                runClassifier(new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)), Learner.EVAL_MODE.CLASS, false);
            }

            if (runSinks) {
                runClassifier(new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)), Learner.EVAL_MODE.CLASS, false);

            }

            if (runSanitizers) {
                runClassifier(new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)),
                        Learner.EVAL_MODE.CLASS, false);
            }

            if (runAuthentications) {
                runClassifier(
                        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,
                                Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION_NEUTRAL, Category.NONE)),
                        Learner.EVAL_MODE.CLASS, false);
            }

            if (runRelevant) {
                runClassifier(new HashSet<>(Arrays.asList(Category.RELEVANT, Category.NONE)), Learner.EVAL_MODE.RELEVANCE, false);
            }
        }
    }


    private double runClassifier(HashSet<Category> categories, Learner.EVAL_MODE eval_mode, boolean forCwe) {
        parser.resetMethods();
        loader.resetMethods();

        logger.info("Starting classification for {}", categories.toString());
        long startAnalysisTime;

        if (forCwe) {

            ArrayList<InstancesHandler> instancesHandlers = new ArrayList<>();
            for (String iteration : predictions.keySet()) {

                if (predictions.get(iteration).size() == 0)
                    continue;

                HashSet<Method> methods = new HashSet<>();

                for (Method method : parser.getMethods()) {
                    if (predictions.get(iteration).contains(method.getArffSafeSignature()))
                        methods.add(method);
                    System.out.println();
                }

                InstancesHandler instancesHandler = new InstancesHandler();
                instancesHandler.createInstances(methods, featureHandler.features(), docFeatureHandler, categories, INSTANCE_TYPE);

                instancesHandlers.add(instancesHandler);
            }

            startAnalysisTime = System.currentTimeMillis();
            learner.trainModel(instancesHandlers, LEARNING_MODE);

        } else {
            InstancesHandler instancesHandler = new InstancesHandler();
            instancesHandler.createInstances(parser.getMethods(), featureHandler.features(), docFeatureHandler, categories, INSTANCE_TYPE);
            startAnalysisTime = System.currentTimeMillis();

            ArrayList<InstancesHandler> instancesHandlers = new ArrayList<>();
            instancesHandlers.add(instancesHandler);
            learner.trainModel(instancesHandlers, LEARNING_MODE);
        }

        long analysisTime = System.currentTimeMillis() - startAnalysisTime;
        logger.info("Total time for classification {}ms", analysisTime);

        return 0.0;
    }
}