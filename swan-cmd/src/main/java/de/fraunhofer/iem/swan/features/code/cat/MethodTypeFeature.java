package de.fraunhofer.iem.swan.features.code.cat;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import soot.*;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodTypeFeature implements ICodeFeature {
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;

    private Values category;

    public MethodTypeFeature() {
        this.featureResult = new FeatureResult();
    }

    public enum Values {
        Getter, Setter, Constructor, None
    }

    @Override
    public FeatureResult applies(Method method) {
        if (method.getName().equals("<init>") || method.getName().equals("<clinit>"))
            this.category = Values.Constructor;
        else if (isGetter(method))
            this.category = Values.Getter;
        else if (isSetter(method))
            this.category = Values.Setter;
        else
            this.category = Values.None;

        this.featureResult.setStringValue(this.category.toString());
        return this.featureResult;
    }

    private Boolean isGetter(Method method) {
        //Check if method is Getter
        if (!method.getSootMethod().getName().startsWith("get") && !method.getSootMethod().getName().startsWith("set"))
            category = Values.None;

        if (method.getSootMethod().getName().length() > 4) {
            String baseName = method.getSootMethod().getName().substring(3);
            String getterName = "get" + baseName;
            String setterName = "set" + baseName;
            try {
                // Find the getter and the setter
                method.getSootClass().getMethodByName(getterName);
                //TODO dynamically check Soot classpath for getter and setter
                SootMethod getter = method.getSootClass().getMethodByName(getterName);
                SootMethod setter = method.getSootClass().getMethodByName(setterName);
                if (getter == null || setter == null) {
                    return false;
                }
                if (!setter.isConcrete() || !getter.isConcrete()) {
                    return false;
                }
                Body bodyGetter;
                bodyGetter = getter.retrieveActiveBody();
                // Find the local that gets returned
                Local returnLocal = null;
                for (Unit u : bodyGetter.getUnits())
                    if (u instanceof ReturnStmt) {
                        ReturnStmt ret = (ReturnStmt) u;
                        if (ret.getOp() instanceof Local) {
                            returnLocal = (Local) ret.getOp();
                            break;
                        }
                    }
                // Find where the local is assigned a value in the code
                List<FieldRef> accessPath = new ArrayList<>();
                Local returnBase = returnLocal;
                while (returnBase != null)
                    for (Unit u : bodyGetter.getUnits()) {
                        if (u instanceof AssignStmt) {
                            AssignStmt assign = (AssignStmt) u;
                            if (assign.getLeftOp().equals(returnBase))
                                if (assign.getRightOp() instanceof InstanceFieldRef) {
                                    InstanceFieldRef ref = (InstanceFieldRef) assign.getRightOp();
                                    accessPath.add(0, ref);
                                    returnBase = (Local) ref.getBase();
                                    break;
                                } else returnBase = null;
                        } else if (u instanceof IdentityStmt) {
                            IdentityStmt id = (IdentityStmt) u;
                            if (id.getLeftOp().equals(returnBase))
                                returnBase = null;
                        }
                    }
                if (accessPath.isEmpty())
                    category = Values.None;
                return true;
            } catch (Exception ex) {
                category = Values.None;
            }
        }
        return false;
    }

    private Boolean isSetter(Method method) {

        //Check if Method is Setter
        if (!method.getSootMethod().getName().startsWith("set")) {
            return true;
        } else if (!method.getSootMethod().isConcrete()) {
            return false;
        } else {
            Set<Value> paramVals = new HashSet<>();
            for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                if (u instanceof IdentityStmt) {
                    IdentityStmt id = (IdentityStmt) u;
                    if (id.getRightOp() instanceof ParameterRef)
                        paramVals.add(id.getLeftOp());
                } else if (u instanceof AssignStmt) {
                    AssignStmt assign = (AssignStmt) u;
                    if (paramVals.contains(assign.getRightOp()))
                        if (assign.getLeftOp() instanceof InstanceFieldRef)
                            return true;
                }
            }
        }
        return false;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.CATEGORICAL;
    }

    @Override
    public String toString() {
        return "MethodType";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>(Stream.of(Values.values()).map(Values::name).collect(Collectors.toList()));
        return this.featureValues;
    }
}
