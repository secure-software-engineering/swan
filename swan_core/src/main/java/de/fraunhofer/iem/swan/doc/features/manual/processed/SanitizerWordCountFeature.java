package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.util.WordList;

/**
 * Evaluates if sink words are found in the doc comment.
 * <p>
 * The number of sanitizer verbs and nouns based on the
 * {@link WordList#SANITIZER_VERBS}
 * and {@link WordList#SANITIZER_NOUNS} lists.
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