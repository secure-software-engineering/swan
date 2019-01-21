package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.data.Method;
import soot.SootMethod;

/**
 * Feature which checks the return type of a method
 *
 * @author Steven Arzt, Siegfried Rasthofer
 *
 */
public class ReturnTypeFeature extends AbstractSootFeature {

  private final String returnType;

  public ReturnTypeFeature(String cp, String returnType) {
    super(cp);
    this.returnType = returnType;
  }

  @Override
  public Type appliesInternal(Method method) {
    if (method.getReturnType().equals(this.returnType))
      return Type.TRUE;

    SootMethod sm = getSootMethod(method);
    if (sm == null)
      return Type.NOT_SUPPORTED;
    try {
      if (this.isOfType(sm.getReturnType(), this.returnType))
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
    return "<Return type is " + this.returnType + ">";
  }

}
