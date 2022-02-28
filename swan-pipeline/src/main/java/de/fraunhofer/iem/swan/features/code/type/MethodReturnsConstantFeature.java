package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Constant;
import soot.jimple.ReturnStmt;

/**
 * Feature that checks whether the current method returns a constant value
 *
 * @author Steven Arzt, Siegfried Rasthofer
 */
public class MethodReturnsConstantFeature extends AbstractSootFeature {

    public MethodReturnsConstantFeature() {
        super();
    }

    @Override
    public Type appliesInternal(Method method) {

        if (method.getSootMethod() == null || !method.getSootMethod().isConcrete())
            return Type.NOT_SUPPORTED;
        try {
            Body body;
            try {
                body = method.getSootMethod().retrieveActiveBody();
            } catch (Exception ex) {
                return Type.NOT_SUPPORTED;
            }

            for (Unit u : body.getUnits())
                if (u instanceof ReturnStmt) {
                    ReturnStmt ret = (ReturnStmt) u;
                    if (ret.getOp() instanceof Constant)
                        return Type.TRUE;
                }
            return Type.FALSE;
        } catch (Exception ex) {
            System.err.println("Something went wrong:");
            ex.printStackTrace();
            return Type.NOT_SUPPORTED;
        }
    }

    @Override
    public String toString() {
        return "<Method returns constant>";
    }
}
