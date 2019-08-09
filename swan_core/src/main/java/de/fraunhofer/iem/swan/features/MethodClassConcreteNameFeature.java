package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.data.Method;

public class MethodClassConcreteNameFeature extends WeightedFeature implements IFeature {

  private final String className;

  public MethodClassConcreteNameFeature(String className) {
    this.className = className;
  }

  @Override
  public Type applies(Method method) {
    return (method.getClassName().equals(className) ? Type.TRUE : Type.FALSE);
  }

  @Override
  public String toString() {
    return "<Method is part of class " + className + ">";
  }

}
