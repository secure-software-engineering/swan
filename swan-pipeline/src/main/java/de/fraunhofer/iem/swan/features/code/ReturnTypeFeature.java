package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReturnTypeFeature extends WeightedFeature implements IFeatureNew {

    private Values category;

    private FeatureResult featureResult;
    private ArrayList<String> featureValues;

    public enum Values{
        String, Native, Custom, Others
    }

    public ReturnTypeFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method, Category category){
        switch (method.getReturnType()){
            case "java.lang.CharSequence":
            case "java.lang.String":
                this.category = Values.String;
                break;

            case "byte[]":
            case "int":
            case "boolean":
                this.category = Values.Native;
                break;

            case "User":
            case "Credential":
            case "java.sql.ResultSet":
            case "Document":
            case "Node":
            case "Servlet":
            case "Request":
                this.category = Values.Custom;
                break;
            default:
                this.category = Values.Others;
                break;
        }

        this.featureResult.setStringValue(String.valueOf(this.category));
        return this.featureResult;

    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.CATEGORICAL;
    }


    @Override
    public String toString(){
        return "MethodReturnType";
    }

    @Override
    public ArrayList<String> getFeatureValues(){
        //Converting Enum Values to ArrayList
        this.featureValues = new ArrayList<>(Stream.of(Values.values()).map(Values::name).collect(Collectors.toList()));
        return this.featureValues;
    }

}
