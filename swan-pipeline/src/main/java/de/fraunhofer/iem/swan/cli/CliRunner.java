package de.fraunhofer.iem.swan.cli;

import picocli.CommandLine;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "SWAN CLI", mixinStandardHelpOptions = true,
        version = "SWAN 3.0.1", description = "")
public class CliRunner implements Callable<Integer> {

    @CommandLine.Option(names = {"-test", "--test-data"}, description = {"Path of test JARs or class files"})
    private String testDataDir = "/input/test-data";

    @CommandLine.Option(names = {"-train", "--train-data"}, description = {"Path of training JARs or class files"})
    private String trainDataDir = "/input/train-data";

    @CommandLine.Option(names = {"-d", "--dataset"}, description = {"Path to JSON dataset file"})
    private String datasetJson = "/input/dataset/swan-dataset.json";

    @CommandLine.Option(names = {"-o", "--output"}, description = {"Directory to save output files"})
    private String outputDir = "/swan-output";

    @CommandLine.Option(names = {"-f", "--feature"}, description = {"Select one or more feature sets: all, code, doc-auto or doc-manual"})
    private List<String> featureSet =  Collections.singletonList("code");

    @CommandLine.Option(names = {"-t", "--toolkit"}, description = {"ML toolkit: meka, weka, ml-plan"})
    private String toolkit = "meka";

    @CommandLine.Option(names = {"-s", "--srm"}, description = {"SRM: all, source, sink, sanitizer, authentication, relevant"})
    private List<String> srmClasses = Collections.singletonList("all");

    @CommandLine.Option(names = {"-c", "--cwe"}, description = {"CWE: all, cwe078, cwe079, cwe089, cwe306, cwe601, cwe862, cwe863"})
    private List<String> cweClasses = Collections.singletonList("all");

    @CommandLine.Option(names = {"-arff", "--arff-data"}, description = {"Export training ARFF files"})
    private boolean exportArffData = true;

    @CommandLine.Option(names = {"-doc", "--documented"}, description = {"Use only methods with Javadoc"})
    private boolean isDocumented = true;

    @CommandLine.Option(names = {"-i", "--iterations"}, description = {"Number of iterations for training"})
    private int iterations = 10;

    @CommandLine.Option(names = {"-sp", "--training-split"}, description = {"Percentage for training"})
    private double split = 0.7;

    @CommandLine.Option(names = {"-p", "--phase"}, description = {"Phase: validate, predict"})
    private String phase = "predict";

    @CommandLine.Option(names = {"-pt", "--prediction-threshold"}, description = {"Threshold for predicting categories"})
    private double predictionThreshold = 0.5;


    @Override
    public Integer call() throws Exception {

        SwanOptions options = new SwanOptions(testDataDir,
                trainDataDir,
                datasetJson,
                outputDir,
                featureSet,
                toolkit,
                srmClasses,
                cweClasses,
                exportArffData,
                isDocumented,
                iterations,
                split,
                phase);
        options.setPredictionThreshold(predictionThreshold);

        return new SwanCli().run(options);
    }
}