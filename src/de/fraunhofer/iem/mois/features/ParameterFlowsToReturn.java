package de.fraunhofer.iem.mois.features;

import java.util.HashSet;
import java.util.Set;

import de.fraunhofer.iem.mois.data.Method;
import soot.SootMethod;
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
 *
 */
public class ParameterFlowsToReturn extends AbstractSootFeature {

  public ParameterFlowsToReturn(String cp) {
    super(cp);
  }

  // TODO: Better analysis.
  @Override
  public Type appliesInternal(Method method) {
    SootMethod sm = getSootMethod(method);

    if (sm == null) {
      System.err.println("Method not declared: " + method);
      return Type.NOT_SUPPORTED;
    }

    // We are only interested in setters
    if (!sm.isConcrete()) return Type.NOT_SUPPORTED;

    try {
      Set<Value> paramVals = new HashSet<Value>();
      for (Unit u : sm.retrieveActiveBody().getUnits()) {
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
          "No return statement in method " + method.getSignature());
    } catch (Exception ex) {
      // System.err.println("Something went wrong:");
      // ex.printStackTrace();
      return Type.NOT_SUPPORTED;
    }
  }

  @Override
  public String toString() {
    return "<Parameter flows to return.>";
  }

}
