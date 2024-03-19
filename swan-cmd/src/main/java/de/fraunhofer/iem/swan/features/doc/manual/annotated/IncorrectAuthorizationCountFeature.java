package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;


/**
 * Evaluates if incorrect authorization words are found in the doc comment.
 * <p>
 * The number of incorrect authorization verbs and nouns is based on the
 * {@link SecurityVocabulary#CWE863_VERBS}
 * and {@link SecurityVocabulary#CWE863_NOUNS} lists.
 *
 * @author Oshando Johnson on 30.09.20
 */
public class IncorrectAuthorizationCountFeature extends WordCountFeature implements IDocFeature {

    public IncorrectAuthorizationCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.CWE863));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.CWE863));

        return featureResult;
    }

    @Override
    public String toString() {
        return "IncorrectAuthorizationCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "IncorrectAuthorizationCountFeature";
    }
}