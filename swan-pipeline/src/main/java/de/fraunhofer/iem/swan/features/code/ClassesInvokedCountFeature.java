package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.SecurityVocabulary.*;
import static de.fraunhofer.iem.swan.features.code.SecurityVocabulary.SANITIZER_CLASS_CONTAINS;

public class ClassesInvokedCountFeature extends WeightedFeature implements IFeatureNew{
    private FeatureResult featureResult;
    private int NumberOfMatches;
    private Set<String> ClassesSet;

    public ClassesInvokedCountFeature() {
        this.NumberOfMatches = 0;
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method, Category category) {
        switch (category) {
            case SOURCE:
                this.ClassesSet = SOURCE_CLASSES_INVOKED;
                break;
            case AUTHENTICATION_NEUTRAL:
            case AUTHENTICATION_TO_HIGH:
            case AUTHENTICATION_TO_LOW:
            case AUTHENTICATION:
                this.ClassesSet = AUTHENTICATION_CLASSES_INVOKED;
                break;
            case SINK:
                this.ClassesSet = SINK_CLASSES_INVOKED;
                break;
            case SANITIZER:
                this.ClassesSet = SANITIZER_CLASSES_INVOKED;
                break;
        }
        for(String className: ClassesSet){
            try {
                for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                    // Check for invocations
                    if (u instanceof Stmt) {
                        Stmt stmt = (Stmt) u;
                        if (stmt.containsInvokeExpr())
                            if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                                InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
                                if (iinv.getMethod().getDeclaringClass().getName().contains(className))
                                    this.NumberOfMatches+=1;
                            }
                    }
                }
            } catch (Exception ex) {
                throw(ex);
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
