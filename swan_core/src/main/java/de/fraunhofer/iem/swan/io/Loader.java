package de.fraunhofer.iem.swan.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.type.AbstractSootFeature;
import de.fraunhofer.iem.swan.util.Util;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public class Loader {

  private final String testCp;
  private Set<Method> methods = new HashSet<Method>();

  public Loader(String testCp) {
    this.testCp = testCp;
  }

  public Set<Method> methods() {
    return methods;
  }

  public void resetMethods() {
    for (Method m : methods) {
      if (m.getCategoryClassified() != null
          && !m.getCategoryClassified().equals(Category.NONE))
        m.addCategoryClassified(m.getCategoryClassified());
      m.setCategoryClassified(null);
    }
  }

  public void loadTestSet(final Set<String> testClasses,
      final Set<Method> trainingSet) {
    loadMethodsFromTestLib(testClasses);
    Util.createSubclassAnnotations(methods, testCp);
    methods = Util.sanityCheck(methods, trainingSet);
    Util.printStatistics("Test set complete", methods);
  }

  public void loadMethodsFromTestLib(final Set<String> testClasses) {
    int methodCount = methods.size();

    new AbstractSootFeature(testCp) {

      @Override
      public Type appliesInternal(Method method) {
        for (String className : testClasses) {
          SootClass sc = Scene.v().forceResolve(className, SootClass.HIERARCHY);
          if (sc == null) continue;
          if (!testClasses.contains(sc.getName())) continue;
          if (!sc.isInterface() && !sc.isPrivate())
            for (SootMethod sm : sc.getMethods()) {
            if (sm.isConcrete()) {
              // This is done by hand here because of the cases where the
              // character ' is in the signature. This is not supported by the
              // current Soot.
              // TODO: Get Soot to support the character '
              String sig = sm.getSignature();
              sig = sig.substring(sig.indexOf(": ") + 2, sig.length());
              String returnType = sig.substring(0, sig.indexOf(" "));
              String methodName =
                  sig.substring(sig.indexOf(" ") + 1, sig.indexOf("("));
              List<String> parameters = new ArrayList<String>();
              for (String parameter : sig
                  .substring(sig.indexOf("(") + 1, sig.indexOf(")"))
                  .split(",")) {
                if (!parameter.trim().isEmpty())
                  parameters.add(parameter.trim());
              }

              Method newMethod =
                  new Method(methodName, parameters, returnType, className);
              //System.out.println(newMethod.getSignature());
              methods.add(newMethod);
            }
          }
        }
        return Type.NOT_SUPPORTED;
      }

    }.applies(new Method("a", "void", "x.y"));
    //System.out.println("Loaded " + (methods.size() - methodCount)  + " methods from the test JAR.");
  }

  public void pruneNone() {
    Set<Method> newMethods = new HashSet<Method>();
    for (Method m : methods) {
      if (!m.getCategoriesClassified().isEmpty()) 
    	  newMethods.add(m);
    }
    //System.out.println(  methods.size() + " methods prunned down to " + newMethods.size());
    methods = newMethods;
  }
}
