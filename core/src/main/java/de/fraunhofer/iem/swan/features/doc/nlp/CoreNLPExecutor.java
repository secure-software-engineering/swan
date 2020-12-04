package de.fraunhofer.iem.swan.features.doc.nlp;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;
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
        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        pipeline = new StanfordCoreNLP(properties);
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


        logger.info("Starting NLP annotation for {} methods", methods.size());
        HashMap<String, AnnotatedMethod> results = new HashMap<>();

        for (Method method : methods) {

            AnnotatedMethod result = new AnnotatedMethod(method);

            //Process method comments
            //if (method.getJavadoc().getMethodComment().length()>0) {

            logger.info("Annotating method {}", method.getSimpleSignature());
            result.setMethodMap(getAnnotation(method.getJavadoc().getMethodComment()));

            //Process class comment
            if (method.getJavadoc().getClassComment().length() > 0) {
                result.setClassMap(getAnnotation(method.getJavadoc().getClassComment()));
            }
            results.put(method.getSignature(), result);
            // }
        }
        return results;
    }

    public List<CoreMap> getAnnotation(String comment) {

        //Clean text
        String cleanedText = NLPUtils.cleanFirstSentence(comment);
        Annotation document = new Annotation(cleanedText.toLowerCase());

        pipeline.annotate(document);
        return document.get(CoreAnnotations.SentencesAnnotation.class);
    }
}
