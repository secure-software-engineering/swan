package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.features.code.soot.SourceFileLoader;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.model.ModelEvaluator;

public class FeatureSetSelector {

    public IFeatureSet select(SrmList trainData, SourceFileLoader testData, SwanOptions options) {

        switch (ModelEvaluator.Mode.valueOf(options.getLearningMode().toUpperCase())) {

            case MEKA:
                MekaFeatureSet mekaFeatureSet = new MekaFeatureSet(trainData, testData, options);
                mekaFeatureSet.createFeatures();
                return mekaFeatureSet;
            case WEKA:
                WekaFeatureSet wekaFeatureSet = new WekaFeatureSet(trainData, testData, options);
                wekaFeatureSet.createFeatures();
                return wekaFeatureSet;
        }
        return null;
    }
}