package de.fraunhofer.iem.swan.doc.features.automatic;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.doc.nlp.NLPUtils;
import de.fraunhofer.iem.swan.io.Parser;

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
import org.nd4j.linalg.cpu.nativecpu.NDArray;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public static void main(String[] args) {

        //Export method and class doc comments

        Parser parser = new Parser();
        parser.parse("/Users/oshando/Projects/thesis/03-code/swan/swan_core/src/main/resources/training-set-javadoc.json");

        for(Method method: parser.getMethods()){
            if(method.getJavadoc().getMethodComment().length()>0)
            System.out.println(NLPUtils.cleanSentence(method.getJavadoc().getMergedComments()));
        }
    }

    public static void mainsd(String[] args) {

        DocCommentVector docCommentVector = new DocCommentVector();
        docCommentVector.fitVectors();

        NDArray array = (NDArray)  docCommentVector.paragraphVectors.inferVector("Performs the request and returns the result object.");

        System.out.println(array.toString());

        for (int index = 0; index < array.columns(); index++) {
            System.out.print(array.getDouble(index)+",");
        }


        NDArray average = (NDArray)array.mean(1);


        System.out.println("ST: "+array.columns()+" "+array.rows());
        System.out.println("AVG: "+average.getDouble(0));

       array = (NDArray)array.put(100, average);

        System.out.println("ST: "+array.columns()+" "+array.rows());

    }
    public void fitVectors() {


        ClassPathResource resource = new ClassPathResource("/dl4j-methods.txt");
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
            stopWords = FileUtils.readLines(new File("/Users/oshando/Projects/thesis/03-code/swan/swan_core/src/main/resources/stopwords-list.txt"), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        paragraphVectors = new ParagraphVectors.Builder()
               // .minWordFrequency(1)
                .iterations(5)
                .epochs(1)
                .layerSize(VECTOR_DIMENSIONS)
                .learningRate(0.1)
                .labelsSource(source)
                .stopWords(stopWords)
                .windowSize(5)
                .iterate(iter)
                .trainWordVectors(false)
                .vocabCache(cache)
                .tokenizerFactory(t)
                .sampling(0)
                .build();

        paragraphVectors.fit();

        /*
            In training corpus we have few lines that contain pretty close words invloved.
            These sentences should be pretty close to each other in vector space
            line 3721: This is my way .
            line 6348: This is my case .
            line 9836: This is my house .
            line 12493: This is my world .
            line 16393: This is my work .
            this is special sentence, that has nothing common with previous sentences
            line 9853: We now have one .
            Note that docs are indexed from 0
         */

       // System.out.println(paragraphVectors.inferVector("Returns the absolute form of this abstract pathname. Equivalent to new File(this..getAbsolutePath)."));


     //   double similarity1 = paragraphVectors.similarity("DOC_0", "DOC_6");
     //   System.out.println("9836/12493 ('This is my house .'/'This is my world .') similarity: " + similarity1);
/*
        double similarity2 = vec.similarity("DOC_3720", "DOC_16392");
        System.out.println("3721/16393 ('This is my way .'/'This is my work .') similarity: " + similarity2);

        double similarity3 = vec.similarity("DOC_6347", "DOC_3720");
        System.out.println("6348/3721 ('This is my case .'/'This is my way .') similarity: " + similarity3);

        // likelihood in this case should be significantly lower
        double similarityX = vec.similarity("DOC_3720", "DOC_9852");
        System.out.println("3721/9853 ('This is my way .'/'We now have one .') similarity: " + similarityX +
                "(should be significantly lower)");
*/

    }
     /*   ClassPathResource resource = new ClassPathResource("/paravec/simple.pv");
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        // we load externally originated model
        ParagraphVectors vectors = WordVectorSerializer.readParagraphVectors(resource.getFile());
        vectors.setTokenizerFactory(t);
        vectors.getConfiguration().setIterations(1); // please note, we set iterations to 1 here, just to speedup inference

        /*
        // here's alternative way of doing this, word2vec model can be used directly
        // PLEASE NOTE: you can't use Google-like model here, since it doesn't have any Huffman tree information shipped.
        ParagraphVectors vectors = new ParagraphVectors.Builder()
            .useExistingWordVectors(word2vec)
            .build();
        */
        // we have to define tokenizer here, because restored model has no idea about it


        /*INDArray inferredVectorA = vectors.inferVector("This is my world .");
        INDArray inferredVectorA2 = vectors.inferVector("This is my world .");
        INDArray inferredVectorB = vectors.inferVector("This is my way .");

        // high similarity expected here, since in underlying corpus words WAY and WORLD have really close context
        log.info("Cosine similarity A/B: {}", Transforms.cosineSim(inferredVectorA, inferredVectorB));

        // equality expected here, since inference is happening for the same sentences
        log.info("Cosine similarity A/A2: {}", Transforms.cosineSim(inferredVectorA, inferredVectorA2));
    }*/

    public static void mains(String[] args) {

      /*  ArrayList<String> testMethods = new ArrayList<>();
        //sinks
        testMethods.add("void java.util.logging.Logger.info(java.lang.String)");
        testMethods.add("java.sql.Connection java.sql.DriverManager.getConnection(java.lang.String, java.util.Properties)");
        testMethods.add("void java.io.PrintWriter.write(java.lang.String, int, int)");

        //sources
        testMethods.add("java.lang.String java.io.DataInputStream.readUTF(java.io.DataInput)");
        testMethods.add("java.io.File java.io.File.getAbsoluteFile()");
        testMethods.add("java.lang.String java.util.Properties.getProperty(java.lang.String)");

        //sanitizers
        testMethods.add("java.lang.String java.net.URLDecoder.decode(java.lang.String, java.lang.String)");
        testMethods.add("java.lang.String java.net.URLEncoder.encode(java.lang.String, java.lang.String)");
        testMethods.add("java.lang.String java.lang.String.replace(java.lang.CharSequence, java.lang.CharSequence)");
        /**/


       /* Parser parser = new Parser();
        parser.parse("/Users/oshando/Projects/thesis/03-code/swandoc/src/main/resources/training-set-javadoc.json");
        Set<Method> methodSet = parser.methods();

        for(Method m: methodSet){

           // if(testMethods.contains(m.getJavaSignature()))
                System.out.println(NLPUtils.cleanSentence(m.getJavadoc().getMethodComment()));
        }*/
    }
}
