package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;

/**
 * Evaluates if source words are found in the doc comment.
 * <p>
 * The number of source verbs and nouns based on the
 * {@link SecurityVocabulary#SOURCE_VERBS}
 * and {@link SecurityVocabulary#SOURCE_NOUNS} lists.
 *
 * @author Oshando Johnson on 29.07.20
 */
public class SourceWordCountFeature extends WordCountFeature implements IDocFeature {

    public SourceWordCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.SOURCE));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.SOURCE));

        return featureResult;
    }

    @Override
    public String toString() {
        return "SourceWordCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "SourceWordCountFeature";
    }
}
