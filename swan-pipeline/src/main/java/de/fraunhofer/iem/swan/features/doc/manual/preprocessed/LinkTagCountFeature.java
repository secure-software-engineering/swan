package de.fraunhofer.iem.swan.features.doc.manual.preprocessed;

import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;

/**
 * Counts the number of linked classes and methods in the Javadoc comment.
 * Link tags have the following format: {@link package.class# label}
 * {@linkplain package.class# label}
 *
 * @author Oshando Johnson on 23.07.20
 */
public class LinkTagCountFeature extends TagCountFeature implements IDocFeature {

    public LinkTagCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        unprocessedDocResult.setClassValue(countTags(annotatedMethod.getMethod().getJavadoc().getClassComment(),
                Constants.TAG.LINK));
        unprocessedDocResult.setMethodValue(countTags(annotatedMethod.getMethod().getJavadoc().getMethodComment(),
                Constants.TAG.LINK));

        return unprocessedDocResult;
    }

    @Override
    public String toString() {
        return "LinkTagCountFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "LinkTagCountFeature";
    }
}
