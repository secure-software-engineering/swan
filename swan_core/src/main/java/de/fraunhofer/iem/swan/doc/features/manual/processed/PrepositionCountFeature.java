package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.util.Constants;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;

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

        featureResult.setMethodValue(countPartOfSpeech(annotatedMethod.getMethodMap(), Constants.POS.PREPOSITION));
        featureResult.setClassValue(countPartOfSpeech(annotatedMethod.getClassMap(), Constants.POS.PREPOSITION));

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