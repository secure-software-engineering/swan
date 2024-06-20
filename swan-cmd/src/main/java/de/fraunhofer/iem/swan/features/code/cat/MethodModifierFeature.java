package de.fraunhofer.iem.swan.features.code.cat;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodModifierFeature implements ICodeFeature {

    private Modifier modifier;
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;

    public enum Modifier {
        FINAL, STATIC, ABSTRACT, SYNCHRONIZED, DEFAULT
    }

    public MethodModifierFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method) {
        if(method.getSootMethod().isFinal()){
            this.modifier = Modifier.FINAL;
        } else if (method.getSootMethod().isStatic()) {
            this.modifier = Modifier.STATIC;
        } else if (method.getSootMethod().isAbstract()) {
            this.modifier = Modifier.ABSTRACT;
        } else if(method.getSootMethod().isSynchronized()){
            this.modifier = Modifier.SYNCHRONIZED;
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
        return "MethodModifier";
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

    @Override
    public String getDefaultStringValue() {
        return Modifier.DEFAULT.name();
    }
}
