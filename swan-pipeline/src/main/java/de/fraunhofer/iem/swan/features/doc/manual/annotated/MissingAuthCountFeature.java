package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;

/**
 * Evaluates if missing authentication words are found in the doc comment.
 * * <p>
 * * The number of missing authentication verbs and nouns is based on the
 * * {@link SecurityVocabulary#CWE306_VERBS}
 * * and {@link SecurityVocabulary#CWE306_NOUNS} lists.
 *
 * @author Oshando Johnson on 30.09.20
 */
public class MissingAuthCountFeature extends WordCountFeature implements IDocFeature {


    public MissingAuthCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.CWE306));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.CWE306));

        return featureResult;
    }

    @Override
    public String toString() {
        return "MissingAuthCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "MissingAuthCountFeature";
    }
}
