package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.io.dataset.Dataset;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.util.Util;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Oshando Johnson on 27.09.20
 */
public class WekaFeatureSet extends FeatureSet implements IFeatureSet {

    public WekaFeatureSet(Dataset dataset, SwanOptions options) {
        super(dataset, options, ModelEvaluator.Toolkit.WEKA);
    }

    /**
     *
     */
    public void createFeatures() {

        List<FeatureSet.Type> featureSets = initializeFeatures();

        for (Category category : options.getAllClasses().stream().map(Category::fromText).collect(Collectors.toList())) {

            //Create and set attributes for the train instances
            ArrayList<Attribute> trainAttributes = createAttributes(category, dataset.getTrainMethods(), featureSets);

            String instanceName = category.getId().toLowerCase() + "-train-instances";
            Instances trainInstances = createInstances(featureSets, trainAttributes, dataset.getTrainMethods(), Collections.singleton(category), instanceName);
            this.instances.put(category.getId().toLowerCase(), trainInstances);
            Util.exportInstancesToArff(trainInstances);

            //Create and set attributes for the test instances.
            /*ArrayList<Attribute> testAttributes = createAttributes(getCategories(category), testData.getMethods(), featureSets);
            Instances testInstances = createInstances(featureSets, testAttributes, testData.getMethods(), getCategories(category), category + "-test-instances");
             */
        }
    }

    /**
     * Creates instances and adds attributes for the features, classes, and method signatures.
     *
     * @param category    list of categories
     * @param methods     list of training methods
     * @param featureSets classification mode
     */
    public ArrayList<Attribute> createAttributes(Category category, Set<Method> methods, List<FeatureSet.Type> featureSets) {

        ArrayList<Attribute> attributes = new ArrayList<>(super.createAttributes(getCategories(category), methods, featureSets));
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