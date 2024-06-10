package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;

/**
 * Evaluates if sink words are found in the doc comment.
 * <p>
 * The number of sanitizer verbs and nouns based on the
 * {@link SecurityVocabulary#SANITIZER_VERBS}
 * and {@link SecurityVocabulary#SANITIZER_NOUNS} lists.
 *
 * @author Oshando Johnson on 29.07.20
 */
public class SanitizerWordCountFeature extends WordCountFeature implements IDocFeature {

    public SanitizerWordCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.SANITIZER));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.SANITIZER));

        return featureResult;
    }

    @Override
    public String toString() {
        return "SanitizerWordCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "SanitizerWordCountFeature";
    }
}