package de.fraunhofer.iem.coveragedoclet;

import java.text.DecimalFormat;

public class CoverageReport {

    private int totalClasses;
    private int documentedClasses;
    private int totalMethods;
    private int documentedMethods;

    public int getTotalClasses() {
        return totalClasses;
    }

    public void incrementTotalClasses() {
        this.totalClasses++;
    }

    public int getDocumentedClasses() {
        return documentedClasses;
    }

    public void incrementDocumentedClasses() {
        this.documentedClasses++;
    }

    public int getTotalMethods() {
        return totalMethods;
    }

    public void incrementTotalMethods() {
        this.totalMethods++;
    }

    public int getDocumentedMethods() {
        return documentedMethods;
    }

    public void incrementDocumentedMethods() {
        this.documentedMethods++;
    }

    public double getCoveragePercent() {

        double covPercent = (double) (documentedClasses + documentedMethods) / (totalClasses + totalMethods) * 100.0;
        DecimalFormat format_2Places = new DecimalFormat("0.00");

       return Double.parseDouble(format_2Places.format(covPercent));
    }

    @Override
    public String toString() {
        return "DocCoverageReport{" +
                "totalClasses=" + totalClasses +
                ", documentedClasses=" + documentedClasses +
                ", totalMethods=" + totalMethods +
                ", documentedMethods=" + documentedMethods +
                ", coveragePercent=" + getCoveragePercent() +
                '}';
    }
}
