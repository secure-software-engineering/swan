package de.fraunhofer.iem.swan.io.dataset;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.io.doc.JavadocProcessor;
import de.fraunhofer.iem.swan.soot.Soot;
import de.fraunhofer.iem.swan.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DatasetProcessor {

    public Dataset dataset;
    public SwanOptions options;
    private static final Logger logger = LoggerFactory.getLogger(DatasetProcessor.class);

    public DatasetProcessor(SwanOptions swanOptions) {

        dataset = new Dataset();
        this.options = swanOptions;
    }

    public Dataset run() {

        //Run Soot
        Soot soot = new Soot(options.getTrainDataDir(), options.getTestDataDir());

        try {
            dataset.setTrain(SrmListUtils.importFile(options.getDatasetJson()));

            if (!options.getTrainDataDir().isEmpty())
                soot.cleanupList(dataset.getTrain());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Loaded {} training methods, distribution={}", dataset.getTrainMethods().size(), Util.countCategories(dataset.getTrainMethods()));

        if (options.getPhase().equals("predict")) {
            //Load methods from the test set
            dataset.setTest(new SrmList(options.getTestDataDir()));
            dataset.getTest().setMethods(soot.loadMethods(dataset.getTest().getTestClasses()));

            if (options.getFeatureSet().contains("doc-")) {

                //Extract doc comments and add to test set, if option is selected
                JavadocProcessor javadocProcessor = new JavadocProcessor(options.getTestDataSourceDir(), options.getOutputDir());
                javadocProcessor.run(dataset.getTestMethods(), options.getFeatureSet());
                logger.info("Loaded {} methods from {}", dataset.getTestMethods().size(), options.getTestDataDir());
            }
        }
        return dataset;
    }
}