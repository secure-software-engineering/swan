package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;

/**
 * Common class for all features that have to do with the method name
 *
 * @author Steven Arzt, Lisa Nguyen Quang Do
 */
public class MethodNameContainsFeature extends WeightedFeature implements IFeature {

    private final String contains;
    private final String doesNotContain;

    public MethodNameContainsFeature(String contains) {
        this.contains = contains.toLowerCase();
        this.doesNotContain = null;
    }

    public MethodNameContainsFeature(String contains, String doesNotContain) {
        this.contains = contains.toLowerCase();
        this.doesNotContain = doesNotContain.toLowerCase();
    }

    @Override
    public Type applies(Method method) {
        if (doesNotContain != null
                && method.getName().toLowerCase().contains(doesNotContain))
            return Type.FALSE;
        return (method.getName().toLowerCase().contains(contains) ? Type.TRUE
                : Type.FALSE);
    }

    @Override
    public String toString() {
        String s = "<Method name contains " + this.contains;
        if (doesNotContain != null) s += " but not " + this.doesNotContain;
        return s + ">";
    }

}
