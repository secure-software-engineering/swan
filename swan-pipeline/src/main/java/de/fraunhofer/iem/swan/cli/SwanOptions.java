package de.fraunhofer.iem.swan.cli;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * POJO for SWAN command line options.
 */
public class SwanOptions {

    private String testDataDir;
    private String trainDataDir;
    private String testDataSourceDir;
    private String trainDataSourceDir;
    private String datasetJson;
    private String outputDir;
    private List<String> featureSet;
    private String toolkit;
    private List<String> srmClasses;
    private List<String> cweClasses;
    private boolean exportArffData;
    private boolean isDocumented;
    private boolean reduceAttributes;
    private int iterations;
    private double trainTestSplit;
    private String phase;
    private double predictionThreshold;
    private List<String> arffInstancesFiles;
    private List<String> discovery;
    private int timeLimit;

    public SwanOptions(String testDataDir, String trainDataDir, String datasetJson, String outputDir,
                       List<String> featureSet, String toolkit, List<String> srmClasses,
                       List<String> cweClasses, boolean exportArffData, boolean isDocumented,
                       int iterations, double trainTestSplit, String phase) {
        this.testDataDir = testDataDir;
        this.trainDataDir = trainDataDir;
        this.datasetJson = datasetJson;
        this.outputDir = outputDir;
        this.featureSet = featureSet;
        this.toolkit = toolkit;
        this.srmClasses = srmClasses;
        this.cweClasses = cweClasses;
        this.exportArffData = exportArffData;
        this.isDocumented = isDocumented;
        this.iterations = iterations;
        this.trainTestSplit = trainTestSplit;
        this.phase = phase;
    }

    public SwanOptions(String testDataDir, String trainDataDir, String datasetJson, String outputDir,
                       List<String> featureSet, String toolkit, List<String> srmClasses, List<String> cweClasses,
                       boolean exportArffData, boolean isDocumented, int iterations, double trainTestSplit) {
        this.testDataDir = testDataDir;
        this.trainDataDir = trainDataDir;
        this.datasetJson = datasetJson;
        this.outputDir = outputDir;
        this.featureSet = featureSet;
        this.toolkit = toolkit;
        this.srmClasses = srmClasses;
        this.cweClasses = cweClasses;
        this.exportArffData = exportArffData;
        this.isDocumented = isDocumented;
        this.iterations = iterations;
        this.trainTestSplit = trainTestSplit;
    }

    public String getTestDataDir() {
        return testDataDir;
    }

    public void setTestDataDir(String testDataDir) {
        this.testDataDir = testDataDir;
    }

    public String getTrainDataDir() {
        return trainDataDir;
    }

    public void setTrainDataDir(String trainDataDir) {
        this.trainDataDir = trainDataDir;
    }

    public String getDatasetJson() {
        return datasetJson;
    }

    public void setDatasetJson(String datasetJson) {
        this.datasetJson = datasetJson;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public List<String> getFeatureSet() {
        return featureSet;
    }

    public void setFeatureSet(List<String> featureSet) {
        this.featureSet = featureSet;
    }

    public String getToolkit() {
        return toolkit;
    }

    public void setToolkit(String toolkit) {
        this.toolkit = toolkit;
    }

    public List<String> getSrmClasses() {
        return srmClasses;
    }

    public void setSrmClasses(List<String> srmClasses) {
        this.srmClasses = srmClasses;
    }

    public List<String> getCweClasses() {
        return cweClasses;
    }

    public void setCweClasses(List<String> cweClasses) {
        this.cweClasses = cweClasses;
    }

    public List<String> getAllClasses() {
        return Stream.of(srmClasses, cweClasses).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public boolean isExportArffData() {
        return exportArffData;
    }

    public boolean isReduceAttributes() {
        return reduceAttributes;
    }

    public void setReduceAttributes(boolean reduceAttributes) {
        this.reduceAttributes = reduceAttributes;
    }

    public void setExportArffData(boolean exportArffData) {
        this.exportArffData = exportArffData;
    }

    public boolean isDocumented() {
        return isDocumented;
    }

    public void setDocumented(boolean documented) {
        this.isDocumented = documented;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public double getTrainTestSplit() {
        return trainTestSplit;
    }

    public void setTrainTestSplit(double trainTestSplit) {
        this.trainTestSplit = trainTestSplit;
    }

    public String getPhase() {
        return phase;
    }

    public boolean isPredictPhase(){

        return getPhase().contentEquals("predict");
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public double getPredictionThreshold() {
        return predictionThreshold;
    }

    public void setPredictionThreshold(double predictionThreshold) {
        this.predictionThreshold = predictionThreshold;
    }

    public List<String> getArffInstancesFiles() {
        return arffInstancesFiles;
    }

    public void setInstances(List<String> instancesArff) {
        this.arffInstancesFiles = instancesArff;
    }

    public String getTestDataSourceDir() {
        return testDataSourceDir;
    }

    public void setTestDataSourceDir(String testDataSourceDir) {
        this.testDataSourceDir = testDataSourceDir;
    }

    public String getTrainDataSourceDir() {
        return trainDataSourceDir;
    }

    public void setTrainDataSourceDir(String trainDataSourceDir) {
        this.trainDataSourceDir = trainDataSourceDir;
    }

    public List<String> getDiscovery() {
        return discovery;
    }

    public void setDiscovery(List<String> discovery) {
        this.discovery = discovery;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public String toString() {
        return "SwanOptions{" +
                "testData='" + testDataDir + '\'' +
                ", trainData='" + trainDataDir + '\'' +
                ", datasetJson='" + datasetJson + '\'' +
                ", instances='" + arffInstancesFiles + '\'' +
                ", outputDir='" + outputDir + '\'' +
                ", featureSet='" + featureSet + '\'' +
                ", learningMode='" + toolkit + '\'' +
                ", srmClasses=" + srmClasses +
                ", cweClasses=" + cweClasses +
                ", exportArffData=" + exportArffData +
                ", isDocumented=" + isDocumented +
                '}';
    }
}
