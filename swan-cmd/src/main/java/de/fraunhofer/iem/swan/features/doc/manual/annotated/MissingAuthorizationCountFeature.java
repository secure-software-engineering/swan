package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;


/**
 * Evaluates if missing authorization words are found in the doc comment.
 * <p>
 * The number of missing authorization verbs and nouns is based on the
 * {@link SecurityVocabulary#CWE862_VERBS}
 * and {@link SecurityVocabulary#CWE862_NOUNS} lists.
 *
 * @author Oshando Johnson on 30.09.20
 */
public class MissingAuthorizationCountFeature extends WordCountFeature implements IDocFeature {


    public MissingAuthorizationCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.CWE862));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.CWE862));

        return featureResult;
    }

    @Override
    public String toString() {
        return "MissingAuthorizationCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "MissingAuthorizationCountFeature";
    }
}