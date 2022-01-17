package de.fraunhofer.iem.swan.features;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.CodeFeatureHandler;
import de.fraunhofer.iem.swan.features.code.soot.SourceFileLoader;
import de.fraunhofer.iem.swan.features.code.type.IFeature;
import de.fraunhofer.iem.swan.features.doc.DocFeatureHandler;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;
import java.util.stream.Collectors;

abstract class FeatureSet {

    protected Map<IFeature, Attribute> codeAttributes;
    protected final HashMap<String, Integer> instanceMap;
    protected final SwanOptions options;
    protected SrmList trainData;
    protected CodeFeatureHandler codeFeatureHandler;
    protected SourceFileLoader testData;
    protected DocFeatureHandler docFeatureHandler;
    protected HashMap<String, Instances> instances;
    protected ModelEvaluator.Toolkit toolkit;

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

        public static FeatureSet.Type getValue(String value) {
            for (FeatureSet.Type featureSet : FeatureSet.Type.values()) {
                if (featureSet.value.contains(value)) {
                    return featureSet;
                }
            }
            return null;
        }
    }

    public FeatureSet(SrmList trainData, SourceFileLoader testData, SwanOptions options, ModelEvaluator.Toolkit toolkit) {
        this.instanceMap = new HashMap<>();
        this.options = options;
        this.trainData = trainData;
        this.testData = testData;
        this.toolkit = toolkit;
        instances = new HashMap<>();
    }

    /**
     *
     */
    public List<FeatureSet.Type> initializeFeatures() {

        List<FeatureSet.Type> featureSets = options.getFeatureSet().stream()
                .map(f -> FeatureSet.Type.getValue(f.toUpperCase()))
                .collect(Collectors.toList());

        for (FeatureSet.Type featureSet : featureSets)
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

        return featureSets;
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

    public Instances createInstances(List<Type> featureSets, ArrayList<Attribute> attributes,
                                     Set<Method> methods, Set<Category> categories, String name) {

        Instances instances = new Instances(name, attributes, 0);

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

            Instance inst = new DenseInstance(attributes.size());
            inst.setDataset(instances);

            for (Category cat : categories) {
                if (cat.isAuthentication() && !method.getAuthSrm().isEmpty()) {

                    if (toolkit == ModelEvaluator.Toolkit.MEKA)
                        inst.setValue(instances.attribute(cat.getId()), "1");
                    else {
                        for (Category auth : method.getAuthSrm()) {
                            switch (auth) {
                                case AUTHENTICATION_TO_LOW:
                                    inst.setValue(instances.attribute(cat.getId()), "1");
                                    break;
                                case AUTHENTICATION_NEUTRAL:
                                    inst.setValue(instances.attribute(cat.getId()), "2");
                                    break;
                                case AUTHENTICATION_TO_HIGH:
                                    inst.setValue(instances.attribute(cat.getId()), "3");
                                    break;
                            }
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
                inst = new DenseInstance(attributes.size());
                inst.setDataset(instances);
                isNewInstance = true;

                switch (toolkit) {
                    case MEKA:
                        for (Category cat : categories) {
                            if (method.getAllCategories().contains(cat) || (cat.isAuthentication() && !method.getAuthSrm().isEmpty())) {
                                inst.setValue(instances.attribute(cat.getId()), "1");
                            } else
                                inst.setValue(instances.attribute(cat.getId()), "0");
                        }
                        break;

                    case WEKA:
                        if (method.getSrm() != null || method.getCwe() != null)
                            // inst.setClassValue(getCategory(method, categories));
                            break;
                }

                inst.setValue(instances.attribute("id"), method.getArffSafeSignature());
            }

            switch (instanceSet) {
                case DOC_MANUAL:
                    for (Class<? extends IDocFeature> feature : docFeatureHandler.getManualFeatureSet()) {

                        try {
                            IDocFeature javadocFeature = feature.newInstance();
                            AnnotatedMethod annotatedMethod = docFeatureHandler.getManualFeatureData().get(method.getSignature());

                            if (annotatedMethod != null)
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
     * @param method   test or training method
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

    public HashMap<String, Instances> getInstances() {
        return instances;
    }

    public Instances getTrainInstances() {
        return instances.get("train");
    }

    public Instances getTestInstances() {
        return instances.get("test");
    }


    public void setInstances(HashMap<String, Instances> instances) {
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