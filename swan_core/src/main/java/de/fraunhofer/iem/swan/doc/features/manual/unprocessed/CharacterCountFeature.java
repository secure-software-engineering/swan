package de.fraunhofer.iem.swan.doc.features.manual.unprocessed;

import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;

/**
 * Counts the number of characters in the comment.
 *
 * @author Oshando Johnson on 29.07.20
 */
public class CharacterCountFeature implements IDocFeature {

    private FeatureResult unprocessedDocResult;

    public CharacterCountFeature() {
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
     * Calculates average token length in the document.
     *
     * @param document document with sentences
     * @return number of
     */
    public int getCount(Document document) {

        int characterCount = 0;

        for (Sentence sentence : document.sentences()) {
            for (Token token : sentence.tokens()) {
                characterCount += token.originalText().length();
            }
        }

        return characterCount;
    }

    @Override
    public String toString() {
        return "CharacterCountFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "CharacterCountFeature";
    }
}
