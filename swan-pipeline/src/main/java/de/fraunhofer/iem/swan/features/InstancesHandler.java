package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.doc.DocFeatureHandler;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

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
    public enum FeatureSet {
        SWAN(0),
        SWAN_SWANDOC_MANUAL(1),
        SWAN_SWANDOC_WORD_EMBEDDING(2),
        SWANDOC_MANUAL(3),
        SWANDOC_WORD_EMBEDDING(4);

        private int value;

        FeatureSet(int value){
            this.value = value;
        }

        public static  FeatureSet getValue(int value) {
            for(FeatureSet featureSet: FeatureSet.values()) {
                if(featureSet.value == value) {
                    return featureSet;
                }
            }
            return null;// not found
        }
    }

    private ArrayList<Attribute> attributes;
    private Map<IFeature, Attribute> swanFeatureAttribs;
    private HashMap<String, Integer> instanceMap;
    private Instances instances;

    public InstancesHandler() {
        this.instanceMap = new HashMap<>();
    }

    /**
     * @param trainingSet
     * @param features
     * @param categories
     * @param instanceSet
     * @return
     */
    public void createInstances(Set<Method> trainingSet,
                                     Map<Category, Set<IFeature>> features, DocFeatureHandler docFeatures, Set<Category> categories,
                                     FeatureSet instanceSet) {

        //Initialize instances
        initializeInstances(features, docFeatures, categories, trainingSet, null, instanceSet);

        // Set attributes to the train instances.
         instances = new Instances("training-methods", attributes, 0);
        instances.setClass(instances.attribute("class"));

        //Populate SWAN feature attributes
        switch (instanceSet) {
            case SWAN:
                instances = addSwanInstances(instances, trainingSet, categories);
                break;
            case SWANDOC_MANUAL:
            case SWANDOC_WORD_EMBEDDING:
                instances = addSwanDocInstances(instances, trainingSet, docFeatures, categories, instanceSet);
                break;
            case SWAN_SWANDOC_MANUAL:
            case SWAN_SWANDOC_WORD_EMBEDDING:
                instances = addSwanInstances(instances, trainingSet, categories);
                instances = addSwanDocInstances(instances,trainingSet, docFeatures, categories, instanceSet);
                break;
        }
        //exportInstances(trainInstances, categories);
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
                                    Set<Method> trainingSet, Set<Method> testSet, FeatureSet instanceSet) {

        attributes = new ArrayList<>();

        //Create feature set and add to attributes
        switch (instanceSet) {

            case SWAN:
                initializeSwanFeatures(features, categories);
                break;
            case SWAN_SWANDOC_MANUAL:
            case SWAN_SWANDOC_WORD_EMBEDDING:
                initializeSwanFeatures(features, categories);
                initializeSwanDocFeatures(docFeatures, instanceSet);
                break;
            case SWANDOC_MANUAL:
            case SWANDOC_WORD_EMBEDDING:
                initializeSwanDocFeatures(docFeatures, instanceSet);
                break;
        }

        // Add method signatures as id attribute
        ArrayList<String> methodStrings = new ArrayList<>();

       // System.out.println("TOTAL SET...."+trainingSet.size());
        int c = 0;
        for (Method am : trainingSet) {
            methodStrings.add(am.getArffSafeSignature());
            //System.out.println("ini: "+am.getArffSafeSignature());
            c++;
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
    public void initializeSwanDocFeatures(DocFeatureHandler features, FeatureSet instanceSet) {

        switch (instanceSet) {
            case SWAN_SWANDOC_MANUAL:
            case SWANDOC_MANUAL:
                for (Class<? extends IDocFeature> javadocFeature : features.getManualFeatureSet()) {

                    Attribute attribute = new Attribute(javadocFeature.getSimpleName());
                    attributes.add(attribute);
                }
                break;
            case SWAN_SWANDOC_WORD_EMBEDDING:
            case SWANDOC_WORD_EMBEDDING:

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
            inst.setValue(trainInstances.attribute("id"), am.getArffSafeSignature());
            c++;

            trainInstances.add(inst);
            instanceMap.put(am.getArffSafeSignature(), instanceIndex++);
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
    public Instances addSwanDocInstances(Instances trainInstances, Set<Method> trainingSet, DocFeatureHandler docFeatures, Set<Category> categories, FeatureSet instanceSet) {

       // System.out.println("TOTAL DOCS...."+docFeatures.getMethodSet().size());
        for (Method method : trainingSet) {

           // System.out.println("Value not found: "+method.getArffSafeSignature());
            Instance inst;
            boolean isNewInstance = false;

            //If instance exists already, update it. Otherwise create a new instance
            if (instanceMap.containsKey(method.getArffSafeSignature())) {

                inst = trainInstances.instance(instanceMap.get(method.getArffSafeSignature()));
                //    System.out.println("Instance for: "+  inst.attribute());
                // trainInstances.delete(instanceMap.get(method.getMethod().getSignature()));
            } else {
                inst = new DenseInstance(attributes.size());
                inst.setDataset(trainInstances);
                isNewInstance = true;
            }

           // System.out.println("Value really not found: "+method.getArffSafeSignature());
            inst.setValue(trainInstances.attribute("id"), method.getArffSafeSignature());

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

                        try {

                            IDocFeature javadocFeature = feature.newInstance();
                            AnnotatedMethod annotatedMethod = docFeatures.getManualFeatureData().get(method.getSignature());
                            //System.out.println("Adding: "+trainInstances.attribute(feature.getSimpleName())+" "+ javadocFeature.evaluate(method).getTotalValue());
                            inst.setValue(trainInstances.attribute(feature.getSimpleName()), javadocFeature.evaluate(annotatedMethod).getTotalValue());
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case SWANDOC_WORD_EMBEDDING:
                case SWAN_SWANDOC_WORD_EMBEDDING:
                    HashMap<String, Double> vectorValues = docFeatures.getAutomaticFeatureData().get(method.getSignature());

                    for (String key : vectorValues.keySet()) {
                        inst.setValue(trainInstances.attribute(key), vectorValues.get(key));
                    }
                    break;
            }
            if (isNewInstance)
                trainInstances.add(inst);
        }
        return trainInstances;
    }

    public Instances getInstances() {
        return instances;
    }
}
