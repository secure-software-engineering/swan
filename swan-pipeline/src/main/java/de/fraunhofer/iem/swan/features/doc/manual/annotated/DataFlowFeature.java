package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.nlp.NLPUtils;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

/**
 * Uses the prepositions before nouns and pronouns to determine the flow of the information.
 * For example, sinks are likely to use "to", "into", "in", etc. While sources would typically
 * use prepositions like "from", "within, etc.
 *
 * @author Oshando Johnson on 07.08.20
 */
public abstract class DataFlowFeature {

    FeatureResult featureResult;
    private static Set<String> nounList;
    private static Set<String> prepositionList;
    int wordCounter;

    public DataFlowFeature() {
        featureResult = new FeatureResult();
    }

    public int wordCounter(List<CoreMap> docMap, Category category) {

        nounList = new HashSet<>();
        prepositionList = new HashSet<>();
        wordCounter = 0;

        switch (category) {
            case SOURCE:
                nounList = SecurityVocabulary.SOURCE_NOUNS;
                prepositionList = SecurityVocabulary.SOURCE_PREPOSITIONS;
                break;
            case SINK:
                nounList = SecurityVocabulary.SINK_NOUNS;
                prepositionList = SecurityVocabulary.SINK_PREPOSITIONS;
                break;
            case SANITIZER:
                nounList = SecurityVocabulary.SANITIZER_NOUNS;
                prepositionList = SecurityVocabulary.SANITIZER_PREPOSITIONS;
                break;
            case AUTHENTICATION_NEUTRAL:
            case AUTHENTICATION_TO_HIGH:
            case AUTHENTICATION_TO_LOW:
                nounList = SecurityVocabulary.AUTHENTICATION_NOUNS;
                prepositionList = SecurityVocabulary.AUTHENTICATION_PREPOSITIONS;
                break;
        }

        // nounList.addAll(WordList.GENERAL_NOUNS);

        if (docMap != null)
            for (CoreMap sentence : docMap) {

                //Get dependency graph for the sentence
                SemanticGraph semanticGraph = sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
                Collection<IndexedWord> rootNodes = semanticGraph.getRoots();

                //Process root nodes
                if (!rootNodes.isEmpty()) {

                    Iterator iterator = rootNodes.iterator();
                    Set<IndexedWord> used = new HashSet<>();

                    IndexedWord word;
                    while (iterator.hasNext()) {

                        word = (IndexedWord) iterator.next();
                        used.add(word);
                        //System.out.println(word.lemma() + "/" + word.get(CoreAnnotations.PartOfSpeechAnnotation.class));
                        evaluatePreposition(word, null, 0);
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
            evaluatePreposition(target, edge, depth);
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
    public void evaluatePreposition(IndexedWord word, SemanticGraphEdge semanticGraphEdge, int depth) {

        //Check if preposition in relation is found in the preposition list
        if (semanticGraphEdge != null && semanticGraphEdge.getRelation().getSpecific() != null) {

            GrammaticalRelation relation = semanticGraphEdge.getRelation();

            if ((prepositionList.contains(relation.getSpecific())) &&
                    (NLPUtils.isNoun(word.get(CoreAnnotations.PartOfSpeechAnnotation.class)) && nounList.contains(word.lemma()))) {
                wordCounter += NLPUtils.getSemanticGraphNodeWeight(depth);
            }
        }
    }
}