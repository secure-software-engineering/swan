package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;

/**
 * Evaluates if open redirect words are found in the doc comment.
 * <p>
 * The number of open redirect verbs and nouns is based on the
 * {@link SecurityVocabulary#CWE601_VERBS}
 * and {@link SecurityVocabulary#CWE601_NOUNS} lists.
 *
 * @author Oshando Johnson on 30.09.20
 */
public class OpenRedirectCountFeature extends WordCountFeature implements IDocFeature {

    public OpenRedirectCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.CWE601));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.CWE601));

        return featureResult;
    }

    @Override
    public String toString() {
        return "OpenRedirectCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "OpenRedirectCountFeature";
    }
}