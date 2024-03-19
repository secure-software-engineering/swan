package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;

import java.util.ArrayList;

public class DefinedFunctionsInClassCountFeature implements ICodeFeature {
    private FeatureResult featureResult;
    private int numberOfDefinedFunctions;

    public DefinedFunctionsInClassCountFeature() {
        this.featureResult = new FeatureResult();
        this.numberOfDefinedFunctions = 0;
    }

    @Override
    public FeatureResult applies(Method method) {
        try {
//            Check all the methods in the declaring class
            for (SootMethod m : method.getSootMethod().getDeclaringClass().getMethods()) {
                if( m.isConcrete() && !m.isAbstract() && m.hasActiveBody()){
                    this.numberOfDefinedFunctions += 1;
                }
            }
        } catch (Exception ex) {
            throw (ex);
        }
        this.featureResult.setIntegerValue(this.numberOfDefinedFunctions);
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.NUMERICAL;
    }

    @Override
    public String toString() {
        return "DefinedFunctionsInvokedCount";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        return null;
    }
}
