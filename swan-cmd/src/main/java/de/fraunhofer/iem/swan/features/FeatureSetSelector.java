package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.io.dataset.Dataset;
import de.fraunhofer.iem.swan.model.ModelEvaluator;

public class FeatureSetSelector {

    public IFeatureSet select(Dataset dataset, SwanOptions options) {

        switch (ModelEvaluator.Toolkit.valueOf(options.getToolkit().toUpperCase())) {

            case ML2PLAN:
            case MEKA:
                MekaFeatureSet mekaFeatureSet = new MekaFeatureSet(dataset, options);
                mekaFeatureSet.createFeatures();
                return mekaFeatureSet;
            case WEKA:
                WekaFeatureSet wekaFeatureSet = new WekaFeatureSet(dataset, options);
                wekaFeatureSet.createFeatures();
                return wekaFeatureSet;
        }
        return null;
    }
}