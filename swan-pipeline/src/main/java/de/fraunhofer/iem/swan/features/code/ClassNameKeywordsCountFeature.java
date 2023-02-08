package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import de.fraunhofer.iem.swan.features.code.type.MethodClassContainsNameFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.SecurityVocabulary.*;

public class ClassNameKeywordsCountFeature extends WeightedFeature implements IFeatureNew {

    private Set<String> Keywords;
    private FeatureResult featureResult;
    private int NumberOfMatches;
    private ArrayList<String> featureValues;

    public ClassNameKeywordsCountFeature() {
        this.NumberOfMatches = 0;
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method, Category category){
        switch (category) {
            case SOURCE:
                this.Keywords = SOURCE_CLASS_CONTAINS;
                break;
            case AUTHENTICATION_NEUTRAL:
            case AUTHENTICATION_TO_HIGH:
            case AUTHENTICATION_TO_LOW:
            case AUTHENTICATION:
                this.Keywords = AUTHENTICATION_CLASS_CONTAINS;
                break;
            case SINK:
                this.Keywords = SINK_CLASS_CONTAINS;
                break;
            case SANITIZER:
                this.Keywords = SANITIZER_CLASS_CONTAINS;
                break;
        }
        for(String keyword : this.Keywords) {
            if(method.getClassName().toLowerCase().contains(keyword)){
                this.NumberOfMatches += 1;
            }
        }

        this.featureResult.setIntegerValue(this.NumberOfMatches);

        return this.featureResult;
    }

    @Override
    public String toString() {
        return "ClassNameKeywordsCount";
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.NUMERICAL;
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        return null;
    }
}
