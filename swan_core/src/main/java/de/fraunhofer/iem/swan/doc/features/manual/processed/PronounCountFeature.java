package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.util.Constants;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;

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

        featureResult.setMethodValue(countPartOfSpeech(annotatedMethod.getMethodMap(), Constants.POS.PRONOUN));
        featureResult.setClassValue(countPartOfSpeech(annotatedMethod.getClassMap(), Constants.POS.PRONOUN));

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