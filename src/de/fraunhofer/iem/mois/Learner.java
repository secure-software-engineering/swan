package de.fraunhofer.iem.mois;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.fraunhofer.iem.mois.data.Category;
import de.fraunhofer.iem.mois.data.Method;

import java.util.Random;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.OneR;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Range;
import weka.core.converters.ArffSaver;

/**
 * Finds possible sources and sinks in a given set of system methods using a
 * probabilistic algorithm trained on a previously annotated sample set.
 *
 * @author Steven Arzt, Lisa Nguyen Quang Do, Goran Piskachev
 *
 */
public class Learner {

  private final static String WEKA_LEARNER_ALL = "SMO";
  private long startAnalysisTime;
  private long analysisTime;
  
  private final boolean CROSS_EVALUATE=true;
  private final boolean CLASSIFY=false;
  
  private final int CROSS_EVALUATE_ITERATIONS=10;
  
  private final Writer writer;

  public Learner(Writer writer) {
    this.writer = writer;
  }

  public void classify(Set<Method> trainingSet, Set<Method> testSet,
      Map<Category, Set<IFeature>> features, Set<Category> categories,
      String outputFile, boolean cweMode) throws IOException {

    startAnalysisTime = System.currentTimeMillis();
    Map<Category, Integer> counters = new HashMap<Category, Integer>();

    // Collect the possible values.
    System.out.println("Initializing classifier.");
    FastVector ordinal = new FastVector();
    ordinal.addElement("true");
    ordinal.addElement("false");

    // Collect categories we classify into.
    FastVector classes = new FastVector();
    for (Category type : Category.values()) {
      if (categories.contains(type)) {
        classes.addElement(type.toString());
      }
    }

    // Collect all attributes for the categories we classify into, and create
    // the instance set.
    System.out.print("Collecting attributes... ");
    Map<IFeature, Attribute> featureAttribs =
        new HashMap<IFeature, Attribute>();
    FastVector attributes = new FastVector();
    for (Category type : features.keySet()) {
      if (type == Category.NONE) continue;
      if (categories.contains(type)) {
        for (IFeature f : features.get(type)) {
          Attribute attr = new Attribute(f.toString(), ordinal);
          if (!featureAttribs.containsKey(attr) && !attributes.contains(attr)) {
            featureAttribs.put(f, attr);
            attributes.addElement(attr);
          }
        }
      }
    }

    // Add class attribute.
    Attribute classAttr = new Attribute("class", classes);

    // Add id attribute.
    FastVector methodStrings = new FastVector();
    for (Method am : trainingSet)
      methodStrings.addElement(am.getSignature());
    for (Method am : testSet)
      methodStrings.addElement(am.getSignature());
    attributes.addElement(classAttr);
    Attribute idAttr = new Attribute("id", methodStrings);
    attributes.addElement(idAttr);

    System.out.println(attributes.size() + " attributes collected.");

    // Set attributes to the train and test instances.
    System.out.print("Creating instances... ");
    Instances trainInstances = new Instances("trainingmethods", attributes, 0);
    Instances testInstances = new Instances("allmethods", attributes, 0);
    trainInstances.setClass(classAttr);
    testInstances.setClass(classAttr);

    // Create one instance object per data row
    int instanceId = 0;
    Map<String, Method> instanceMethods = new HashMap<String, Method>();
    Map<Integer, Method> instanceIndices = new HashMap<Integer, Method>();

    // Set the known classifications for the training set.
    for (Method am : trainingSet) {
      Category categoryClassified = null;
      for (Category category : am.getCategoriesTrained()) {
        if (category.isCwe() != cweMode) continue;
        if (categories.contains(category)) {
          am.setCategoryClassified(category);
          // Policy: Take the first category that is interesting.
          // TODO: Better policy in case of multiple matching categories.
          categoryClassified = category;
          break;
        }
      }

      // If no category, annotate with NONE.
      if (categoryClassified == null) {
        am.setCategoryClassified(Category.NONE);
        categoryClassified = Category.NONE;
      }
    }

    // Evaluate all methods against the features.
    Set<Method> methods = new HashSet<Method>();
    methods.addAll(trainingSet);
    methods.addAll(testSet);

    for (Method am : methods) {
      Instance inst = new Instance(attributes.size());
      inst.setDataset(trainInstances);
      for (Entry<IFeature, Attribute> entry : featureAttribs.entrySet()) {
        switch (entry.getKey().applies(am)) {
        case TRUE:
          inst.setValue(entry.getValue(), "true");
          break;
        case FALSE:
          inst.setValue(entry.getValue(), "false");
          break;
        default:
          inst.setMissing(entry.getValue());
        }
      }
      inst.setValue(idAttr, am.getSignature());
      instanceMethods.put(am.getSignature(), am);
      instanceIndices.put(instanceId++, am);

      Category category = am.getCategoryClassified();
      if (am.getCategoryClassified() != null) {
        inst.setClassValue(category.toString());
        if (counters.containsKey(category)) {
          int counter = counters.get(category);
          counters.put(category, ++counter);
        } else
          counters.put(category, 1);
        trainInstances.add(inst);
      } else {
        inst.setClassMissing();
        testInstances.add(inst);
      }
    }
    System.out.println("Done.");

    // Create classifier.
    try {
      // instances.randomize(new Random(1337));
      Classifier classifier = null;
      // (IBK / kNN) vs. SMO vs. (J48 vs. JRIP) vs. NaiveBayes
      // MultiClassClassifier für ClassifierPerformanceEvaluator
      if (WEKA_LEARNER_ALL.equals("BayesNet"))
        classifier = new BayesNet();
      else if (WEKA_LEARNER_ALL.equals("NaiveBayes"))
        classifier = new NaiveBayes();
      else if (WEKA_LEARNER_ALL.equals("J48"))
        classifier = new J48();
      else if (WEKA_LEARNER_ALL.equals("SMO"))
        classifier = new SMO();
      else if (WEKA_LEARNER_ALL.equals("JRip"))
        classifier = new JRip();
      else if (WEKA_LEARNER_ALL.equals("DecisionStump"))
          classifier = new DecisionStump();
      else if (WEKA_LEARNER_ALL.equals("OneR"))
          classifier = new OneR();
      else if (WEKA_LEARNER_ALL.equals("Logistic"))
          classifier = new Logistic();
      else
        throw new Exception("Wrong WEKA learner!");
      System.out.println("Classifier created: " + WEKA_LEARNER_ALL);

      // Save arff data.
      ArffSaver saver = new ArffSaver();
      saver.setInstances(trainInstances);
      List<Category> fileNameList = new ArrayList<Category>(categories);
      Collections.sort(fileNameList);
      String fileName = fileNameList.toString();
      fileName = fileName.substring(1, fileName.length() - 1);
      fileName = fileName.replace(", ", "_");
      saver.setFile(new File("Train_" + fileName + ".arff"));
      saver.writeBatch();
      System.out.println(
          "Arff data saved at: " + saver.retrieveFile().getCanonicalPath());

   // Cross evaluation.
      if(CROSS_EVALUATE) {
    	
    	  double precision= 0;
    	  double recall = 0; 
    	  for(int i = 0; i< CROSS_EVALUATE_ITERATIONS; i++)
    	  {
    		  System.out.println("Starting cross evaluation (iteration "+i+").");
              Evaluation eval = new Evaluation(trainInstances);
              
              StringBuffer sb = new StringBuffer();
              eval.crossValidateModel(classifier, trainInstances, 10, new Random(1337+i*11),
                  sb, new Range(attributes.indexOf(idAttr) + 1
                      + ""/* "1-" + (attributes.size() - 1) */),
                  true);
              //System.out.println(sb.toString());
              System.out.println("Class details: " + eval.toClassDetailsString());
              precision += eval.weightedPrecision();
              recall += eval.weightedRecall();
              
              for (Category counter : counters.keySet())
                System.out.println("Cross evaluation finished on a training set of "
                    + counters.get(counter) + " " + counter + ".");
    	  }
    	  System.out.println("The precision over "+ CROSS_EVALUATE_ITERATIONS +" iterations is " + (precision/CROSS_EVALUATE_ITERATIONS));
    	  System.out.println("The recall over "+ CROSS_EVALUATE_ITERATIONS +" iterations is " + (recall/CROSS_EVALUATE_ITERATIONS));
      }
      
      // Classification.
      
      if(CLASSIFY) {
    	  System.out.println("Classification starting.");
          classifier.buildClassifier(trainInstances);
          if (WEKA_LEARNER_ALL.equals("J48")) {
            System.out.println(((J48) (classifier)).graph());
          }
          for (int instIdx = 0; instIdx < testInstances.numInstances(); instIdx++) {
            Instance inst = testInstances.instance(instIdx);
            assert inst.classIsMissing();
            Method meth = instanceMethods.get(inst.stringValue(idAttr));
            double d = classifier.classifyInstance(inst);
            String cName = testInstances.classAttribute().value((int) d);
            boolean found = false;
            for (Category type : categories) {
              if (cName.equals(type.toString())) {
                inst.setClassValue(type.toString());
                meth.setCategoryClassified(type);
                found = true;
                break;
              }
            }
            if (!found) System.err.println("Unknown class name");
          }  
          System.out.println("Finished classification.");
      }
      
    } 
    catch (Exception ex) {
      System.err.println("Something went all wonky: " + ex);
      ex.printStackTrace();
    }
    

    System.out.println("Writing results to files:");
    writer.writeResultsToFiles(outputFile, methods, categories);
    //writer.writeResultsToFilesQWEL(outputFile, methods, categories);
    
    Runtime.getRuntime().gc();
    analysisTime = System.currentTimeMillis() - startAnalysisTime;
    System.out.println("Time to classify " + categories.toString() + ": "
        + analysisTime + " ms");

    // writer.writeRIFLSpecification(outputFile, methods);
  }
}
