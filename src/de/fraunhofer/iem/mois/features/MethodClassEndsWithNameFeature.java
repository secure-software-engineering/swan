package de.fraunhofer.iem.mois.features;

import de.fraunhofer.iem.mois.IFeature;
import de.fraunhofer.iem.mois.data.Method;

public class MethodClassEndsWithNameFeature implements IFeature {

  private final String partOfName;

  public MethodClassEndsWithNameFeature(String partOfName) {
    this.partOfName = partOfName;
  }

  @Override
  public Type applies(Method method) {
    return (method.getClassName().endsWith(partOfName) ? Type.TRUE
        : Type.FALSE);
  }

  @Override
  public String toString() {
    return "<Method is part of class that ends with " + partOfName + ">";
  }
}
