package de.fraunhofer.iem.swan.features.code.bow;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;

public class ClassNameContainsToken extends WeightedFeature implements ICodeFeature {

    private final String token;
    private ArrayList<String> featureValues;
    private FeatureResult featureResult;

    public ClassNameContainsToken(String token) {
        this.token = token.toLowerCase();
        featureResult = new FeatureResult();;
    }

    @Override
    public FeatureResult applies(Method method) {

        if (method.getClassName().toLowerCase().contains(token))
            this.featureResult.setBooleanValue(Boolean.TRUE);
        else
            this.featureResult.setBooleanValue(Boolean.FALSE);

        return featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.BOOLEAN;
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        this.featureValues.add("true");
        this.featureValues.add("false");
        return this.featureValues;
    }

    @Override
    public String toString() {
        return "ClassNameContains" + this.token.toUpperCase();
    }
}
