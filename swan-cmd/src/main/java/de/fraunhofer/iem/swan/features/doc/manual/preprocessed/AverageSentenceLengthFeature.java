package de.fraunhofer.iem.swan.features.doc.manual.preprocessed;

import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

/**
 * Calculates the average length of sentences in the comment.
 *
 * @author Oshando Johnson on 29.07.20
 */
public class AverageSentenceLengthFeature implements IDocFeature {

    private FeatureResult unprocessedDocResult;

    public AverageSentenceLengthFeature() {
        unprocessedDocResult = new FeatureResult();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        Document classComment = new Document(annotatedMethod.getMethod().getJavadoc().getClassComment());
        Document methodComment = new Document(annotatedMethod.getMethod().getJavadoc().getMethodComment());

        unprocessedDocResult.setClassValue(calculateAverage(classComment));
        unprocessedDocResult.setMethodValue(calculateAverage(methodComment));

        return unprocessedDocResult;
    }

    /**
     * Returns average length of sentences for the document.
     *
     * @param document list of sentences
     * @return returns average length of sentences
     */
    public double calculateAverage(Document document) {

        int sentenceLength = 0;

        for (Sentence sentence : document.sentences()) {
            sentenceLength += sentence.length();
        }

        return (double)sentenceLength / (document.sentences().size() <= 0 ? 1 : document.sentences().size());
    }

    @Override
    public String toString() {
        return "AverageSentenceLengthFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "AverageSentenceLengthFeature";
    }
}
