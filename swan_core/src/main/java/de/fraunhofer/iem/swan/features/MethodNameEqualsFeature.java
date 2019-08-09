package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.data.Method;

/**
 * Method name.
 *
 * @author Lisa Nguyen Quang Do
 */
public class MethodNameEqualsFeature extends WeightedFeature implements IFeature {

  private final String contains;

  public MethodNameEqualsFeature(String contains) {
    this.contains = contains.toLowerCase();
  }

  @Override
  public Type applies(Method method) {
    return (method.getMethodName().toLowerCase().equals(contains) ? Type.TRUE
        : Type.FALSE);
  }

  @Override
  public String toString() {
    return "<Method name contains " + this.contains + ">";
  }

}
