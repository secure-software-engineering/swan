package de.fraunhofer.iem.swan.features.doc.nlp;

import edu.stanford.nlp.simple.Document;
import org.jsoup.Jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Oshando Johnson on 23.07.20
 */
public class NLPUtils {


    public enum POS{
        NOUN,
        VERB,
        ADJECTIVE,
        PRONOUN,
        ADVERB,
        PUNCTUATION,
        PREPOSITION,
        CONJUNCTION
    }

    public static String cleanFirstSentence(String text) {

        if (text.length() > 0) {
            //Obtain first sentence from Javadoc comment
            Document javadocComment = new Document(text);

            return cleanText(javadocComment.sentences().get(0).text());
        } else
            return text;
    }


    public static String cleanText(String text) {

            return Jsoup.parse(text).text()
                    .replaceAll("<.*?>", "")
                    .replaceAll("<&lt;[^&]*&gt;>", "")
                    .replaceAll("\\{\\S+.\\s", "")
                    .replaceAll("#", ".")
                    .replace("\n", "")
                    .replaceAll("\\u0000", "null")
                    .replaceAll("\\s{2,}", " ")
                    .replaceAll("}", "")
                    .trim();
    }

    public static int regexCounter(String text, String regex) {

        Pattern LINK_PATTERN = Pattern.compile(regex);

        Matcher matcher = LINK_PATTERN.matcher(text);

        int matches = 0;
        while (matcher.find()) {
            matches++;
        }
        return matches;
    }

    /**
     * Checks if the part of speech tag is a verb.
     *
     * @param pos string part of speech
     * @return boolean if pos is a verb
     */
    public static boolean isVerb(String pos) {

        return pos.startsWith("V");
    }

    /**
     * Checks if the part of speech tag is a noun.
     *
     * @param pos string part of speech
     * @return boolean if pos is a noun
     */
    public static boolean isNoun(String pos) {

        return pos.startsWith("N");
    }

    /**
     * Checks if the part of speech tag is a pronoun.
     *
     * @param pos string part of speech
     * @return boolean if pos is a verb
     */
    public static boolean isPronoun(String pos) {

        return pos.startsWith("PP");
    }

    /**
     * Checks if the part of speech tag is a adjective.
     *
     * @param pos string part of speech
     * @return boolean if pos is a noun
     */
    public static boolean isAdjective(String pos) {

        return pos.startsWith("JJ");
    }

    /**
     * Checks if the part of speech tag is a adverb.
     *
     * @param pos string part of speech
     * @return boolean if pos is a noun
     */
    public static boolean isAdverb(String pos) {

        return pos.startsWith("RB");
    }

    /**
     * Checks if the part of speech tag is a punctuation or a symbol.
     *
     * @param pos string part of speech
     * @return boolean if pos is a noun
     */
    public static boolean isPunctuation(String pos) {
        switch (pos) {
            case "SYM":
            case "$":
            case "#":
            case "”":
            case "“":
            case "(":
            case ")":
            case ",":
            case ".":
            case ":":
            case "‘":
            case "’":
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if the part of speech tag is a conjunction.
     *
     * @param pos string part of speech
     * @return boolean if pos is a verb
     */
    public static boolean isConjunction(String pos) {

        return pos.startsWith("CC");
    }

    /**
     * Checks if the part of speech tag is a preposition.
     *
     * @param pos string part of speech
     * @return boolean if pos is a noun
     */
    public static boolean isPreposition(String pos) {

        return pos.startsWith("IN");
    }

    /**
     * Assigns weight for word based on its depth in the semantic graph.
     *
     * @param depth int dept of node
     * @return weight of node
     */
    public static int getSemanticGraphNodeWeight(int depth) {

        //100 10 5 1
        switch (depth) {
            case 0:
                return 100;
            case 1:
                return 30;
            case 2:
                return 10;
            default:
                return 2;
        }
    }
}