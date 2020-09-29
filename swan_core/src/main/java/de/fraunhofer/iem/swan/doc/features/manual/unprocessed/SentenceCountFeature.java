package de.fraunhofer.iem.swan.doc.features.manual.unprocessed;

import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import edu.stanford.nlp.simple.Document;

/**
 * Counts the number of sentences found in the comment.
 *
 * @author Oshando Johnson on 29.07.20
 */
public class SentenceCountFeature implements IDocFeature {

    private FeatureResult unprocessedDocResult;

    public SentenceCountFeature() {
        unprocessedDocResult = new FeatureResult();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        Document classComment = new Document(annotatedMethod.getMethod().getJavadoc().getClassComment());
        Document methodComment = new Document(annotatedMethod.getMethod().getJavadoc().getMethodComment());

        unprocessedDocResult.setClassValue(classComment.sentences().size());
        unprocessedDocResult.setMethodValue(methodComment.sentences().size());

        return unprocessedDocResult;
    }

    @Override
    public String toString() {
        return "SentenceCountFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "SentenceCountFeature";
    }
}
