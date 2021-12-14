package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.CodeFeatureHandler;
import de.fraunhofer.iem.swan.features.code.soot.SourceFileLoader;
import de.fraunhofer.iem.swan.features.doc.DocFeatureHandler;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.util.Util;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Oshando Johnson on 27.09.20
 */
public class FeaturesHandler {
    private ArrayList<Attribute> attributes;
    private Map<IFeature, Attribute> codeAttributes;
    private final HashMap<String, Integer> instanceMap;
    private final SwanOptions options;
    private SrmList trainData;
    private CodeFeatureHandler codeFeatureHandler;
    private SourceFileLoader testData;
    private DocFeatureHandler docFeatureHandler;
    private ArrayList<Instances> instances;

    /**
     * Available feature sets:
     * CODE: source code features
     * DOC_MANUAL: Javadoc manual features
     * DOC_AUTO: Javadoc automatic (word embedding) features
     */
    public enum FeatureSet {
        CODE("CODE"),
        DOC_AUTO("DOC-AUTO"),
        DOC_MANUAL("DOC-MANUAL");

        private final String value;

        FeatureSet(String value) {
            this.value = value;
        }

        public static FeaturesHandler.FeatureSet getValue(String value) {
            for (FeaturesHandler.FeatureSet featureSet : FeaturesHandler.FeatureSet.values()) {
                if (featureSet.value.contains(value)) {
                    return featureSet;
                }
            }
            return null;// not found
        }
    }

    public FeaturesHandler(SrmList trainData, SourceFileLoader testData, SwanOptions options) {
        this.instanceMap = new HashMap<>();
        this.options = options;
        this.trainData = trainData;
        this.testData = testData;
        instances = new ArrayList<>();
    }

    /**
     *
     */
    public void createFeatures() {

        List<FeaturesHandler.FeatureSet> featureSets = options.getFeatureSet().stream()
                .map(f -> FeaturesHandler.FeatureSet.getValue(f.toUpperCase()))
                .collect(Collectors.toList());

        //Creat
        for (FeaturesHandler.FeatureSet featureSet : featureSets)
            switch (featureSet) {
                case CODE:
                    codeFeatureHandler = new CodeFeatureHandler(trainData.getClasspath(), testData.getClasspath());
                    codeFeatureHandler.initializeFeatures();
                    break;
                case DOC_MANUAL:

                    docFeatureHandler = new DocFeatureHandler(trainData.getMethods());
                    docFeatureHandler.initialiseManualFeatureSet();
                    docFeatureHandler.evaluateManualFeatureData();
                    break;
                case DOC_AUTO:

                    docFeatureHandler = new DocFeatureHandler(trainData.getMethods());
                    docFeatureHandler.initialiseAutomaticFeatureSet();
                    docFeatureHandler.evaluateAutomaticFeatureData();
                    break;
            }

        for (String category : options.getSrmClasses()) {

            //Create attributes for feature set
            attributes = new ArrayList<>();
            createAttributes(getCategories(category), trainData.getMethods(), featureSets);

            // Set attributes to the train instances.
            Instances trainingInstances = createInstances(featureSets, Category.fromText(category));
            instances.add(trainingInstances);
            Util.exportInstancesToArff(trainingInstances);
        }
    }


    public HashSet<Category> getCategories(String cat) {

        HashSet<Category> categories;

        if (cat.contentEquals("authentication"))
            categories = new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,
                    Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION_NEUTRAL, Category.NONE));
        else
            categories = new HashSet<>(Arrays.asList(Category.fromText(cat), Category.NONE));

        return categories;
    }

    /**
     * Creates instances and adds attributes for the features, classes, and method signatures.
     *
     * @param categories  list of categories
     * @param methods     list of training methods
     * @param featureSets classification mode
     */
    public void createAttributes(Set<Category> categories, Set<Method> methods, List<FeaturesHandler.FeatureSet> featureSets) {

        //Create feature set and add to attributes
        for (FeaturesHandler.FeatureSet featureSet : featureSets)
            switch (featureSet) {

                case CODE:
                    addCodeAttributes(categories);
                    break;
                case DOC_MANUAL:
                case DOC_AUTO:
                    addDocAttributes(featureSet);
                    break;
            }

        // Add method signatures as id attribute
        Attribute idAttr = new Attribute("id", methods.stream().map(Method::getArffSafeSignature).collect(Collectors.toList()));
        attributes.add(idAttr);

        // Collect classes and add to attributes
        Attribute classAttr = new Attribute("class", categories.stream().map(Category::toString).collect(Collectors.toList()));
        attributes.add(classAttr);
    }

    /**
     * Adds SWAN features as attributes to the instance set.
     *
     * @param categories list of categories
     */
    public void addCodeAttributes(Set<Category> categories) {

        // Collect the possible values
        ArrayList<String> ordinal = new ArrayList<>();
        ordinal.add("true");
        ordinal.add("false");

        // Collect all attributes for the categories we classify into, and create the instance set.
        codeAttributes = new HashMap<>();

        for (Category type : codeFeatureHandler.features().keySet()) {

            if (type == Category.NONE) continue;

            if (categories.contains(type)) {
                for (IFeature f : codeFeatureHandler.features().get(type)) {
                    Attribute attr = new Attribute(f.toString(), ordinal);
                    if (!codeAttributes.containsKey(f) && !attributes.contains(attr)) {
                        codeAttributes.put(f, attr);
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
    public void addDocAttributes(FeaturesHandler.FeatureSet instanceSet) {

        switch (instanceSet) {
            case DOC_MANUAL:
                for (Class<? extends IDocFeature> javadocFeature : docFeatureHandler.getManualFeatureSet()) {

                    Attribute attribute = new Attribute(javadocFeature.getSimpleName());
                    attributes.add(attribute);
                }
                break;
            case DOC_AUTO:

                for (String feature : docFeatureHandler.getAutomaticFeatureSet()) {
                    Attribute attribute = new Attribute(feature);
                    attributes.add(attribute);
                }
                break;
        }
    }


    public Instances createInstances(List<FeaturesHandler.FeatureSet> featureSets, Category category) {

        Instances instances = new Instances(category.toString() + "-methods-instances", attributes, 0);
        instances.setClass(instances.attribute("class"));

        for (FeaturesHandler.FeatureSet featureSet : featureSets)
            switch (featureSet) {
                case CODE:
                    instances.addAll(getCodeInstances(instances, trainData.getMethods(), category));
                    break;
                case DOC_MANUAL:
                case DOC_AUTO:
                    instances.addAll(getDocInstances(instances, trainData.getMethods(), category, featureSet));
                    break;
            }
        return instances;
    }

    /**
     * Adds data for SWAN features to instance set.
     *
     * @param instances instance srt
     * @param methods   training set
     * @return instance set containing data from SWAN
     */
    public ArrayList<Instance> getCodeInstances(Instances instances, Set<Method> methods, Category category) {

        ArrayList<Instance> instanceList = new ArrayList<>();

        int instanceIndex = 0;

        // Evaluate all methods against the features.
        for (Method method : methods) {

            Instance inst = new DenseInstance(attributes.size());
            inst.setDataset(instances);
            inst.setClassValue(getCategory(method, category));

            for (Map.Entry<IFeature, Attribute> entry : codeAttributes.entrySet()) {
                switch (entry.getKey().applies(method)) {
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
            inst.setValue(instances.attribute("id"), method.getArffSafeSignature());

            instanceList.add(inst);
            instanceMap.put(method.getArffSafeSignature(), instanceIndex++);
        }
        return instanceList;
    }

    /**
     * Adds data for SWAN-DOC features to instance set.
     *
     * @param instances instance srt
     * @return Instances containing data from SWAN-DOC
     */
    public ArrayList<Instance> getDocInstances(Instances instances, Set<Method> methods, Category category, FeaturesHandler.FeatureSet instanceSet) {

        ArrayList<Instance> instanceList = new ArrayList<>();

        for (Method method : methods) {

            Instance inst;
            boolean isNewInstance = false;

            //If instance exists already, update it. Otherwise, create a new instance
            if (instanceMap.containsKey(method.getArffSafeSignature())) {
                inst = instances.instance(instanceMap.get(method.getArffSafeSignature()));
            } else {
                inst = new DenseInstance(attributes.size());
                inst.setDataset(instances);
                isNewInstance = true;
                inst.setClassValue(getCategory(method, category));
                inst.setValue(instances.attribute("id"), method.getArffSafeSignature());
            }


            switch (instanceSet) {
                case DOC_MANUAL:
                    for (Class<? extends IDocFeature> feature : docFeatureHandler.getManualFeatureSet()) {

                        try {
                            IDocFeature javadocFeature = feature.newInstance();
                            AnnotatedMethod annotatedMethod = docFeatureHandler.getManualFeatureData().get(method.getSignature());
                            inst.setValue(instances.attribute(feature.getSimpleName()), javadocFeature.evaluate(annotatedMethod).getTotalValue());
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case DOC_AUTO:
                    HashMap<String, Double> vectorValues = docFeatureHandler.getAutomaticFeatureData().get(method.getSignature());

                    for (String key : vectorValues.keySet()) {
                        inst.setValue(instances.attribute(key), vectorValues.get(key));
                    }
                    break;
            }
            if (isNewInstance)
                instanceList.add(inst);
        }
        return instanceList;
    }

    /**
     * Checks if method belongs to the specified category.
     *
     * @param method test or training method
     * @param category SRM or CWE class being evaluated
     * @return string representation of the method
     */
    public String getCategory(Method method, Category category) {

        if (method.getSrm().contains(category) || (method.getCwe().contains(category))
                || category.toString().contains("relevant") && !method.getSrm().isEmpty())
            return category.toString();
        else if (category.toString().contains("authentication")) {

            Set<Category> auth = method.getAuthSrm();

            if (!auth.isEmpty()) {
                return auth.stream().findFirst().get().toString();
            }
        }
        return Category.NONE.toString();
    }

    public ArrayList<Instances> getInstances() {
        return instances;
    }

    public void setInstances(ArrayList<Instances> instances) {
        this.instances = instances;
    }

    public CodeFeatureHandler getCodeFeatureHandler() {
        return codeFeatureHandler;
    }

    public void setCodeFeatureHandler(CodeFeatureHandler codeFeatureHandler) {
        this.codeFeatureHandler = codeFeatureHandler;
    }

    public DocFeatureHandler getDocFeatureHandler() {
        return docFeatureHandler;
    }

    public void setDocFeatureHandler(DocFeatureHandler docFeatureHandler) {
        this.docFeatureHandler = docFeatureHandler;
    }
}
