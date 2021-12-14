package de.fraunhofer.iem.swan.training;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.DocFeatureHandler;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import weka.core.*;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.NominalToBinary;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Oshando Johnson on 08.08.20
 */
public class TrainingSetCreator {

    private FastVector attributesVector;
    private HashMap<String, Attribute> featureAttributes;
    private Attribute methodSignatureAttribute;
    private Set<Class<? extends IDocFeature>> featuresSet;
    private Set<Category> categorySet;

    /**
     * Exports and returns the data set.
     *
     * @param annotatedMethods array list of annotated methods
     * @return instances based on data set.
     */
    public Instances getDataSet(Set<Category> categories, ArrayList<AnnotatedMethod> annotatedMethods, boolean isCweMode) {

        categorySet = categories;

        Instances instances = createInstances(annotatedMethods);

        //Apply filters to dataset
        MultiFilter filters = new MultiFilter();
        filters.setFilters(new Filter[]{new NominalToBinary()});
        instances = applyFilter(instances, filters);

        exportInstanceSet(instances);

        return instances;
    }

    /**
     * Applies the Weka filters to the instances.
     *
     * @param instances instane set
     * @param filters   array of filters
     * @return instances with filter applied
     */
    public Instances applyFilter(Instances instances, MultiFilter filters) {

        try {
            filters.setInputFormat(instances);
            return Filter.useFilter(instances, filters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Initializes the instances with the necessary attributes.
     *
     * @param annotatedMethods array list of annotated methods
     */
    public void initializeInstances(ArrayList<AnnotatedMethod> annotatedMethods) {

        attributesVector = new FastVector();

        getFeatureAttributes();

        //Add method signatures to attribute set
        FastVector signatureVector = new FastVector();

        for (AnnotatedMethod method : annotatedMethods) {
            signatureVector.addElement(method.getMethod().getJavaSignature());
        }

        methodSignatureAttribute = new Attribute("id", signatureVector);
        attributesVector.addElement(methodSignatureAttribute);

        //Add classes to attribute set
        FastVector classesVector = new FastVector();

        for (Category type : categorySet) {
            classesVector.addElement(type.toString());
        }

        Attribute classAttribute = new Attribute("class", classesVector);
        attributesVector.addElement(classAttribute);
    }



    public void getFeatureAttributes(){

        DocFeatureHandler featureHandler = new DocFeatureHandler(null);
        featuresSet = featureHandler.getManualFeatureSet();
        featureAttributes = new HashMap<>();

        for (Class<? extends IDocFeature> unprocessedDocFeature : featuresSet) {

            Attribute attribute = new Attribute(unprocessedDocFeature.getSimpleName());
            attributesVector.addElement(attribute);
            featureAttributes.put(unprocessedDocFeature.getSimpleName(), attribute);
        }
    }

    /**
     * Creates the instances uses the feature set.
     *
     * @param annotatedMethods array list of annotated methods
     * @return instances object containing instance data
     */
    public Instances createInstances(ArrayList<AnnotatedMethod> annotatedMethods) {

        //Set up instances template
        initializeInstances(annotatedMethods);

        Instances instances = new Instances("swandoc-training", attributesVector, 0);
        instances.setClass(instances.attribute("class"));

        for (AnnotatedMethod method : annotatedMethods) {

            Instance inst = new DenseInstance(attributesVector.size());
            inst.setDataset(instances);
            inst.setValue(instances.attribute("id"), method.getMethod().getJavaSignature());

            List<Category> list = new ArrayList<>(method.getMethod().getSrm());
            //System.out.println(method.getMethod().getJavaSignature() + "/" + list);

            Category categoryClassified = null;

            for (Category category : method.getMethod().getSrm()) {
                if (categorySet.contains(category)) {
                    categoryClassified = category;
                    break;
                }
            }

            if (categoryClassified == null)
                categoryClassified = Category.NONE;

            inst.setClassValue(categoryClassified.toString());

            for (Class feature : featuresSet) {

                IDocFeature javadocFeature;

                try {
                    javadocFeature = (IDocFeature) feature.newInstance();
                    inst.setValue(featureAttributes.get(feature.getSimpleName()), javadocFeature.evaluate(method).getTotalValue());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            instances.add(inst);
        }
        return instances;
    }

    /**
     * Exports the instances to an ARFF file.
     *
     * @param instances instances to be exported
     */
    public void exportInstanceSet(Instances instances) {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);

        try {
            saver.setFile(new File("/swandoc/training-set/swandoc-training-set.arff"));
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}