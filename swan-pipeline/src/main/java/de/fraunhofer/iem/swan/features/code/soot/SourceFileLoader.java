package de.fraunhofer.iem.swan.features.code.soot;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.AbstractSootFeature;
import de.fraunhofer.iem.swan.util.SootUtils;
import de.fraunhofer.iem.swan.util.Util;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SourceFileLoader {

    private Set<Method> methods;
    private Set<String> testClasses;

    public SourceFileLoader(String sourceFileDir) throws IOException {

        testClasses = Util.getAllClassesFromDirectory(sourceFileDir);
        methods = new HashSet<>();
    }

    public void load(final Set<Method> trainingSet) {

        Util.createSubclassAnnotations(methods, "classpath");
        methods = Util.sanityCheck(methods, trainingSet);
    }


    public Set<String> getTestClasses() {
        return testClasses;
    }

    public void setTestClasses(Set<String> testClasses) {
        this.testClasses = testClasses;
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public void setMethods(Set<Method> methods) {
        this.methods = methods;
    }
}
