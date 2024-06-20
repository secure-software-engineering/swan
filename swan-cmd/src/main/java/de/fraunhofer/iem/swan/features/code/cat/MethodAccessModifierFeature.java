package de.fraunhofer.iem.swan.features.code.cat;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodAccessModifierFeature implements ICodeFeature {

    private Modifier modifier;
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;

    public enum Modifier {
        PRIVATE, PUBLIC, PROTECTED, DEFAULT
    }

    public MethodAccessModifierFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method) {
        if(method.getSootMethod().isPublic()){
            this.modifier = Modifier.PUBLIC;
        } else if (method.getSootMethod().isPrivate()) {
            this.modifier = Modifier.PRIVATE;
        } else if (method.getSootMethod().isProtected()) {
            this.modifier = Modifier.PROTECTED;
        } else {
            this.modifier = Modifier.DEFAULT;
        }
        this.featureResult.setStringValue(String.valueOf(this.modifier));
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.CATEGORICAL;
    }

    @Override
    public String toString() {
        return "MethodAccessModifier";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        //Converting Enum Values to ArrayList
        this.featureValues = new ArrayList<>(Stream.of(Modifier.values()).map(Modifier::name).collect(Collectors.toList()));
        return this.featureValues;
    }

    @Override
    public AnalysisType getFeatureAnalysisType() {
        return AnalysisType.SIGNATURE;
    }

}
