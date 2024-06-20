package de.fraunhofer.iem.swan.features.code.sa;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ParameterFlowsToReturnFeature implements ICodeFeature {
    private ArrayList<String> featureValues;
    private FeatureResult featureResult;

    public ParameterFlowsToReturnFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method) {
        this.featureResult.setBooleanValue(Boolean.FALSE);
        if (method.getSootMethod() == null) {
            return this.featureResult;
        }
        if (!method.getSootMethod().isConcrete()){
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
                    if (paramVals.contains(leftOp)) paramVals.remove(leftOp);
                    if (paramVals.contains(rightOp)) {
                        paramVals.add(leftOp);
                    }
                }
                // Check for invocations
                if (u instanceof ReturnStmt) {
                    ReturnStmt stmt = (ReturnStmt) u;
                    if(paramVals.contains(stmt.getOp()))
                        this.featureResult.setBooleanValue(Boolean.TRUE);
                }
            }
        } catch (Exception ex) {
            this.featureResult.setBooleanValue(Boolean.FALSE);
        }
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.BOOLEAN;
    }

    @Override
    public String toString(){
        return "ParameterFlowsToReturn";
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
    public String getDefaultStringValue() {
        return String.valueOf(false);
    }
}
