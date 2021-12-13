package de.fraunhofer.iem.swan.cli;

import java.util.List;

/**
 * POJO for SWAN command line options.
 */
public class SwanOptions {

    private String testData;
    private String trainData;
    private String datasetJson;
    private String outputDir;
    private String featureSet;
    private String learningMode;
    private List<String> srmClasses;
    private List<String> cweClasses;
    private boolean exportArffData;
    private boolean isDocumented;
    private int iterations;
    private double trainTestSplit;

    public SwanOptions(String testData, String trainData, String datasetJson, String outputDir, String featureSet,
                       String learningMode, List<String> srmClasses, List<String> cweClasses, boolean exportArffData) {
        this.testData = testData;
        this.trainData = trainData;
        this.datasetJson = datasetJson;
        this.outputDir = outputDir;
        this.featureSet = featureSet;
        this.learningMode = learningMode;
        this.srmClasses = srmClasses;
        this.cweClasses = cweClasses;
        this.exportArffData = exportArffData;
        this.isDocumented = isDocumented;
        this.iterations = iterations;
        this.trainTestSplit = trainTestSplit;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    public String getTrainData() {
        return trainData;
    }

    public void setTrainData(String trainData) {
        this.trainData = trainData;
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

    public String getFeatureSet() {
        return featureSet;
    }

    public void setFeatureSet(String featureSet) {
        this.featureSet = featureSet;
    }

    public String getLearningMode() {
        return learningMode;
    }

    public void setLearningMode(String learningMode) {
        this.learningMode = learningMode;
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

    public boolean isExportArffData() {
        return exportArffData;
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

    @Override
    public String toString() {
        return "SwanOptions{" +
                "testData='" + testData + '\'' +
                ", trainData='" + trainData + '\'' +
                ", datasetJson='" + datasetJson + '\'' +
                ", outputDir='" + outputDir + '\'' +
                ", featureSet='" + featureSet + '\'' +
                ", learningMode='" + learningMode + '\'' +
                ", srmClasses=" + srmClasses +
                ", cweClasses=" + cweClasses +
                ", exportArffData=" + exportArffData +
                ", isDocumented=" + isDocumented +
                '}';
    }
}
