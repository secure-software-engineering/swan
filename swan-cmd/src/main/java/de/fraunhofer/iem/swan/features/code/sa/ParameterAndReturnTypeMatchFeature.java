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
    }

    @Override
    public FeatureResult applies(Method method) {

        List<String> paramList = method.getParameters();

        this.featureResult.setBooleanValue(Boolean.FALSE);

        for (String param : paramList) {
            if (param.toLowerCase().contains(method.getReturnType().toLowerCase())){
                this.featureResult.setBooleanValue(Boolean.TRUE);
                break;
            }
        }
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.BOOLEAN;
    }

    @Override
    public String toString(){ return "ParameterAndReturnTypeMatch"; }


    @Override
    public ArrayList<String> getFeatureValues() {
        featureValues = new ArrayList<>();
        this.featureValues.add("true");
        this.featureValues.add("false");
        return this.featureValues;
    }

}
