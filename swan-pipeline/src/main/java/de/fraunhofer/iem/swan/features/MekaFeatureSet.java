package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.io.dataset.Dataset;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.util.Util;
import meka.filters.unsupervised.attribute.MekaClassAttributes;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MekaFeatureSet extends FeatureSet implements IFeatureSet {

    public MekaFeatureSet(Dataset dataset, SwanOptions options) {
        super(dataset, options, ModelEvaluator.Toolkit.MEKA);
    }

    /**
     *
     */
    public void createFeatures() {

        List<FeatureSet.Type> featureSets = initializeFeatures();

        Instances trainInstances = null;
        Instances structure = null;

        //Create and set attributes for the train instances
        if (options.getInstances().isEmpty()) {
            ArrayList<Attribute> trainAttributes = createAttributes(getCategories(options.getAllClasses()), dataset.getTrainMethods(), featureSets);
            trainInstances = createInstances(featureSets, trainAttributes, dataset.getTrainMethods(), getCategories(options.getAllClasses()));
        } else {
            ArffLoader loader = new ArffLoader();

            try {
                loader.setSource(new File(options.getInstances().get(0)));
                trainInstances = loader.getDataSet();
                structure = loader.getStructure();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Create and set attributes for the test instances.
        Attribute idAttr = new Attribute("id", dataset.getTestMethods().stream().map(Method::getArffSafeSignature).collect(Collectors.toList()));
        structure.replaceAttributeAt(idAttr, structure.attribute("id").index());
        ArrayList<Attribute> aList = Collections.list(structure.enumerateAttributes());

        return createInstances(structure, aList, dataset.getTestMethods(), getCategories(options.getAllClasses()));
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

        attributes.addAll(super.createAttributes(categories, methods));

        return attributes;
    }

    public Instances convertToMekaInstances(Instances instances) {

        MekaClassAttributes filter = new MekaClassAttributes();
        Instances output = null;
        try {
            filter.setAttributeIndices("1-11");
            filter.setInputFormat(instances);
            output = Filter.useFilter(instances, filter);
            output.setRelationName("swan-srm:" + output.relationName());

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
