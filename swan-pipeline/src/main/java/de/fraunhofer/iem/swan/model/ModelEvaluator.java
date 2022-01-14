package de.fraunhofer.iem.swan.model;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.IFeatureSet;
import de.fraunhofer.iem.swan.features.MekaFeatureSet;
import de.fraunhofer.iem.swan.features.WekaFeatureSet;
import de.fraunhofer.iem.swan.model.engine.MLPlan;
import de.fraunhofer.iem.swan.model.engine.Meka;
import de.fraunhofer.iem.swan.model.engine.Weka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;

/**
 * Finds possible sources and sinks in a given set of system methods using a
 * probabilistic algorithm trained on a previously annotated sample set.
 *
 * @author Steven Arzt, Lisa Nguyen Quang Do, Goran Piskachev
 */
public class ModelEvaluator {

    public enum Mode {
        WEKA,
        MEKA,
        MLPLAN
    }

    public enum Phase {
        VALIDATE,
        PREDICT
    }

    private IFeatureSet features;
    private SwanOptions options;
    private Set<Method> methods;
    private static final Logger logger = LoggerFactory.getLogger(ModelEvaluator.class);

    public ModelEvaluator(IFeatureSet features, SwanOptions options, Set<Method> methods) {
        this.features = features;
        this.options = options;
        this.methods = methods;
    }

    /**
     * Trains and evaluates the model with the given training data and specified classification mode.
     *
     * @return Hashmap containing the name of the classifier and it's F-Measure
     */
    public void trainModel() {

        switch (Mode.valueOf(options.getLearningMode().toUpperCase())) {

            case MEKA:
                logger.info("Evaluating model with MEKA");
                Meka meka = new Meka((MekaFeatureSet)features, options, methods);
                meka.trainModel();
                break;
            case WEKA:
                logger.info("Evaluating model with WEKA");
                Weka weka = new Weka((WekaFeatureSet) features, options);
                weka.trainModel();
                break;
            case MLPLAN:
                logger.info("Evaluating model with ML-PLAN");
                MLPlan mlPlan = new MLPlan();
                mlPlan.evaluateDataset(((WekaFeatureSet)features).getInstances().get("train"));
                break;
        }
    }
}