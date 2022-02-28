package de.fraunhofer.iem.swan.soot;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.io.dataset.SrmListUtils;
import de.fraunhofer.iem.swan.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Soot {

    private static final Logger logger = LoggerFactory.getLogger(SrmListUtils.class);
    private String classpath;

    public Soot(String... path) {

        this.classpath = Util.buildCP(path);
        configure(classpath);
    }

    /**
     * Configures Soot.
     *
     * @param classpath test and/or train source code classpath
     */
    private void configure(String classpath) {

        Options.v().set_allow_phantom_refs(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_whole_program(true);
        Options.v().set_include_all(true);
        Options.v().set_soot_classpath(classpath);

        Scene.v().loadNecessaryClasses();
    }

    /**
     * Purges list of methods and performs sanity check.
     *
     * @param srmList SRM list to be cleaned.
     */
    public void cleanupList(SrmList srmList) throws IOException {

        prefilterInterfaces(srmList.getMethods());

        Util.createSubclassAnnotations(srmList.getMethods(), classpath);
        Util.sanityCheck(srmList.getMethods(), new HashSet<>());
    }

    /**
     * Returns SootClass for method.
     *
     * @param method Method from test or train set.
     * @return Soot class
     */
    protected SootClass getClass(Method method) {

        SootClass c = Scene.v().forceResolve(method.getClassName(), SootClass.BODIES);

        if (c == null || c.isPhantom()) {
            return null;
        }

        return c;
    }

    /**
     * Returns SootMethod for provided method.
     *
     * @param method Method from test/train data set.
     * @return Soot method
     */
    protected SootMethod getMethod(Method method) {
        return getMethod(method, true);
    }

    /**
     * Returns SootMethod for provided method.
     *
     * @param method          Method from test or train data set.
     * @param lookInHierarchy whether hierarchy should be searched
     * @return Soot method
     */
    protected SootMethod getMethod(Method method, boolean lookInHierarchy) {

        SootClass c = Scene.v().forceResolve(method.getClassName(), SootClass.BODIES);

        if (c == null || c.isPhantom()) {
            return null;
        }

        c.setApplicationClass();
        if (c.isInterface()) return null;

        while (c != null) {
            // Does the current class declare the method we are looking for?
            if (method.getReturnType().isEmpty()) {
                if (c.declaresMethodByName(method.getName()))
                    return c.getMethodByName(method.getName());
            } else {
                if (c.declaresMethod(method.getSubSignature()))
                    return c.getMethod(method.getSubSignature());
            }

            // Continue our search up the class hierarchy
            if (lookInHierarchy && c.hasSuperclass())
                c = c.getSuperclass();
            else
                c = null;
        }
        return null;
    }

    /**
     * Removes all interfaces from the given set of methods and returns the purged
     * set.
     */
    private void prefilterInterfaces(Set<Method> methods) {
        Set<Method> abstractMethods = new HashSet<>();

        for (Method method : methods) {

            SootMethod sootMethod = getMethod(method);

            if (sootMethod == null || sootMethod.isAbstract()) {
                abstractMethods.add(method);
                logger.info("Method purged from list {}", method.getSignature());
            } else {
                method.setSootMethod(sootMethod);
                method.setSootClass(getClass(method));
            }
        }
        methods.removeAll(abstractMethods);
        logger.info("{} abstract methods removed, {} remaining methods", abstractMethods.size(), methods.size());
    }

    public Set<Method> loadMethods(Set<String> testClasses) {
        Set<Method> methods = new HashSet<>();

        for (String className : testClasses) {

            SootClass sc = Scene.v().forceResolve(className, SootClass.BODIES);

            if (sc == null || !testClasses.contains(sc.getName()))
                continue;

            if (!sc.isInterface() && !sc.isPrivate()) {
                for (SootMethod sm : sc.getMethods()) {

                    if (sm.isConcrete()) {
                        // This is done by hand here because of the cases where the
                        // character ' is in the signature. This is not supported by the
                        // current Soot.

                        // TODO: Get Soot to support the character '
                        Method method = SootUtils.convertSootSignature(sm.getSignature());
                        method.setSootMethod(sm);
                        method.setSootClass(sc);
                        methods.add(method);
                    }
                }
            }
        }
        return methods;
    }
}
