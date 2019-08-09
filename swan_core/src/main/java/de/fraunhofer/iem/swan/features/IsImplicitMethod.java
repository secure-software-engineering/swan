package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.data.Method;

/**
 * Implicit methods (e.g. methods from bytecode for access of private fields)
 *
 * @author Lisa Nguyen Quang Do
 *
 */
public class IsImplicitMethod extends WeightedFeature implements IFeature {

  @Override
  public Type applies(Method method) {
    return (method.getMethodName().contains("$") ? Type.TRUE : Type.FALSE);
  }

  @Override
  public String toString() {
    return "<Implicit method>";
  }

  @Override
  public void setWeight(int weight) {
    super.setWeight(weight);
  }

  @Override
  public int getWeight() {
    return super.getWeight();
  }
}
