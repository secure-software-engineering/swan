package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.util.WordList;

/**
 * Evaluates if auth-unsafe-state words are found in the doc comment.
 * <p>
 * The number of auth-unsafe-state verbs and nouns based on the
 * {@link WordList#AUTH_UNSAFE_VERBS}
 * and {@link WordList#AUTHENTICATION_NOUNS} lists.
 *
 * @author Oshando Johnson on 07.08.20
 */
public class AuthUnsafeWordCountFeature extends WordCountFeature implements IDocFeature {

    public AuthUnsafeWordCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.AUTHENTICATION_TO_LOW));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.AUTHENTICATION_TO_LOW));

        return featureResult;
    }

    @Override
    public String toString() {
        return "AuthUnsafeWordCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "AuthUnsafeWordCountFeature";
    }
}
