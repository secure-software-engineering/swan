package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.data.Method;

public class MethodClassContainsNameFeature extends WeightedFeature implements IFeature {

  private final String partOfName;

  public MethodClassContainsNameFeature(String partOfName) {
    this.partOfName = partOfName.toLowerCase();
  }

  @Override
  public Type applies(Method method) {
    return (method.getClassName().toLowerCase().contains(partOfName) ? Type.TRUE
        : Type.FALSE);
  }

  @Override
  public String toString() {
    return "<Method is part of class that contains the name " + partOfName
        + ">";
  }
}
