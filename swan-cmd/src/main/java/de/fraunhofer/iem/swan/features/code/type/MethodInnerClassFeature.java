package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;

public class MethodInnerClassFeature extends AbstractSootFeature {

    private final boolean innerClass;

    public MethodInnerClassFeature(boolean innerClass) {
        super();
        this.innerClass = innerClass;
    }

    @Override
    public Type appliesInternal(Method method) {

        if (method.getSootMethod() == null)
            return Type.NOT_SUPPORTED;
        try {
            if (method.getSootMethod().getDeclaringClass().hasOuterClass() && innerClass)
                return Type.TRUE;
            else if (innerClass && !method.getSootMethod().getDeclaringClass().hasOuterClass())
                return Type.FALSE;
            else if (!innerClass && method.getSootMethod().getDeclaringClass().hasOuterClass())
                return Type.FALSE;
            else return Type.TRUE;
        } catch (Exception ex) {
            System.err.println("Something went wrong: " + ex.getMessage());
            return Type.NOT_SUPPORTED;
        }
    }

    public String toString() {
        return "<Method is " + (innerClass ? "" : "not ") + "part of inner class>";
    }
}
