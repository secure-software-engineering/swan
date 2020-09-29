package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.util.SecurityVocabulary;

/**
 * Evaluates if sanitizer data flow prepositions are found in the doc comment.
 * <p>
 * The sanitizer prepositions and nouns are based on the
 * {@link SecurityVocabulary#SANITIZER_PREPOSITIONS} and {@link SecurityVocabulary#SANITIZER_NOUNS} lists.
 *
 * @author Oshando Johnson on 01.09.20
 */
public class SanitizerDataFlowFeature extends DataFlowFeature implements IDocFeature {

    public SanitizerDataFlowFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.SANITIZER));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.SANITIZER));

        return featureResult;
    }

    @Override
    public String toString() {
        return "SanitizerDataFlowFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "SanitizerDataFlowFeature";
    }
}