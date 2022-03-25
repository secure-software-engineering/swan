package de.fraunhofer.iem.coveragedoclet;

import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;

public class Main {

    public static void main(String[] args) {

        String[] docletArgs = new String[]{
                "-doclet", CoverageDoclet.class.getName(),
                "-docletpath", "target/classes/",
                "-sourcepath", "/home/oshando/IdeaProjects/swan/swan-pipeline/test-doc/encoder-1.2.3-sources.jar",
                "-subpackages", "org.owasp.encoder",
        };

        DocumentationTool docTool = ToolProvider.getSystemDocumentationTool();
        docTool.run(System.in, System.out, System.err, docletArgs);
    }
}
