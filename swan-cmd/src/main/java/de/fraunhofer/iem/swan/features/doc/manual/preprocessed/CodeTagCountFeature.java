package de.fraunhofer.iem.swan.features.doc.manual.preprocessed;

import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;

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
