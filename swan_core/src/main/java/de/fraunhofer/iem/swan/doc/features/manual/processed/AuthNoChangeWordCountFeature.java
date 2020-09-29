package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.util.WordList;

/**
 * Evaluates if auth-no-change words are found in the doc comment.
 * <p>
 * The number of auth-no-change verbs and nouns based on the
 * {@link WordList#AUTH_NO_CHANGE_VERBS}
 * and {@link WordList#AUTHENTICATION_NOUNS} lists.
 *
 * @author Oshando Johnson on 07.08.20
 */
public class AuthNoChangeWordCountFeature extends WordCountFeature implements IDocFeature {

    public AuthNoChangeWordCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.AUTHENTICATION_NEUTRAL));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.AUTHENTICATION_NEUTRAL));

        return featureResult;
    }

    @Override
    public String toString() {
        return "AuthNoChangeWordCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "AuthNoChangeWordCountFeature";
    }
}
