package de.fraunhofer.iem.swan.model;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.IFeatureSet;
import de.fraunhofer.iem.swan.features.MekaFeatureSet;
import de.fraunhofer.iem.swan.features.WekaFeatureSet;
import de.fraunhofer.iem.swan.io.dataset.Dataset;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.io.dataset.SrmListUtils;
import de.fraunhofer.iem.swan.model.toolkit.Meka;
import de.fraunhofer.iem.swan.model.toolkit.Weka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds possible sources and sinks in a given set of system methods using a
 * probabilistic algorithm trained on a previously annotated sample set.
 *
 * @author Steven Arzt, Lisa Nguyen Quang Do, Goran Piskachev
 */
public class ModelEvaluator {

    public enum Toolkit {
        WEKA,
        MEKA
    }

    public enum Phase {
        VALIDATE,
        PREDICT
    }

    private IFeatureSet features;
    private SwanOptions options;
    private Dataset dataset;
    private SrmList predictedSrmList;
    private static final Logger logger = LoggerFactory.getLogger(ModelEvaluator.class);

    public ModelEvaluator(IFeatureSet features, SwanOptions options, Dataset dataset) {
        this.features = features;
        this.options = options;
        this.dataset = dataset;
        predictedSrmList = new SrmList();
    }

    /**
     * Trains and evaluates the model with the given training data and specified classification mode.
     *
     * @return Hashmap containing the name of the classifier and it's F-Measure
     */
    public void trainModel() {

        switch (Toolkit.valueOf(options.getToolkit().toUpperCase())) {

            case MEKA:
                logger.info("Evaluating model with MEKA");
                Meka meka = new Meka((MekaFeatureSet) features, options, dataset.getTestMethods());
                processResults(meka.trainModel());
                break;
            case WEKA:
                Weka weka = new Weka((WekaFeatureSet) features, options, dataset.getTestMethods());
                processResults(weka.trainModel());
                break;
        }
    }

    public void processResults(SrmList srmList) {

        switch (ModelEvaluator.Phase.valueOf(options.getPhase().toUpperCase())) {
            case PREDICT:

                if (srmList != null)
                    predictedSrmList = srmList;

                predictedSrmList.removeUnclassifiedMethods();
                logger.info("{} SRMs detected", predictedSrmList.getMethods().size());

                Set<Method> srmRepo = new HashSet<>();

                if (options.isAddKnownSrms()) {
                    for (Method method : dataset.getTrainMethods()) {
                        if (!method.getSrm().isEmpty() && !method.getSrm().contains(Category.NONE)
                        ) {
                            srmRepo.add(method);
                        }
                    }
                    logger.info("Adding {} SRMs from repository", srmRepo.size());
                    predictedSrmList.addMethods(srmRepo);

                }

                try {
                    if (!options.getOutputDir().isEmpty())
                        SrmListUtils.exportFile(predictedSrmList, options.getOutputDir() + File.separator + "detected-srm.json");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public SrmList getPredictedSrmList() {
        return predictedSrmList;
    }
}