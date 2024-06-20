package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import soot.SootMethod;

import java.util.ArrayList;

/**
 * Calculates the total number of lines in the declaring class of the method.
 */
public class ClassLineCountFeature implements ICodeFeature{
    private FeatureResult featureResult;
    private int numberOfLines;
    public ClassLineCountFeature(){
        this.featureResult = new FeatureResult();
        this.numberOfLines = 0;
    }

    @Override
    public FeatureResult applies(Method method) {
        for(SootMethod sm : method.getSootClass().getMethods()){
            if(sm.hasActiveBody()){
                this.numberOfLines += sm.getActiveBody().getUnits().size();
            }else{
                this.numberOfLines += 0;
            }

        }
        this.featureResult.setIntegerValue(this.numberOfLines);
        return this.featureResult;
    }

    @Override
    public ICodeFeature.FeatureType getFeatureType() {
        return ICodeFeature.FeatureType.NUMERICAL;
    }

    @Override
    public String toString() {
        return "ClassLinesCount";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        return null;
    }

    @Override
    public AnalysisType getFeatureAnalysisType() {
        return AnalysisType.BODY;
    }

    @Override
    public String getDefaultStringValue() {
        return String.valueOf(0);
    }
}
