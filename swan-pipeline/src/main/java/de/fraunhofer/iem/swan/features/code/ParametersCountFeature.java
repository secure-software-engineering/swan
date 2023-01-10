package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParametersCountFeature extends WeightedFeature implements IFeatureNew{

    private int NumberOfParametersFeature;
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;



    @Override
    public FeatureResult applies(Method method, Category category) {
        this.featureResult = new FeatureResult();
        this.NumberOfParametersFeature = method.getParameters().size();
        this.featureResult.setIntegerValue(this.NumberOfParametersFeature);
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
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        return this.featureValues;
    }
}
