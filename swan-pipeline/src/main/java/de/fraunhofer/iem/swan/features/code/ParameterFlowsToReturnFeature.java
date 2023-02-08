package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ParameterFlowsToReturnFeature extends WeightedFeature implements IFeatureNew{
    private ArrayList<String> featureValues;
    private FeatureResult featureResult;

    public ParameterFlowsToReturnFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method, Category category) {
        if (method.getSootMethod() == null) {
            this.featureResult.setBooleanValue(Boolean.FALSE);
            return this.featureResult;
        }
        if (!method.getSootMethod().isConcrete()){
            this.featureResult.setBooleanValue(Boolean.FALSE);
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
                    else
                        this.featureResult.setBooleanValue(Boolean.FALSE);
                }
            }
        } catch (Exception ex) {
            this.featureResult.setBooleanValue(Boolean.TRUE);
        }
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.BOOLEAN;
    }

    @Override
    public String toString(){
        return "ParameterFlowsToReturnFeature";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        this.featureValues.add("true");
        this.featureValues.add("false");
        return this.featureValues;
    }
}