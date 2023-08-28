package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.CodeFeatureHandler;
import de.fraunhofer.iem.swan.features.code.CodeFeatureHandlerOld;
import de.fraunhofer.iem.swan.features.code.ICodeFeature;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import de.fraunhofer.iem.swan.features.doc.DocFeatureHandler;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.io.dataset.Dataset;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class FeatureSet {

    protected Map<ICodeFeature, Attribute> codeAttributes;
    protected Map<IFeature, Attribute> codeAttributesOld;
    protected final HashMap<String, Integer> instanceMap;
    protected final SwanOptions options;
    protected Dataset dataset;
    protected CodeFeatureHandler codeFeatureHandler;
    protected CodeFeatureHandlerOld codeFeatureHandlerOld;
    protected DocFeatureHandler docFeatureHandler;
    protected HashMap<String, Instances> trainInstances;
    protected HashMap<String, Instances> testInstances;
    protected ModelEvaluator.Toolkit toolkit;
    protected List<FeatureSet.Type> featureSets;

    /**
     * Available feature sets:
     * CODE: source code features
     * DOC_MANUAL: Javadoc manual features
     * DOC_AUTO: Javadoc automatic (word embedding) features
     */
    public enum Type {
        CODE("CODE"),
        CODX("CODX"),
        DOC_AUTO("DOC-AUTO"),
        DOC_MANUAL("DOC-MANUAL");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value.toLowerCase();
        }

        public static FeatureSet.Type getValue(String value) {
            for (FeatureSet.Type featureSet : FeatureSet.Type.values()) {
                if (featureSet.value.contains(value)) {
                    return featureSet;
                }
            }
            return null;
        }
    }

    public FeatureSet(Dataset dataset, SwanOptions options, ModelEvaluator.Toolkit toolkit) {
        this.instanceMap = new HashMap<>();
        this.options = options;
        this.dataset = dataset;
        this.toolkit = toolkit;
        trainInstances = new HashMap<>();
        testInstances = new HashMap<>();

        featureSets = options.getFeatureSet().stream()
                .map(f -> FeatureSet.Type.getValue(f.toUpperCase()))
                .collect(Collectors.toList());
    }

    /**
     * Initialize feature handlers.
     */
    public void initializeFeatures() {

        for (FeatureSet.Type featureSet : featureSets)
            switch (featureSet) {
                case CODX:
                    codeFeatureHandler = new CodeFeatureHandler();
                    codeFeatureHandler.initializeFeatures();
                    break;
                case CODE:
                    codeFeatureHandlerOld = new CodeFeatureHandlerOld();
                    codeFeatureHandlerOld.initializeFeatures();
                    break;
                case DOC_MANUAL:
                    docFeatureHandler = new DocFeatureHandler();
                    docFeatureHandler.initialiseManualFeatureSet();
                    break;
                case DOC_AUTO:
                    docFeatureHandler = new DocFeatureHandler();
                    docFeatureHandler.initialiseAutomaticFeatureSet();
                    break;
            }
    }

    /**
     * Creates instances and adds attributes for the features, classes, and method signatures.
     *
     * @param categories list of categories
     * @param methods    list of training methods
     */
    public ArrayList<Attribute> createAttributes(Set<Category> categories, Set<Method> methods) {

        ArrayList<Attribute> attributes = new ArrayList<>();

        //Create feature set and add to attributes
        for (FeatureSet.Type featureSet : featureSets)
            switch (featureSet) {

                case CODE:
                case CODX:
                    attributes.addAll(addCodeAttributes(categories));
                    break;
                case DOC_MANUAL:
                case DOC_AUTO:
                    attributes.addAll(addDocAttributes(featureSet));
                    break;
            }

        return attributes;
    }

    /**
     * Adds SWAN features as attributes to the instance set.
     *
     * @param categories list of categories
     */
    public ArrayList<Attribute> addCodeAttributes(Set<Category> categories) {

        ArrayList<Attribute> attributes = new ArrayList<>();

        // Collect all attributes for the categories we classify into, and create the instance set.

        for (FeatureSet.Type featureSet : featureSets)
            switch (featureSet) {

                case CODX:
                    codeAttributes = new HashMap<>();
                    for (Category type : codeFeatureHandler.features().keySet()) {
                        if (type == Category.NONE) continue;
                        if (categories.contains(type)) {
                            for (ICodeFeature f : codeFeatureHandler.features().get(type)) {
                                Attribute attr = null;
                                switch (f.getFeatureType()) { //Initializing instance values for each feature type.
                                    case NUMERICAL:
                                        attr = new Attribute(f.toString());
                                        break;
                                    case BOOLEAN:
                                    case CATEGORICAL:
                                        ArrayList<String> featureValues = f.getFeatureValues();
                                        attr = new Attribute(f.toString(), featureValues);
                                }
                                //Set all the possible values for a given feature
                                if (!codeAttributes.containsKey(f) && !attributes.contains(attr)) {
                                    codeAttributes.put(f, attr);
                                    attributes.add(attr);
                                }
                            }
                        }
                    }
                    break;
                case CODE:
                    codeAttributesOld = new HashMap<>();
                    ArrayList<String> ordinal = new ArrayList<>();
                    ordinal.add("true");
                    ordinal.add("false");

                    // Collect all attributes for the categories we classify into, and create the instance set.

                    for (Category type : codeFeatureHandlerOld.features().keySet()) {

                        if (type == Category.NONE) continue;

                        if (categories.contains(type)) {
                            for (IFeature f : codeFeatureHandlerOld.features().get(type)) {
                                Attribute attr = new Attribute(f.toString(), ordinal);
                                if (!codeAttributesOld.containsKey(f) && !attributes.contains(attr)) {
                                    codeAttributesOld.put(f, attr);
                                    attributes.add(attr);
                                }
                            }
                        }
                    }

                    break;
            }
        return attributes;
    }

    /**
     * Adds SWAN-DOC features as attributes to the instance set.
     *
     * @param instanceSet classification mode
     */
    public ArrayList<Attribute> addDocAttributes(FeatureSet.Type instanceSet) {

        ArrayList<Attribute> attributes = new ArrayList<>();

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
        return attributes;
    }

    public void evaluateFeatureData(Set<Method> methods) {

        for (FeatureSet.Type featureSet : featureSets)
            switch (featureSet) {
                case CODX:
                    codeFeatureHandler.evaluateCodeFeatureData(methods);
                    break;
                case CODE:
                    codeFeatureHandlerOld.evaluateCodeFeatureData(methods);
                    break;
                case DOC_MANUAL:
                    docFeatureHandler.evaluateManualFeatureData(methods);
                    break;
                case DOC_AUTO:
                    docFeatureHandler.evaluateAutomaticFeatureData(methods);
                    break;
            }
    }

    public Instances createInstances(Instances instances, ArrayList<Attribute> attributes,
                                     Set<Method> methods, Set<Category> categories) {

        for (FeatureSet.Type featureSet : featureSets)
            switch (featureSet) {
                case CODE:
                case CODX:
                    instances.addAll(getCodeInstances(instances, methods, categories, attributes));
                    break;
                case DOC_MANUAL:
                case DOC_AUTO:
                    instances.addAll(getDocInstances(instances, methods, categories, featureSet, attributes));
                    break;
            }
        return instances;
    }


    public Instances createInstances(ArrayList<Attribute> attributes,
                                     Set<Method> methods, Set<Category> categories) {

        Instances instances = new Instances("swan-srm", attributes, 0);

        return createInstances(instances, attributes, methods, categories);
    }

    public Instances mergeInstances(Instances first, Instances second) {
        Instances instances = Instances.mergeInstances(first, second);

        ArrayList<Integer> indices = new ArrayList<>();

        for (int att = 0; att < instances.numAttributes(); att++) {
            if (instances.attribute(att).name().startsWith("b_")) {
                indices.add(att);
            }
        }

        Remove removeFilter = new Remove();
        removeFilter.setAttributeIndicesArray(indices.stream().mapToInt(i -> i).toArray());
        removeFilter.setInvertSelection(false);

        try {
            removeFilter.setInputFormat(instances);
            instances = Filter.useFilter(instances, removeFilter);

        } catch (Exception e) {
            e.printStackTrace();
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
    public ArrayList<Instance> getCodeInstances(Instances instances, Set<Method> methods, Set<Category> categories, ArrayList<Attribute> attributes) {

        ArrayList<Instance> instanceList = new ArrayList<>();

        int instanceIndex = 0;

        for (FeatureSet.Type featureSet : featureSets)

            switch (featureSet) {

                case CODX:

                    for (Method method : methods) {

                        Instance inst = setClassValues(categories, method, instances, new DenseInstance(attributes.size()));
                        inst.setDataset(instances);

                        for (Map.Entry<ICodeFeature, Attribute> entry : codeAttributes.entrySet()) {

                            switch (entry.getKey().getFeatureType()) {
                                case BOOLEAN:
                                    boolean booleanData = entry.getKey().applies(method).getBooleanValue();
                                    inst.setValue(instances.attribute(entry.getKey().toString()), String.valueOf(booleanData));
                                    break;
                                case CATEGORICAL:

                                    String stringData = entry.getKey().applies(method).getStringValue();
                                    inst.setValue(instances.attribute(entry.getKey().toString()), stringData);
                                    break;
                                case NUMERICAL:
                                    int integerData = entry.getKey().applies(method).getIntegerValue();
                                    inst.setValue(instances.attribute(entry.getKey().toString()), integerData);
                                    break;
                            }
                        }
                        instanceList.add(inst);
                        instanceMap.put(method.getSignature(), instanceIndex++);
                    }
                    break;
                case CODE:
                    // Evaluate all methods against the features.
                    for (Method method : methods) {

                        Instance inst = setClassValues(categories, method, instances, new DenseInstance(attributes.size()));
                        inst.setDataset(instances);

                        for (Map.Entry<IFeature, Attribute> entry : codeAttributesOld.entrySet()) {

                            switch (entry.getKey().applies(method)) {
                                case TRUE:
                                    inst.setValue(instances.attribute(entry.getKey().toString()), "true");
                                    break;
                                case FALSE:
                                    inst.setValue(instances.attribute(entry.getKey().toString()), "false");
                                    break;
                                default:
                                    inst.setMissing(instances.attribute(entry.getKey().toString()));
                            }
                        }

                        instanceList.add(inst);
                        instanceMap.put(method.getSignature(), instanceIndex++);
                    }
                    break;
            }
        return instanceList;
    }

    /**
     * Adds data for SWAN-DOC features to instance set.
     *
     * @param instances instance srt
     * @return Instances containing data from SWAN-DOC
     */
    public ArrayList<Instance> getDocInstances(Instances instances, Set<Method> methods, Set<Category> categories,
                                               FeatureSet.Type instanceSet, ArrayList<Attribute> attributes) {

        ArrayList<Instance> instanceList = new ArrayList<>();

        for (Method method : methods) {

            Instance inst;
            boolean isNewInstance = false;

            //If instance exists already, update it. Otherwise, create a new instance
            if (instanceMap.containsKey(method.getSignature())) {
                inst = instances.instance(instanceMap.get(method.getSignature()));
            } else {
                inst = setClassValues(categories, method, instances, new DenseInstance(attributes.size()));
                inst.setDataset(instances);
                isNewInstance = true;

                inst.setValue(instances.attribute("id"), method.getSignature());
            }

            switch (instanceSet) {
                case DOC_MANUAL:
                    for (Class<? extends IDocFeature> feature : docFeatureHandler.getManualFeatureSet()) {

                        try {
                            IDocFeature javadocFeature = feature.getDeclaredConstructor().newInstance();
                            AnnotatedMethod annotatedMethod = docFeatureHandler.getManualFeatureData().get(method.getSignature());

                            if (annotatedMethod != null)
                                inst.setValue(instances.attribute(feature.getSimpleName()), javadocFeature.evaluate(annotatedMethod).getTotalValue());
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case DOC_AUTO:
                    HashMap<String, Double> vectorValues = docFeatureHandler.getAutomaticFeatureData().get(method.getSignature());

                    if (vectorValues != null) {
                        for (String key : vectorValues.keySet()) {
                            inst.setValue(instances.attribute(key), vectorValues.get(key));
                        }
                    }

                    break;
            }
            if (isNewInstance)
                instanceList.add(inst);
        }
        return instanceList;
    }

    /**
     * Returns authentication class ID for an authentication category.
     *
     * @param category
     * @return Class ID
     */
    String getAuthClass(Category category) {

        switch (category) {
            case AUTHENTICATION_TO_LOW:
                return "1";
            case AUTHENTICATION_NEUTRAL:
                return "2";
            case AUTHENTICATION_TO_HIGH:
            default:
                return "3";
        }
    }

    Instance setClassValues(Set<Category> categories, Method method, Instances instances, Instance inst) {

        for (Category cat : categories) {

            if (instances.relationName().endsWith("test")) {
                inst.setMissing(instances.attribute(cat.getId()));
            } else if (cat.isAuthentication() && !method.getAuthSrm().isEmpty() && toolkit == ModelEvaluator.Toolkit.WEKA) {

                for (Category auth : method.getAuthSrm()) {
                    inst.setValue(instances.attribute(cat.getId()), getAuthClass(auth));
                }
            } else if (method.getAllCategories().contains(cat)) {
                inst.setValue(instances.attribute(cat.getId()), "1");
            } else
                inst.setValue(instances.attribute(cat.getId()), "0");
        }
        return inst;
    }

    public int getInstanceIndex(String methodSignature) {

        return instanceMap.get(methodSignature);
    }

    public HashMap<String, Integer> getInstanceMap() {
        return instanceMap;
    }

    /**
     * Checks if method belongs to the specified category.
     *
     * @param method   test or training method
     * @param category SRM or CWE class being evaluated
     * @return string representation of the method
     */
    public boolean getCategory(Method method, Category category) {

        if (method.getSrm().contains(category) || (method.getCwe().contains(category))
                || category.toString().contains("relevant") && !method.getSrm().isEmpty())
            return true;
        else if (category.toString().contains("authentication")) {

            Set<Category> auth = method.getAuthSrm();

            return !auth.isEmpty();
        }
        return false;
    }

    public HashMap<String, Instances> getTestInstances() {
        return testInstances;
    }

    public HashMap<String, Instances> getTrainInstances() {
        return trainInstances;
    }

    public void setTrainInstances(HashMap<String, Instances> trainInstances) {
        this.trainInstances = trainInstances;
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

    public Dataset getDataset() {
        return dataset;
    }
}