package de.fraunhofer.iem.coveragedoclet;

import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;

public class Main {

    public static void main(String[] args) {

        String[] docletArgs = new String[]{
                "-doclet", CoverageDoclet.class.getName(),
                "-docletpath", "target/classes/",
                "-sourcepath", "source-jars/",
                "-subpackages", "org.owasp.encoder",
        };

        DocumentationTool docTool = ToolProvider.getSystemDocumentationTool();
        docTool.run(System.in, System.out, System.err, docletArgs);
    }
}
