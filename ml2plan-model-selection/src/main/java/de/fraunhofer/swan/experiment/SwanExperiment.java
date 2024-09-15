package de.fraunhofer.swan.experiment;

import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.meta.EnsembleML;
import weka.classifiers.Evaluation;
import meka.core.MLUtils;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class SwanExperiment {

    public static void main(String[] args) {
        try {
            // Load dataset
            DataSource source = new DataSource("/home/oshando/IdeaProjects/swan/swan-cmd/src/main/resources/dataset/meka/meka-code.arff");
            Instances data = source.getDataSet();

            // Set the class indices (assumes the last n columns are the labels)
            MLUtils.prepareData(data);

            // Choose a classifier
            EnsembleML classifier = new EnsembleML();

            // Evaluate the classifier using cross-validation
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(classifier, data, 10, data.getRandomNumberGenerator(1));

            // Output evaluation results
            System.out.println(eval.toClassDetailsString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  public static void mains(String[] args) throws Exception {


        ArffLoader loader = new ArffLoader();
      loader.setSource(new File("/home/oshando/IdeaProjects/swan/swan-cmd/src/main/resources/dataset/meka/meka-code.arff"));
        Instances data = loader.getDataSet();

        // Train-test split (70:30)
        int trainSize = (int) Math.round(data.size() * 0.7);
        int testSize = data.size() - trainSize;
        Instances trainData = new Instances(data, 0, trainSize);
        Instances testData = new Instances(data, trainSize, testSize);

       //create the classifier

        Classifier classifier = (Classifier) Class.forName("meka.classifiers.multilabel.PS").newInstance();


        BR classy = new BR();
        String [] opt = {"-S","1","-I","10","-P","67","-W","meka.classifiers.multilabel.PS","--","-P","0",
                "-N","0","-S","0","-W","weka.classifiers.trees.LMT","--","-I","-1","-M","15","-W","0.0"};
        classy.setOptions(opt);

        try {
            Result result = Evaluation.cvModel(classy, data, 2, "PCutL", "7");
            System.out.println("Model cross-validation results {}" + result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



        System.out.println("Hello world!");
    }*/
}