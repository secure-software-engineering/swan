package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.data.Method;

/**
 * Return not void
 *
 * @author Lisa Nguyen Quang Do
 *
 */

public class MethodHasReturnTypeFeature extends WeightedFeature implements IFeature {

  public MethodHasReturnTypeFeature() {}

  @Override
  public Type applies(Method method) {
    return (!method.getReturnType().equals("void") ? Type.TRUE : Type.FALSE);
  }

  @Override
  public String toString() {
    return "<Method has parameters>";
  }

}
