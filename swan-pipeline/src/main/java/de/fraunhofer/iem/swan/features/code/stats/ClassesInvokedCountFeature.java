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

public class ClassesInvokedCountFeature implements ICodeFeature {

    private FeatureResult featureResult;
    private int NumberOfMatches;
    private Set<String> ClassesSet;

    public ClassesInvokedCountFeature() {
        this.featureResult = new FeatureResult();
        ClassesSet = new HashSet<>();
    }

    @Override
    public FeatureResult applies(Method method) {

        this.NumberOfMatches = 0;

        ClassesSet.addAll(INNVOKED_CLASS_NAME_TOKENS);

        for (String className : ClassesSet) {
            try {
                for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                    // Check for invocations
                    if (u instanceof Stmt) {
                        Stmt stmt = (Stmt) u;
                        if (stmt.containsInvokeExpr())
                            if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                                InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
                                if (iinv.getMethod().getDeclaringClass().getName().contains(className))
                                    this.NumberOfMatches += 1;
                            }
                    }
                }
            } catch (Exception ex) {
                throw (ex);
            }
        }
        this.featureResult.setIntegerValue(this.NumberOfMatches);
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.NUMERICAL;
    }

    @Override
    public String toString() {
        return "ClassesInvokedCount";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        return null;
    }
}
