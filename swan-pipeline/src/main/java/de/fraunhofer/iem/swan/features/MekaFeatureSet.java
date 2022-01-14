package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.soot.SourceFileLoader;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.util.Util;
import meka.filters.unsupervised.attribute.MekaClassAttributes;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;

import java.util.*;

public class MekaFeatureSet extends FeatureSet implements IFeatureSet {

    public MekaFeatureSet(SrmList trainData, SourceFileLoader testData, SwanOptions options) {
        super(trainData, testData, options, ModelEvaluator.Mode.MEKA);
    }

    /**
     *
     */
    public void createFeatures() {

        List<FeatureSet.Type> featureSets = initializeFeatures();

        //Create and set attributes for the train instances
        ArrayList<Attribute> trainAttributes = createAttributes(getCategories(options.getAllClasses()), trainData.getMethods(), featureSets);
        Instances trainInstances = createInstances(featureSets, trainAttributes, trainData.getMethods(), getCategories(options.getAllClasses()), "train-instances");
        this.instances.put("train", convertToMekaInstances(trainInstances));

        //Create and set attributes for the test instances.
        ArrayList<Attribute> testAttributes = createAttributes(getCategories(options.getAllClasses()), testData.getMethods(), featureSets);
        Instances testInstances = createInstances(featureSets, testAttributes, testData.getMethods(), getCategories(options.getAllClasses()), "test-instances");
        this.instances.put("test", convertToMekaInstances(testInstances));
    }

    /**
     * Creates instances and adds attributes for the features, classes, and method signatures.
     *
     * @param categories  list of categories
     * @param methods     list of training methods
     * @param featureSets classification mode
     */
    public ArrayList<Attribute> createAttributes(Set<Category> categories, Set<Method> methods, List<FeatureSet.Type> featureSets) {

        ArrayList<Attribute> attributes = new ArrayList<>();

        // Collect classes and add to attributes
        for (Category category : categories) {
            Attribute catAttribute = new Attribute(category.getId(), new ArrayList<>(Arrays.asList("0", "1")));
            attributes.add(catAttribute);
        }

        attributes.addAll(super.createAttributes(categories, methods, featureSets));

        return attributes;
    }

    public Instances convertToMekaInstances(Instances instances) {

        MekaClassAttributes filter = new MekaClassAttributes();
        Instances output = null;
        try {
            filter.setAttributeIndices("1-11");
            filter.setInputFormat(instances);
            output = Filter.useFilter(instances, filter);
            output.setRelationName(instances.relationName() + ":" + output.relationName());

            Util.exportInstancesToArff(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public HashSet<Category> getCategories(List<String> cat) {

        HashSet<Category> categories = new HashSet<>();

        for (String c : cat)
            categories.add(Category.fromText(c));

        return categories;
    }

}
