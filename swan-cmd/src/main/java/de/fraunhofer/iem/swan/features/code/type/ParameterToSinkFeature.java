package de.fraunhofer.iem.swan.features.code.type;

import java.util.HashSet;
import java.util.Set;

import de.fraunhofer.iem.swan.data.Method;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;

/**
 * Simpler version of ParameterInCallFeature for sinks.
 *
 * @author Lisa Nguyen Quang Do
 */
public class ParameterToSinkFeature extends AbstractSootFeature {

    private final String sinkMethodName;

    public ParameterToSinkFeature(String sinkMethodName) {
        super();
        this.sinkMethodName = sinkMethodName;
    }

    // TODO: Better analysis.
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

                if (u instanceof AssignStmt) {
                    Value leftOp = ((AssignStmt) u).getLeftOp();
                    Value rightOp = ((AssignStmt) u).getRightOp();
                    if (paramVals.contains(leftOp)) paramVals.remove(leftOp);
                    if (paramVals.contains(rightOp)) {
                        paramVals.add(leftOp);
                    }
                }

                // Check for invocations
                if (((Stmt) u).containsInvokeExpr()) {
                    InvokeExpr invokeExpr = ((Stmt) u).getInvokeExpr();
                    if (invokeExpr.getMethod().getName().toLowerCase()
                            .contains(sinkMethodName.toLowerCase())) {
                        for (Value arg : invokeExpr.getArgs())
                            if (paramVals.contains(arg)) return Type.TRUE;
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
        return "<Parameter to sink method " + sinkMethodName + ">";
    }

}
