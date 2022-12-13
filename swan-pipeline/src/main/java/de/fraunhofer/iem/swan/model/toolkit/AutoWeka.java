package de.fraunhofer.iem.swan.model.toolkit;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.features.WekaFeatureSet;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Evaluation;
//import weka.classifiers.meta.AutoWEKAClassifier;
import weka.core.Instances;

import java.util.HashMap;
import java.util.LinkedHashMap;


public class AutoWeka {

    private WekaFeatureSet features;
    private SwanOptions options;
    private static final Logger logger = LoggerFactory.getLogger(AutoWeka.class);

    public AutoWeka(WekaFeatureSet features, SwanOptions options) {
        this.features = features;
        this.options = options;
    }

    /**
     * Trains and evaluates the model with the given training data and specified classification mode.
     *
     * @return Hashmap containing the name of the classifier and it's F-Measure
     */
    public HashMap<String, HashMap<String, String>> trainModel() {

        switch (ModelEvaluator.Phase.valueOf(options.getPhase().toUpperCase())) {
            case VALIDATE:

                //Phase 1: classify SRM classes
                for (String srm : options.getSrmClasses())
                    runManualEvaluation(features.getTrainInstances().get(srm));

                //Filter methods from CWE instances that were not classified into one of the SRM classes
                //Phase 2: classify CWE classes
                for (String cwe : options.getCweClasses())
                    runManualEvaluation(features.getTrainInstances().get(cwe));

                return null;
            case PREDICT:

        }
        return null;
    }

    /**
     * @return
     */
    public HashMap<String, HashMap<String, String>> runManualEvaluation(Instances instances) {

        String category = instances.attribute(instances.numAttributes() - 1).name();
        instances.setClass(instances.attribute(instances.numAttributes() - 1));
        String instancesFile = Util.exportInstancesToArff(instances, category + "-autoweka");

        LinkedHashMap<String, HashMap<String, String>> fMeasure = new LinkedHashMap<>();
        logger.info("Selecting model for {} using Auto-Weka:  timelimit= {}, instances={}",
                category, options.getTimeLimit(), instancesFile);

        try {

            String[] args = {"-t", instancesFile,
                    "-seed", "1",
                    "-no-cv",
                    "-timeLimit", Integer.toString(options.getTimeLimit())};

//            String out = Evaluation.evaluateModel(new AutoWEKAClassifier(), args);
//            logger.info("Auto-Weka Results: {]}",out);

            //autoWekaClassifier.buildClassifier(instances);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fMeasure;
    }
}