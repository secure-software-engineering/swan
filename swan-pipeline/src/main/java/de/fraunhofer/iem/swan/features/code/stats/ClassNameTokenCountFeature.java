package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;

import java.util.ArrayList;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.bow.SecurityVocabulary.*;

public class ClassNameTokenCountFeature implements ICodeFeature {

    private Set<String> Keywords;
    private FeatureResult featureResult;
    private int NumberOfMatches;
    private ArrayList<String> featureValues;

    public ClassNameTokenCountFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method){
        this.NumberOfMatches = 0;
        for(String item: METHOD_NAME_TOKENS){
            if(method.getName().toLowerCase().contains(item)){
                this.NumberOfMatches ++;
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
