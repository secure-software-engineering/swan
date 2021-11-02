package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;

/**
 * Evaluates if authentication data flow prepositions are found in the doc comment.
 * <p>
 * The authentication prepositions and nouns are based on the
 * {@link SecurityVocabulary#AUTHENTICATION_PREPOSITIONS} and {@link SecurityVocabulary#AUTHENTICATION_NOUNS} lists.
 *
 * @author Oshando Johnson on 01.09.20
 */
public class AuthenticationDataFlowFeature extends DataFlowFeature implements IDocFeature {

    public AuthenticationDataFlowFeature() {
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
        return "AuthenticationDataFlowFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "AuthenticationDataFlowFeature";
    }
}