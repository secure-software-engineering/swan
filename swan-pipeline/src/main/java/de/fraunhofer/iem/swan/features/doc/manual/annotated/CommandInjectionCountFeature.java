package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;

/**
 * Evaluates if Command Injection words are found in the doc comment.
 * * <p>
 * * The number of Command Injection verbs and nouns is based on the
 * * {@link SecurityVocabulary#CWE78_VERBS}
 * * and {@link SecurityVocabulary#CWE78_NOUNS} lists.
 *
 * @author Oshando Johnson on 30.09.20
 */
public class CommandInjectionCountFeature extends WordCountFeature implements IDocFeature {


    public CommandInjectionCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.CWE78));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.CWE78));

        return featureResult;
    }

    @Override
    public String toString() {
        return "CommandInjectionCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "CommandInjectionCountFeature";
    }
}
