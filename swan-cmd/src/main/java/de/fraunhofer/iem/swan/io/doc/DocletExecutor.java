package de.fraunhofer.iem.swan.io.doc;

import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oshando Johnson on 03.06.20
 */
public class DocletExecutor {

    public static final String SSL_DOCLET = "de.fraunhofer.iem.doclet.xml.XMLDoclet";
    public static final String COVERAGE_DOCLET = "de.fraunhofer.iem.coveragedoclet.CoverageDoclet";
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

        ArrayList<String> docletArgs = new ArrayList<>(List.of(
                "-doclet", doclet,
                "-docletpath", getDocletPath(doclet),
                "-sourcepath", sourcePath,
                "-subpackages", packages
        ));

        if (doclet.equals(SSL_DOCLET)) {
            docletArgs.add("--destdir");
            docletArgs.add(outputPath);
        }

        DocumentationTool docTool = ToolProvider.getSystemDocumentationTool();
        docTool.run(System.in, System.out, System.err, docletArgs.toArray(new String[0]));
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
                return "../doc-xml-exporter-doclet/target/classes";
            case COVERAGE_DOCLET:
                return "../doc-coverage-doclet/target/classes/";
            case STANDARD_DOCLET:
                return "/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/lib/tools.jar";
        }
        return null;
    }
}