package de.fraunhofer.iem.swan.io.doc;

import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.FeatureSet;
import de.fraunhofer.iem.swan.io.dataset.SrmListUtils;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JavadocProcessor {

    private File sourceDirectory;
    private String outputDirectory;

    public JavadocProcessor(String sourceDirectory, String outputDirectory) {

        this.sourceDirectory = new File(sourceDirectory);

        outputDirectory += File.separator + "xml-files";
        File output = new File(outputDirectory);
        output.mkdirs();

        this.outputDirectory = output.getAbsolutePath();
    }

    /**
     * Runs doclets to calculate documentation coverage and export doc comments to XML files.
     * Exported doc comments are used to update set of methods.
     *
     * @param methods     set of methods
     * @param featureSets feature set specified by user
     */
    public void run(Set<Method> methods, List<String> featureSets) {

        if (featureSets.contains(FeatureSet.Type.DOC_AUTO.getValue()) ||
                featureSets.contains(FeatureSet.Type.DOC_MANUAL.getValue())) {

            if (sourceDirectory.isDirectory()) {
                for (File file : FileUtils.listFiles(sourceDirectory, new String[]{"jar"}, true)) {
                    SrmListUtils.addDocComments(methods, processJar(file));
                }
            } else {
                SrmListUtils.addDocComments(methods, processJar(sourceDirectory));
            }
        }
    }

    public ArrayList<Javadoc> processJar(File file) {

        DocletExecutor coverageDoclet = new DocletExecutor();
        coverageDoclet.setDoclet(DocletExecutor.COVERAGE_DOCLET);
        coverageDoclet.runDoclet(file.getAbsolutePath(), SourceJarEvaluator.getRootPackages(file));

        DocletExecutor exporterDoclet = new DocletExecutor();
        exporterDoclet.setDoclet(DocletExecutor.SSL_DOCLET);
        exporterDoclet.setOutputDir(outputDirectory);
        exporterDoclet.runDoclet(file.getAbsolutePath(), SourceJarEvaluator.getRootPackages(file));

        XmlDocletParser javadocParser = new XmlDocletParser(outputDirectory);
        return javadocParser.parse();
    }
}