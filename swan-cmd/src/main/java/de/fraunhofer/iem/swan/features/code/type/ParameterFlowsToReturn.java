package de.fraunhofer.iem.swan.features.code.type;

import java.util.HashSet;
import java.util.Set;

import de.fraunhofer.iem.swan.data.Method;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;

/**
 * Feature that checks whether a parameter flows to the sink.
 *
 * @author Steven Arzt, Siegfried Rasthofer
 */
public class ParameterFlowsToReturn extends AbstractSootFeature {

    public ParameterFlowsToReturn() {
        super();
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
        return "<Parameter flows to return.>";
    }
}
