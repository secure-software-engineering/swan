package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;

/**
 * Returns if the method is a constructor or not.
 *
 * @author Lisa Nguyen Quang Do
 *
 */
public class MethodIsConstructor extends WeightedFeature implements IFeature {

  @Override
  public Type applies(Method method) {
    if (method.getName().equals("<init>")
        || method.getName().equals("<clinit>"))
      return Type.TRUE;
    return Type.FALSE;
  }

  @Override
  public String toString() {
    return "<Method is constructor>";
  }

}
