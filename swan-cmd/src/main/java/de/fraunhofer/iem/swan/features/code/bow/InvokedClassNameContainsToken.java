package de.fraunhofer.iem.swan.features.code.bow;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;

import java.util.ArrayList;

/**
 * Evaluates whether method's class name contains tokens in the security vocabulary.
 *
 * @author Rohith Kumar
 */

public class InvokedClassNameContainsToken extends WeightedFeature implements ICodeFeature {
    private FeatureResult featureResult;
    private String token;
    private ArrayList<String> featureValues;

    public InvokedClassNameContainsToken(String token) {
        this.token = token;
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method) {
        this.featureResult.setBooleanValue(Boolean.FALSE);
        try {
            if(method.getSootMethod().hasActiveBody()){
                for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                    // Check for invocations
                    if (u instanceof Stmt) {
                        Stmt stmt = (Stmt) u;
                        if (stmt.containsInvokeExpr())
                            if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                                InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
                                if (iinv.getMethod().getDeclaringClass().getName().contains(this
                                        .token))
                                    this.featureResult.setBooleanValue(Boolean.TRUE);
                            }
                    }
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
    public String toString() {
        return "InvokedClassNameContains" + this.token.toUpperCase();
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        this.featureValues.add("true");
        this.featureValues.add("false");
        return this.featureValues;
    }

}
