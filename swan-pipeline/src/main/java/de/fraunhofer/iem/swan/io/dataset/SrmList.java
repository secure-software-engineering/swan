package de.fraunhofer.iem.swan.io.dataset;

import de.fraunhofer.iem.swan.data.Method;
import java.util.Set;

/**
 * Represents SRM JSON file.
 */
public class SrmList {

    private Set<Method> methods;
    private String classpath;

    public SrmList() {
    }

    public SrmList(Set<Method> methodList) {
        this.methods = methodList;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public void setMethods(Set<Method> methods) {
        this.methods = methods;
    }
}