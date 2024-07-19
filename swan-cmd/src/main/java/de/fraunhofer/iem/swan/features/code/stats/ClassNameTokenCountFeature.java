package de.fraunhofer.iem.swan.features.code.stats;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.bow.SecurityVocabulary.*;

/***
 * Evaluates the number of tokens in the method's class name.
 *
 * @author Rohith Kumar
 */

public class ClassNameTokenCountFeature implements ICodeFeature {

    private Set<String> keywords;
    private FeatureResult featureResult;
    private int numberOfMatches;
    private ArrayList<String> featureValues;
    private Set<String> classesSet;

    public ClassNameTokenCountFeature() {
        this.featureResult = new FeatureResult();
        this.classesSet = new HashSet<>();
    }

    @Override
    public FeatureResult applies(Method method){
        this.numberOfMatches = 0;
        this.classesSet.addAll(AUTHENTICATION_CLASSES_INVOKED);
        this.classesSet.addAll(SANITIZER_CLASSES_INVOKED);
        this.classesSet.addAll(SINK_CLASSES_INVOKED);
        this.classesSet.addAll(SOURCE_CLASSES_INVOKED);

        for(String item: this.classesSet){
            if(method.getClassName().toLowerCase().contains(item)){
                this.numberOfMatches ++;
            }
        }
        this.featureResult.setIntegerValue(this.numberOfMatches);
        return this.featureResult;
    }

    @Override
    public String toString() {
        return "ClassNameKeywordsCount";
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.NUMERICAL;
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        return null;
    }
}
