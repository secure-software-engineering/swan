package de.fraunhofer.iem.swan.doc.features.manual.unprocessed;

import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

/**
 * Counts the number of tokens in the method and class comment.
 *
 * @author Oshando Johnson on 29.07.20
 */
public class TokenCountFeature implements IDocFeature {

    private FeatureResult unprocessedDocResult;

    public TokenCountFeature() {
        unprocessedDocResult = new FeatureResult();
    }


    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        Document classComment = new Document(annotatedMethod.getMethod().getJavadoc().getClassComment());
        Document methodComment = new Document(annotatedMethod.getMethod().getJavadoc().getMethodComment());

        unprocessedDocResult.setClassValue(getCount(classComment));
        unprocessedDocResult.setMethodValue(getCount(methodComment));

        return unprocessedDocResult;
    }

    /**
     * Counts the number of words in the document.
     *
     * @param document document with sentences
     * @return number of
     */
    public int getCount(Document document) {

        int tokenCount = 0;

        for (Sentence sentence : document.sentences()) {
            tokenCount += sentence.tokens().size();
        }

        return tokenCount;
    }

    @Override
    public String toString() {
        return "TokenCountFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "TokenCountFeature";
    }
}
