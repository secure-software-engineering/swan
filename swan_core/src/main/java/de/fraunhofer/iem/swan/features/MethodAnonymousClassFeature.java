package de.fraunhofer.iem.swan.features;

import java.util.regex.Pattern;

import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.data.Method;

/**
 * This feature checks wether the method is part of an anonymous class or not.
 * 
 * @author Siegfried Rasthofer
 *
 */
public class MethodAnonymousClassFeature extends WeightedFeature implements IFeature {

  private final boolean anonymousClass;

  public MethodAnonymousClassFeature(boolean anonymousClass) {
    this.anonymousClass = anonymousClass;
  }

  @Override
  public Type applies(Method method) {
    int index = method.getClassName().lastIndexOf("$");

    if (index != -1) {
      String subclassName = method.getClassName().substring(index + 1);
      return (Pattern.matches("^\\d+$", subclassName) ? Type.TRUE : Type.FALSE);
    }
    return Type.FALSE;
  }

  @Override
  public String toString() {
    return "<Method is part of a"
        + (anonymousClass ? "n anonymous" : " non-anonymous") + " class>";
  }

}
