package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.io.dataset.Dataset;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.util.Util;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Oshando Johnson on 27.09.20
 */
public class WekaFeatureSet extends FeatureSet implements IFeatureSet {

    HashMap<String, Instances> structures;

    public WekaFeatureSet(Dataset dataset, SwanOptions options) {
        super(dataset, options, ModelEvaluator.Toolkit.WEKA);
        structures = new HashMap<>();
    }

    /**
     *
     */
    public void createFeatures() {

        initializeFeatures();

        if (options.getArffInstancesFiles().isEmpty()) {

            Set<Method> methods = new HashSet<>(dataset.getTrainMethods());

            if (options.isPredictPhase())
                methods.addAll(dataset.getTestMethods());

            evaluateFeatureData(methods);

            for (Category category : options.getAllClasses().stream().map(Category::fromText).collect(Collectors.toList())) {

                //Create and set attributes for the train instances
                ArrayList<Attribute> trainAttributes = createAttributes(category, dataset.getTrainMethods());
                structures.put(category.getId().toLowerCase(), new Instances("weka-", trainAttributes, 0));

                Instances trainInstances = createInstances(trainAttributes, dataset.getTrainMethods(), Collections.singleton(category));
                trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
                this.trainInstances.put(category.getId().toLowerCase(), trainInstances);
                Util.exportInstancesToArff(trainInstances, category.getId());
            }
        } else {

            ArffLoader loader = new ArffLoader();

            for (Category category : options.getAllClasses().stream().map(Category::fromText).collect(Collectors.toList())) {

                List<String> instancesFile = options.getArffInstancesFiles().stream().filter(c -> c.contains(category.getId().toLowerCase())).collect(Collectors.toList());

                try {
                    loader.setSource(new File(instancesFile.get(0)));
                    Instances trainInstances = loader.getDataSet();
                    Instances structure = loader.getStructure();
                    trainInstances.setClassIndex(trainInstances.numAttributes() - 1);

                    //append remaining instances
                    if (instancesFile.size() > 1) {
                        for (int x = 1; x < instancesFile.size(); x++) {

                            ArffLoader arffLoader = new ArffLoader();
                            arffLoader.setSource(new File(instancesFile.get(x)));

                            trainInstances = mergeInstances(trainInstances, arffLoader.getDataSet());
                            structure = mergeInstances(structure, arffLoader.getStructure());
                        }
                    }

                    this.trainInstances.put(category.getId().toLowerCase(), trainInstances);
                    structures.put(category.getId().toLowerCase(), structure);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Set attributes for the test instances
        if (options.isPredictPhase()) {
            //TODO implement predict phase for WEKA
        }


        /*  for (Category category : options.getAllClasses().stream().map(Category::fromText).collect(Collectors.toList())) {

            //Create and set attributes for the train instances
            ArrayList<Attribute> trainAttributes = createAttributes(category, dataset.getTrainMethods());

            Instances trainInstances = createInstances(trainAttributes, dataset.getTrainMethods(), Collections.singleton(category));
            this.instances.put(category.getId().toLowerCase(), trainInstances);
            Util.exportInstancesToArff(trainInstances, "weka-"+category.getId());

            //Create and set attributes for the test instances.
            /*ArrayList<Attribute> testAttributes = createAttributes(getCategories(category), testData.getMethods(), featureSets);
            Instances testInstances = createInstances(featureSets, testAttributes, testData.getMethods(), getCategories(category), category + "-test-instances");

        }*/
    }


    /**
     * Merge two instances into one instances object.
     *
     * @param first  instances
     * @param second instances
     * @return merged instances
     */
    public Instances mergeInstances(Instances first, Instances second) {

        //rename ID and class attributes
        first.renameAttribute(first.attribute(first.numAttributes() - 1), "b_" + first.attribute(first.numAttributes() - 1).name());
        second.renameAttribute(second.attribute(0), "b_" + second.attribute(0).name());

        return this.mergeInstances(first, second);
    }

    /**
     * Creates instances and adds attributes for the features, classes, and method signatures.
     *
     * @param category list of categories
     * @param methods  list of training methods
     */
    public ArrayList<Attribute> createAttributes(Category category, Set<Method> methods) {

        ArrayList<Attribute> attributes = new ArrayList<>(super.createAttributes(getCategories(category), methods));
        ArrayList<String> attributeValues = new ArrayList<>(Arrays.asList("0", "1"));

        // Collect classes and add to attributes
        if (category.isAuthentication()) {
            attributeValues.addAll(Arrays.asList("2", "3"));
        }

        attributes.add(new Attribute(category.getId(), attributeValues));

        return attributes;
    }


    public HashSet<Category> getCategories(Category cat) {

        if (cat.isAuthentication())
            return new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,
                    Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION_NEUTRAL));
        else
            return new HashSet<>(Collections.singleton(cat));
    }
}