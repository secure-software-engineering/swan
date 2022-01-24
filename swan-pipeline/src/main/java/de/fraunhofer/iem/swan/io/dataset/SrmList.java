package de.fraunhofer.iem.swan.io.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iem.swan.data.Method;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents SRM JSON file.
 */
public class SrmList {

    private Set<Method> methods;

    public SrmList() {
    }

    public SrmList(Set<Method> methodList) {
        this.methods = methodList;
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public void setMethods(Set<Method> methods) {
        this.methods = methods;
    }

    public void removeUnclassifiedMethods() {

        methods = methods.stream().filter(m -> m.getAllCategories().size() > 0)
                .collect(Collectors.toSet());
    }
}