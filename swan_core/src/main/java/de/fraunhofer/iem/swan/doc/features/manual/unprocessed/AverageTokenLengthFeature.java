package de.fraunhofer.iem.swan.doc.features.manual.unprocessed;

import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;

/**
 * Calculates the average length of tokens in the document.
 *
 * @author Oshando Johnson on 29.07.20
 */
public class AverageTokenLengthFeature implements IDocFeature {

    private FeatureResult unprocessedDocResult;

    public AverageTokenLengthFeature() {
        unprocessedDocResult = new FeatureResult();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        Document classComment = new Document(annotatedMethod.getMethod().getJavadoc().getClassComment());
        Document methodComment = new Document(annotatedMethod.getMethod().getJavadoc().getMethodComment());

        unprocessedDocResult.setClassValue(getAverage(classComment));
        unprocessedDocResult.setMethodValue(getAverage(methodComment));

        return unprocessedDocResult;
    }

    /**
     * Calculates average token length in the document.
     *
     * @param document document with sentences
     * @return number of
     */
    public double getAverage(Document document) {

        int tokenCount = 0;
        int tokenLength = 0;

        for (Sentence sentence : document.sentences()) {
            tokenCount += sentence.tokens().size();

            for (Token token : sentence.tokens()) {
                tokenLength += token.originalText().length();
            }
        }

        return (double) tokenLength / tokenCount;
    }

    @Override
    public String toString() {
        return "AverageTokenLengthFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "AverageTokenLengthFeature";
    }
}
