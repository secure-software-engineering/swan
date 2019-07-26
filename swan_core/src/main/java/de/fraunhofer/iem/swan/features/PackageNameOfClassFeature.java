package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.data.Method;

/**
 * This feature checks the name of the package
 * 
 * @author Siegfried Rasthofer
 */
public class PackageNameOfClassFeature extends WeightedFeature implements IFeature {
  private final String packageNameOfClass;

  public PackageNameOfClassFeature(String packageNameOfClass, float weight) {
    this.packageNameOfClass = packageNameOfClass;
  }

  @Override
  public Type applies(Method method) {
    String otherPackageNameOfClass = method.getClassName().substring(0,
        method.getClassName().lastIndexOf("."));

    return (otherPackageNameOfClass.equals(packageNameOfClass) ? Type.TRUE
        : Type.FALSE);
  }

  @Override
  public String toString() {
    return "<Package path of method class-name is: " + packageNameOfClass + ">";
  }
}
