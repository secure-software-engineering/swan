package de.fraunhofer.iem.swan.features.code.soot;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.io.dataset.SrmListUtils;
import de.fraunhofer.iem.swan.util.SootUtils;
import de.fraunhofer.iem.swan.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InitializeSoot {

    private static boolean SOOT_INITIALIZED = false;
    private static final Logger logger = LoggerFactory.getLogger(SrmListUtils.class);
    private String classpath;

    public InitializeSoot(String... path) {

        this.classpath = Util.buildCP(path);
        initializeSoot(classpath);
    }

    private String[] buildArgs(String path) {
        String[] result = {
                "-w",
                "-no-bodies-for-excluded",
                "-include-all",
                "-p",
                "cg.spark",
                "on",
                "-cp",
                path,
                "-p",
                "jb",
                "use-original-names:true",
                "-f",
                "n",
                //do not merge variables (causes problems with PointsToSets)
                "-p",
                "jb.ulp",
                "off"
        };

        return result;
    }

    public void initialize(String path) {
        String[] args = buildArgs(path);

        Options.v().set_allow_phantom_refs(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_output_format(Options.output_format_none);
        Options.v().parse(args);

        Options.v().set_whole_program(true);
        Scene.v().addBasicClass(Object.class.getName());
        Scene.v().loadNecessaryClasses();
    }


    public Set<Method> cleanupList(Set<Method> methods) throws IOException {

        Set<Method> purgedMethods = prefilterInterfaces(methods);

        Util.createSubclassAnnotations(purgedMethods, classpath);
        Util.sanityCheck(purgedMethods, new HashSet<>());

        return purgedMethods;
    }

    public void runSoot(String path) {

    }


    private void initializeSoot(String cp) {
        if (SOOT_INITIALIZED)
            return;

        G.reset();

        Options.v().set_allow_phantom_refs(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_whole_program(true);
        Options.v().set_include_all(true);
        Options.v().set_soot_classpath(cp);

        Scene.v().loadNecessaryClasses();
        SOOT_INITIALIZED = true;
    }

    protected SootClass getSootClass(Method method) {

        SootClass c = Scene.v().forceResolve(method.getClassName(), SootClass.BODIES);

        if (c == null || c.isPhantom()) {
            return null;
        }

        return c;
    }

    protected SootMethod getSootMethod(Method method) {
        return getSootMethod(method, true);
    }

    protected SootMethod getSootMethod(Method method, boolean lookInHierarchy) {

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
                //System.out.println(method.getSubSignature());
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
    private Set<Method> prefilterInterfaces(Set<Method> methods) {
        Set<Method> purgedMethods = new HashSet<>();

        for (Method method : methods) {

            SootMethod sootMethod = getSootMethod(method);
            method.setSootMethod(sootMethod);
            method.setSootClass(getSootClass(method));

            if (sootMethod == null)
                continue;

            if (sootMethod.isAbstract())
                logger.info("Method purged from list {}", method.getSignature());
            else
                purgedMethods.add(method);
        }

        logger.info("{} methods purged down to {}", methods.size(), purgedMethods.size());
        return purgedMethods;
    }

    public Set<Method> loadMethodsFromTestLib(Set<String> testClasses) {
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
