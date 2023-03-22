package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.SecurityVocabulary.*;
import static de.fraunhofer.iem.swan.features.code.SecurityVocabulary.SANITIZER_CLASSES_INVOKED;

public class ParametersTypesFeature extends WeightedFeature implements IFeatureNew{
    private int NumberOfParameters;
    private Set<String> ParametersList;
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;

    public ParametersTypesFeature() {
        this.NumberOfParameters = 0;
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method, Category category) {
        switch (category) {
            case SOURCE:
                this.ParametersList = SOURCE_PARAMETER_TYPES;
                break;
            case AUTHENTICATION_NEUTRAL:
            case AUTHENTICATION_TO_HIGH:
            case AUTHENTICATION_TO_LOW:
            case AUTHENTICATION:
                this.ParametersList = AUTHENTICATION_PARAMETER_TYPES;
                break;
            case SINK:
                this.ParametersList = SINK_PARAMETER_TYPES;
                break;
            case SANITIZER:
                this.ParametersList = SANITIZER_PARAMETER_TYPES;
                break;
        }
        for (String methodParameter: method.getParameters()){
            for(String parameter: this.ParametersList){
                if(methodParameter.contains(parameter)){
                    this.NumberOfParameters +=1;
                }
            }
        }
        this.featureResult.setIntegerValue(this.NumberOfParameters);
        return featureResult;
    }

    @Override
    public FeatureType getFeatureType() {return FeatureType.NUMERICAL;}

    @Override
    public ArrayList<String> getFeatureValues() {return null;}

    @Override
    public String toString(){
        return "MethodParametersTypes";
    }
}
