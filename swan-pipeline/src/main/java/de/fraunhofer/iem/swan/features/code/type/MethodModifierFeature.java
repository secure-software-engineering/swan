package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;
import soot.SootMethod;

/**
 * Feature checking the method modifiers
 *
 * @author Steven Arzt
 */
public class MethodModifierFeature extends AbstractSootFeature {
    public enum Modifier {
        PUBLIC, PRIVATE, STATIC, PROTECTED, ABSTRACT, FINAL, SYNCHRONIZED, NATIVE
    }

    private final Modifier modifier;

    public MethodModifierFeature(Modifier modifier) {
        super();
        this.modifier = modifier;
    }

    @Override
    public Type appliesInternal(Method method) {

        if (method.getSootMethod() == null)
            return Type.NOT_SUPPORTED;
        try {
            switch (modifier) {
                case PUBLIC:
                    return (method.getSootMethod().isPublic() ? Type.TRUE : Type.FALSE);
                case PRIVATE:
                    return (method.getSootMethod().isPrivate() ? Type.TRUE : Type.FALSE);
                case ABSTRACT:
                    return (method.getSootMethod().isAbstract() ? Type.TRUE : Type.FALSE);
                case STATIC:
                    return (method.getSootMethod().isStatic() ? Type.TRUE : Type.FALSE);
                case PROTECTED:
                    return (method.getSootMethod().isProtected() ? Type.TRUE : Type.FALSE);
                case FINAL:
                    return (method.getSootMethod().isFinal() ? Type.TRUE : Type.FALSE);
                case SYNCHRONIZED:
                    return (method.getSootMethod().isSynchronized() ? Type.TRUE : Type.FALSE);
                case NATIVE:
                    return (method.getSootMethod().isNative() ? Type.TRUE : Type.FALSE);
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
