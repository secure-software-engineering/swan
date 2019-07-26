package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.data.Method;

public class MethodClassEndsWithNameFeature extends WeightedFeature implements IFeature {

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
