package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.util.Constants;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;

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

        featureResult.setMethodValue(countPartOfSpeech(annotatedMethod.getMethodMap(), Constants.POS.ADVERB));
        featureResult.setClassValue(countPartOfSpeech(annotatedMethod.getClassMap(), Constants.POS.ADVERB));

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