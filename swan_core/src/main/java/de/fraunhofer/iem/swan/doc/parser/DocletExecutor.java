package de.fraunhofer.iem.swan.doc.parser;

import com.sun.tools.javadoc.Main;

/**
 * @author Oshando Johnson on 03.06.20
 */
public class DocletExecutor {

    public static final String SSL_DOCLET = "info.semanticsoftware.doclet.SSLDoclet";
    public static final String COVERAGE_DOCLET = "com.manoelcampos.javadoc.coverage.CoverageDoclet";
    public static final String STANDARD_DOCLET = "com.sun.tools.doclets.standard.Standard";

    private String outputDir;
    private String doclet;

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getDoclet() {
        return doclet;
    }

    public void setDoclet(String doclet) {
        this.doclet = doclet;
    }

    public void runDoclet(String sourcePath, String packages, String outputPath) {

        System.out.println("runnin:"+ sourcePath+ " "+ packages);
        String[] docletParams = new String[]{
              //  "Xdoclint:none",
                "-private",
                "-doclet", getDoclet(),
                "-docletpath", getDocletPath(doclet),
                "-d", outputPath,
               // "-source", "8",
                // "-o", "coverage-report.html",
                "-sourcepath", sourcePath,
                "-subpackages", packages
        };
        System.out.println();
       Main.execute(docletParams);
    }

    public void runDoclet(String sourcePath, String packages) {
        runDoclet(sourcePath, packages, getOutputDir());
    }

    /**
     * Returns jar location for the the specified doclet.
     *
     * @param doclet name of doclet
     * @return path to doclet jar
     */
    private String getDocletPath(String doclet) {
        switch (doclet) {
            case SSL_DOCLET:
                return "/Users/oshando/Projects/thesis/03-code/ssldoclet/target/ssldoclet-1.2.jar";
            case COVERAGE_DOCLET:
                return "/Users/oshando/Projects/thesis/03-code/javadoc-coverage/target/javadoc-coverage-1.2.0.jar";
            case STANDARD_DOCLET:
                return "/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/lib/tools.jar";
        }
        return null;
    }
}