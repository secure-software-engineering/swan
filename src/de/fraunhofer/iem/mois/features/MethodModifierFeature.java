package de.fraunhofer.iem.mois.features;

import de.fraunhofer.iem.mois.data.Method;
import soot.SootMethod;

/**
 * Feature checking the method modifiers
 *
 * @author Steven Arzt
 *
 */
public class MethodModifierFeature extends AbstractSootFeature {
  public enum Modifier {
    PUBLIC, PRIVATE, STATIC, PROTECTED, ABSTRACT, FINAL, SYNCHRONIZED, NATIVE
  };

  private final Modifier modifier;

  public MethodModifierFeature(String cp, Modifier modifier) {
    super(cp);
    this.modifier = modifier;
  }

  @Override
  public Type appliesInternal(Method method) {
    SootMethod sm = getSootMethod(method);
    if (sm == null)
      return Type.NOT_SUPPORTED;
    try {
      switch (modifier) {
      case PUBLIC:
        return (sm.isPublic() ? Type.TRUE : Type.FALSE);
      case PRIVATE:
        return (sm.isPrivate() ? Type.TRUE : Type.FALSE);
      case ABSTRACT:
        return (sm.isAbstract() ? Type.TRUE : Type.FALSE);
      case STATIC:
        return (sm.isStatic() ? Type.TRUE : Type.FALSE);
      case PROTECTED:
        return (sm.isProtected() ? Type.TRUE : Type.FALSE);
      case FINAL:
        return (sm.isFinal() ? Type.TRUE : Type.FALSE);
      case SYNCHRONIZED:
        return (sm.isSynchronized() ? Type.TRUE : Type.FALSE);
      case NATIVE:
        return (sm.isNative() ? Type.TRUE : Type.FALSE);
      }
      throw new Exception("Modifier not declared!");
    } catch (Exception ex) {
      System.err.println("Something went wrong: " + ex.getMessage());
      return Type.NOT_SUPPORTED;
    }
  }

  @Override
  public String toString() {
    return "<Method modifier is " + modifier.name() + ">";
  }

}
