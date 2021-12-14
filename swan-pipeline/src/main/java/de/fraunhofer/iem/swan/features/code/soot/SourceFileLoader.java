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

    private final String classpath;
    private Set<Method> methods = new HashSet<>();
    Set<String> testClasses;

    public SourceFileLoader(String sourceFileDir) throws IOException {

        testClasses = Util.getAllClassesFromDirectory(sourceFileDir);
        this.classpath = Util.buildCP(sourceFileDir);
    }

    public void load(final Set<Method> trainingSet) {
        loadMethodsFromTestLib();
        Util.createSubclassAnnotations(methods, classpath);
        methods = Util.sanityCheck(methods, trainingSet);
    }

    public void loadMethodsFromTestLib() {

        new AbstractSootFeature(classpath) {

            @Override
            public Type appliesInternal(Method method) {

                for (String className : testClasses) {
                    SootClass sc = Scene.v().forceResolve(className, SootClass.HIERARCHY);

                    if (sc == null || !testClasses.contains(sc.getName()))
                        continue;

                    if (!sc.isInterface() && !sc.isPrivate())
                        for (SootMethod sm : sc.getMethods()) {
                            if (sm.isConcrete()) {
                                // This is done by hand here because of the cases where the
                                // character ' is in the signature. This is not supported by the
                                // current Soot.
                                // TODO: Get Soot to support the character '
                                methods.add(SootUtils.convertSootSignature(sm.getSignature()));
                            }
                        }
                }
                return Type.NOT_SUPPORTED;
            }

        }.applies(new Method("a", "void", "x.y"));
    }

    public Set<Method> getMethods() {
        return methods;
    }

    public String getClasspath() {
        return classpath;
    }
}
