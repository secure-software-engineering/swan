package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.List;

public class ParameterAndReturnTypeMatchFeature extends WeightedFeature implements IFeatureNew{
    private ArrayList<String> featureValues;
    private FeatureResult featureResult;

    public ParameterAndReturnTypeMatchFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method, Category category) {
        List<String> paramList = method.getParameters();
        for (String param : paramList) {
            if (param.toLowerCase().contains(method.getReturnType().toLowerCase())){
                this.featureResult.setBooleanValue(Boolean.TRUE);
                break;
            }else{
                this.featureResult.setBooleanValue(Boolean.FALSE);
            }
        }
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.BOOLEAN;
    }

    @Override
    public String toString(){ return "ParameterAndReturnTypeMatchFeature"; }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        this.featureValues.add("true");
        this.featureValues.add("false");
        return this.featureValues;
    }
}
