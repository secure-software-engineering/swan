package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.CodeFeatureHandler;
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

    protected Map<IFeature, Attribute> codeAttributes;
    protected final HashMap<String, Integer> instanceMap;
    protected final SwanOptions options;
    protected Dataset dataset;
    protected CodeFeatureHandler codeFeatureHandler;
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
                case CODE:
                    codeFeatureHandler = new CodeFeatureHandler();
                    codeFeatureHandler.initializeFeatures();
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

        // Add method signatures as id attribute
        Attribute idAttr = new Attribute("id", methods.stream().map(Method::getArffSafeSignature).collect(Collectors.toList()));
        attributes.add(idAttr);

        //Create feature set and add to attributes
        for (FeatureSet.Type featureSet : featureSets)
            switch (featureSet) {

                case CODE:
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
                case CODE:
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

        // Evaluate all methods against the features.
        for (Method method : methods) {

            Instance inst = setClassValues(categories, method, instances, new DenseInstance(attributes.size()));
            inst.setDataset(instances);

            for (Category cat : categories) {
                if (cat.isAuthentication() && !method.getAuthSrm().isEmpty()) {

                    if (toolkit == ModelEvaluator.Toolkit.MEKA)
                        inst.setValue(instances.attribute(cat.getId()), "1");
                    else {
                        for (Category auth : method.getAuthSrm()) {
                            inst.setValue(instances.attribute(cat.getId()), getAuthClass(auth));
                        }
                    }
                } else if (method.getAllCategories().contains(cat)) {
                    inst.setValue(instances.attribute(cat.getId()), "1");
                } else
                    inst.setValue(instances.attribute(cat.getId()), "0");
            }

            for (Map.Entry<IFeature, Attribute> entry : codeAttributes.entrySet()) {

                switch (entry.getKey().applies(method)) {
                    case TRUE:
                        inst.setValue(instances.attribute(String.valueOf(entry.getKey())), "true");
                        break;
                    case FALSE:
                        inst.setValue(instances.attribute(String.valueOf(entry.getKey())), "false");
                        break;
                    default:
                        inst.setMissing(instances.attribute(String.valueOf(entry.getKey())));
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
    public ArrayList<Instance> getDocInstances(Instances instances, Set<Method> methods, Set<Category> categories,
                                               FeatureSet.Type instanceSet, ArrayList<Attribute> attributes) {

        ArrayList<Instance> instanceList = new ArrayList<>();

        for (Method method : methods) {

            Instance inst;
            boolean isNewInstance = false;

            //If instance exists already, update it. Otherwise, create a new instance
            if (instanceMap.containsKey(method.getArffSafeSignature())) {
                inst = instances.instance(instanceMap.get(method.getArffSafeSignature()));
            } else {
                inst = setClassValues(categories, method, instances, new DenseInstance(attributes.size()));
                inst.setDataset(instances);
                isNewInstance = true;

                inst.setValue(instances.attribute("id"), method.getArffSafeSignature());
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


    String getAuthClass(Category category){

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

            if (cat.isAuthentication() && !method.getAuthSrm().isEmpty() && toolkit == ModelEvaluator.Toolkit.WEKA) {

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
}