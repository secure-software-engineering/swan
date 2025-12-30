package de.fraunhofer.iem.swan.io.dataset;

import de.fraunhofer.iem.srm.dataset.Category;
import de.fraunhofer.iem.srm.dataset.SrmDataset;
import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.srm.dataset.Method;
import de.fraunhofer.iem.swan.io.doc.JavadocProcessor;
import de.fraunhofer.iem.swan.soot.Soot;
import de.fraunhofer.iem.swan.util.Util;
import edu.stanford.nlp.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
            dataset.setTrain(SrmDatasetUtils.importFile(options.getDatasetJson()));


            Set<Category> cats = new HashSet<>();
            cats.add(Category.SOURCE);
            cats.add(Category.SINK);
            cats.add(Category.SANITIZER);
            cats.add(Category.PROPAGATOR);
            cats.add(Category.AUTHENTICATION);
            cats.add(Category.NONE);
            cats.add(Category.CWE22);
            cats.add(Category.CWE35);
            cats.add(Category.CWE77);
            cats.add(Category.CWE78);
            cats.add(Category.CWE79);
            cats.add(Category.CWE89);
            cats.add(Category.CWE90);
            cats.add(Category.CWE91);
            cats.add(Category.CWE117);
            cats.add(Category.CWE233);
            cats.add(Category.CWE306);
            cats.add(Category.CWE327);
            cats.add(Category.CWE328);
            cats.add(Category.CWE443);
            cats.add(Category.CWE501);
            cats.add(Category.CWE601);
            cats.add(Category.CWE643);
            cats.add(Category.CWE862);
            cats.add(Category.CWE863);
            cats.add(Category.CWE917);
            cats.add(Category.CWE918);
            cats.add(Category.CWE_NONE);


            for (Method met : dataset.getTrainMethods()) {


                StringBuffer bf = new StringBuffer();

                bf.append(met.getSignature().replace(",", "+"));

              /*  for (Category c :met.getAllCategories())
                    System.out.println(met.getSignature().replace(",", "+") +","+c.getId());
*/
                for (Category c : cats)
                    if (met.getAllCategories().toString().contains(c.toString()))
                        bf.append(", 1");
                    else if (c.getId().contains("authen") && met.getAllCategories().toString().contains("auth-")) {
                        // System.out.println(met.getSignature().replace(",", "+") +","+c.getId());
                        bf.append(", 1");
                    } else bf.append(", 0");


                /*
                for(Category cat : cats) {
                    if(met.getAllCategories().toString())
                }


                System.out.println(met.getSignature());

                results.put("all-methods", results.get("all-methods") + 1);

                for (Category cat : met.getAllCategories()) {
                    results.put(cat.toString(), results.get(cat.toString()) + 1);
                }*/
            }


            if (!options.getTrainDataDir().isEmpty())
                soot.cleanupList(dataset.getTrain());

            logger.info("Importing {} SRMs from dataset {}, distribution={}",
                    dataset.getTrainMethods().size(), options.getDatasetJson(),
                    Util.countCategories(dataset.getTrainMethods()));

            //Apply filters to dataset
            if (!options.getDiscovery().isEmpty() || options.isDocumented()) {

                logger.info("Filters applied to dataset: discovery={}, documented={}",
                        options.getDiscovery(), options.isDocumented());

                for (Method method : new HashSet<>(dataset.getTrainMethods())) {

                    if ((!options.getDiscovery().contains(method.getDiscovery()) && !options.getDiscovery().isEmpty()) ||
                            ((method.getDocumentation().getMethodComment().isEmpty()
                                    || StringUtils.split(method.getDocumentation().getMethodComment(), " ").size() <= 1) && options.isDocumented())) {
                        dataset.getTrainMethods().remove(method);
                    }
                }
                logger.info("Importing {} SRMs from dataset {}, distribution={}",
                        dataset.getTrainMethods().size(), options.getDatasetJson(),
                        Util.countCategories(dataset.getTrainMethods()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (options.getPhase().equals("predict")) {
            //Load methods from the test set

            try {
                Set<String> testClasses = Util.getAllClassesFromDirectory(options.getTestDataDir());
                SrmDataset testCls = new SrmDataset();
                testCls.setTestClasses(testClasses);

                dataset.setTest(testCls);
            } catch (IOException e) {
                e.printStackTrace();
            }


            dataset.getTest().setMethods(soot.loadMethods(dataset.getTest().getTestClasses()));

            //Filter known methods in the test set.
            InvokedMethodsFilter filter = new InvokedMethodsFilter(dataset.getTestMethods(), dataset.getTrainMethods());
            dataset.getTest().setMethods(filter.filterUnknownInvokedMethods());

            logger.info("Importing {} TEST methods from {}",
                    dataset.getTestMethods().size(), options.getTestDataDir());

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