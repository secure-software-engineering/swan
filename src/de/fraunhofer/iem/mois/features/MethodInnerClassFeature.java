package de.fraunhofer.iem.mois.features;

import de.fraunhofer.iem.mois.data.Method;
import soot.SootMethod;

public class MethodInnerClassFeature extends AbstractSootFeature {

  private final boolean innerClass;

  public MethodInnerClassFeature(String cp, boolean innerClass) {
    super(cp);
    this.innerClass = innerClass;
  }

  @Override
  public Type appliesInternal(Method method) {
    SootMethod sm = getSootMethod(method);
    if (sm == null)
      return Type.NOT_SUPPORTED;
    try {
      if (sm.getDeclaringClass().hasOuterClass() && innerClass)
        return Type.TRUE;
      else if (innerClass && !sm.getDeclaringClass().hasOuterClass())
        return Type.FALSE;
      else if (!innerClass && sm.getDeclaringClass().hasOuterClass())
        return Type.FALSE;
      else return Type.TRUE;
    } catch (Exception ex) {
      System.err.println("Something went wrong: " + ex.getMessage());
      return Type.NOT_SUPPORTED;
    }
  }

  public String toString() {
    return "<Method is " + (innerClass ? "" : "not ") + "part of inner class>";
  }

}
