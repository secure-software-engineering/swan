package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;

/**
 * Check if an invocation to a certain method is made.
 *
 * @author Lisa Nguyen Quang Do
 */
public class MethodInvocationName extends AbstractSootFeature {

    private final String methodName;

    public MethodInvocationName(String methodName) {
        super();
        this.methodName = methodName;
    }

    @Override
    public Type appliesInternal(Method method) {

        if (method.getSootMethod() == null) {
            return Type.FALSE;
        }

        if (!method.getSootMethod().isConcrete()) return Type.FALSE;

        try {
            for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                // Check for invocations
                if (u instanceof Stmt) {
                    Stmt stmt = (Stmt) u;
                    if (stmt.containsInvokeExpr())
                        if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                            InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
                            if (iinv.getMethod().getName().contains(methodName)) return Type.TRUE;
                        }
                }
            }
            return Type.FALSE;
        } catch (Exception ex) {
            return Type.FALSE;
        }
    }

    @Override
    public String toString() {
        return "<Method " + methodName + " invoked>";
    }
}
