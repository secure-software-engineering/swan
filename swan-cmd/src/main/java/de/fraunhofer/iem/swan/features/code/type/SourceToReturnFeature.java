package de.fraunhofer.iem.swan.features.code.type;

import java.util.HashSet;
import java.util.Set;

import de.fraunhofer.iem.swan.data.Method;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;

/**
 * Simpler version of ParameterInCallFeature for sources.
 *
 * @author Lisa Nguyen Quang Do
 */
public class SourceToReturnFeature extends AbstractSootFeature {

    private final String sourceMethodName;

    public SourceToReturnFeature(String sourceMethodName) {
        super();
        this.sourceMethodName = sourceMethodName;
    }

    // TODO: Better analysis.
    @Override
    public Type appliesInternal(Method method) {

        if (method.getSootMethod() == null) {
            return Type.FALSE;
        }

        // We are only interested in setters
        if (!method.getSootMethod().isConcrete()) return Type.FALSE;

        try {
            Set<Value> paramVals = new HashSet<>();

            for (Unit u : method.getSootMethod().retrieveActiveBody().getUnits()) {
                // Check for invocations
                if (((Stmt) u).containsInvokeExpr()) {
                    InvokeExpr invokeExpr = ((Stmt) u).getInvokeExpr();
                    Value leftOp = null;
                    if (u instanceof AssignStmt) leftOp = ((AssignStmt) u).getLeftOp();
                    if (leftOp != null) paramVals.add(leftOp);
                    // TODO: Add arguments as well? Not sure.
                    if (invokeExpr.getMethod().getName().toLowerCase()
                            .contains(sourceMethodName.toLowerCase())) {
                        paramVals.addAll(invokeExpr.getArgs());
                    }
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
                if (u instanceof ReturnStmt) {
                    ReturnStmt stmt = (ReturnStmt) u;
                    return paramVals.contains(stmt.getOp()) ? Type.TRUE : Type.FALSE;
                }
            }
            throw new RuntimeException(
                    "No return statement in method " + method.getSootSignature());
        } catch (Exception ex) {
            return Type.FALSE;
        }
    }

    @Override
    public String toString() {
        return "<Source method to return " + sourceMethodName + ">";
    }
}
