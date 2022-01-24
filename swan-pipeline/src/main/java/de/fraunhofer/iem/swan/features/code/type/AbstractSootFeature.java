package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;
import soot.Hierarchy;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSootFeature extends WeightedFeature implements IFeature {


    private Map<Method, Type> resultCache = new HashMap<>();

    public AbstractSootFeature() {
    }

    protected boolean isOfType(soot.Type type, String typeName) {
        if (!(type instanceof RefType)) return false;

        // Check for a direct match
        RefType refType = (RefType) type;
        if (refType.getSootClass().getName().equals(typeName)) return true;

        // interface treatment
        if (refType.getSootClass().isInterface()) return false;

        // class treatment
        Hierarchy h = Scene.v().getActiveHierarchy();
        List<SootClass> ancestors = h.getSuperclassesOf(refType.getSootClass());
        for (SootClass ancestor : ancestors) {
            if (ancestor.getName().equals(typeName)) return true;
            for (SootClass sc : ancestor.getInterfaces())
                if (sc.getName().equals(typeName)) return true;
        }
        return false;
    }

    @Override
    public Type applies(Method method) {
        if (this.resultCache.containsKey(method))
            return this.resultCache.get(method);
        else {
            Type tp = this.appliesInternal(method);
            this.resultCache.put(method, tp);
            return tp;
        }
    }

    public abstract Type appliesInternal(Method method);

}
