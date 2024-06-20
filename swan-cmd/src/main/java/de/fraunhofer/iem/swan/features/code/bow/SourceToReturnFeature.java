package de.fraunhofer.iem.swan.features.code.bow;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Evaluates whether the parameter in a Source flows to the return statement of the method.
 *
 * @author Rohith Kumar
 */

public class SourceToReturnFeature extends WeightedFeature implements ICodeFeature {
    private String token;
    private FeatureResult featureResult;

    private ArrayList<String> featureValues;

    public SourceToReturnFeature(String token){
        this.token = token;
        this.featureResult = new FeatureResult();
    }
    @Override
    public FeatureResult applies(Method method) {
        if (method.getSootMethod() == null) {
            this.featureResult.setBooleanValue(Boolean.FALSE);
            return this.featureResult;
        }
        // We are only interested in setters
        if (!method.getSootMethod().isConcrete()) {
            this.featureResult.setBooleanValue(Boolean.FALSE);
            return this.featureResult;
        }
        try {
            Set<Value> paramVals = new HashSet<>();
            for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                // Check for invocations
                if (((Stmt) u).containsInvokeExpr()) {
                    InvokeExpr invokeExpr = ((Stmt) u).getInvokeExpr();
                    Value leftOp = null;
                    if (u instanceof AssignStmt) leftOp = ((AssignStmt) u).getLeftOp();
                    if (leftOp != null) paramVals.add(leftOp);
                    // TODO: Add arguments as well? Not sure.
                    if (invokeExpr.getMethod().getName().toLowerCase()
                            .contains(this.token.toLowerCase())) {
                        paramVals.addAll(invokeExpr.getArgs());
                    }
                }
                if (u instanceof AssignStmt) {
                    Value leftOp = ((AssignStmt) u).getLeftOp();
                    Value rightOp = ((AssignStmt) u).getRightOp();
                    if (paramVals.contains(leftOp)) paramVals.remove(leftOp);
                    if (paramVals.contains(rightOp)) {
                        paramVals.add(leftOp);
                    }
                }
                // Check for invocations
                if (u instanceof ReturnStmt) {
                    ReturnStmt stmt = (ReturnStmt) u;
                    if(paramVals.contains(stmt.getOp())){
                        this.featureResult.setBooleanValue(Boolean.FALSE);
                    } else {
                        this.featureResult.setBooleanValue(Boolean.FALSE);
                    }
                    return this.featureResult;
                }
            }
            throw new RuntimeException(
                    "No return statement in method " + method.getSignature());
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
    public AnalysisType getFeatureAnalysisType() {
        return AnalysisType.BODY;
    }

    @Override
    public String toString() {
        return "SourceToReturnContains" + this.token.toUpperCase();
    }
}
