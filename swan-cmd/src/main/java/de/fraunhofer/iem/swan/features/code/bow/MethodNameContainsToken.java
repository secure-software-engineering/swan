package de.fraunhofer.iem.swan.features.code.bow;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;

/**
 * Evaluates whether method name contains tokens in the security vocabulary.
 *
 * @author Lisa Nguyen Quang Do
 */
public class MethodNameContainsToken extends WeightedFeature implements ICodeFeature {

    private final String token;
    private ArrayList<String> featureValues;
    private FeatureResult featureResult;

    public MethodNameContainsToken(String contains) {
        this.token = contains.toLowerCase();
        featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method) {

        if (method.getName().toLowerCase().contains(token))
            this.featureResult.setBooleanValue(Boolean.TRUE);
        else
            this.featureResult.setBooleanValue(Boolean.FALSE);

        return featureResult;
    }

    @Override
    public String toString() {
        return "MethodNameContains" + this.token.toUpperCase();
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.BOOLEAN;
    }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        this.featureValues.add("true");
        this.featureValues.add("false");
        return this.featureValues;
    }

}
