package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;
import soot.Modifier;
import soot.SootClass;

/**
 * This feature checks the modifier of the class where the method is part of.
 *
 * @author Siegfried Rasthofer
 */
public class MethodClassModifierFeature extends AbstractSootFeature {
    public enum ClassModifier {
        ABSTRACT, FINAL, PRIVATE, PROTECTED, PUBLIC, STATIC
    }

    private final ClassModifier classModifier;

    public MethodClassModifierFeature(ClassModifier classModifier) {
        super();
        this.classModifier = classModifier;
    }

    @Override
    public Type appliesInternal(Method method) {
        try {

            if (method.getSootMethod() == null)
                return Type.NOT_SUPPORTED;
            SootClass sClass = method.getSootMethod().getDeclaringClass();
            switch (classModifier) {
                case ABSTRACT:
                    return (sClass.isAbstract() ? Type.TRUE : Type.FALSE);
                case FINAL:
                    return (Modifier.isFinal(sClass.getModifiers()) ? Type.TRUE
                            : Type.FALSE);
                case PRIVATE:
                    return (sClass.isPrivate() ? Type.TRUE : Type.FALSE);
                case PROTECTED:
                    return (sClass.isProtected() ? Type.TRUE : Type.FALSE);
                case PUBLIC:
                    return (sClass.isPublic() ? Type.TRUE : Type.FALSE);
                case STATIC:
                    return (Modifier.isStatic(sClass.getModifiers()) ? Type.TRUE
                            : Type.FALSE);
            }

            throw new Exception("Modifier not declared!");
        } catch (Exception ex) {
            System.err.println("Something went wrong: " + ex.getMessage());
            return Type.NOT_SUPPORTED;
        }
    }

    @Override
    public String toString() {
        return "<Method is part of a " + classModifier.name() + " class>";
    }
}
