package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;
import soot.Body;
import soot.SootMethod;

/**
 * Feature that checks if the body of a method contains a specific object.
 * 
 * @author Siegfried Rasthofer
 *
 */
public class MethodBodyContainsObjectFeature extends AbstractSootFeature {
  private final String objectName;

  public MethodBodyContainsObjectFeature(String cp, String objectName) {
    super(cp);
    this.objectName = objectName.trim().toLowerCase();
  }

  @Override
  public Type appliesInternal(Method method) {
    try {
      SootMethod sm = getSootMethod(method);
      if (sm == null) {
        return Type.NOT_SUPPORTED;
      }
      if (!sm.isConcrete()) return Type.NOT_SUPPORTED;

      Body body = null;
      try {
        body = sm.retrieveActiveBody();
      } catch (Exception ex) {
        return Type.NOT_SUPPORTED;
      }

      if (body.toString().toLowerCase().contains(objectName)) return Type.TRUE;

      // for(Local local : sm.getActiveBody().getLocals())
      // if(local.getType().toString().trim().toLowerCase().contains(objectName)){
      // if(objectName.equals("android.location.LocationListener"))
      // System.out.println();
      // return Type.TRUE;
      // }

      return Type.FALSE;
    } catch (Exception ex) {
      System.err.println("Something went wrong:");
      ex.printStackTrace();
      return Type.NOT_SUPPORTED;
    }
  }

  @Override
  public String toString() {
    return "Method-Body contains object '" + this.objectName;
  }
}
