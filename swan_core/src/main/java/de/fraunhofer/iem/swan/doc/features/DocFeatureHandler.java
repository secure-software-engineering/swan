package de.fraunhofer.iem.swan.doc.features;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.doc.features.automatic.DocCommentVector;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.nlp.CoreNLPExecutor;
import de.fraunhofer.iem.swan.doc.nlp.NLPUtils;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.reflections.Reflections;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Oshando Johnson on 06.09.20
 */
public class DocFeatureHandler {

    private Set<Class<? extends IDocFeature>> manualFeatureSet;
    private ArrayList<String> automaticFeatureSet;
    private HashMap<String, AnnotatedMethod> manualFeatureData;
    private HashMap<String, HashMap<String, Double>> automaticFeatureData;
    private Set<Method> methodSet;


    public DocFeatureHandler(Set<Method> trainingSet) {
        manualFeatureSet = new HashSet<>();
        manualFeatureData = new HashMap<>();
        automaticFeatureSet = new ArrayList<>();
        automaticFeatureData = new HashMap<>();
        methodSet = new HashSet<>(trainingSet);
    }

    public Set<Method> getMethodSet() {
        return methodSet;
    }

    public HashMap<String, AnnotatedMethod> getManualFeatureData() {
        return manualFeatureData;
    }

    public HashMap<String, HashMap<String, Double>> getAutomaticFeatureData() {
        return automaticFeatureData;
    }

    public void evaluateManualFeatureData() {
        CoreNLPExecutor nlpExecutor = new CoreNLPExecutor();
        manualFeatureData = nlpExecutor.run(new ArrayList<>(methodSet));
    }

    public void evaluateAutomaticFeatureData() {
        DocCommentVector docCommentVector = new DocCommentVector();
        docCommentVector.fitVectors();

        for (Method method : methodSet) {

            String docComment = NLPUtils.cleanSentence(method.getJavadoc().getMergedComments());
            NDArray array = (NDArray) docCommentVector.getParagraphVectors().inferVector(docComment);

            HashMap<String, Double> vectorValues = new HashMap<>();

            for (int index = 0; index < array.columns(); index++) {
                vectorValues.put("dl4j-col-" + index, array.getDouble(index));
            }

            NDArray average = (NDArray) array.mean(1);
            vectorValues.put("dl4j-avg", average.getDouble(0));
            automaticFeatureData.put(method.getSignature(), vectorValues);
        }
    }


    public ArrayList<String> getAutomaticFeatureSet() {

        return automaticFeatureSet;
    }

    /**
     * @return
     */
    public Set<Class<? extends IDocFeature>> getManualFeatureSet() {
        return manualFeatureSet;
    }

    /**
     * @return
     */
    public void initialiseManualFeatureSet() {

        Reflections features = new Reflections("de.fraunhofer.iem.swan.doc.features.manual");
        manualFeatureSet = features.getSubTypesOf(IDocFeature.class);

        excludeFeatures();

    }

    public void initialiseAutomaticFeatureSet() {

        for (int index = 0; index < DocCommentVector.VECTOR_DIMENSIONS; index++) {
            automaticFeatureSet.add("dl4j-col-" + index);
        }
        automaticFeatureSet.add("dl4j-avg");
    }

    /**
     *
     */
    public void excludeFeatures() {
        Set<String> exclude = new HashSet<>();

        //Unprocessed features
//        exclude.add("AverageTokenLengthFeature");
//        exclude.add("AverageSentenceLengthFeature");
//
//        exclude.add("SentenceCountFeature");
//        exclude.add("TokenCountFeature");
//        exclude.add("UppercaseWordsCountFeature");
//        exclude.add("NumberCountFeature");
//        exclude.add("CharacterCountFeature");
//
//        exclude.add("DeprecatedTagCountFeature");
//        exclude.add("CodeTagCountFeature");
//        exclude.add("SeeTagCountFeature");
//        exclude.add("LinkTagCountFeature");
//        exclude.add("IncompleteCodeFeature");


        //Processed features

//        exclude.add("SanitizerWordCountFeature");
//        exclude.add("SqlInjectionCountFeature");
//        exclude.add("AuthUnsafeWordCountFeature");
//        exclude.add("AuthSafeWordCountFeature");
//        exclude.add("SourceWordCountFeature");
//        exclude.add("AuthNoChangeWordCountFeature");


        /*

        @attribute LinkTagCountFeature numeric
@attribute SanitizerWordCountFeature numeric
@attribute AverageTokenLengthFeature numeric
@attribute AdverbCountFeature numeric
@attribute SqlInjectionCountFeature numeric
@attribute AuthUnsafeWordCountFeature numeric
@attribute TokenCountFeature numeric
@attribute AuthenticationDataFlowFeature numeric
@attribute UppercaseWordsCountFeature numeric
@attribute PrepositionCountFeature numeric
@attribute PronounCountFeature numeric
@attribute AverageSentenceLengthFeature numeric
@attribute CodeTagCountFeature numeric
@attribute IncompleteCodeFeature numeric
@attribute LemmaCountFeature numeric
@attribute AuthSafeWordCountFeature numeric
@attribute AuthNoChangeWordCountFeature numeric
@attribute SentenceCountFeature numeric
@attribute VerbCountFeature numeric
@attribute StopWordCountFeature numeric
@attribute SanitizerDataFlowFeature numeric
@attribute SeeTagCountFeature numeric
@attribute SinkWordCountFeature numeric
@attribute PunctuationCountFeature numeric
@attribute SourceDataFlowFeature numeric
@attribute DeprecatedTagCountFeature numeric
@attribute CharacterCountFeature numeric
@attribute NounCountFeature numeric
@attribute NumberCountFeature numeric
@attribute SinkDataFlowFeature numeric
@attribute AdjectiveCountFeature numeric
@attribute SourceWordCountFeature numeric
@attribute ConjunctionCountFeature numeric
         */


        Set<Class<? extends IDocFeature>> features = new HashSet<>(manualFeatureSet);

        for (Class<? extends IDocFeature> feature : features) {

            if (exclude.contains(feature.getSimpleName()))
                manualFeatureSet.remove(feature);
        }
    }
}
