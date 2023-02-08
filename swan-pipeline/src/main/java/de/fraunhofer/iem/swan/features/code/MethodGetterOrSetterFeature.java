package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;
import soot.*;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodGetterOrSetterFeature extends WeightedFeature implements IFeatureNew{
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;
    private Values category;

    public enum Values{
        Getter, Setter, None
    }

    public MethodGetterOrSetterFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method, Category category) {
        this.category = Values.None;
        if(isSetter(method)==Values.Setter){
            this.category = Values.Setter;
        } else if (isGetter(method)==Values.Getter) {
            this.category = Values.Getter;
        }else{
            this.category = Values.None;
        }
        this.featureResult.setStringValue(String.valueOf(this.category));
        return this.featureResult;
    }
    
    private Values isGetter(Method method){
        //Check if method is Getter
        Values category = null;
        if (!method.getSootMethod().getName().startsWith("get") && !method.getSootMethod().getName().startsWith("set"))
            category = Values.None;
        String baseName = method.getSootMethod().getName().substring(3);
        String getterName = "get" + baseName;
        String setterName = "set" + baseName;
        try {
            // Find the getter and the setter
            method.getSootClass().getMethodByName(getterName);
            //TODO dynamically check Soot classpath for getter and setter
            SootMethod getter = method.getSootClass().getMethodByName(getterName);
            SootMethod setter = method.getSootClass().getMethodByName(setterName);
            if (getter == null || setter == null){
                category = Values.None;
            }
            if (!setter.isConcrete() || !getter.isConcrete()){
                category = Values.None;
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
            this.category = Values.Getter;
        } catch (Exception ex) {
            category = Values.None;
        }
        return category;
    }
    
    private Values isSetter(Method method){
        //Check if Method is Setter
        if (!method.getSootMethod().getName().startsWith("set")) {
            this.category = Values.Setter;
        } else if (!method.getSootMethod().isConcrete()) {
            this.category = Values.None;
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
                            this.category = Values.Setter;
                }
            }
        }
        return this.category;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.CATEGORICAL;
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>(Stream.of(Values.values()).map(Values::name).collect(Collectors.toList()));
        return this.featureValues;
    }

    @Override
    public String toString(){
        return "MethodGetterOrSetter";
    }
}
