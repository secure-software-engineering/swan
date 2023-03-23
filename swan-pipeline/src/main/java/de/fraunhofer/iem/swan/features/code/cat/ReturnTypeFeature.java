package de.fraunhofer.iem.swan.features.code.cat;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReturnTypeFeature implements ICodeFeature {

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
    public FeatureResult applies(Method method){
        switch (method.getReturnType()){
            case "java.lang.CharSequence":
            case "java.lang.String":
                this.category = Values.String;
                break;
            //TODO maybe split the categories for numerical, bytes and boolean
            case "byte[]":
            case "int":
            case "boolean":
                this.category = Values.Native;
                break;
            //TODO we could find a way to split these into more categories
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
