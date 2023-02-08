package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import de.fraunhofer.iem.swan.features.code.type.MethodNameContainsFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.SecurityVocabulary.*;

public class MethodNameStringsCountFeature extends WeightedFeature implements IFeatureNew {

    private Set<String> Keywords;
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;
    private int NumberOfMatches;

    public MethodNameStringsCountFeature() {
        this.NumberOfMatches = 0;
        this.featureResult = new FeatureResult();
    }

    public FeatureResult applies(Method method, Category category){
        switch (category) {
            case SOURCE:
                this.Keywords = SOURCE_METHOD_CONTAINS;
                break;
            case AUTHENTICATION_NEUTRAL:
            case AUTHENTICATION_TO_HIGH:
            case AUTHENTICATION_TO_LOW:
            case AUTHENTICATION:
                this.Keywords = AUTHENTICATION_METHOD_CONTAINS;
                break;
            case SINK:
                this.Keywords = SINK_METHOD_CONTAINS;
                break;
            case SANITIZER:
                this.Keywords = SANITIZER_METHOD_CONTAINS;
                break;
        }
        for(String keyword : this.Keywords) {
            if(method.getName().toLowerCase().contains(keyword)){
                this.NumberOfMatches += 1;
            }
        }
        this.featureResult.setIntegerValue(this.NumberOfMatches);
        return this.featureResult;
    }


    @Override
    public String toString() {
        return "MethodNameStringsCount";
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
