package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;
import soot.Body;

/**
 * Feature that checks if the body of a method contains a specific object.
 *
 * @author Siegfried Rasthofer
 */
public class MethodBodyContainsObjectFeature extends AbstractSootFeature {
    private final String objectName;

    public MethodBodyContainsObjectFeature(String objectName) {
        super();
        this.objectName = objectName.trim().toLowerCase();
    }

    @Override
    public Type appliesInternal(Method method) {
        try {

            if (method.getSootMethod() == null) {
                return Type.FALSE;
            }
            if (!method.getSootMethod().isConcrete()) return Type.FALSE;

            Body body;
            try {
                body = method.getSootMethod().retrieveActiveBody();
            } catch (Exception ex) {
                return Type.FALSE;
            }

            if (body.toString().toLowerCase().contains(objectName)) return Type.TRUE;

            // for(Local local : sm.getActiveBody().getLocals())
            // if(local.getType().toString().trim().toLowerCase().contains(objectName)){
            // if(objectName.equals("android.location.LocationListener"))
            // System.out.println();
            // return Type.TRUE;
            // }

            return Type.FALSE;
        } catch (Exception ex) {
            System.err.println("Something went wrong:");
            ex.printStackTrace();
            return Type.FALSE;
        }
    }

    @Override
    public String toString() {
        return "Method-Body contains object '" + this.objectName;
    }
}
