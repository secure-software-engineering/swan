package de.fraunhofer.iem.swan.cli;

import java.util.List;

/**
 * POJO for SWAN command line options.
 */
public class SwanOptions {

    private String testDataDir;
    private String trainDataDir;
    private String datasetJson;
    private String outputDir;
    private List<String> featureSet;
    private String learningMode;
    private List<String> srmClasses;
    private List<String> cweClasses;
    private boolean exportArffData;
    private boolean isDocumented;
    private int iterations;
    private double trainTestSplit;

    public SwanOptions(String testDataDir, String trainDataDir, String datasetJson, String outputDir,
                       List<String> featureSet, String learningMode, List<String> srmClasses, List<String> cweClasses,
                       boolean exportArffData, boolean isDocumented, int iterations, double trainTestSplit) {
        this.testDataDir = testDataDir;
        this.trainDataDir = trainDataDir;
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
                "testData='" + testDataDir + '\'' +
                ", trainData='" + trainDataDir + '\'' +
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
