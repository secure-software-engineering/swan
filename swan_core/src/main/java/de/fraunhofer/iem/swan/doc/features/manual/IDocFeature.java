package de.fraunhofer.iem.swan.doc.features.manual;


import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;

/**
 * @author Oshando Johnson on 20.07.20
 */
public interface IDocFeature {

    FeatureResult evaluate(AnnotatedMethod annotatedMethod);

    /**
     * Converts feature information to string.
     * @return String representation of the feature
     */
    String toString();

    /**
     * Returns the name of the feature.
     * @return feature name as a string
     */
    String getName();
}
