package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.util.WordList;
import de.fraunhofer.iem.swan.doc.nlp.NLPUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;


/**
 * @author Oshando Johnson on 07.08.20
 */
public abstract class WordCountFeature {

    FeatureResult featureResult;
    private static Set<String> nounList;
    private static Set<String> verbList;
    int wordCounter;

    public WordCountFeature() {
        featureResult = new FeatureResult();
    }

    public int wordCounter(List<CoreMap> docMap, Category category) {

        nounList = new HashSet<>();
        verbList = new HashSet<>();
        wordCounter = 0;

        switch (category) {
            case SOURCE:
                nounList = WordList.SOURCE_NOUNS;
                verbList = WordList.SOURCE_VERBS;
                break;
            case SINK:
                nounList = WordList.SINK_NOUNS;
                verbList = WordList.SINK_VERBS;
                break;
            case SANITIZER:
                nounList = WordList.SANITIZER_NOUNS;
                verbList = WordList.SANITIZER_VERBS;
                break;
            case CWE089:
                nounList = WordList.CWE089_NOUNS;
                verbList = WordList.CWE089_VERBS;
                break;
            case AUTHENTICATION_NEUTRAL:
                nounList = WordList.AUTHENTICATION_NOUNS;
                verbList = WordList.AUTH_NO_CHANGE_VERBS;
                break;
            case AUTHENTICATION_TO_HIGH:
                nounList = WordList.AUTHENTICATION_NOUNS;
                verbList = WordList.AUTH_SAFE_VERBS;
                break;
            case AUTHENTICATION_TO_LOW:
                nounList = WordList.AUTHENTICATION_NOUNS;
                verbList = WordList.AUTH_UNSAFE_VERBS;
                break;
        }

        // nounList.addAll(WordList.GENERAL_NOUNS);

        if (docMap != null)
            for (CoreMap sentence : docMap) {

                //Get dependency graph for the sentence
                SemanticGraph semanticGraph = sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
                Collection<IndexedWord> rootNodes = semanticGraph.getRoots();

                // System.out.println(sentence);
                //System.out.println("Dependencies:\n" + semanticGraph.toString());

                //Process root nodes
                if (!rootNodes.isEmpty()) {

                    Iterator iterator = rootNodes.iterator();
                    Set<IndexedWord> used = new HashSet<>();

                    IndexedWord word;
                    while (iterator.hasNext()) {

                        word = (IndexedWord) iterator.next();
                        used.add(word);
                        //System.out.println(word.lemma() + "/" + word.get(CoreAnnotations.PartOfSpeechAnnotation.class));
                        evaluateWord(word, null, 0);
                        processChildren(word, semanticGraph, used, 0);
                    }

                    Set<IndexedWord> nodes = new HashSet<>();
                    nodes.addAll(semanticGraph.vertexSet());
                    nodes.removeAll(used);

                    while (!nodes.isEmpty()) {

                        word = (IndexedWord) iterator.next();
                        processChildren(word, semanticGraph, used, 0);
                        nodes.removeAll(used);
                    }
                }
               // System.out.println("Weight: " + wordCounter);
            }
        return wordCounter;
    }

    public int wordCounter(List<CoreMap> docMap, Category category, boolean verb) {

        if (verb) {
            verbList = new HashSet<>();

        }
        nounList = new HashSet<>();

        wordCounter = 0;

        switch (category) {
            case SOURCE:
                nounList = WordList.SOURCE_NOUNS;
                verbList = WordList.SOURCE_VERBS;
                break;
            case SINK:
                nounList = WordList.SINK_NOUNS;
                verbList = WordList.SINK_VERBS;
                break;
            case SANITIZER:
                nounList = WordList.SANITIZER_NOUNS;
                verbList = WordList.SANITIZER_VERBS;
                break;
            case CWE089:
                nounList = WordList.CWE089_NOUNS;
                verbList = WordList.CWE089_VERBS;
                break;
            case AUTHENTICATION_NEUTRAL:
                nounList = WordList.AUTHENTICATION_NOUNS;
                verbList = WordList.AUTH_NO_CHANGE_VERBS;
                break;
            case AUTHENTICATION_TO_HIGH:
                nounList = WordList.AUTHENTICATION_NOUNS;
                verbList = WordList.AUTH_SAFE_VERBS;
                break;
            case AUTHENTICATION_TO_LOW:
                nounList = WordList.AUTHENTICATION_NOUNS;
                verbList = WordList.AUTH_UNSAFE_VERBS;
                break;
        }

        // nounList.addAll(WordList.GENERAL_NOUNS);

        if (docMap != null)
            for (CoreMap sentence : docMap) {

                //Get dependency graph for the sentence
                SemanticGraph semanticGraph = sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
                Collection<IndexedWord> rootNodes = semanticGraph.getRoots();

                // System.out.println(sentence);
                //System.out.println("Dependencies:\n" + semanticGraph.toString());

                //Process root nodes
                if (!rootNodes.isEmpty()) {

                    Iterator iterator = rootNodes.iterator();
                    Set<IndexedWord> used = new HashSet<>();

                    IndexedWord word;
                    while (iterator.hasNext()) {

                        word = (IndexedWord) iterator.next();
                        used.add(word);
                        //System.out.println(word.lemma() + "/" + word.get(CoreAnnotations.PartOfSpeechAnnotation.class));
                        evaluateWord(word, null, 0);
                        processChildren(word, semanticGraph, used, 0);
                    }

                    Set<IndexedWord> nodes = new HashSet<>();
                    nodes.addAll(semanticGraph.vertexSet());
                    nodes.removeAll(used);

                    while (!nodes.isEmpty()) {

                        word = (IndexedWord) iterator.next();
                        processChildren(word, semanticGraph, used, 0);
                        nodes.removeAll(used);
                    }
                }
                // System.out.println("Weight: " + wordCounter);
            }
        return wordCounter;
    }


    private void processChildren(IndexedWord curr, SemanticGraph semanticGraph, Set<IndexedWord> used, int depth) {
        used.add(curr);
        List<SemanticGraphEdge> edges = semanticGraph.outgoingEdgeList(curr);
        Collections.sort(edges);
        Iterator iterator = edges.iterator();

        while (iterator.hasNext()) {
            SemanticGraphEdge edge = (SemanticGraphEdge) iterator.next();
            IndexedWord target = edge.getTarget();

            //System.out.println("2: " + target.lemma() + "/" + target.get(CoreAnnotations.PartOfSpeechAnnotation.class) + "/" + edge.getRelation().getShortName() + " " + edge.getRelation().getLongName());
            evaluateWord(target, edge, depth);
            if (!used.contains(target)) {
                processChildren(target, semanticGraph, used, depth + 1);
            }
        }
    }

    /**
     * Evaluates word based on list of verbs and nouns.
     *
     * @param word              IndexWord
     * @param semanticGraphEdge SemanticGraphEdge
     */
    public void evaluateWord(IndexedWord word, SemanticGraphEdge semanticGraphEdge, int depth) {

        //Computes weights for nouns and verbs based on depth
        if (NLPUtils.isVerb(word.get(CoreAnnotations.PartOfSpeechAnnotation.class)) && verbList.contains(word.lemma()))
            wordCounter += NLPUtils.getSemanticGraphNodeWeight(depth);
        else if (NLPUtils.isNoun(word.get(CoreAnnotations.PartOfSpeechAnnotation.class))
                && (nounList.contains(word.lemma()) || wordContainsNoun(word.lemma())))
            wordCounter += NLPUtils.getSemanticGraphNodeWeight(depth);


        //Split method name and check if words are in the WordList

        //Checks

        /*if (semanticGraphEdge == null) {
            //Checks if the root word is in the noun or verb lists
            if ((verbList.contains(word.lemma()) && NLPUtils.isVerb(word.get(CoreAnnotations.PartOfSpeechAnnotation.class)))
                    || (nounList.contains(word.lemma()) && NLPUtils.isNoun(word.get(CoreAnnotations.PartOfSpeechAnnotation.class))))
                wordCounter += getWeight(depth);
        } else {

            if (semanticGraphEdge.getRelation().getShortName().equals("obj") ||
                    semanticGraphEdge.getRelation().getShortName().equals("nsubj")) {
                if (nounList.contains(word.lemma()))
                    wordCounter += getWeight(depth);
            } else {

                //Default case checks if the verb or nouns belong to the Wordlist
                if (NLPUtils.isVerb(word.get(CoreAnnotations.PartOfSpeechAnnotation.class)) && verbList.contains(word.lemma()))
                    wordCounter += getWeight(depth);
                else if (NLPUtils.isNoun(word.get(CoreAnnotations.PartOfSpeechAnnotation.class)) && nounList.contains(word.lemma()))
                    wordCounter += getWeight(depth);
            }
        }*/
    }


    boolean wordContainsNoun(String word) {

        for (String value : nounList) {
            if (word.contains(value))
                return true;
        }
        return false;
    }

}
