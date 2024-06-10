package de.fraunhofer.iem.swan.features.code.type;

import java.util.HashSet;
import java.util.Set;

import de.fraunhofer.iem.swan.data.Method;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;

/**
 * Feature that checks whether a method starting with a specific string is
 * invoked on one of the parameters
 *
 * @author Steven Arzt, Siegfried Rasthofer
 */
public class MethodInvocationOnParameterFeature extends AbstractSootFeature {

    private final String methodName;

    public MethodInvocationOnParameterFeature(String methodName) {
        super();
        this.methodName = methodName;
    }

    @Override
    public Type appliesInternal(Method method) {

        if (method.getSootMethod() == null) {
            return Type.NOT_SUPPORTED;
        }

        // We are only interested in setters
        if (!method.getSootMethod().isConcrete()) return Type.NOT_SUPPORTED;

        try {
            Set<Value> paramVals = new HashSet<>();
            for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                // Collect the parameters
                if (u instanceof IdentityStmt) {
                    IdentityStmt id = (IdentityStmt) u;
                    if (id.getRightOp() instanceof ParameterRef)
                        paramVals.add(id.getLeftOp());
                }

                // Check for invocations
                if (u instanceof Stmt) {
                    Stmt stmt = (Stmt) u;
                    if (stmt.containsInvokeExpr())
                        if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                            InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
                            if (paramVals.contains(iinv.getBase()))
                                if (iinv.getMethod().getName().startsWith(methodName)) return Type.TRUE;
                        }
                }
            }
            return Type.FALSE;
        } catch (Exception ex) {
            return Type.NOT_SUPPORTED;
        }
    }

    @Override
    public String toString() {
        return "<Method " + methodName + "invoked on parameter object>";
    }
}
