package de.fraunhofer.iem.swan.features.code.cat;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.FeatureResult;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;

import java.util.ArrayList;

/***
 * Evaluates whether the method is an application method.
 *
 * @author Rohith Kumar
 */

public class IsMethodFromLibraryFeature implements ICodeFeature {
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;

    public IsMethodFromLibraryFeature(){
        this.featureResult = new FeatureResult();
    }
    @Override
    public FeatureResult applies(Method method) {
        this.featureResult.setBooleanValue(!method.isApplicationMethod());
        return this.featureResult;
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
