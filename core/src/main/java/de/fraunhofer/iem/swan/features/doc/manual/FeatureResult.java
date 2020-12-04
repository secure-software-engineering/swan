package de.fraunhofer.iem.swan.features.doc.manual;

/**
 * Stores the method and class comment feature evaluation results.
 */
public class FeatureResult {

    private double methodValue;
    private double classValue;

    public FeatureResult() {
        this.methodValue = 0.0;
        this.classValue = 0.0;
    }

    public FeatureResult(double classValue, double methodValue) {
        this.methodValue = methodValue;
        this.classValue = classValue;
    }

    public double getMethodValue() {
        return methodValue;
    }

    public void setMethodValue(double methodValue) {
        this.methodValue = methodValue;
    }

    public double getClassValue() {
        return classValue;
    }

    public void setClassValue(double classValue) {
        this.classValue = classValue;
    }

    public double getTotalValue() {
        return classValue + methodValue;
    }

    public void incrementClassValue() {
        classValue += 1;
    }

    public void incrementMethodValue(int increment) {
        methodValue += increment;
    }

    @Override
    public String toString() {
        return "class=" + classValue + ", method=" + methodValue;
    }
}
