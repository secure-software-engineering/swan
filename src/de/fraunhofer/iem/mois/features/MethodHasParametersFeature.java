package de.fraunhofer.iem.mois.features;

import de.fraunhofer.iem.mois.IFeature;
import de.fraunhofer.iem.mois.data.Method;

public class MethodHasParametersFeature implements IFeature {

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
