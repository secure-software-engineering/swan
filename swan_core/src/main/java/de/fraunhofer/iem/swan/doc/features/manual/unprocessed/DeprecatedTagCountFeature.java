package de.fraunhofer.iem.swan.doc.features.manual.unprocessed;

import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.util.Constants;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;

/**
 * Counts the the number of deprecated tags in the document.
 * Deprecated tags have this format: @deprecated deprecated-text
 *
 * @author Oshando Johnson on 02.08.20
 */
public class DeprecatedTagCountFeature extends TagCountFeature implements IDocFeature {

    public DeprecatedTagCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        unprocessedDocResult.setClassValue(countTags(annotatedMethod.getMethod().getJavadoc().getClassComment(),
                Constants.TAG.DEPRECATED));
        unprocessedDocResult.setMethodValue(countTags(annotatedMethod.getMethod().getJavadoc().getMethodComment(),
                Constants.TAG.DEPRECATED));

        return unprocessedDocResult;
    }

    @Override
    public String toString() {
        return "DeprecatedTagCountFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "DeprecatedTagCountFeature";
    }
}