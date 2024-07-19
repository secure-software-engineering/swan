package de.fraunhofer.iem.swan.features.code.cat;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * Evaluates whether the method is an application method.
 *
 * @author Rohith Kumar
 */

public class IsMethodFromLibraryFeature implements ICodeFeature {
    private MethodType methodType;
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;

    public enum MethodType {
        LIBRARY, JAVA_LIBRARY, APPLICATION
    }

    public IsMethodFromLibraryFeature(){
        this.featureResult = new FeatureResult();
    }
    @Override
    public FeatureResult applies(Method method) {
        if(method.getSootMethod().getDeclaringClass().isJavaLibraryClass()){
            this.methodType = MethodType.JAVA_LIBRARY;
        } else if (method.getSootMethod().getDeclaringClass().isLibraryClass()) {
            this.methodType = MethodType.LIBRARY;
        } else {
            this.methodType = MethodType.APPLICATION;
        }
        this.featureResult.setStringValue(String.valueOf(this.methodType));
        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.CATEGORICAL;
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        //Converting Enum Values to ArrayList
        this.featureValues = Stream.of(MethodType.values()).map(MethodType::name).collect(Collectors.toCollection(ArrayList::new));
        return this.featureValues;
    }

    @Override
    public String toString() {
        return "MethodOrigin";
    }
}
