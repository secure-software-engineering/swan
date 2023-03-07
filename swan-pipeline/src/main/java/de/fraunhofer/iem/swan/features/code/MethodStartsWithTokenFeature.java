package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import de.fraunhofer.iem.swan.features.code.type.MethodNameStartsWithFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.SecurityVocabulary.*;

public class MethodStartsWithTokenFeature extends WeightedFeature implements IFeatureNew {

    private Set<String> Keywords;

    private ArrayList<String> featureValues;

    private FeatureResult featureResult;

    public MethodStartsWithTokenFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method, Category category) {
        switch (category) {
            case SOURCE:
                this.Keywords = SOURCE_METHOD_START;
                break;

            case AUTHENTICATION_NEUTRAL:
            case AUTHENTICATION_TO_HIGH:
            case AUTHENTICATION_TO_LOW:
            case AUTHENTICATION:
                this.Keywords = AUTHENTICATION_METHOD_START;
                break;

            case SINK:
                this.Keywords = SINK_METHOD_START;
                break;

            case SANITIZER:
                this.Keywords = SANITIZER_METHOD_START;
                break;
        }
        for(String keyword : this.Keywords) {
            IFeature checkForKeyword = new MethodNameStartsWithFeature(keyword);
            if(method.getName().startsWith(keyword)){
                this.featureResult.setBooleanValue(Boolean.TRUE);
                return this.featureResult;
            }
        }
        this.featureResult.setBooleanValue(Boolean.FALSE);
        return this.featureResult;
    }


    @Override
    public String toString() {
        return "MethodStartsWithString";
    }

    @Override
    public FeatureType getFeatureType(){
        return FeatureType.BOOLEAN;
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        this.featureValues.add("true");
        this.featureValues.add("false");
        return this.featureValues;
    }
}
