package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import soot.Unit;

import java.util.ArrayList;

public class BranchingStatementCountFeature implements ICodeFeature {
    private int numberOfBranchingStatements;
    private FeatureResult featureResult;

    public BranchingStatementCountFeature() {
        this.featureResult = new FeatureResult();
        this.numberOfBranchingStatements = 0;
    }

    @Override
    public FeatureResult applies(Method method) {
        if(method.getSootMethod().hasActiveBody()){
            for (Unit u: method.getSootMethod().retrieveActiveBody().getUnits()){
                if(u.branches()){
                    this.numberOfBranchingStatements += 1;
                }
            }
        }
        this.featureResult.setIntegerValue(this.numberOfBranchingStatements);
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.NUMERICAL;
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        return null;
    }

    @Override
    public String toString() {
        return "BranchingStatementCount";
    }
}
