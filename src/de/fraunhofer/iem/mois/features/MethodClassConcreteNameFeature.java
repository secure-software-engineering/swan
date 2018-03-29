package de.fraunhofer.iem.mois.features;

import de.fraunhofer.iem.mois.IFeature;
import de.fraunhofer.iem.mois.data.Method;

public class MethodClassConcreteNameFeature implements IFeature {

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
