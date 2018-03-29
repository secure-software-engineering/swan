package de.fraunhofer.iem.mois.features;

import de.fraunhofer.iem.mois.IFeature;
import de.fraunhofer.iem.mois.data.Method;

/**
 * Return not void
 *
 * @author Lisa Nguyen Quang Do
 *
 */

public class MethodHasReturnTypeFeature implements IFeature {

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
