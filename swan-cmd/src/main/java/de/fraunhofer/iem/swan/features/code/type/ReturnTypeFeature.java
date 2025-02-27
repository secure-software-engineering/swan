package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;
import soot.SootMethod;

/**
 * Feature which checks the return type of a method
 *
 * @author Steven Arzt, Siegfried Rasthofer
 */
public class ReturnTypeFeature extends AbstractSootFeature {

    private final String returnType;

    public ReturnTypeFeature(String returnType) {
        super();
        this.returnType = returnType;
    }

    @Override
    public Type appliesInternal(Method method) {
        if (method.getReturnType().equals(this.returnType))
            return Type.TRUE;

        if (method.getSootMethod() == null)
            return Type.FALSE;
        try {
            if (this.isOfType(method.getSootMethod().getReturnType(), this.returnType))
                return Type.TRUE;
            else return Type.FALSE;
        } catch (Exception ex) {
            System.err.println("Something went wrong:");
            ex.printStackTrace();
            return Type.FALSE;
        }
    }

    @Override
    public String toString() {
        return "<Return type is " + this.returnType + ">";
    }
}
