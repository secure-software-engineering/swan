package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.data.Method;

public class MethodHasParametersFeature extends WeightedFeature implements IFeature {

  public MethodHasParametersFeature() {}

  @Override
  public Type applies(Method method) {
    return (method.getParameters().size() > 0 ? Type.TRUE : Type.FALSE);
  }

  @Override
  public String toString() {
    return "<Method has parameters>";
  }

}
