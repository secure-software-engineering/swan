package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.util.Constants;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;

/**
 * Counts the number of conjunction tokens in the doc comment.
 *
 * @author Oshando Johnson on 20.08.20
 */
public class ConjunctionCountFeature extends POSCountFeature implements IDocFeature {

    public ConjunctionCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(countPartOfSpeech(annotatedMethod.getMethodMap(), Constants.POS.CONJUNCTION));
        featureResult.setClassValue(countPartOfSpeech(annotatedMethod.getClassMap(), Constants.POS.CONJUNCTION));

        return featureResult;
    }

    @Override
    public String toString() {
        return "ConjunctionCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "ConjunctionCountFeature";
    }
}