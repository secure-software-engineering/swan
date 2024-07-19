package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;

import java.util.ArrayList;

/***
 * Evaluates the total number of invoked methods.
 *
 * @author Rohith Kumar
 */
public class TotalMethodsInvokedCountFeature implements ICodeFeature {

    private int numberOfFunctions;
    private FeatureResult featureResult;

    private ArrayList<String> featureValues;

    public TotalMethodsInvokedCountFeature() {
        this.featureResult = new FeatureResult();
        this.numberOfFunctions = 0;
    }

    @Override
    public FeatureResult applies(Method method){
        if (method.getSootMethod() == null || !method.getSootMethod().isConcrete() || !method.getSootMethod().hasActiveBody()) {
            this.featureResult.setIntegerValue(this.numberOfFunctions);
            return this.featureResult;
        }
        try {
            for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                // Count total number of invocations
                if (u instanceof Stmt) {
                    Stmt stmt = (Stmt) u;
                    if (stmt.containsInvokeExpr()) {
                        if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                            this.numberOfFunctions += 1;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            this.featureResult.setIntegerValue(this.numberOfFunctions);
            return this.featureResult;
        }
        this.featureResult.setIntegerValue(this.numberOfFunctions);
        return this.featureResult;
    }


    @Override
    public String toString() {
        return "TotalMethodsInvokedCount";
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
