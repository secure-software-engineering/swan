package de.fraunhofer.iem.swan.features.doc;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.doc.embedding.DocCommentVector;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.nlp.CoreNLPExecutor;
import de.fraunhofer.iem.swan.features.doc.nlp.NLPUtils;
import edu.stanford.nlp.util.StringUtils;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.reflections.Reflections;

import java.util.*;

/**
 * @author Oshando Johnson on 06.09.20
 */
public class DocFeatureHandler {

    private Set<Class<? extends IDocFeature>> manualFeatureSet;
    private ArrayList<String> automaticFeatureSet;
    private HashMap<String, AnnotatedMethod> manualFeatureData;
    private HashMap<String, HashMap<String, Double>> automaticFeatureData;

    public DocFeatureHandler() {
        manualFeatureSet = new HashSet<>();
        manualFeatureData = new HashMap<>();
        automaticFeatureSet = new ArrayList<>();
        automaticFeatureData = new HashMap<>();
    }

    public HashMap<String, AnnotatedMethod> getManualFeatureData() {
        return manualFeatureData;
    }

    public HashMap<String, HashMap<String, Double>> getAutomaticFeatureData() {
        return automaticFeatureData;
    }

    public void evaluateManualFeatureData(Set<Method> methodSet) {
        CoreNLPExecutor nlpExecutor = new CoreNLPExecutor();
        manualFeatureData = nlpExecutor.run(new ArrayList<>(methodSet));
    }

    public void evaluateAutomaticFeatureData(Set<Method> methodSet) {
        DocCommentVector docCommentVector = new DocCommentVector();
        docCommentVector.fitVectors();

        for (Method method : methodSet) {

            String docComment = NLPUtils.cleanFirstSentence(method.getJavadoc().getMethodComment()) + " " +
                    NLPUtils.cleanFirstSentence(method.getJavadoc().getClassComment());

            List<String> words = StringUtils.split(method.getJavadoc().getMethodComment(), " ");

            if (method.getJavadoc().getMethodComment().length() > 0 && words.size() > 1) {
                NDArray array = (NDArray) docCommentVector.getParagraphVectors().inferVector(docComment);
                HashMap<String, Double> vectorValues = new HashMap<>();

                for (int index = 0; index < array.columns(); index++) {
                    vectorValues.put("dl4j-col-" + index, array.getDouble(index));
                }

                NDArray average = (NDArray) array.mean(1);
                vectorValues.put("dl4j-avg", average.getDouble(0));
                automaticFeatureData.put(method.getSootSignature(), vectorValues);
            }
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

        Reflections features = new Reflections("de.fraunhofer.iem.swan.features.doc.manual");
        manualFeatureSet = features.getSubTypesOf(IDocFeature.class);
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
    public void excludeFeatures(Set<String> exclude) {

        Set<Class<? extends IDocFeature>> features = new HashSet<>(manualFeatureSet);

        for (Class<? extends IDocFeature> feature : features) {

            if (exclude.contains(feature.getSimpleName()))
                manualFeatureSet.remove(feature);
        }
    }
}
