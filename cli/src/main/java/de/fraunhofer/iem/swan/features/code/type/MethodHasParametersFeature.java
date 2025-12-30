package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.srm.dataset.Method;

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
