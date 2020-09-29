package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.util.WordList;

/**
 * Evaluates if sink data flow prepositions are found in the doc comment.
 * <p>
 * The sink prepositions and nouns are based on the
 * {@link WordList#SINK_PREPOSITIONS} and {@link WordList#SINK_NOUNS} lists.
 *
 * @author Oshando Johnson on 01.09.20
 */
public class SinkDataFlowFeature extends DataFlowFeature implements IDocFeature {

    public SinkDataFlowFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.SINK));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.SINK));

        return featureResult;
    }

    @Override
    public String toString() {
        return "SinkDataFlowFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "SinkDataFlowFeature";
    }
}