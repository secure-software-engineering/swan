package de.fraunhofer.iem.swan.features.code.bow;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.Set;

/**
 * Evaluates whether invoked method name contains tokens in the security vocabulary.
 *
 * @author Rohith Kumar
 */

public class InvokedMethodNameContainsToken extends WeightedFeature implements ICodeFeature {

    private String token;
    private FeatureResult featureResult;

    private ArrayList<String> featureValues;

    public InvokedMethodNameContainsToken(String token) {
        this.token = token;
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method) {
        this.featureResult.setBooleanValue(Boolean.FALSE);
        if(method.getSootMethod().hasActiveBody()) {
            for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                // Check for invocations
                if (u instanceof Stmt) {
                    Stmt stmt = (Stmt) u;
                    if (stmt.containsInvokeExpr()) {
                        if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                            InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
                            if (iinv.getMethod().getName().contains(this.token)) {
                                this.featureResult.setBooleanValue(Boolean.TRUE);
                            }
                        }
                    }
                }
            }
        }
        return this.featureResult;
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
        return "InvokedMethodNameContains" + this.token.toUpperCase();
    }
}
