package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.data.Method;
import soot.SootMethod;

/**
 * Feature that checks whether the current method is the run method of a thread
 *
 * @author Steven Arzt, Siegfried Rasthofer
 *
 */
public class IsThreadRunFeature extends AbstractSootFeature {

  public IsThreadRunFeature(String cp) {
    super(cp);
  }

  @Override
  public Type appliesInternal(Method method) {
    if (!method.getMethodName().equals("run"))
      return Type.FALSE;

    SootMethod sm = getSootMethod(method);
    if (sm == null)
      return Type.NOT_SUPPORTED;
    try {
      if (this.isOfType(sm.getDeclaringClass().getType(), "java.lang.Runnable"))
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
