package de.fraunhofer.iem.swan.training;

/**
 * @author Oshando Johnson on 23.06.20
 */

import de.fraunhofer.iem.swan.io.dataset.Parser;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.io.doc.DocletExecutor;
import edu.stanford.nlp.util.StringUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class Training {


    public static String getClassname(String methodSig) {

        String methodName = methodSig.substring(methodSig.indexOf(" "), methodSig.indexOf("("));

        return methodName.substring(0, methodName.lastIndexOf(".")).trim();
    }

    public static void main(String[] args) throws IOException {
        //Step 1: Export signatures for training
        Parser parser = new Parser();
        parser.parse("/swan/core/src/main/resources/input/Dataset/data-set.json");
        Set<Method> methodSet = parser.methods();

        Set<String> uniqueClasses = new HashSet<>();

        Parser p2 = new Parser();
        p2.parse("/swan/core/src/main/resources/input/Dataset/swan-dataset.json");
        Set<Method> m2 = p2.methods();
        System.out.println(m2.size());

        Set<String> c2 = new HashSet<>();

        for (Method method : m2) {
            String sig = method.getReturnType() + " " + method.getClassName() + "." + method.getMethodName() + "(" + StringUtils.join(method.getParameters(), ", ") + ")";
            c2.add(sig);
        }

        for (Method method : methodSet) {
            String sig = method.getReturnType() + " " + method.getClassName() + "." + method.getMethodName() + "(" + StringUtils.join(method.getParameters(), ", ") + ")";
            //System.out.println(sig);

            uniqueClasses.add(sig);
        }

        System.out.println(uniqueClasses.size());

        int count = 0;
        for (String cl : uniqueClasses) {

            if (c2.contains(cl)) {
                count++;
            } else {
                System.out.println(cl);
            }
            //System.out.println(cl);

        }
        System.out.println(count);
    }
    public static void mains(String[] args) throws IOException {


        //Step 1: Export signatures for training
        Parser parser = new Parser();
        parser.parse("/swan/src/main/resources/training-set-original.json");
        Set<Method> methodSet = parser.methods();

        HashMap<String, Method> methodMap = new HashMap<String, Method>();

        Set<String> uniqueClasses = new HashSet<>();

        for (Method method : methodSet) {

            String sig = method.getReturnType() + " " + method.getClassName() + "." + method.getMethodName() + "(" + StringUtils.join(method.getParameters(), ", ") + ")";
            //System.out.println(sig);
            methodMap.put(sig, method);
            uniqueClasses.add(method.getClassName());
        }

        System.out.println("--------------------------------------");

        Set<String> lines = null;
        try {
            lines = new HashSet<>(FileUtils.readLines(new File(" /src/main/resources/thecodemaster-methods.txt"), Charset.defaultCharset().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String methodSig : lines) {
            //  System.out.println("|" + getClassname(methodSig) + "|");
        }

        String input = "/src/main/resources/jars";
        String output = "/javadoc";


        DocletExecutor xmlDoclet = new DocletExecutor();
        xmlDoclet.setDoclet(DocletExecutor.SSL_DOCLET);
        xmlDoclet.setOutputDir(output);


        HashMap<String, String> jarMap = new HashMap<String, String>();

        File sourceFiles = new File(input);

        for (File file : Objects.requireNonNull(sourceFiles.listFiles())) {

            if (file.getName().endsWith("esapi-2.0_rc10-sources.jar"))
            if (file.getName().endsWith(".jar")) {

                JarInputStream jarFiles = new JarInputStream(new FileInputStream(file.getAbsolutePath()));

                Set<String> packages = new HashSet<>();
                JarEntry jarEntry = null;

                while ((jarEntry = jarFiles.getNextJarEntry()) != null) {

                    //  System.out.println(jarEntry.getName());
                    if (jarEntry.isDirectory()) {

                        packages.add(jarEntry.getName().replace("/", ".").substring(0, jarEntry.getName().length() - 1));
                    } else {

                        String cpath = jarEntry.getName().replace("/", ".").substring(0, jarEntry.getName().length() - 5);

                        //   System.out.println(cpath.substring(0, cpath.lastIndexOf(".")));
                        packages.add(cpath.substring(0, cpath.lastIndexOf(".")));
                        for (String sig : methodMap.keySet()) {
                            if (getClassname(sig).equals(cpath)) {

                                //   System.out.println(file.getName());
                                jarMap.put(sig, file.getName());
                            }
                        }
                    }
                }

                System.out.println("Trou: " + file.getAbsolutePath() + packages.size());

                if (packages.size() > 0) {

                   String rootPackages = "";// StringUtils.join(getRootPackages(new ArrayList<>(packages)), ":");

                    System.out.println("PACKAGES: " + rootPackages);

                    DocletExecutor coverageDoclet = new DocletExecutor();
                    coverageDoclet.setDoclet(DocletExecutor.COVERAGE_DOCLET);
                    coverageDoclet.setOutputDir(output);

                    coverageDoclet.runDoclet(file.getAbsolutePath(), rootPackages);
                }
            } else if (file.isDirectory() && false) {

                System.out.println(file.getPath());
                ArrayList<String> packages = new ArrayList<>();

                for (File sub : Objects.requireNonNull(file.listFiles())) {//FileUtils.listFilesAndDirs(file, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY)) {

                    String subDir = sub.getPath().replace(file.getPath(), "");

                    if (subDir.length() > 1) {
                        packages.add(subDir
                                .replace("/", ".")
                                .substring(1));
//                        System.out.println(subDir
//                                .replace("/", ".")
//                                .substring(1)
//                        );
                    }
                }


              //  String rootPackages = StringUtils.join(Main.getRootPackages(packages), ":");
                //System.out.println(file.getName()+">>> "+ rootPackages);

                String sourcePath = "/src/main/resources/jars/rt/";

                DocletExecutor coverageDoclet = new DocletExecutor();
                coverageDoclet.setDoclet(DocletExecutor.STANDARD_DOCLET);
                coverageDoclet.setOutputDir(output);

                //   simplePath = sourcePath.substring(sourcePath.lastIndexOf("/"));
                coverageDoclet.runDoclet(file.getAbsolutePath(), StringUtils.join(packages, ":"));

                System.out.println("PACKAGES: " + StringUtils.join(packages, ":"));
            }
        }
        System.out.println("--------------------------------------");

        for (String method : methodMap.keySet()) {

            //      System.out.println("%%%%|"+method + "|" + getClassname(method)+"|");
        }
        System.out.println("Done");//+ "| "+jarMap.get(method));*/


    }


}