package de.fraunhofer.iem.swan.cli;

import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "SWAN CLI", mixinStandardHelpOptions = true,
        version = "SWAN 3.2.0", description = "")
public class CliRunner implements Callable<Integer> {

    @CommandLine.Option(names = {"-test", "--test-data"}, description = {"Path of test JARs or class files"})
    private String testDataDir = "";

    @CommandLine.Option(names = {"-train", "--train-data"}, description = {"Path of training JARs or class files"})
    private String trainDataDir = "";

    @CommandLine.Option(names = {"-test-source", "--test-data-source"}, description = {"Path of test source files"})
    private String testDataSourceDir = "";

    @CommandLine.Option(names = {"-train-source", "--train-data-source"}, description = {"Path of training source files"})
    private String trainDataSourceDir = "";

    @CommandLine.Option(names = {"-d", "--dataset"}, description = {"Path to JSON dataset file"})
    private String datasetJson = "/dataset/srm-repo-dataset.json";

    @CommandLine.Option(names = {"-in", "--train-instances"}, description = {"Path to ARFF files that contain training instances"})
    private List<String> arffInstancesFiles = new ArrayList<>();

    @CommandLine.Option(names = {"-o", "--output"}, description = {"Directory to save output files"})
    private String outputDir = "";

    @CommandLine.Option(names = {"-f", "--feature"}, arity = "1..*", description = {"Select one or more feature sets: all, code, doc-auto or doc-manual"})
    private List<String> featureSet =  Collections.singletonList("code");

    @CommandLine.Option(names = {"-t", "--toolkit"}, description = {"ML toolkit: meka, weka, autoweka, mlplan"})
    private String toolkit = "meka";

    @CommandLine.Option(names = {"-s", "--srm"}, description = {"SRM: all, source, sink, sanitizer, authentication, relevant"})
    private List<String> srmClasses = Collections.singletonList("all");

    @CommandLine.Option(names = {"-c", "--cwe"}, description = {"CWE: all, cwe078, cwe079, cwe089, cwe306, cwe601, cwe862, cwe863"})
    private List<String> cweClasses = Collections.singletonList("all");

    @CommandLine.Option(names = {"-arff", "--arff-data"}, description = {"Export training ARFF files"})
    private boolean exportArffData = true;

    @CommandLine.Option(names = {"-doc", "--documented"}, description = {"Use only methods with Javadoc"})
    private boolean isDocumented = true;

    @CommandLine.Option(names = {"-att", "--attribute-selection"}, description = {"Use attribute selection"})
    private boolean reduceAttributes = false;

    @CommandLine.Option(names = {"-i", "--iterations"}, description = {"Number of iterations for training"})
    private int iterations = 10;

    @CommandLine.Option(names = {"-sp", "--training-split"}, description = {"Percentage for training"})
    private double split = 0.7;

    @CommandLine.Option(names = {"-p", "--phase"}, description = {"Phase: validate, predict"})
    private String phase = "validate";

    @CommandLine.Option(names = {"-pt", "--prediction-threshold"}, description = {"Threshold for predicting categories"})
    private double predictionThreshold = 0.5;

    @CommandLine.Option(names = {"-ds", "--discovery"}, arity = "1..*", description = {"Select discovery for training set SRMs"})
    private List<String> discovery =  new ArrayList<>();//  Collections.singletonList("manual");

    @CommandLine.Option(names = {"-tl", "--timelimit"}, description = {"Time (minutes) to execute operation "})
    private int timeLimit = 1;

    public SwanOptions initializeOptions(){

        SwanOptions options = new SwanOptions();
        options.setToolkit(toolkit);
        options.setPhase(phase);

        options.setDatasetJson(datasetJson);
        options.setTrainDataDir(trainDataDir);
        options.setTrainDataSourceDir(trainDataSourceDir);
        options.setTestDataDir(testDataDir);
        options.setTestDataSourceDir(testDataSourceDir);

        options.setFeatureSet(featureSet);
        options.setSrmClasses(srmClasses);
        options.setCweClasses(cweClasses);
        options.setInstances(arffInstancesFiles);

        options.setOutputDir(outputDir);

        options.setExportArffData(exportArffData);
        options.setDocumented(isDocumented);
        options.setIterations(iterations);
        options.setTrainTestSplit(split);
        options.setPredictionThreshold(predictionThreshold);
        options.setReduceAttributes(false);
        options.setDiscovery(discovery);
        options.setTimeLimit(timeLimit);

        return options;
    }

    @Override
    public Integer call() throws Exception {

        return new SwanCli().run(initializeOptions());
    }
}
