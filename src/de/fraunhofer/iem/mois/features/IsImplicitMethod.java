package de.fraunhofer.iem.mois.features;

import de.fraunhofer.iem.mois.IFeature;
import de.fraunhofer.iem.mois.data.Method;

/**
 * Implicit methods
 *
 * @author Lisa Nguyen Quang Do
 *
 */
public class IsImplicitMethod implements IFeature {

  @Override
  public Type applies(Method method) {
    return (method.getMethodName().contains("$") ? Type.TRUE : Type.FALSE);
  }

  @Override
  public String toString() {
    return "<Implicit method>";
  }

}
