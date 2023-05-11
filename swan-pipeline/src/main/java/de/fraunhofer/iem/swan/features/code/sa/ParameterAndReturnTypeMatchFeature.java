package de.fraunhofer.iem.swan.features.code.sa;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.List;

public class ParameterAndReturnTypeMatchFeature extends WeightedFeature implements ICodeFeature {
    private ArrayList<String> featureValues;
    private FeatureResult featureResult;

    public ParameterAndReturnTypeMatchFeature() {
        this.featureResult = new FeatureResult();
        this.featureResult.setBooleanValue(Boolean.FALSE);
    }

    @Override
    public FeatureResult applies(Method method) {

        for (String param : method.getParameters()) {
            if (param.toLowerCase().contains(method.getReturnType().toLowerCase())) {
                this.featureResult.setBooleanValue(Boolean.TRUE);
                return this.featureResult;
            }
        }
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.BOOLEAN;
    }

    @Override
    public String toString() {
        return "ParameterAndReturnTypeMatchFeature";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        this.featureValues.add("true");
        this.featureValues.add("false");
        return this.featureValues;
    }
}
