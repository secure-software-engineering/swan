package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import de.fraunhofer.iem.swan.features.code.type.MethodInvocationName;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.SecurityVocabulary.*;

public class MethodsInvokedCountFeature extends WeightedFeature implements IFeatureNew {

    private Set<String> MethodsList;
    private int NumberOfMatches;
    private FeatureResult featureResult;

    private ArrayList<String> featureValues;

    @Override
    public FeatureResult applies(Method method, Category category){
        this.featureResult = new FeatureResult();
        this.NumberOfMatches = 0;
        switch (category) {
            case SOURCE:
                this.MethodsList = SOURCE_METHOD_INVOKED;
                break;
            case AUTHENTICATION_NEUTRAL:
            case AUTHENTICATION_TO_HIGH:
            case AUTHENTICATION_TO_LOW:
            case AUTHENTICATION:
                this.MethodsList = AUTHENTICATION_METHOD_INVOKED;
                break;
            case SINK:
                this.MethodsList = SINK_METHOD_INVOKED;
                break;
            case SANITIZER:
                this.MethodsList = SANITIZER_METHOD_INVOKED;
                break;
        }
        for(String methodName : this.MethodsList) {
            IFeature checkForKeyword = new MethodInvocationName(methodName);
            if(checkForKeyword.applies(method) == de.fraunhofer.iem.swan.features.code.type.IFeature.Type.TRUE){
                this.NumberOfMatches += 1;
            }
        }
        this.featureResult.setIntegerValue(this.NumberOfMatches);
        return this.featureResult;
    }


    @Override
    public String toString() {
        return "MethodsInvokedCount";
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.NUMERICAL;
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        for(int i=0;i<100;i++){
            this.featureValues.add(String.valueOf(i));
        }
        return this.featureValues;
    }
}
