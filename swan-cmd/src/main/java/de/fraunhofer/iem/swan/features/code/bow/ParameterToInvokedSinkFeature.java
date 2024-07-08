package de.fraunhofer.iem.swan.features.code.bow;


import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;
import soot.Unit;
import soot.Value;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/**
 * Checks if there is parameter flow from a method to the invoked Sink
 *
 * @author Rohith Kumar
 */
public class ParameterToInvokedSinkFeature extends WeightedFeature implements ICodeFeature {

    private String token;
    private FeatureResult featureResult;

    private ArrayList<String> featureValues;

    public ParameterToInvokedSinkFeature(String token){
        this.token = token;
        this.featureResult = new FeatureResult();
    }
    @Override
    public FeatureResult applies(Method method) {
        if (method.getSootMethod() == null) {
            this.featureResult.setBooleanValue(Boolean.FALSE);
            return this.featureResult;
        }
        if (!method.getSootMethod().isConcrete() || !method.getSootMethod().hasActiveBody()){
            return this.featureResult;
        }
        try {
            Set<Value> paramVals = new HashSet<>();
            for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                // Collect the parameters
                if (u instanceof IdentityStmt) {
                    IdentityStmt id = (IdentityStmt) u;
                    if (id.getRightOp() instanceof ParameterRef)
                        paramVals.add(id.getLeftOp());
                }
                if (u instanceof AssignStmt) {
                    Value leftOp = ((AssignStmt) u).getLeftOp();
                    Value rightOp = ((AssignStmt) u).getRightOp();
                    paramVals.remove(leftOp);
                    if (paramVals.contains(rightOp)) {
                        paramVals.add(leftOp);
                    }
                }
                // Check for invocations
                if (((Stmt) u).containsInvokeExpr()) {
                    InvokeExpr invokeExpr = ((Stmt) u).getInvokeExpr();
                    if (invokeExpr.getMethod().getName().toLowerCase()
                            .contains(this.token.toLowerCase())) {
                        for (Value arg : invokeExpr.getArgs())
                            if (paramVals.contains(arg)){
                                this.featureResult.setBooleanValue(Boolean.TRUE);
                                return this.featureResult;
                            }
                    }
                }
            }
            this.featureResult.setBooleanValue(Boolean.FALSE);
            return this.featureResult;
        } catch (Exception ex) {
            this.featureResult.setBooleanValue(Boolean.FALSE);
            return this.featureResult;
        }
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.BOOLEAN;
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        this.featureValues.add("true");
        this.featureValues.add("false");
        return this.featureValues;
    }

    @Override
    public String toString() {
        return "ParameterToInvokedSinkContains" + this.token.toUpperCase();
    }
}
