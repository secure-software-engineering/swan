package de.fraunhofer.iem.swan.cli;

import de.fraunhofer.iem.swan.SwanPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;

/**
 * Checks command line parameters and runs SWAN.
 */
public class SwanCli {

    private static final Logger logger = LoggerFactory.getLogger(SwanCli.class);
    private SwanPipeline swanPipeline;

    public Integer run(SwanOptions options) throws Exception {

        FileUtility fileUtility = new FileUtility();

        if (options.getDatasetJson().contentEquals("/dataset/swan-dataset.json")) {
            options.setDatasetJson(fileUtility.getResourceFile(options.getDatasetJson(), null).getAbsolutePath());
        }

        if (options.getSrmClasses().contains("all")) {
            options.setSrmClasses(Arrays.asList("source", "sink", "sanitizer", "authentication"));
        }

        if (options.getCweClasses().contains("all")) {
            options.setCweClasses(Arrays.asList("cwe078", "cwe079", "cwe089", "cwe306", "cwe601", "cwe862", "cwe863"));
        }

        if (options.getFeatureSet().contains("all")) {
            options.setFeatureSet(Arrays.asList("code", "doc-manual", "doc-auto"));
        }

        if (options.getArffInstancesFiles().isEmpty() && options.getTrainDataDir().isEmpty()) {

            List<String> instances = new ArrayList<>();

            for (String feature : options.getFeatureSet()) {
                String filepath = File.separator + "dataset" + File.separator + options.getToolkit() + File.separator + feature;

                ArrayList<String> files = new ArrayList<>();

                for (File f : Objects.requireNonNull(fileUtility.getResourceDirectory(filepath).listFiles())) {
                    files.add(f.getAbsolutePath());
                }
                instances.addAll(files);
            }
            options.setInstances(instances);
        }

        logger.info("SWAN options: {}", options);

        try {
            swanPipeline = new SwanPipeline(options);
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

    public SwanPipeline getSwanPipeline() {
        return swanPipeline;
    }
}
