package de.fraunhofer.iem.coveragedoclet;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A minimal doclet that just prints out the names of the
 * selected elements. It goes on and on
 */
public class CoverageDoclet implements Doclet {

    final private Logger log = Logger.getLogger(CoverageDoclet.class.getName());

    @Override
    public void init(Locale locale, Reporter reporter) {
    }

    @Override
    public String getName() {
        // For this doclet, the name of the doclet is just the
        // simple name of the class. The name may be used in
        // messages related to this doclet, such as in command-line
        // help when doclet-specific options are provided.
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        // This doclet does not support any options.
        return Collections.emptySet();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // This doclet supports all source versions.
        // More sophisticated doclets may use a more
        // specific version, to ensure that they do not
        // encounter more recent language features that
        // they may not be able to handle.
        return SourceVersion.latest();
    }

    private static final boolean OK = true;

    @Override
    public boolean run(DocletEnvironment environment) {
        // This method is called to perform the work of the doclet.
        // In this case, it just prints out the names of the
        // elements specified on the command line.
        //environment.getSpecifiedElements().forEach(System.out::println);

        DocTrees docTrees = environment.getDocTrees();

        CoverageReport docReport = new CoverageReport();


        ArrayList<String> classnames = new ArrayList<>();

        for (TypeElement typeElement : ElementFilter.typesIn(environment.getIncludedElements())) {

            docReport.incrementTotalClasses();
            classnames.add(typeElement.getQualifiedName().toString());

            if (isDocumented(docTrees.getDocCommentTree(typeElement)))
                docReport.incrementDocumentedClasses();

            for (Element element : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
                docReport.incrementTotalMethods();

                if (isDocumented(docTrees.getDocCommentTree(element)))
                    docReport.incrementDocumentedMethods();
            }
        }
        log.info(classnames.size() + " classes found");
        log.info("Documentation coverage report: " + docReport);
        return OK;
    }

    public boolean isDocumented(DocCommentTree docCommentTree) {

        return !(docCommentTree == null || docCommentTree.getFullBody().isEmpty());
    }
}