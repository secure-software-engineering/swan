package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.bow.SecurityVocabulary.*;

public class MethodsInvokedCountFeature implements ICodeFeature {

    private Set<String> methodsList;
    private int numberOfMatches;
    private FeatureResult featureResult;

    private ArrayList<String> featureValues;

    public MethodsInvokedCountFeature() {
        this.featureResult = new FeatureResult();
        this.numberOfMatches = 0;
        this.methodsList = new HashSet<>();
    }

    @Override
    public FeatureResult applies(Method method){

        methodsList.addAll(SANITIZER_METHOD_INVOKED);
        methodsList.addAll(AUTHENTICATION_METHOD_INVOKED);
        methodsList.addAll(SINK_METHOD_INVOKED);
        methodsList.addAll(SOURCE_METHOD_INVOKED);

        if (method.getSootMethod() == null || !method.getSootMethod().isConcrete() || !method.getSootMethod().hasActiveBody()) {
            this.featureResult.setIntegerValue(this.numberOfMatches);
            return this.featureResult;
        }
        try {
            for(String methodName : this.methodsList) {
                for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                    // Check for invocations
                    if (u instanceof Stmt) {
                        Stmt stmt = (Stmt) u;
                        if (stmt.containsInvokeExpr()) {
                            if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                                InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
                                if (iinv.getMethod().getName().contains(methodName)) {
                                    this.numberOfMatches += 1;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            this.featureResult.setIntegerValue(this.numberOfMatches);
            return this.featureResult;
        }
        this.featureResult.setIntegerValue(this.numberOfMatches);
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
        return null;
    }
}
