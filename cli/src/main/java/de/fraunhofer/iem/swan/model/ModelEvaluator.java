package de.fraunhofer.iem.swan.model;

import de.fraunhofer.iem.srm.dataset.SrmDataset;
import de.fraunhofer.iem.swan.io.dataset.InvokedMethodsFilter;
import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.srm.dataset.Category;
import de.fraunhofer.iem.srm.dataset.Method;
import de.fraunhofer.iem.swan.features.IFeatureSet;
import de.fraunhofer.iem.swan.features.MekaFeatureSet;
import de.fraunhofer.iem.swan.features.WekaFeatureSet;
import de.fraunhofer.iem.swan.io.dataset.Dataset;
import de.fraunhofer.iem.swan.io.dataset.SrmDatasetUtils;
import de.fraunhofer.iem.swan.model.toolkit.Meka;
import de.fraunhofer.iem.swan.model.toolkit.Weka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    private SrmDataset srmDataset;
    private static final Logger logger = LoggerFactory.getLogger(ModelEvaluator.class);

    public ModelEvaluator(IFeatureSet features, SwanOptions options, Dataset dataset) {
        this.features = features;
        this.options = options;
        this.dataset = dataset;
        srmDataset = new SrmDataset();
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

    public void processResults(SrmDataset srmDataset) {

        switch (ModelEvaluator.Phase.valueOf(options.getPhase().toUpperCase())) {
            case PREDICT:

                if (srmDataset != null)
                    this.srmDataset = srmDataset;

                this.srmDataset.getMethods().stream().filter(m -> m.getAllCategories().size() > 0)
                        .collect(Collectors.toSet());


                logger.info("{} SRMs detected", this.srmDataset.getMethods().size());

                Set<Method> srmRepo = new HashSet<>();

                if (options.isAddKnownSrms()) {
                    for (Method method : dataset.getTrainMethods()) {
                        if (!method.getSrm().isEmpty() && !method.getSrm().contains(Category.NONE)
                        ) {
                            srmRepo.add(method);
                        }
                    }
                    logger.info("Adding {} SRMs from repository", srmRepo.size());
                    this.srmDataset.getMethods().addAll(srmRepo);

                }

                try {
                    if (!options.getOutputDir().isEmpty()){
                        this.srmDataset.getMethods().addAll(InvokedMethodsFilter.getKnownInvokedMethods());
                        SrmDatasetUtils.exportFile(this.srmDataset, options.getOutputDir() + File.separator + "detected-srm.json");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public SrmDataset getSrmDataset() {
        return srmDataset;
    }
}