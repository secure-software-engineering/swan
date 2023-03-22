package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.SecurityVocabulary.*;

public class MethodNameTokenCountFeature extends WeightedFeature implements IFeatureNew {

    private Set<String> Keywords;
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;
    private int NumberOfMatches;

    public MethodNameTokenCountFeature() {
        this.featureResult = new FeatureResult();
    }

    public FeatureResult applies(Method method, Category category){
        this.NumberOfMatches = 0;
        for(Pair<String, Integer> item: METHOD_CONTAINS){
            if(method.getName().toLowerCase().contains(item.getLeft())){
                this.NumberOfMatches += item.getRight();
            }
        }
        this.featureResult.setIntegerValue(this.NumberOfMatches);
        return this.featureResult;
    }


    @Override
    public String toString() {
        return "MethodNameTokenCount";
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
