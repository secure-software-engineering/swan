package de.fraunhofer.iem.swan.io.dataset;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.io.doc.JavadocProcessor;
import de.fraunhofer.iem.swan.soot.Soot;
import de.fraunhofer.iem.swan.util.Util;
import edu.stanford.nlp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.HashSet;

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

            logger.info("Importing {} SRMs from TRAIN dataset in {}, distribution={}",
                    dataset.getTrainMethods().size(), options.getDatasetJson(),
                    Util.countCategories(dataset.getTrainMethods()));

            //Apply filters to dataset
            if (options.getDiscovery().size() > 0 || options.isDocumented()) {

                for (Method method : new HashSet<>(dataset.getTrainMethods())) {

                    if ((!options.getDiscovery().contains(method.getDiscovery()) && options.getDiscovery().size() > 0) ||
                            ((method.getJavadoc().getMethodComment().length() == 0
                                    || StringUtils.split(method.getJavadoc().getMethodComment(), " ").size() <= 1) && options.isDocumented())) {
                        dataset.getTrainMethods().remove(method);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (options.getPhase().equals("predict")) {
            //Load methods from the test set
            dataset.setTest(new SrmList(options.getTestDataDir()));
            dataset.getTest().setMethods(soot.loadMethods(dataset.getTest().getTestClasses()));

            logger.info("Importing {} SRMs from TEST dataset in {}, distribution={}",
                    dataset.getTestMethods().size(), options.getTestDataDir(),
                    Util.countCategories(dataset.getTestMethods()));

            if (options.getFeatureSet().contains("doc-")) {

                //Extract doc comments and add to test set, if option is selected
                JavadocProcessor javadocProcessor = new JavadocProcessor(options.getTestDataSourceDir(), options.getOutputDir());
                javadocProcessor.run(dataset.getTestMethods(), options.getFeatureSet());

                logger.info("Extracting doc comments for {} methods in {}", dataset.getTestMethods().size(), options.getTestDataDir());
            }
        }
        return dataset;
    }
}