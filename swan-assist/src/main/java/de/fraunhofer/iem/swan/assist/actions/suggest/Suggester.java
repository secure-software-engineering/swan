/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Goran Piskachev (goran.piskachev@iem.fraunhofer.de) - initial implementation
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - plugin integration
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions.suggest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import de.fraunhofer.iem.swan.IFeature;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.WeightedFeature;


public class Suggester {

    Set<IFeature> coveredFeatures;
    Map<IFeature, Integer> nonCoveredRankedFeatures = new HashMap<>();

    /**
     * Returns a pair of suggested methods based on the methods and features.
     * @param testSetMatrix of method and the features that they conform to.
     * @param features list of features used by SWAN.
     * @return a pair of method that meet particular features.
     */
    MethodPair suggestMethod(Map<Method, Set<IFeature>> testSetMatrix, Set<IFeature> features) {

        // Reinitialize coverage.
        if (nonCoveredRankedFeatures.isEmpty()) {
            coveredFeatures = new HashSet<>();
            nonCoveredRankedFeatures = calculateRanking(features);
        }

        // Keep track of most impactful method pair.
        MethodPair miMethods = null;
        int miWeight = -1000;
        Set<IFeature> miCoveredFeatures = null;

        boolean isChanged = false;

        // Iterate over pairs of methods not yet suggested.
        for (Method m1 : testSetMatrix.keySet()) {
            int weight;
            for (Method m2 : testSetMatrix.keySet()) {

                if (m1.equals(m2))
                    continue;

                MethodPair pair = new MethodPair(new MethodWrapper(m1), new MethodWrapper(m2));
                weight = -999;
                Set<IFeature> covered = new HashSet<IFeature>();

                // Calculate impact of the pair.
                for (IFeature f : nonCoveredRankedFeatures.keySet()) {
                    // MethodPair must be positive/negative example.
                    if (testSetMatrix.get(m1).contains(f) != testSetMatrix.get(m2).contains(f)) {
                        covered.add(f);
                        weight += nonCoveredRankedFeatures.get(f);
                        isChanged = true;
                    }
                }
                // Keep the pair with the best impact.
                if (isChanged & weight > miWeight) {
                    miWeight = weight;
                    miMethods = pair;
                    miCoveredFeatures = covered;
                }
            }
            // need to reset
            if (!isChanged)
                nonCoveredRankedFeatures = calculateRanking(features);
        }

        // Update the global variables.
        if (miMethods != null) {

            coveredFeatures.addAll(miCoveredFeatures);
            removeFeatures(miCoveredFeatures);
            testSetMatrix.remove(miMethods.getMethod1());
            testSetMatrix.remove(miMethods.getMethod2());
        }
        return miMethods;
    }

    /**
     * Removes features that are covered by methods.
     * @param miCoveredFeatures features to be removed from the list of features that aren't covered.
     */
    private void removeFeatures(Set<IFeature> miCoveredFeatures) {

        for (IFeature f : miCoveredFeatures) {
            nonCoveredRankedFeatures.remove(f);
        }
    }

    /**
     * Computes the ranking for a feature using its weights.
     * @param features features for which weighting should be calculated.
     * @return a map of features and their corresponding weights.
     */
    private Map<IFeature, Integer> calculateRanking(Set<IFeature> features) {

        Map<IFeature, Integer> map = new HashMap<>();
        for (IFeature f : features) {
            map.put(f, ((WeightedFeature) f).getWeight());
        }

        return map;
    }
}