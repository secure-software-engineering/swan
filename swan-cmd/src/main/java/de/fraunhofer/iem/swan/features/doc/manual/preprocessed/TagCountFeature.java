package de.fraunhofer.iem.swan.features.doc.manual.preprocessed;

import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.nlp.NLPUtils;

/**
 * @author Oshando Johnson on 23.08.20
 */
public abstract class TagCountFeature {

    FeatureResult unprocessedDocResult;

    public TagCountFeature() {
        unprocessedDocResult = new FeatureResult();
    }

    /**
     * @param text
     * @param tag
     * @return
     */
    public int countTags(String text, Constants.TAG tag) {

        switch (tag) {

            case SEE:
                return NLPUtils.regexCounter(text, Constants.SEE_TAG_REGEX);
            case CODE:
                return NLPUtils.regexCounter(text, Constants.CODE_TAG_REGEX);
            case LINK:
                return NLPUtils.regexCounter(text, Constants.LINK_TAG_REGEX);
            case DEPRECATED:
                return NLPUtils.regexCounter(text, Constants.DEPRECATED_TAG_REGEX);
            default:
                return 0;
        }
    }
}
