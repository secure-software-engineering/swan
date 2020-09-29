package de.fraunhofer.iem.swan.doc.features.manual.processed;

import de.fraunhofer.iem.swan.doc.features.manual.FeatureResult;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Counts the number of stop words in the doc comment.
 *
 * @author Oshando Johnson on 23.07.20
 */
public class StopWordCountFeature implements IDocFeature {

    private FeatureResult featureResult;
    private Set<String> stopWordSet;

    public StopWordCountFeature() {
        featureResult = new FeatureResult();

        //TODO load stop words list only once
        try {
            stopWordSet = new HashSet<>(FileUtils.readLines(new File("src/main/resources/stopwords-list.txt"), Charset.defaultCharset()));
        } catch (IOException e) {
            stopWordSet = new HashSet<>();
        }
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(countStopWords(annotatedMethod.getMethodMap()));
        featureResult.setClassValue(countStopWords(annotatedMethod.getClassMap()));

        return featureResult;
    }

    public int countStopWords(List<CoreMap> sentences) {

        ArrayList<String> lemmas = new ArrayList<>();

        if(sentences!=null)
        for (CoreLabel token : sentences.get(0).get(CoreAnnotations.TokensAnnotation.class)) {

            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            if (stopWordSet.contains(lemma))
                lemmas.add(lemma);
        }

        return lemmas.size();
    }

    @Override
    public String toString() {
        return "StopWordCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "StopWordCountFeature";
    }
}
