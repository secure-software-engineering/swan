package de.fraunhofer.iem.swan.features.code.type;

import de.fraunhofer.iem.swan.data.Method;

/**
 * Feature which checks whether the return type of a method contains a given "key" in the fully qualified name.
 *
 * @author Goran Piskachev
 */
public class ReturnTypeContainsNameFeature extends AbstractSootFeature {
    private final String key;

    public ReturnTypeContainsNameFeature(String key) {
        super();
        this.key = key;
    }

    @Override
    public Type appliesInternal(Method method) {
        if (method.getReturnType().toLowerCase().contains(this.key.toLowerCase()))
            return Type.TRUE;
        else return Type.FALSE;
    }

    @Override
    public String toString() {
        return "<Return type is " + this.key + ">";
    }
}
