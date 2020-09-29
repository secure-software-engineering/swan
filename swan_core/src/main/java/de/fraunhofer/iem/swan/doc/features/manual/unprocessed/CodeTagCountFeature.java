package de.fraunhofer.iem.swan.doc.features.manual.unprocessed;

import de.fraunhofer.iem.swan.doc.util.Constants;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;

/**
 * Counts the number of code blocks in the Javadoc comment. Code tags are of the form {@code text}.
 *
 * @author Oshando Johnson on 30.07.20
 */
public class CodeTagCountFeature extends TagCountFeature implements IDocFeature {

    public CodeTagCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        unprocessedDocResult.setClassValue(countTags(annotatedMethod.getMethod().getJavadoc().getClassComment(),
                Constants.TAG.CODE));
        unprocessedDocResult.setMethodValue(countTags(annotatedMethod.getMethod().getJavadoc().getMethodComment(),
                Constants.TAG.CODE));

        return unprocessedDocResult;
    }

    @Override
    public String toString() {
        return "CodeTagCountFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "CodeTagCountFeature";
    }
}
