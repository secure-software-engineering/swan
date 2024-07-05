package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;

import java.util.ArrayList;

public class ExceptionsCountFeature implements ICodeFeature {
    private FeatureResult featureResult;
    private int numberOfExceptions;

    public ExceptionsCountFeature() {
        this.featureResult = new FeatureResult();
        this.numberOfExceptions = 0;
    }

    @Override
    public FeatureResult applies(Method method) {
        this.numberOfExceptions = method.getSootMethod().getExceptions().size();
        featureResult.setIntegerValue(this.numberOfExceptions);
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.NUMERICAL;
    }

    @Override
    public String toString() {
        return "ExceptionsCount";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        return null;
    }

    @Override
    public AnalysisType getFeatureAnalysisType() {
        return AnalysisType.BODY;
    }

    @Override
    public String getDefaultStringValue() {
        return String.valueOf(0);
    }
}
