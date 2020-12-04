package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.nlp.NLPUtils;

/**
 * Counts the number of pronoun tokens in the doc comment.
 *
 * @author Oshando Johnson on 20.08.20
 */
public class PronounCountFeature extends POSCountFeature implements IDocFeature {

    public PronounCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(countPartOfSpeech(annotatedMethod.getMethodMap(), NLPUtils.POS.PRONOUN));
        featureResult.setClassValue(countPartOfSpeech(annotatedMethod.getClassMap(), NLPUtils.POS.PRONOUN));

        return featureResult;
    }

    @Override
    public String toString() {
        return "PronounCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "PronounCountFeature";
    }
}