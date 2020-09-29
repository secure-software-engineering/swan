package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.util.SecurityVocabulary;

/**
 * Evaluates if auth-safe-state words are found in the doc comment.
 * <p>
 * The number of auth-safe-state verbs and nouns based on the
 * {@link SecurityVocabulary#AUTH_SAFE_VERBS}
 * and {@link SecurityVocabulary#AUTHENTICATION_NOUNS} lists.
 *
 * @author Oshando Johnson on 07.08.20
 */
public class AuthSafeWordCountFeature extends WordCountFeature implements IDocFeature {

    public AuthSafeWordCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.AUTHENTICATION_TO_HIGH));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.AUTHENTICATION_TO_HIGH));

        return featureResult;
    }

    @Override
    public String toString() {
        return "AuthSafeWordCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "AuthSafeWordCountFeature";
    }
}
