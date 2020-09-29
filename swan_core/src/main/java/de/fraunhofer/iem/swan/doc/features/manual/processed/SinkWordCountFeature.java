package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.util.WordList;

/**
 * Evaluates if sink words are found in the doc comment.
 * <p>
 * The number of sink verbs and nouns based on the
 * {@link WordList#SINK_VERBS}
 * and {@link WordList#SINK_NOUNS} lists.
 *
 * @author Oshando Johnson on 20.07.20
 */
public class SinkWordCountFeature extends WordCountFeature implements IDocFeature {

    public SinkWordCountFeature() {
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
        return "SinkWordCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "SinkWordCountFeature";
    }
}
