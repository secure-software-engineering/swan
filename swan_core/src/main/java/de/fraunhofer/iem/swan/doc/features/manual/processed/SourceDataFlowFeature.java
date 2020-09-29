package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.util.WordList;

/**
 * Evaluates if source data flow prepositions are found in the doc comment.
 * <p>
 * The source prepositions and nouns are based on the
 * {@link WordList#SOURCE_PREPOSITIONS} and {@link WordList#SOURCE_NOUNS} lists.
 *
 * @author Oshando Johnson on 01.09.20
 */
public class SourceDataFlowFeature extends DataFlowFeature implements IDocFeature {

    public SourceDataFlowFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.SOURCE));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.SOURCE));

        return featureResult;
    }

    @Override
    public String toString() {
        return "SourceDataFlowFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "SourceDataFlowFeature";
    }
}