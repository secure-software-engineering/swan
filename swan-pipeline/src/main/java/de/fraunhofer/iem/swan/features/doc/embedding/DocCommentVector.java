package de.fraunhofer.iem.swan.features.doc.embedding;

import de.fraunhofer.iem.swan.cli.FileUtility;
import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.common.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * https://medium.com/towards-artificial-intelligence/how-to-get-same-word2vec-doc2vec-paragraph-vectors-in-every-time-of-training-335bac809c83
 *
 * @author Oshando Johnson on 25.09.20
 */
public class DocCommentVector {

    public static final int VECTOR_DIMENSIONS = 100;

    private ParagraphVectors paragraphVectors;

    public ParagraphVectors getParagraphVectors() {
        return paragraphVectors;
    }

    public void fitVectors() {

        ClassPathResource resource = new ClassPathResource("/dl4j-methods-first.txt");
        File file = null;
        SentenceIterator iter = null;
        try {
            file = resource.getFile();
            iter = new BasicLineIterator(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AbstractCache<VocabWord> cache = new AbstractCache<>();

        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        /*
             if you don't have LabelAwareIterator handy, you can use synchronized labels generator
              it will be used to label each document/sequence/line with it's own label.
              But if you have LabelAwareIterator ready, you can can provide it, for your in-house labels
        */
        LabelsSource source = new LabelsSource("DOC_");

        List<String> stopWords = new ArrayList<>();

        try {

            FileUtility fileUtility = new FileUtility();

            stopWords = FileUtils.readLines(fileUtility.getResourceFile("/stopwords-list.txt", null), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        paragraphVectors = new ParagraphVectors.Builder()
                // .minWordFrequency(1)
                .iterations(10) //set to 10 for final run
                .seed(0)
                .allowParallelTokenization(false)
                .workers(8)
                .epochs(10) //set to 10 for final run
                .layerSize(VECTOR_DIMENSIONS)
                .learningRate(0.1)
                .labelsSource(source)
                .stopWords(stopWords)
                .windowSize(5)
                .iterate(iter)
                .trainWordVectors(false)
                .vocabCache(cache)
                //.sequenceLearningAlgorithm("org.deeplearning4j.models.embeddings.learning.impl.sequence.DM")
                .tokenizerFactory(t)
                .sampling(0)
                .build();

        paragraphVectors.fit();
    }
}
