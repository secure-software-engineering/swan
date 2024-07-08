package de.fraunhofer.iem.swan.features.code.cat;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static de.fraunhofer.iem.swan.features.code.bow.SecurityVocabulary.*;

/***
 * Evaluates the number of parameter types that match with the security vocabulary.
 *
 * @author Rohith Kumar
 */

public class ParametersTypeFeature extends WeightedFeature implements ICodeFeature {

    //TODO make sure that use use camel case for all variable names
    private int numberOfParameters;
    private Set<String> parametersList;
    private FeatureResult featureResult;

    public ParametersTypeFeature() {
        this.numberOfParameters = 0;
        this.featureResult = new FeatureResult();
        this.parametersList = new HashSet<>();
    }

    @Override
    public FeatureResult applies(Method method) {

        //TODO Convert to categorical feature
        this.parametersList.addAll(AUTHENTICATION_PARAMETER_TYPES);
        this.parametersList.addAll(SOURCE_PARAMETER_TYPES);
        this.parametersList.addAll(SINK_PARAMETER_TYPES);
        this.parametersList.addAll(SANITIZER_PARAMETER_TYPES);

        for (String methodParameter: method.getParameters()){
            for(String parameter: this.parametersList){
                if(methodParameter.contains(parameter)){
                    this.numberOfParameters +=1;
                }
            }
        }
        this.featureResult.setIntegerValue(this.numberOfParameters);
        return featureResult;
    }

    @Override
    public FeatureType getFeatureType() {return FeatureType.NUMERICAL;}

    @Override
    public ArrayList<String> getFeatureValues() {return null;}

    @Override
    public String toString(){
        return "MethodParametersTypes";
    }
}
