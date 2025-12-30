package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.srm.dataset.Method;

import java.util.ArrayList;

/**
 * Common interface for all features in the probabilistic model
 *
 * @author Steven Arzt
 *
 */
public interface ICodeFeature {

    enum FeatureType{
        NUMERICAL,
        CATEGORICAL,
        BOOLEAN
    }
    FeatureResult applies(Method method);

    String toString();

    FeatureType getFeatureType();

    ArrayList<String> getFeatureValues();

}