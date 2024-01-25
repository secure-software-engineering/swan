package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;

import java.util.ArrayList;

public class ParametersCountFeature implements ICodeFeature {

    private int numberOfParameters;
    private FeatureResult featureResult;

    public ParametersCountFeature() {
        this.featureResult = new FeatureResult();
        this.numberOfParameters = 0;
    }

    @Override
    public FeatureResult applies(Method method) {
        this.numberOfParameters = method.getParameters().size();
        this.featureResult.setIntegerValue(this.numberOfParameters);
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.NUMERICAL;
    }

    @Override
    public String toString(){
        return "ParametersCount";
    }

    @Override
    public ArrayList<String> getFeatureValues() {return null;}
}
