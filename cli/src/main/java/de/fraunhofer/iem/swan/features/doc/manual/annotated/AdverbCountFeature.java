package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.nlp.NLPUtils;

/**
 * Counts the number of adverb tokens in the doc comment.
 *
 * @author Oshando Johnson on 20.08.20
 */
public class AdverbCountFeature extends POSCountFeature implements IDocFeature {

    public AdverbCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(countPartOfSpeech(annotatedMethod.getMethodMap(), NLPUtils.POS.ADVERB));
        featureResult.setClassValue(countPartOfSpeech(annotatedMethod.getClassMap(), NLPUtils.POS.ADVERB));

        return featureResult;
    }

    @Override
    public String toString() {
        return "AdverbCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "AdverbCountFeature";
    }
}