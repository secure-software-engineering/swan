package de.fraunhofer.iem.swan.io.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.util.Util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents SRM JSON file.
 */
public class SrmList {

    private Set<Method> methods;
    private String version;
    @JsonIgnore
    private Set<String> testClasses;

    public SrmList() {
        testClasses = new HashSet<>();
    }

    public SrmList(String sourceFileDir) {
        try {
            testClasses = Util.getAllClassesFromDirectory(sourceFileDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        methods = new HashSet<>();
    }

    public void load(final Set<Method> trainingSet) {

        Util.createSubclassAnnotations(methods, "classpath");
        methods = Util.sanityCheck(methods, trainingSet);
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

    public Set<String> getTestClasses() {
        return testClasses;
    }

    public void setTestClasses(Set<String> testClasses) {
        this.testClasses = testClasses;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void removeUnclassifiedMethods() {

        methods = methods.stream().filter(m -> m.getAllCategories().size() > 0)
                .collect(Collectors.toSet());
    }

    public void addMethods(Set<Method> m){
        if(m!=null && !m.isEmpty()){
            methods.addAll(m);
        }
    }
}