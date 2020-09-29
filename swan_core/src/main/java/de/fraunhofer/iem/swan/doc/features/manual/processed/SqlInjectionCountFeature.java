package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.util.SecurityVocabulary;

/**
 * Evaluates if SQL Injection words are found in the doc comment.
 * <p>
 * The number of SQL Injection verbs and nouns based on the
 * {@link SecurityVocabulary#CWE089_VERBS}
 * and {@link SecurityVocabulary#CWE089_NOUNS} lists.
 *
 * @author Oshando Johnson on 07.08.20
 */
public class SqlInjectionCountFeature extends WordCountFeature implements IDocFeature {

    public SqlInjectionCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.CWE089));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.CWE089));

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