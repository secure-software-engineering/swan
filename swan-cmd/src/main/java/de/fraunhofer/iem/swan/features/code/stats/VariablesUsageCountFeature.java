package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import soot.Unit;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class VariablesUsageCountFeature implements ICodeFeature {

    private Set<String> methodsList;
    private int variablesUsageCount;
    private FeatureResult featureResult;

    private ArrayList<String> featureValues;

    public VariablesUsageCountFeature() {
        this.featureResult = new FeatureResult();
        this.variablesUsageCount = 0;
        methodsList = new HashSet<>();
    }

    @Override
    public FeatureResult applies(Method method){
        if(method.getSootMethod().hasActiveBody()){
            for (Unit u: method.getSootMethod().retrieveActiveBody().getUnits()){
//                System.out.println(u.getUseAndDefBoxes());
                this.variablesUsageCount += u.getUseAndDefBoxes().size();
            }
        }
        this.featureResult.setIntegerValue(this.variablesUsageCount);
        return this.featureResult;
    }


    @Override
    public String toString() {
        return "VariablesUsageCount";
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