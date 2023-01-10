package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassModifierFeature extends WeightedFeature implements IFeatureNew{

    private Modifier modifier;
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;

    public enum Modifier{
        STATIC, ABSTRACT, FINAL, DEFAULT
    }

    @Override
    public FeatureResult applies(Method method, Category category) {
        this.featureResult = new FeatureResult();
        if(method.getSootMethod().getDeclaringClass().isFinal()){
            this.modifier = Modifier.FINAL;
        } else if (method.getSootMethod().getDeclaringClass().isStatic()) {
            this.modifier = Modifier.STATIC;
        } else if (method.getSootMethod().getDeclaringClass().isAbstract()) {
            this.modifier = Modifier.ABSTRACT;
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
    public String toString(){
        return "ClassModifier";
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        //Converting Enum Values to ArrayList
        this.featureValues = new ArrayList<>(Stream.of(Modifier.values()).map(Modifier::name).collect(Collectors.toList()));
        return this.featureValues;
    }
}
