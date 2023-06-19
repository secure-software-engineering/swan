package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;

import java.util.ArrayList;

public class MethodLinesCountFeature implements ICodeFeature {
    private FeatureResult featureResult;
    private int numberOfLines;

    public MethodLinesCountFeature(){
        this.featureResult = new FeatureResult();
        this.numberOfLines = 0;
    }

    @Override
    public FeatureResult applies(Method method) {
        this.numberOfLines = method.getSootMethod().retrieveActiveBody().getUnits().size();
        this.featureResult.setIntegerValue(this.numberOfLines);
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.NUMERICAL;
    }

    @Override
    public String toString() {
        return "MethodLinesCount";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        return null;
    }
}
