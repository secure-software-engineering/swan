package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;
import soot.SootMethod;

/**
 * Feature that checks whether the current method is the run method of a thread
 *
 * @author Steven Arzt, Siegfried Rasthofer
 */
public class IsThreadRunFeature extends AbstractSootFeature {

    public IsThreadRunFeature() {
        super();
    }

    @Override
    public Type appliesInternal(Method method) {
        if (!method.getName().equals("run"))
            return Type.FALSE;

        if (method.getSootMethod() == null)
            return Type.NOT_SUPPORTED;
        try {
            if (this.isOfType(method.getSootMethod().getDeclaringClass().getType(), "java.lang.Runnable"))
                return Type.TRUE;
            else return Type.FALSE;
        } catch (Exception ex) {
            System.err.println("Something went wrong:");
            ex.printStackTrace();
            return Type.NOT_SUPPORTED;
        }
    }

    @Override
    public String toString() {
        return "<Method is thread runner>";
    }
}
