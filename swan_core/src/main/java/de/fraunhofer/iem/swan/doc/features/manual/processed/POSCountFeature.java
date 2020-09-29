package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.util.Constants;
import de.fraunhofer.iem.swan.doc.nlp.NLPUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;

/**
 * Counts the occurrences of a part of speech in the doc comment.
 * https://towardsdatascience.com/natural-language-processing-for-automated-feature-engineering-46a61a3930b1
 * https://docs.featuretools.com/en/stable/generated/nlp_primitives.PartOfSpeechCount.html
 *
 * @author Oshando Johnson on 19.08.20
 */
public abstract class POSCountFeature {
    FeatureResult featureResult;

    public POSCountFeature() {
        featureResult = new FeatureResult();
    }

    public int countPartOfSpeech(List<CoreMap> docMap, Constants.POS pos) {

        int counter = 0;
        if (docMap != null)
            for (CoreMap sentence : docMap) {

                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {

                    // this is the POS tag of the token
                    String posTag = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                    if (pos == Constants.POS.NOUN && NLPUtils.isNoun(posTag))
                        counter++;
                    else if (pos == Constants.POS.VERB && NLPUtils.isVerb(posTag))
                        counter++;
                    if (pos == Constants.POS.ADVERB && NLPUtils.isAdverb(posTag))
                        counter++;
                    else if (pos == Constants.POS.ADJECTIVE && NLPUtils.isAdjective(posTag))
                        counter++;
                    if (pos == Constants.POS.PREPOSITION && NLPUtils.isPreposition(posTag))
                        counter++;
                    else if (pos == Constants.POS.PUNCTUATION && NLPUtils.isPunctuation(posTag))
                        counter++;
                    if (pos == Constants.POS.PRONOUN && NLPUtils.isPronoun(posTag))
                        counter++;
                    else if (pos == Constants.POS.CONJUNCTION && NLPUtils.isConjunction(posTag))
                        counter++;
                }
            }
        return counter;
    }
}
