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
    private int numberOfMatches;
    private Set<String> classesSet;

    public ClassesInvokedCountFeature() {
        this.featureResult = new FeatureResult();
        this.classesSet = new HashSet<>();
    }

    @Override
    public FeatureResult applies(Method method) {
        this.numberOfMatches = 0;
        this.classesSet.addAll(AUTHENTICATION_CLASSES_INVOKED);
        this.classesSet.addAll(SANITIZER_CLASSES_INVOKED);
        this.classesSet.addAll(SINK_CLASSES_INVOKED);
        this.classesSet.addAll(SOURCE_CLASSES_INVOKED);

        for (String className : this.classesSet) {
            try {
                if(method.getSootMethod().hasActiveBody()){
                    for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                        // Check for invocations
                        if (u instanceof Stmt) {
                            Stmt stmt = (Stmt) u;
                            if (stmt.containsInvokeExpr())
                                if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                                    InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
                                    if (iinv.getMethod().getDeclaringClass().getName().contains(className))
                                        this.numberOfMatches += 1;
                                }
                        }
                    }
                }
            } catch (Exception ex) {
                throw (ex);
            }
        }
        this.featureResult.setIntegerValue(this.numberOfMatches);
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
