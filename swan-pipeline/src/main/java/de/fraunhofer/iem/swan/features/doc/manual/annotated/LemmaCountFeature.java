package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Counts the number of unique lemmas in the doc comment.
 *
 * @author Oshando Johnson on 23.07.20
 */
public class LemmaCountFeature implements IDocFeature {

    private FeatureResult featureResult;

    public LemmaCountFeature() {
        featureResult = new FeatureResult();
    }


    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(countLemmas(annotatedMethod.getMethodMap()));
        featureResult.setClassValue(countLemmas(annotatedMethod.getClassMap()));

        return featureResult;
    }

    public int countLemmas(List<CoreMap> sentences){

        Set<String> lemmas = new HashSet<>();

        if(sentences!=null)
        for (CoreLabel token : sentences.get(0).get(CoreAnnotations.TokensAnnotation.class)) {

            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            lemmas.add(lemma);
        }

        return lemmas.size();
    }


    @Override
    public String toString() {
        return "LemmaCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "LemmaCountFeature";
    }
}
