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
      if (args.length != 4) {
        System.out.println(
            "Usage: java de.fraunhofer.iem.swan.Main"
                + "<Dir with all JAR files with the sources of the Test Data> " //actual user library being evaluated
                + "<Dir with all JAR Files of the Train Data>" //learn from these examples (source code)
                + "<Path to the train data file (JSON)>" //learn from these examples (method signatures)
                + "<Dir for the output files>");
        return;
      }
      Main main = new Main();
      main.run(args);
      //System.out.println("Done.");
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

  
  // Configuration tags for debugging 
  private static final boolean runSources = true;
  private static final boolean runSinks = true;
  private static final boolean runSanitizers = true;
  private static final boolean runAuthentications = false;
  private static final boolean runCwes = true;
  
  private static final boolean runOAT = false; // run one at a time analysis
  
  private void run(String[] args) throws IOException {
	  int iterations = 0;
	  if(runOAT)
		  iterations = 206; // number of features //TODO: improve code: better borders here. 
	
	  // for OAT analysis. Each feature is disabled once. 
	  for(int i = 0; i<=iterations; i++)
	  {
		  if (i == 0)
			  System.out.println("***** Running with all features.");
		  else {
			  System.out.println("***** Running without " + i + "th feature");
		  }
		// Cache the list of classes and the CP.
		    //System.out.println("***** Loading CP");
		    Set<String> testClasses = Util.getAllClassesFromDirectory(args[0]);
		    String testCp = Util.buildCP(args[0]);
		    String trainingCp = Util.buildCP(args[1]);
		    outputPath = args[3];
		    //System.out.println("Training set cp: " + trainingCp + "\nTest set cp: " + testCp);

		    
		    // Cache the features.
		    //System.out.println("***** Loading features");
		    featureHandler = new FeatureHandler(
		        trainingCp + System.getProperty("path.separator") + testCp);
		    featureHandler.initializeFeatures(i); // use 0 for all feature instances

		    // Cache the methods from the training set.
		    //System.out.println("***** Loading train data");
		    parser = new Parser(trainingCp);
		    parser.loadTrainingSet(Collections.singleton(args[2]));

		    // Cache the methods from the testing set.
		    //System.out.println("***** Loading test data");
		    loader = new Loader(testCp);
		    loader.loadTestSet(testClasses, parser.methods());

		    // Prepare classifier.
		    //System.out.println("***** Preparing classifier");
		    writer = new Writer(loader.methods());
		    learner = new Learner(writer);

		    double averageF=0;
		    int iter =0;
		    // Classify.
		    if(runSources){
		    	averageF+= runClassifier(
		    			new HashSet<Category>(Arrays.asList(Category.SOURCE, Category.NONE)),
		    			false);
		    	iter++;
		    }
		    if(runSinks) {
		    	averageF+= runClassifier(
		    			new HashSet<Category>(Arrays.asList(Category.SINK, Category.NONE)),
		    			false);
		    	iter++;
		    }
		    	
		    if(runSanitizers) {
		    	averageF+= runClassifier(
		    			new HashSet<Category>(Arrays.asList(Category.SANITIZER, Category.NONE)),
		    			false);
		    	iter++;
		    }
		    	
		    if(runAuthentications)
		    {
		    	averageF+=runClassifier(new HashSet<Category>(Arrays.asList(
	    			Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
	    			Category.AUTHENTICATION_NEUTRAL, Category.NONE)), false);
		    iter++;
		    }
		    // Save data from last classification.
		    loader.resetMethods();

		    // Cache the methods from the second test set.
		    //System.out.println("***** Loading 2nd test set");
		    loader.pruneNone();

		    if (runCwes) {
		       //Run classifications for all cwes in JSON file.
		      for (String cweId : parser.cwe()) {
		        averageF += runClassifier(
		            new HashSet<Category>(Arrays
		                .asList(Category.getCategoryForCWE(cweId), Category.NONE)),
		            true);
		        iter++;
		      }
		    }
		    //System.out.println("***** F Measure is " + averageF/iter);
		    
		    //System.out.println("***** Writing final results");
		    Set<String> tmpFiles = Util.getFiles(args[3]);
		    writer.printResultsTXT(loader.methods(), args[3] + File.separator + "txt" + File.separator + "output.txt");
		    writer.writeResultsQWEL(loader.methods(), args[3] + File.separator + "qwel" + File.separator + "output.qwel");
		    writer.writeResultsSoot(loader.methods(), args[3] + File.separator + "soot-qwel" + File.separator + "output.sqwel");
		    writer.printResultsJSON(loader.methods(), args[3] + File.separator + "json" + File.separator + "output.json" );
	  }
	
  }

  private double runClassifier(HashSet<Category> categories, boolean cweMode)
      throws IOException {
    parser.resetMethods();
    loader.resetMethods();
    //System.out.println("***** Starting classification for " + categories.toString());
    return learner.classify(parser.methods(), loader.methods(),
        featureHandler.features(), categories, outputPath + File.separator + "txt"+ File.separator+"output.txt", cweMode);
  }

  
}