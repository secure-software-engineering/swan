package de.fraunhofer.iem.swan.features.doc.nlp;

import de.fraunhofer.iem.swan.data.Method;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

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
        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
        properties.setProperty("ser.useSUTime", "false");
        pipeline = new StanfordCoreNLP(properties);
    }

    public HashMap<String, AnnotatedMethod> run(ArrayList<Method> methods) {

        logger.info("Starting NLP annotation for {} methods", methods.size());
        HashMap<String, AnnotatedMethod> results = new HashMap<>();

        for (Method method : methods) {

            AnnotatedMethod result = new AnnotatedMethod(method);

            //Process method comments
            logger.debug("Annotating method {}", method.getSimpleSignature());
            result.setMethodMap(getAnnotation(method.getJavadoc().getMethodComment()));

            //Process class comment
            if (method.getJavadoc().getClassComment().length() > 0) {
                result.setClassMap(getAnnotation(method.getJavadoc().getClassComment()));
            }
            results.put(method.getSignature(), result);
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
