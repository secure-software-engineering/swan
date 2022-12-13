package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;

public class ReturnTypeOfMethodFeature extends WeightedFeature implements IFeatureNew {

    private Values category;

    private FeatureResult featureResult;
    private ArrayList<String> featureValues;

    public enum Values{
        String, Native, Custom, Others
    }

    @Override
    public FeatureResult applies(Method method, Category category){
        this.featureResult = new FeatureResult();
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
        return "<Type of the return type>";
    }

    @Override
    public ArrayList<String> getFeatureValues(){
        this.featureValues = new ArrayList<>();
        this.featureValues.add(String.valueOf(Values.Native));
        this.featureValues.add(String.valueOf(Values.Custom));
        this.featureValues.add(String.valueOf(Values.String));
        this.featureValues.add(String.valueOf(Values.Others));

        return this.featureValues;
    }

}
