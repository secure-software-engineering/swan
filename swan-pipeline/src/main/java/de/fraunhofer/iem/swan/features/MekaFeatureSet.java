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

public class MekaFeatureSet extends FeatureSet implements IFeatureSet {

    Instances structure = null;

    public MekaFeatureSet(Dataset dataset, SwanOptions options) {
        super(dataset, options, ModelEvaluator.Toolkit.MEKA);
    }

    /**
     *
     */
    public void createFeatures() {

        initializeFeatures();

        Instances trainingInstances = null;

        //Create and set attributes for the train instances
        if (options.getArffInstancesFiles().isEmpty()) {

            ArrayList<Attribute> trainAttributes = createAttributes(getCategories(options.getAllClasses()), dataset.getTrainMethods());
            structure = new Instances("meka", trainAttributes, 0);

            Set<Method> methods = new HashSet<>(dataset.getTrainMethods());

            if (options.isPredictPhase())
                methods.addAll(dataset.getTestMethods());

            evaluateFeatureData(methods);

            trainingInstances = createInstances(new Instances(structure), trainAttributes, dataset.getTrainMethods(), getCategories(options.getAllClasses()));
            Util.exportInstancesToArff(trainingInstances, options.getFeatureSet().get(0));
        } else {

            ArffLoader loader = new ArffLoader();

            try {
                loader.setSource(new File(options.getArffInstancesFiles().get(0)));

                trainingInstances = loader.getDataSet();
                structure = loader.getStructure();

                //append remaining instances
                if (options.getArffInstancesFiles().size() > 1) {
                    for (int x = 1; x < options.getArffInstancesFiles().size(); x++) {

                        ArffLoader arffLoader = new ArffLoader();
                        arffLoader.setSource(new File(options.getArffInstancesFiles().get(x)));

                        trainingInstances = joinInstances(trainingInstances, arffLoader.getDataSet());
                        structure = joinInstances(structure, arffLoader.getStructure());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.trainInstances.put("meka", convertToMekaInstances(trainingInstances));

        //Set attributed for the test instances
        //Set attributes for the test instances
        if (options.getPhase().toUpperCase().contentEquals(ModelEvaluator.Phase.PREDICT.name())) {

            createAttributes(getCategories(options.getAllClasses()), dataset.getTestMethods());
            evaluateFeatureData(dataset.getTestMethods());

            this.testInstances.put("meka", convertToMekaInstances(createTestSet()));
        }
    }

    /**
     * Merge two instances into one instances object.
     *
     * @param first  instances
     * @param second instances
     * @return merged instances
     */
    public Instances joinInstances(Instances first, Instances second) {

        for (int c = 0; c < 12; c++) {
            second.renameAttribute(second.attribute(c), "b_" + second.attribute(c).name());
        }

        return mergeInstances(first, second);
    }


    public Instances createTestSet() {

        Instances testInstances = new Instances(structure);
        ArrayList<Attribute> aList = Collections.list(testInstances.enumerateAttributes());

        return createInstances(testInstances, aList, dataset.getTestMethods(), getCategories(options.getAllClasses()));
    }

    /**
     * Creates instances and adds attributes for the features, classes, and method signatures.
     *
     * @param categories list of categories
     * @param methods    list of training methods
     */
    public ArrayList<Attribute> createAttributes(Set<Category> categories, Set<Method> methods) {

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

            Util.exportInstancesToArff(output, "meka");
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
