package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;

/**
 * Check if an invocation to a method of a certain class is made.
 *
 * @author Lisa Nguyen Quang Do
 *
 */
public class MethodInvocationClassName extends AbstractSootFeature {

  private final String className;

  public MethodInvocationClassName(String cp, String className) {
    super(cp);
    this.className = className;
  }

  @Override
  public Type appliesInternal(Method method) {
    SootMethod sm = getSootMethod(method);

    if (sm == null) {
      return Type.NOT_SUPPORTED;
    }

    // We are only interested in setters
    if (!sm.isConcrete()) return Type.NOT_SUPPORTED;

    try {
      for (Unit u : sm.retrieveActiveBody().getUnits()) {
        // Check for invocations
        if (u instanceof Stmt) {
          Stmt stmt = (Stmt) u;
          if (stmt.containsInvokeExpr())
            if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
            InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
            if (iinv.getMethod().getDeclaringClass().getName().contains(className)) return Type.TRUE;
            }
        }
      }
      return Type.FALSE;
    } catch (Exception ex) {
      // System.err.println("Something went wrong:");
      // ex.printStackTrace();
      return Type.NOT_SUPPORTED;
    }
  }

  @Override
  public String toString() {
    return "<Method from class " + className + " invoked>";
  }

}
