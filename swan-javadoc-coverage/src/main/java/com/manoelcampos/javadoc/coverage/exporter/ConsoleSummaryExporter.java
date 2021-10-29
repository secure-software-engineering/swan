package com.manoelcampos.javadoc.coverage.exporter;

import com.manoelcampos.javadoc.coverage.CoverageDoclet;
import com.manoelcampos.javadoc.coverage.Utils;
import com.manoelcampos.javadoc.coverage.stats.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oshando Johnson on 28.05.20
 */
public class ConsoleSummaryExporter extends AbstractDataExporter {

    private double docCoverage;
    private String sourcePath;
    String simplePath;

    final private Logger log = Logger.getLogger(ConsoleSummaryExporter.class.getName());

    public ConsoleSummaryExporter(final CoverageDoclet doclet, String sourcePath) {
        super(doclet, ".txt");
        this.sourcePath = sourcePath;

        System.out.println("SOURCE: "+sourcePath);
        if (sourcePath.endsWith(".jar/")) {
            simplePath = sourcePath.substring(1, sourcePath.length() - 1);
            simplePath = simplePath.substring(simplePath.lastIndexOf("/") + 1);
        }else {
            simplePath = sourcePath.substring(sourcePath.lastIndexOf("/", sourcePath.length() - 2));
        }
    }


    @Override
    protected void exportProjectDocumentationCoverageSummary() {

        System.out.printf(">>>>" + simplePath + " Coverage: %.2f%% <<<<\n", docCoverage);

    }

    @Override
    protected void header() {

    }

    @Override
    protected void footer() {

    }

    @Override
    protected void afterBuild() {
        getWriter().printf("\nJavaDoc Coverage report saved to %s\n", getFile().getAbsolutePath());

    }

    @Override
    protected void exportPackagesDocStats() {

        final PackagesDocStats packagesDocStats = getStats().getPackagesDocStats();

        long documentedPackages = packagesDocStats.getDocumentedMembers();
        long undocumentedPackages = packagesDocStats.getUndocumentedMembers();
        long totalPackages = packagesDocStats.getMembersNumber();

        final String format = " %d/%d [%.2f%%] -- Undocumented: %d \n";
        getWriter().printf("Packages:" + format,
                documentedPackages, totalPackages, Utils.computePercentage(documentedPackages, totalPackages),
                undocumentedPackages);
    }


    public static String getClassname(String methodSig) {

        String methodName = methodSig.substring(methodSig.indexOf(" "), methodSig.indexOf("("));

        return methodName.substring(0, methodName.lastIndexOf(".")).trim();
    }

    public static String getSignature(String methodSig) {

        String methodName = methodSig.substring(methodSig.indexOf(" "));

        return methodName.trim();
    }


    @Override
    protected void exportClassesDocStats() {
        int documentedClasses = 0;
        int undocumentedClasses = 0;
        int totalClasses = 0;

        int documentedInterfaces = 0;
        int undocumentedInterfaces = 0;
        int totalInterfaces = 0;

        int documentedMethods = 0;
        int undocumentedMethods = 0;
        int totalMethods = 0;

        Set<String> lines = null;
        try {
            lines = new HashSet<String>(FileUtils.readLines(new File("/Users/oshando/Projects/thesis/03-code/src/main/resources/methods.txt"), Charset.defaultCharset().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, String> lineMap = new HashMap<>();

        for(String l: lines){
            lineMap.put(getSignature(l), l);
        }

        log.info("Total  methods: " + lines.size());


/*        for (final ClassDocStats classStat : getStats().getClassesDocStats().getClassesList()) {
            for (MethodDocStats method : classStat.getMethodsStats())
                System.out.println("$$$"+method.getMethodSignature());
        }*/


        boolean classDocumented, methodDocumented = false;



//        for (String signature : lines) {
//            classDocumented = false;

            for (final ClassDocStats classStats : getStats().getClassesDocStats().getClassesList()) {
//
//                if ((classStats.getDoc().qualifiedName()).contentEquals(getClassname(signature))) {
//                    classDocumented = classStats.isDocumented();
//                }


                List<MethodDocStats> methods = Stream.concat(classStats.getMethodsStats().stream(), classStats.getConstructorsStats().stream())
                        .collect(Collectors.toList());

                for (MethodDocStats method : methods) {

                    if (lineMap.containsKey(method.getMethodSignature())) {
                        methodDocumented = method.isDocumented();
                        System.out.println("%%%%|" + lineMap.get(method.getMethodSignature()) + "|" + getClassname(lineMap.get(method.getMethodSignature())) + "|" + (classStats.isDocumented()?1:0) + "|" + (methodDocumented?1:0) + "|" + simplePath);
                    }
//                }
          }
        }






        for (final ClassDocStats classStats : getStats().getClassesDocStats().getClassesList()) {

            switch (classStats.getType()) {

                case "Interface":

                    if (classStats.isDocumented())
                        documentedInterfaces++;
                    else
                        undocumentedInterfaces++;

                    totalInterfaces++;

                    break;
                case "Class":


                    if (classStats.isDocumented())
                        documentedClasses++;
                    else
                        undocumentedClasses++;

                    totalClasses++;

                    break;
            }


            List<MethodDocStats> methods = Stream.concat(classStats.getMethodsStats().stream(), classStats.getConstructorsStats().stream())
                    .collect(Collectors.toList());

            for (MethodDocStats method : methods) {

                if (method.isDocumented())
                    documentedMethods++;
                else
                    undocumentedMethods++;

                totalMethods++;
            }
        }

        final String format = " %d/%d [%.2f%%] -- Undocumented: %d \n";

        getWriter().printf("\tInterfaces" + format,
                documentedInterfaces, totalInterfaces, Utils.computePercentage(documentedInterfaces, totalInterfaces), undocumentedInterfaces);

        getWriter().printf("\tClasses" + format,
                documentedClasses, totalClasses, Utils.computePercentage(documentedClasses, totalClasses), undocumentedClasses);

        getWriter().printf("\t\tMethods" + format,
                documentedMethods, totalMethods, Utils.computePercentage(documentedMethods, totalMethods),
                undocumentedMethods);


        //Interface, classes, methods, constructors
        final String summary = " %d,%d,%d,%d,%d,%d\n";

        System.out.printf(">>>>>>" + simplePath + "," + summary,
                documentedInterfaces, totalInterfaces,
                documentedClasses, totalClasses,
                documentedMethods, totalMethods);

        docCoverage = Utils.mean(//Utils.computePercentage(documentedInterfaces, totalInterfaces),
                Utils.computePercentage(documentedClasses + documentedMethods,
                        totalClasses + totalMethods));
    }

    public int getCount(final int... values) {
        return 0;
    }
}
