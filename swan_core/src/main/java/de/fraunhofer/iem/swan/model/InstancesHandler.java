package de.fraunhofer.iem.swan.model;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.doc.features.DocFeatureHandler;
import de.fraunhofer.iem.swan.doc.features.automatic.DocCommentVector;
import de.fraunhofer.iem.swan.doc.features.manual.IDocFeature;
import de.fraunhofer.iem.swan.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.doc.nlp.CoreNLPExecutor;
import de.fraunhofer.iem.swan.doc.nlp.NLPUtils;
import de.fraunhofer.iem.swan.features.type.IFeature;
import de.fraunhofer.iem.swan.util.SwanConfig;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Oshando Johnson on 27.09.20
 */
public class InstancesHandler {

    /**
     * Classification can be done the following modes:
     * SWAN: only source code features are used
     * SWAN_SWANDOC_MANUAL: source code and Javadoc manual features
     * SWAN_SWANDOC_AUTOMATIC: source code and Javadoc automatic features
     * SWANDOC: only Javadoc features
     */
    public enum INSTANCE_SET {
        SWAN,
        SWAN_SWANDOC_MANUAL,
        SWAN_SWANDOC_AUTOMATIC,
        SWANDOC_MANUAL,
        SWANDOC_AUTOMATIC
    }

    private ArrayList<Attribute> attributes;
    private Map<IFeature, Attribute> swanFeatureAttribs;
    private Set<Class<? extends IDocFeature>> swanDocFeatureSet;
    private HashMap<String, Integer> instanceMap;
    private HashMap<String, Method> methodMap;

    public InstancesHandler() {
        this.instanceMap = new HashMap<>();
        this.methodMap = new HashMap<>();
    }

    /**
     * @param trainingSet
     * @param features
     * @param categories
     * @param instanceSet
     * @return
     */
    public Instances createInstances(Set<Method> trainingSet,
                                     Map<Category, Set<IFeature>> features, DocFeatureHandler docFeatures, Set<Category> categories,
                                     INSTANCE_SET instanceSet) {

        for (Method method : trainingSet) {
            methodMap.put(method.getSignature(), method);
        }

        //Initialize instances
        initializeInstances(features, docFeatures, categories, trainingSet, null, instanceSet);

        // Set attributes to the train instances.
        Instances trainInstances = new Instances("training-methods", attributes, 0);
        trainInstances.setClass(trainInstances.attribute("class"));

        //Populate SWAN feature attributes
        switch (instanceSet) {
            case SWAN:
                trainInstances = addSwanInstances(trainInstances, trainingSet, categories);
                break;
            case SWANDOC_MANUAL:
            case SWANDOC_AUTOMATIC:
                trainInstances = addSwanDocInstances(trainInstances, docFeatures, categories, instanceSet);
                break;
            case SWAN_SWANDOC_MANUAL:
            case SWAN_SWANDOC_AUTOMATIC:
                trainInstances = addSwanInstances(trainInstances, trainingSet, categories);
                trainInstances = addSwanDocInstances(trainInstances, docFeatures, categories, instanceSet);
                break;
        }

        exportInstances(trainInstances, categories);
        return trainInstances;
    }


    /**
     * Creates instances and adds attributes for the features, classes, and method signatures.
     *
     * @param features    SWAN features/category mapping
     * @param categories  list of categories
     * @param trainingSet list of training methods
     * @param testSet     list of test methods
     * @param instanceSet classification mode
     */
    public void initializeInstances(Map<Category, Set<IFeature>> features, DocFeatureHandler docFeatures, Set<Category> categories,
                                    Set<Method> trainingSet, Set<Method> testSet, INSTANCE_SET instanceSet) {

        attributes = new ArrayList<>();

        //Create feature set and add to attributes
        switch (instanceSet) {

            case SWAN:
                initializeSwanFeatures(features, categories);
                break;
            case SWAN_SWANDOC_MANUAL:
            case SWAN_SWANDOC_AUTOMATIC:
                initializeSwanFeatures(features, categories);
                initializeSwanDocFeatures(docFeatures, instanceSet);
                break;
            case SWANDOC_MANUAL:
            case SWANDOC_AUTOMATIC:
                initializeSwanDocFeatures(docFeatures, instanceSet);
                break;
        }

        // Add method signatures as id attribute
        ArrayList<String> methodStrings = new ArrayList<>();

        int c = 0;
        for (Method am : trainingSet) {
            methodStrings.add(am.getSignature().replace(",", "+"));
            c++;
        }

        if (testSet != null)
            for (Method am : testSet) {
                methodStrings.add(am.getSignature());
            }

        Attribute idAttr = new Attribute("id", methodStrings);
        attributes.add(idAttr);

        // Collect classes and add to attributes
        ArrayList<String> classes = new ArrayList<>();
        for (Category type : Category.values()) {
            if (categories.contains(type)) {
                classes.add(type.toString());
            }
        }

        Attribute classAttr = new Attribute("class", classes);
        attributes.add(classAttr);
    }

    /**
     * Adds SWAN features as attributes to the instance set.
     *
     * @param features   SWAN features
     * @param categories list of categories
     */
    public void initializeSwanFeatures(Map<Category, Set<IFeature>> features, Set<Category> categories) {

        // Collect the possible values
        ArrayList<String> ordinal = new ArrayList<>();
        ordinal.add("true");
        ordinal.add("false");

        // Collect all attributes for the categories we classify into, and create the instance set.
        swanFeatureAttribs = new HashMap<>();

        for (Category type : features.keySet()) {

            if (type == Category.NONE) continue;

            if (categories.contains(type)) {
                for (IFeature f : features.get(type)) {
                    Attribute attr = new Attribute(f.toString(), ordinal);
                    if (!swanFeatureAttribs.containsKey(f) && !attributes.contains(attr)) {
                        swanFeatureAttribs.put(f, attr);
                        attributes.add(attr);
                    }
                }
            }
        }
    }

    /**
     * Adds SWAN-DOC features as attributes to the instance set.
     *
     * @param instanceSet classification mode
     */
    public void initializeSwanDocFeatures(DocFeatureHandler features, INSTANCE_SET instanceSet) {

        switch (instanceSet) {
            case SWAN_SWANDOC_MANUAL:
            case SWANDOC_MANUAL:
                for (Class<? extends IDocFeature> javadocFeature : features.getManualFeatureSet()) {

                    Attribute attribute = new Attribute(javadocFeature.getSimpleName());
                    attributes.add(attribute);
                }
                break;
            case SWAN_SWANDOC_AUTOMATIC:
            case SWANDOC_AUTOMATIC:

                for (String feature : features.getAutomaticFeatureSet()) {
                    Attribute attribute = new Attribute(feature);
                    attributes.add(attribute);
                }
                break;
        }
    }

    /**
     * Adds data for SWAN features to instance set.
     *
     * @param trainInstances instance srt
     * @param trainingSet    training set
     * @param categories     set of categories
     * @return instance set containing data from SWAN
     */
    public Instances addSwanInstances(Instances trainInstances, Set<Method> trainingSet, Set<Category> categories) {

        int instanceIndex = 0;

        int c = 0;
        // Evaluate all methods against the features.
        for (Method am : trainingSet) {

            Instance inst = new DenseInstance(attributes.size());
            inst.setDataset(trainInstances);

            // Set the known classifications for the training set.
            Category categoryClassified = null;

            //Add category to training set
            for (Category category : am.getCategoriesTrained()) {

                if (categories.contains(Category.RELEVANT) && !category.isCwe()) {

                    categoryClassified = Category.RELEVANT;
                    break;

                } else if (categories.contains(category)) {
                    categoryClassified = category;
                    break;
                }
            }

            if (categoryClassified == null)
                categoryClassified = Category.NONE;

            inst.setClassValue(categoryClassified.toString());

            for (Map.Entry<IFeature, Attribute> entry : swanFeatureAttribs.entrySet()) {
                switch (entry.getKey().applies(am)) {
                    case TRUE:
                        inst.setValue(entry.getValue(), "true");
                        break;
                    case FALSE:
                        inst.setValue(entry.getValue(), "false");
                        break;
                    default:
                        inst.setMissing(entry.getValue());
                }
            }

            //Set id attribute
            inst.setValue(trainInstances.attribute("id"), am.getSignature().replace(",", "+"));
            c++;

            trainInstances.add(inst);
            instanceMap.put(am.getSignature(), instanceIndex++);
        }
        return trainInstances;
    }

    /**
     * Adds data for SWAN-DOC features to instance set.
     *
     * @param trainInstances instance srt
     * @param docFeatures    features based on doc comments
     * @param categories     set of categories
     * @return Instances containing data from SWAN-DOC
     */
    public Instances addSwanDocInstances(Instances trainInstances, DocFeatureHandler docFeatures, Set<Category> categories, INSTANCE_SET instanceSet) {

        for (Method method : docFeatures.getMethodSet()) {

            Instance inst;

            //If instance exists already, update it. Otherwise create a new instance
            if (instanceMap.containsKey(method.getSignature())) {

                inst = trainInstances.instance(instanceMap.get(method.getSignature()));
                //    System.out.println("Instance for: "+  inst.attribute());
                // trainInstances.delete(instanceMap.get(method.getMethod().getSignature()));
            } else {
                inst = new DenseInstance(attributes.size());
                inst.setDataset(trainInstances);
            }

            //System.out.println(method.getMethod().getSignature());
            inst.setValue(trainInstances.attribute("id"), method.getSignature().replace(",", "+"));
            Category categoryClassified = null;

            for (Category category : method.getCategoriesTrained()) {
                if (categories.contains(Category.RELEVANT) && !category.isCwe()) {

                    categoryClassified = Category.RELEVANT;
                    break;

                } else if (categories.contains(category)) {
                    categoryClassified = category;
                    break;
                }
            }

            if (categoryClassified == null)
                categoryClassified = Category.NONE;

            inst.setClassValue(categoryClassified.toString());

            switch (instanceSet) {
                case SWANDOC_MANUAL:
                case SWAN_SWANDOC_MANUAL:
                    for (Class<? extends IDocFeature> feature : docFeatures.getManualFeatureSet()) {

                        IDocFeature javadocFeature;

                        try {

                            javadocFeature = feature.newInstance();
                            AnnotatedMethod annotatedMethod = docFeatures.getManualFeatureData().get(method.getSignature());
                            //System.out.println("Adding: "+trainInstances.attribute(feature.getSimpleName())+" "+ javadocFeature.evaluate(method).getTotalValue());
                            inst.setValue(trainInstances.attribute(feature.getSimpleName()), javadocFeature.evaluate(annotatedMethod).getTotalValue());
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case SWANDOC_AUTOMATIC:
                case SWAN_SWANDOC_AUTOMATIC:
                    HashMap<String, Double> vectorValues = docFeatures.getAutomaticFeatureData().get(method.getSignature());

                    for (String key : vectorValues.keySet()) {
                        inst.setValue(trainInstances.attribute(key), vectorValues.get(key));
                    }
                    break;
            }
            trainInstances.add(inst);
        }
        return trainInstances;
    }

    public void exportInstances(Instances trainInstances, Set<Category> categories) {
        SwanConfig swanConfig = new SwanConfig();
        Properties config = swanConfig.getConfig();

        if (Boolean.parseBoolean(config.getProperty("output_train_arff_data"))) {
            // Save arff data.
            ArffSaver saver = new ArffSaver();
            saver.setInstances(trainInstances);
            List<Category> fileNameList = new ArrayList<>(categories);
            Collections.sort(fileNameList);
            String fileName = fileNameList.toString();
            fileName = fileName.substring(1, fileName.length() - 1);
            fileName = fileName.replace(", ", "_");
            try {
                saver.setFile(new File("/Users/oshando/Projects/thesis/03-code/swan/swan_core/swan-out/weka/" + "Train_" + fileName + ".arff"));
                saver.writeBatch();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
