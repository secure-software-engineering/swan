package de.fraunhofer.iem.swan;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.fraunhofer.iem.swan.data.Category;

/**
 * Runner for SWAN
 *
 * @author Lisa Nguyen Quang Do
 *
 */

public class Main {

	public static void main(String[] args) {

		try {
			boolean correctNumberOfArguments = args.length >= 2 && args.length <= 4;
			if (!correctNumberOfArguments) {
				System.err.println("");
				System.err.println(
						"Usage: java de.fraunhofer.iem.swan.Main <source-dir> [<train-sourcecode>] [<train-json>] <output-dir>\n");
				System.err.println("<source-dir>:\tDirectory with all JAR files or source code of the Test Data.");
				System.err.println("\t\tThis is the actual user library being evaluated.\n");
				System.err.println(
						"<train-sourcecode>: Directory with all JAR Files or source code of the Train Data to learn from.");
				System.err.println(
						"\t\tThis is an optional parameter. If it is not specified, the builtin traindata is used.\n");
				System.err
						.println("<train-json>: Path to the train data file (JSON), which includes method signatures.");
				System.err.println(
						"\t\tThis is an optional parameter. If it is not specified, the builtin json file is used.\n");
				System.err.println("<output-dir>:\tDirectory where the output should be written.\n");
				return;
			}

			// Get configuration options from command line arguments.
			String sourceDir = args[0];
			String trainSourceCode = null;
			String trainJson = null;
			String outputDir = args[args.length - 1];

			// Train Sourcecode (extract from JAR, if not specified)
			if (args.length >= 3) {
				trainSourceCode = args[1];
			}

			// Train JSON (extract from JAR, if not specified)
			if (args.length == 4) {
				trainJson = args[2];
			}

			Main main = new Main();
			main.run(sourceDir, trainSourceCode, trainJson, outputDir);
			// System.out.println("Done.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

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
	private static final boolean runAuthentications = false;
	private static final boolean runCwes = true;

	private static final boolean runOAT = false; // run one at a time analysis

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
		// to be deleted before exiting the
		// application.
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

		int iterations = 0;
		if (runOAT)
			iterations = 206; // number of features //TODO: improve code: better borders here.

		// for OAT analysis. Each feature is disabled once.
		for (int i = 0; i <= iterations; i++) {
			if (i == 0)
				System.out.println("***** Running with all features.");
			else {
				System.out.println("***** Running without " + i + "th feature");
			}
			// Cache the list of classes and the CP.
			// System.out.println("***** Loading CP");
			Set<String> testClasses = Util.getAllClassesFromDirectory(sourceDir);
			String testCp = Util.buildCP(sourceDir);
			String trainingCp = Util.buildCP(trainSourceCode);
			outputPath = outputDir;
			// System.out.println("Training set cp: " + trainingCp + "\nTest set cp: " +
			// testCp);

			// Cache the features.
			// System.out.println("***** Loading features");
			featureHandler = new FeatureHandler(trainingCp + System.getProperty("path.separator") + testCp);
			featureHandler.initializeFeatures(i); // use 0 for all feature instances

			// Cache the methods from the training set.
			// System.out.println("***** Loading train data");
			parser = new Parser(trainingCp);
			parser.loadTrainingSet(Collections.singleton(trainJson));

			// Cache the methods from the testing set.
			// System.out.println("***** Loading test data");
			loader = new Loader(testCp);
			loader.loadTestSet(testClasses, parser.methods());

			// Prepare classifier.
			// System.out.println("***** Preparing classifier");
			writer = new Writer(loader.methods());
			learner = new Learner(writer);

			double averageF = 0;
			int iter = 0;
			// Classify.
			if (runSources) {
				averageF += runClassifier(new HashSet<Category>(Arrays.asList(Category.SOURCE, Category.NONE)), false);
				iter++;
			}
			if (runSinks) {
				averageF += runClassifier(new HashSet<Category>(Arrays.asList(Category.SINK, Category.NONE)), false);
				iter++;
			}

			if (runSanitizers) {
				averageF += runClassifier(new HashSet<Category>(Arrays.asList(Category.SANITIZER, Category.NONE)),
						false);
				iter++;
			}

			if (runAuthentications) {
				averageF += runClassifier(
						new HashSet<Category>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,
								Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION_NEUTRAL, Category.NONE)),
						false);
				iter++;
			}
			// Save data from last classification.
			loader.resetMethods();

			// Cache the methods from the second test set.
			// System.out.println("***** Loading 2nd test set");
			loader.pruneNone();

			if (runCwes) {
				// Run classifications for all cwes in JSON file.
				for (String cweId : parser.cwe()) {
					averageF += runClassifier(
							new HashSet<Category>(Arrays.asList(Category.getCategoryForCWE(cweId), Category.NONE)),
							true);
					iter++;
				}
			}
			// System.out.println("***** F Measure is " + averageF/iter);

			// System.out.println("***** Writing final results");
//			Set<String> tmpFiles = Util.getFiles(outputDir);
			writer.printResultsTXT(loader.methods(),
					outputDir + File.separator + "txt" + File.separator + "output.txt");
			writer.writeResultsQWEL(loader.methods(),
					outputDir + File.separator + "qwel" + File.separator + "output.qwel");
			writer.writeResultsSoot(loader.methods(),
					outputDir + File.separator + "soot-qwel" + File.separator + "output.sqwel");
			writer.printResultsJSON(loader.methods(), outputDir + File.separator + "output.json");
		}

	}

	private double runClassifier(HashSet<Category> categories, boolean cweMode)
			throws IOException, InterruptedException {
		parser.resetMethods();
		loader.resetMethods();
		// System.out.println("***** Starting classification for " +
		// categories.toString());
		return learner.classify(parser.methods(), loader.methods(), featureHandler.features(), categories,
				outputPath + File.separator + "txt" + File.separator + "output.txt", cweMode);
	}

}