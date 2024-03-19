package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.nlp.NLPUtils;

/**
 * Counts the number of preposition tokens in the doc comment.
 *
 * @author Oshando Johnson on 20.08.20
 */
public class PrepositionCountFeature extends POSCountFeature implements IDocFeature {

    public PrepositionCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(countPartOfSpeech(annotatedMethod.getMethodMap(), NLPUtils.POS.PREPOSITION));
        featureResult.setClassValue(countPartOfSpeech(annotatedMethod.getClassMap(), NLPUtils.POS.PREPOSITION));

        return featureResult;
    }

    @Override
    public String toString() {
        return "PrepositionCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "PrepositionCountFeature";
    }
}