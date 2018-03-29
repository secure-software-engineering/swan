package de.fraunhofer.iem.mois;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.fraunhofer.iem.mois.data.Category;

/**
 * Runner for MOIS
 *
 * @author Lisa Nguyen Quang Do
 *
 */

public class Main {

  public static void main(String[] args) {
    try {
      if (args.length != 4) {
        System.out.println(
            "Usage: java de.fraunhofer.iem.mois.Main"
                + "<Dir with all JAR files with the sources of the Test Data> " //actual user library being evaluated
                + "<Dir with all JAR Files of the Train Data>" //learn from these examples (source code)
                + "<Path to the train data file (JSON)>" // learn from these examples (method signatures)
                + "<Dir for the output files>");
        return;
      }
      Main main = new Main();
      main.run(args);
      System.out.println("Done.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Learner learner;
  private Loader loader;
  private Parser parser;
  private FeatureHandler featureHandler;
  private String outputPath;
  private Writer writer;

  private static final boolean classifyCwe = true;

  private void run(String[] args) throws IOException {
    // Cache the list of classes and the CP.
    System.out.println("***** Loading CP");
    Set<String> testClasses = Util.getAllClassesFromDirectory(args[0]);
    String testCp = Util.buildCP(args[0]);
    String trainingCp = Util.buildCP(args[1]);
    outputPath = args[3];
    System.out
        .println("Training set cp: " + trainingCp + "\nTest set cp: " + testCp);

    // Cache the features.
    System.out.println("***** Loading features");
    featureHandler = new FeatureHandler(
        trainingCp + System.getProperty("path.separator") + testCp);
    featureHandler.initializeFeatures();

    // Cache the methods from the training set.
    System.out.println("***** Loading train data");
    parser = new Parser(trainingCp);
    parser.loadTrainingSet(Collections.singleton(args[2]));

    // Cache the methods from the testing set.
    System.out.println("***** Loading test data");
    loader = new Loader(testCp);
    loader.loadTestSet(testClasses, parser.methods());

    // Prepare classifier.
    System.out.println("***** Preparing classifier");
    writer = new Writer(loader.methods());
    learner = new Learner(writer);

    // Classify.
    runClassifier(
        new HashSet<Category>(Arrays.asList(Category.SOURCE, Category.NONE)),
        false);
   runClassifier(
        new HashSet<Category>(Arrays.asList(Category.SINK, Category.NONE)),
        false);
    runClassifier(
        new HashSet<Category>(Arrays.asList(Category.SANITIZER, Category.NONE)),
        false);
    runClassifier(new HashSet<Category>(Arrays.asList(
        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
        Category.AUTHENTICATION_NEUTRAL, Category.NONE)), false);

    // Save data from last classification.
    loader.resetMethods();

    // Cache the methods from the second test set.
    System.out.println("***** Loading 2nd test set");
    loader.pruneNone();

    if (classifyCwe) {
      // Run classifications for all cwes in JSON file.
      for (String cweId : parser.cwe()) {
        runClassifier(
            new HashSet<Category>(Arrays
                .asList(Category.getCategoryForCWE(cweId), Category.NONE)),
            true);
      }
    }

    System.out.println("***** Writing final results");
    Set<String> tmpFiles = Util.getFiles(args[3]);
    writer.printResultsTXT(loader.methods(), tmpFiles, args[3] + File.separator + "txt" + File.separator + "output.txt");
    writer.writeResultsQWEL(loader.methods(), args[3] + File.separator + "qwel" + File.separator + "output.qwel");
    writer.writeResultsSoot(loader.methods(), args[3] + File.separator + "soot-qwel" + File.separator + "output.sqwel");
    writer.printResultsJSON(loader.methods(), tmpFiles, args[3] + File.separator + "json" + File.separator + "output.json" );
  }

  private void runClassifier(HashSet<Category> categories, boolean cweMode)
      throws IOException {
    parser.resetMethods();
    loader.resetMethods();
    System.out
        .println("***** Starting classification for " + categories.toString());
    learner.classify(parser.methods(), loader.methods(),
        featureHandler.features(), categories, outputPath + File.separator + "txt"+ File.separator+"output.txt", cweMode);
  }

}
