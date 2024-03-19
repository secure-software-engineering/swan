package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;
import de.fraunhofer.iem.swan.features.doc.nlp.NLPUtils;
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
    private static Set<String> adverbList;
    private static Set<String> adjectiveList;
    int wordCounter;

    public WordCountFeature() {
        featureResult = new FeatureResult();
        nounList = new HashSet<>();
        verbList = new HashSet<>();
        adverbList = new HashSet<>();
        adjectiveList = new HashSet<>();
        wordCounter = 0;
    }

    public int wordCounter(List<CoreMap> docMap, Category category) {

        switch (category) {
            case SOURCE:
                nounList = SecurityVocabulary.SOURCE_NOUNS;
                verbList = SecurityVocabulary.SOURCE_VERBS;
                adjectiveList = SecurityVocabulary.SOURCE_ADJECTIVE;
                break;
            case SINK:
                nounList = SecurityVocabulary.SINK_NOUNS;
                verbList = SecurityVocabulary.SINK_VERBS;
                adjectiveList = SecurityVocabulary.SINK_ADJECTIVE;
                break;
            case SANITIZER:
                nounList = SecurityVocabulary.SANITIZER_NOUNS;
                verbList = SecurityVocabulary.SANITIZER_VERBS;
                break;
            case CWE78:
                nounList = SecurityVocabulary.CWE78_NOUNS;
                verbList = SecurityVocabulary.CWE78_VERBS;
                break;
            case CWE79:
                nounList = SecurityVocabulary.CWE79_NOUNS;
                verbList = SecurityVocabulary.CWE79_VERBS;
                break;
            case CWE89:
                nounList = SecurityVocabulary.CWE89_NOUNS;
                verbList = SecurityVocabulary.CWE89_VERBS;
                break;
            case CWE306:
                nounList = SecurityVocabulary.CWE306_NOUNS;
                verbList = SecurityVocabulary.CWE306_VERBS;
                break;
            case CWE601:
                nounList = SecurityVocabulary.CWE601_NOUNS;
                verbList = SecurityVocabulary.CWE601_VERBS;
                break;
            case CWE862:
                nounList = SecurityVocabulary.CWE862_NOUNS;
                verbList = SecurityVocabulary.CWE862_VERBS;
                break;
            case CWE863:
                nounList = SecurityVocabulary.CWE863_NOUNS;
                verbList = SecurityVocabulary.CWE863_VERBS;
                break;
            case AUTHENTICATION_NEUTRAL:
                nounList = SecurityVocabulary.AUTHENTICATION_NOUNS;
                verbList = SecurityVocabulary.AUTH_NO_CHANGE_VERBS;
                adjectiveList = SecurityVocabulary.AUTHENTICATION_ADJECTIVE;
                break;
            case AUTHENTICATION_TO_HIGH:
                nounList = SecurityVocabulary.AUTHENTICATION_NOUNS;
                verbList = SecurityVocabulary.AUTH_SAFE_VERBS;
                adjectiveList = SecurityVocabulary.AUTHENTICATION_ADJECTIVE;
                break;
            case AUTHENTICATION_TO_LOW:
                nounList = SecurityVocabulary.AUTHENTICATION_NOUNS;
                verbList = SecurityVocabulary.AUTH_UNSAFE_VERBS;
                adjectiveList = SecurityVocabulary.AUTHENTICATION_ADJECTIVE;
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
                        processChildren(word, semanticGraph, used, 1);
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
                //System.out.println("Weight: " + wordCounter);
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
        if (NLPUtils.isVerb(word.get(CoreAnnotations.PartOfSpeechAnnotation.class)) && verbList.contains(word.lemma())) {
            wordCounter += NLPUtils.getSemanticGraphNodeWeight(depth);
            //System.out.println(word + " " + NLPUtils.getSemanticGraphNodeWeight(depth) + " " + wordCounter);
        } else if (NLPUtils.isNoun(word.get(CoreAnnotations.PartOfSpeechAnnotation.class))
                && (nounList.contains(word.lemma()) || wordContainsNoun(word.lemma()))){
            wordCounter += NLPUtils.getSemanticGraphNodeWeight(depth);
            //System.out.println(word + " " + NLPUtils.getSemanticGraphNodeWeight(depth) + " " + wordCounter);
        }else if (NLPUtils.isAdjective(word.get(CoreAnnotations.PartOfSpeechAnnotation.class))
                && adjectiveList.contains(word.lemma())) {
            wordCounter += NLPUtils.getSemanticGraphNodeWeight(depth);
            //System.out.println(word + " " + NLPUtils.getSemanticGraphNodeWeight(depth) + " " + wordCounter);
        }else if (NLPUtils.isAdverb(word.get(CoreAnnotations.PartOfSpeechAnnotation.class))
                && adverbList.contains(word.lemma())){
            wordCounter += NLPUtils.getSemanticGraphNodeWeight(depth);
            //System.out.println(word + " " + NLPUtils.getSemanticGraphNodeWeight(depth) + " " + wordCounter);
            }



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
