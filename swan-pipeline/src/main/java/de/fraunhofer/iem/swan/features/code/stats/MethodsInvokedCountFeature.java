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

    private Set<String> MethodsList;
    private int NumberOfMatches;
    private FeatureResult featureResult;

    private ArrayList<String> featureValues;

    public MethodsInvokedCountFeature() {
        this.featureResult = new FeatureResult();
        this.NumberOfMatches = 0;
        MethodsList = new HashSet<>();
    }

    @Override
    public FeatureResult applies(Method method){

        MethodsList.addAll(INNVOKED_METHOD_NAME_TOKENS);

        if (method.getSootMethod() == null || !method.getSootMethod().isConcrete()) {
            this.featureResult.setIntegerValue(this.NumberOfMatches);
            return this.featureResult;
        }
        try {
            for(String methodName : this.MethodsList) {
                for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                    // Check for invocations
                    if (u instanceof Stmt) {
                        Stmt stmt = (Stmt) u;
                        if (stmt.containsInvokeExpr()) {
                            if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                                InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
                                if (iinv.getMethod().getName().contains(methodName)) {
                                    this.NumberOfMatches += 1;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            this.featureResult.setIntegerValue(this.NumberOfMatches);
            return this.featureResult;
        }
        this.featureResult.setIntegerValue(this.NumberOfMatches);
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
        this.featureValues = new ArrayList<>();
        for(int i=0;i<100;i++){
            this.featureValues.add(String.valueOf(i));
        }
        return this.featureValues;
    }
}
