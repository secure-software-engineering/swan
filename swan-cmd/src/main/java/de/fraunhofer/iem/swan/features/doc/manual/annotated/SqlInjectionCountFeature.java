package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;

/**
 * Evaluates if SQL Injection words are found in the doc comment.
 * <p>
 * The number of SQL Injection verbs and nouns based on the
 * {@link SecurityVocabulary#CWE89_VERBS}
 * and {@link SecurityVocabulary#CWE89_NOUNS} lists.
 *
 * @author Oshando Johnson on 07.08.20
 */
public class SqlInjectionCountFeature extends WordCountFeature implements IDocFeature {

    public SqlInjectionCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.CWE89));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.CWE89));

        return featureResult;
    }

    @Override
    public String toString() {
        return "SqlInjectionCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "SqlInjectionCountFeature";
    }
}