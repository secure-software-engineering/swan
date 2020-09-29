package de.fraunhofer.iem.swan.doc.features.manual.unprocessed;

import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.util.Constants;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.nlp.NLPUtils;

/**
 * Counts the number of numerical characters in the original text.
 *
 * @author Oshando Johnson on 23.07.20
 */
public class NumberCountFeature implements IDocFeature {

    private FeatureResult unprocessedDocResult;

    public NumberCountFeature() {
        unprocessedDocResult = new FeatureResult();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        unprocessedDocResult.setClassValue(NLPUtils.regexCounter(annotatedMethod.getMethod().getJavadoc().getClassComment(), Constants.NUMBER_PATTERN));
        unprocessedDocResult.setMethodValue(NLPUtils.regexCounter(annotatedMethod.getMethod().getJavadoc().getMethodComment(), Constants.NUMBER_PATTERN));

        return unprocessedDocResult;
    }

    @Override
    public String toString() {
        return "NumberCountFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "NumberCountFeature";
    }
}