package de.fraunhofer.iem.swan.features.code.cat;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodInnerOrAnonymousClassFeature implements ICodeFeature {
    private ArrayList<String> featureValues;
    private FeatureResult featureResult;

    private Values category;

    private enum Values{
        INNER_CLASS, ANONYMOUS_CLASS, None
    }

    public MethodInnerOrAnonymousClassFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method) {
        int index = method.getClassName().lastIndexOf("$");
        if (index != -1) {
            String subclassName = method.getClassName().substring(index + 1);
            this.category =  Pattern.matches("^\\d+$", subclassName) ? Values.ANONYMOUS_CLASS : Values.None;
        }
        if(this.category == Values.None){
            if (method.getSootMethod() == null)
                this.category = Values.None;
            try {
                if (method.getSootMethod().getDeclaringClass().hasOuterClass())
                    this.category = Values.INNER_CLASS;
                else if (!method.getSootMethod().getDeclaringClass().hasOuterClass())
                    this.category = Values.None;
                else if (method.getSootMethod().getDeclaringClass().hasOuterClass())
                    this.category = Values.None;
                else this.category = Values.INNER_CLASS;
            } catch (Exception ex) {
                System.err.println("Something went wrong: " + ex.getMessage());
                this.category = Values.None;
            }
        }
        if(this.category == null){
            this.category = Values.None;
        }
        this.featureResult.setStringValue(String.valueOf(this.category));
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {return FeatureType.CATEGORICAL;}

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>(Stream.of(Values.values()).map(Values::name).collect(Collectors.toList()));
        return this.featureValues;
    }

    @Override
    public String toString(){
        return "MethodInnerOrAnonymousClass";
    }
}
