package de.fraunhofer.iem.swan.cli;

import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "SWAN CLI", mixinStandardHelpOptions = true,
        version = "SWAN 3.0.1", description = "")
public
class CliRunner implements Callable<Integer> {

    @CommandLine.Option(names = {"-test", "--test-data"}, description = {"Path of test JARs or class files"})
    private String testDataDir = "/input/test-data";

    @CommandLine.Option(names = {"-train", "--train-data"}, description = {"Path of training JARs or class files"})
    private String trainDataDir = "/input/train-data";

    @CommandLine.Option(names = {"-d", "--dataset"}, description = {"Path to JSON dataset file"})
    private String datasetJson = "/input/dataset/swan-dataset.json";

    @CommandLine.Option(names = {"-o", "--output"}, description = {"Directory to save output files"})
    private String outputDir = "/swan-output";

    @CommandLine.Option(names = {"-f", "--feature"}, description = {"Feature set: 0 - SWAN; 1 - SWANDOC-AUTO; 2 - SWANDOC-MANUAL; 3 - SWAN-SWANDOC-MANUAL; 4 - SWAN-SWANDOC-AUTO"})
    private String featureSet = "0";

    @CommandLine.Option(names = {"-l", "--learning"}, description = {"Learning modes: manual, auto"})
    private String learningMode = "manual";

    @CommandLine.Option(names = {"-s", "--srm"}, description = {"SRM: all, source, sink, sanitizer, authentication"})
    private List<String> srmClasses = Arrays.asList("all");

    @CommandLine.Option(names = {"-c", "--cwe"}, description = {"CWE: all, cwe078, cwe079, cwe089, cwe306, cwe601, cwe862, cwe863"})
    private List<String> cweClasses = Arrays.asList("all");

    @CommandLine.Option(names = {"-", "--arff-data"}, description = {"Export training ARFF files"})
    private boolean exportArffData = true;

    @CommandLine.Option(names = {"-doc", "--documented"}, description = {"Use only methods with Javadoc"})
    private boolean isDocumented = true;

    @CommandLine.Option(names = {"-i", "--iterations"}, description = {"Number of iterations for training"})
    private int iterations = 10;

    @CommandLine.Option(names = {"-sp", "--training-split"}, description = {"Percentage for training"})
    private double split = 0.7;

    @Override
    public Integer call() throws Exception {

        SwanOptions options = new SwanOptions(testDataDir,
                trainDataDir,
                datasetJson,
                outputDir,
                featureSet,
                learningMode,
                srmClasses,
                cweClasses,
                exportArffData,
                isDocumented,
                iterations,
                split);

        return new SwanCli().run(options);
    }
}