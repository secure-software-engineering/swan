package de.fraunhofer.iem.swan.features.doc.manual.annotated;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;


/**
 * Evaluates if cross-site scripting words are found in the doc comment.
 * * <p>
 * * The number of cross-site scripting verbs and nouns is based on the
 * * {@link SecurityVocabulary#CWE079_VERBS}
 * * and {@link SecurityVocabulary#CWE079_NOUNS} lists.
 *
 * @author Oshando Johnson on 30.09.20
 */
public class CrossSiteScriptingCountFeature extends WordCountFeature implements IDocFeature {

    public CrossSiteScriptingCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        featureResult.setMethodValue(wordCounter(annotatedMethod.getMethodMap(), Category.CWE079));
        featureResult.setClassValue(wordCounter(annotatedMethod.getClassMap(), Category.CWE079));

        return featureResult;
    }

    @Override
    public String toString() {
        return "CrossSiteScriptingCountFeature [" + featureResult + "]";
    }

    @Override
    public String getName() {
        return "CrossSiteScriptingCountFeature";
    }
}
