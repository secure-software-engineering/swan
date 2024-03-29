package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.bow.SecurityVocabulary;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CodeFeatureHandler {
    private Map<Category, Set<ICodeFeature>> featuresMap;

    private HashSet<Category> allCategories;
    private static final Logger logger = LoggerFactory.getLogger(CodeFeatureHandler.class);

    public Map<Category, Set<ICodeFeature>> features() {
        return featuresMap;
    }

    public CodeFeatureHandler() {

    }

    public void evaluateCodeFeatureData(Set<Method> methodSet) {
        //TODO refactor code features implementation to be similar to the doc features
    }

    private void addFeature(ICodeFeature feature, Set<Category> categoriesForFeature) {

        for (Category category : categoriesForFeature) {
            Set<ICodeFeature> typeFeatures = featuresMap.get(category);
            typeFeatures.add(feature);
            featuresMap.put(category, typeFeatures);
        }
    }

    public void initializeFeatures() {

        //change - loop over all classes that use the IfeatureNew Interface, create class object and
        // use addFeature method, remove feature weights completely
        featuresMap = new HashMap<>();
        allCategories = new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK, Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION));

        for (Category category : Category.values())
            featuresMap.put(category, new HashSet<>());

        Set<Class<? extends ICodeFeature>> featureSet;
        Reflections features = new Reflections("de.fraunhofer.iem.swan.features.code");
        featureSet = features.getSubTypesOf(ICodeFeature.class);

        for (Class<? extends ICodeFeature> featureClass : featureSet) {

            try {

                switch (featureClass.getName()) {
                    case "de.fraunhofer.iem.swan.features.code.bow.MethodNameContainsToken":
                        Constructor<? extends ICodeFeature> methodNameConstructor = featureClass.getDeclaredConstructor(String.class);
                        for (String token : SecurityVocabulary.METHOD_NAME_TOKENS)
                            addFeature(methodNameConstructor.newInstance(token), allCategories);
                        break;

                    case "de.fraunhofer.iem.swan.features.code.bow.ClassNameContainsToken":
                        Constructor<? extends ICodeFeature> classNameConstructor = featureClass.getDeclaredConstructor(String.class);
                        for (String token : SecurityVocabulary.CLASS_CONTAINS_TOKENS)
                            addFeature(classNameConstructor.newInstance(token), allCategories);
                        break;

                    case "de.fraunhofer.iem.swan.features.code.bow.InvokedMethodNameContainsToken":
                        Constructor<? extends ICodeFeature> invokedMethodNameConstructor = featureClass.getDeclaredConstructor(String.class);
                        for (String token : SecurityVocabulary.INNVOKED_METHOD_NAME_TOKENS)
                            addFeature(invokedMethodNameConstructor.newInstance(token), allCategories);
                        break;


                    case "de.fraunhofer.iem.swan.features.code.bow.InvokedClassNameContainsToken":
                        Constructor<? extends ICodeFeature> invokedClassNameConstructor = featureClass.getDeclaredConstructor(String.class);
                        for (String token : SecurityVocabulary.INNVOKED_CLASS_NAME_TOKENS)
                            addFeature(invokedClassNameConstructor.newInstance(token), allCategories);
                        break;

                    case "de.fraunhofer.iem.swan.features.code.bow.SourceToReturnFeature":
                        Constructor<? extends ICodeFeature> SourceToReturnConstructor = featureClass.getDeclaredConstructor(String.class);
                        for (String token : SecurityVocabulary.SOURCE_TO_RETURN)
                            addFeature(SourceToReturnConstructor.newInstance(token), allCategories);
                        break;

                    case "de.fraunhofer.iem.swan.features.code.bow.ParameterToInvokedSinkFeature":
                        Constructor<? extends ICodeFeature> ParameterToInvokedSinkConstructor = featureClass.getDeclaredConstructor(String.class);
                        for (String token : SecurityVocabulary.PARAMETER_TO_SINK)
                            addFeature(ParameterToInvokedSinkConstructor.newInstance(token), allCategories);
                        break;
                    default:
                        ICodeFeature codeFeature = featureClass.getDeclaredConstructor().newInstance();
                        addFeature(codeFeature, allCategories);
                }
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void getFeaturesSize() {
        int count = 0;

        HashMap<String, Integer> featuresCount = new HashMap<>();

        for (Category c : featuresMap.keySet()) {
            int features = featuresMap.get(c).size();
            featuresCount.put(c.toString(), features);
            count += features;
        }
        logger.info("Created {} code features,  distribution of methods={}", count, featuresCount);
    }
}
