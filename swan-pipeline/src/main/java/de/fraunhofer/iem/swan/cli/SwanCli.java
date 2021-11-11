package de.fraunhofer.iem.swan.cli;

import de.fraunhofer.iem.swan.SwanPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CancellationException;

/**
 * Checks command line parameters and runs SWAN.
 */
public class SwanCli {

    private static final Logger logger = LoggerFactory.getLogger(SwanCli.class);

    public Integer run(SwanOptions options) throws Exception {

        FileUtility fileUtility = new FileUtility();

        if (options.getDatasetJson().contentEquals("/input/dataset/swan-dataset.json")) {
            options.setDatasetJson(fileUtility.getResourceFile(options.getDatasetJson()).getAbsolutePath());
        }

        if (options.getTrainData().contentEquals("/input/train-data")) {
            options.setTrainData(fileUtility.getResourceDirectory("/input/train-data").getAbsolutePath());
        }

        if (options.getTestData().contentEquals("/input/test-data")) {
            options.setTestData(fileUtility.getResourceDirectory("/input/test-data").getAbsolutePath());
        }

        if(options.getSrmClasses().contains("all")){
            options.setSrmClasses(Arrays.asList("source", "sink", "sanitizer", "authentication", "relevant"));
        }

        if(options.getCweClasses().contains("all")){
            options.setCweClasses(Arrays.asList("cwe078", "cwe079", "cwe089", "cwe306", "cwe601", "cwe862", "cwe863"));
        }

        logger.info("SWAN options: {}", options);

        try {
            SwanPipeline swanPipeline = new SwanPipeline(options);
            swanPipeline.run();

            return 0;
        } catch (CancellationException e) {
            logger.warn("Analysis run was cancelled");
            return 66;
        } catch (Exception e) {
            logger.error("Analysis run terminated with error", e);
            return 500;
        } finally {
            // Delete temporary files and folders that have been created.
            fileUtility.dispose();
        }
    }
}
