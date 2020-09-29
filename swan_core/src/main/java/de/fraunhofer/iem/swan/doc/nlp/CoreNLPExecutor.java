package de.fraunhofer.iem.swan.doc.nlp;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.doc.util.SecurityVocabulary;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Oshando Johnson on 09.06.20
 */
public class CoreNLPExecutor {

    private Properties properties;
    private StanfordCoreNLP pipeline;

    private static final Logger logger = LoggerFactory.getLogger(CoreNLPExecutor.class);

    public CoreNLPExecutor() {
        logger.info("Initializing CoreNLP pipeline");
        properties = new Properties();
        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(properties);
        System.out.println("done set up");
    }

    public static void mains(String[] args) {
        HashSet<String> general = new HashSet<>();
        general.addAll(SecurityVocabulary.AUTHENTICATION_NOUNS);
        general.addAll(SecurityVocabulary.SOURCE_NOUNS);
        general.addAll(SecurityVocabulary.SINK_NOUNS);
        general.addAll(SecurityVocabulary.SOURCE_NOUNS);
        general.addAll(SecurityVocabulary.CWE089_NOUNS);

        List<String> list = new ArrayList<>(general);

        Collections.sort(list);
        System.out.println(StringUtils.join(list, "\", \""));

    }

    public static void main(String[] args) {

        String s = "obl:to";

        System.out.println(s.substring(s.indexOf(":") + 1));

        CoreNLPExecutor coreNLPExecutor = new CoreNLPExecutor();

        List<CoreMap> s1 = coreNLPExecutor.getAnnotation("Deletes all rows in the table.");
        List<CoreMap> s2 = coreNLPExecutor.getAnnotation("Log a FINEST message to the console.");


        for (CoreLabel token : s1.get(0).get(CoreAnnotations.TokensAnnotation.class)) {
            // this is the text of the token

            System.out.println("L: " + token.lemma());

        }

        SemanticGraph semanticGraph2 = s1.get(0).get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);

        System.out.println(semanticGraph2);

        SemanticGraph semanticGraph = s2.get(0).get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);

        System.out.println(semanticGraph);

        for (CoreLabel token : s1.get(0).get(CoreAnnotations.TokensAnnotation.class)) {
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            System.out.println(lemma);
        }
        for (CoreLabel token : s2.get(0).get(CoreAnnotations.TokensAnnotation.class)) {
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            System.out.println(lemma);
        }
    }

    public HashMap<String, AnnotatedMethod> run(ArrayList<Method> methods) {

        ArrayList<String> testMethods = new ArrayList<>();
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

        logger.info("Starting NLP annotation for {} methods", methods.size());
        HashMap<String, AnnotatedMethod> results = new HashMap<>();

        for (Method method : methods) {

            AnnotatedMethod result = new AnnotatedMethod(method);

            //Process method comments
            if (!method.getJavadoc().getMethodComment().equals("")) {

                result.setMethodMap(getAnnotation(NLPUtils.cleanFirstSentence(method.getJavadoc().getMethodComment())));

                //Process class comment
                if (!method.getJavadoc().getClassComment().equals("")) {
                    result.setClassMap(getAnnotation(NLPUtils.cleanFirstSentence(method.getJavadoc().getClassComment())));
                }
                results.put(method.getSignature(),result);
            }
        }
        return results;
    }

    public List<CoreMap> getAnnotation(String comment) {

        //Clean text
        String cleanedText = NLPUtils.cleanFirstSentence(comment);
        Annotation document = new Annotation(cleanedText.toLowerCase());

        pipeline.annotate(document);
        return document.get(CoreAnnotations.SentencesAnnotation.class);

     /*   for (AnnotatedMethod doc : results) {
            System.out.println("CLASS>>>>"+doc.getMethod().getClassDoc().getSummary());
            System.out.println("METHOD>>>>"+doc.getMethod().getMethodDoc().getSummary());

            SinkWordCountFeature preFeature = new SinkWordCountFeature();
            System.out.println("SinkWordCountFeature: " + preFeature.evaluate(doc).toString());

            SourceWordCountFeature source = new SourceWordCountFeature();
            System.out.println("SourceWordCountFeature: " + source.evaluate(doc).toString());

            SanitizerWordCountFeature sanitizer = new SanitizerWordCountFeature();
            System.out.println("SanitizerWordCountFeature: " + sanitizer.evaluate(doc).toString() + "\n");

            StopWordCountFeature stop = new StopWordCountFeature();
            System.out.println("StopWordCountFeature: " + stop.evaluate(doc).toString() + "\n");

        }

        /*for (AnnotatedMethod doc : results) {

            // these are all the sentences in this document
            List<CoreMap> sentences = doc.getMethodMap();

            System.out.println(sentences.toString());

            List<String> words = new ArrayList<>();
            List<String> posTags = new ArrayList<>();
            List<String> nerTags = new ArrayList<>();

            for (CoreMap sentence : sentences) {
                // traversing the words in the current sentence
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    // this is the text of the token
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    words.add(word);

                    // this is the POS tag of the token
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    posTags.add(pos);
                    // this is the NER label of the token
                    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                    nerTags.add(ne);

                    System.out.println(word + "--->" + pos);
                }

                // This is the syntactic parse tree of sentence
                Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
                System.out.println("Tree:\n" + tree);
                // This is the dependency graph of the sentence
                SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
                System.out.println("Dependencies\n:" + dependencies);
            }
            System.out.println(words.toString());
            System.out.println(posTags.toString());
            System.out.println(nerTags.toString());
        }*/
    }
}
