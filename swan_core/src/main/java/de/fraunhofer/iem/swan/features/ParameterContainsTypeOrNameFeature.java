package de.fraunhofer.iem.swan.features;

import java.util.List;

import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.data.Method;

/**
 * Common class for all features that have to do with the method name
 *
 * @author Steven Arzt
 *
 */
public class ParameterContainsTypeOrNameFeature extends WeightedFeature implements IFeature {

  private final String insideName;

  public ParameterContainsTypeOrNameFeature(String insideName) {
    this.insideName = insideName.toLowerCase();
  }

  @Override
  public Type applies(Method method) {
    List<String> paramList = method.getParameters();
    for (String param : paramList) {
      if (param.toLowerCase().contains(this.insideName)) return Type.TRUE;
    }
    return Type.FALSE;
  }

  @Override
  public String toString() {
    return "<Parameter type contains " + this.insideName + ">";
  }

}
