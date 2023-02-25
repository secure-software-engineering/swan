package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.*;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CodeFeatureHandler {
    private Map<Category, Set<IFeatureNew>> featuresMap;

    private HashSet<Category> allCategories;
    private static final Logger logger = LoggerFactory.getLogger(CodeFeatureHandler.class);
    public Map<Category, Set<IFeatureNew>> features() {
        return featuresMap;
    }

    public CodeFeatureHandler() {

    }

    public void evaluateCodeFeatureData(Set<Method> methodSet) {
        //TODO refactor code features implementation to be similar to the doc features
    }

    private void addFeature(IFeatureNew feature, Set<Category> categoriesForFeature) {

        for (Category category : categoriesForFeature) {
            Set<IFeatureNew> typeFeatures = featuresMap.get(category);
            typeFeatures.add(feature);
            featuresMap.put(category, typeFeatures);
        }
    }
    public void initializeFeatures() {

        //change - loop over all classes that use the IfeatureNew Interface, create class object and
        // use addFeature method, remove feature weights completely
        featuresMap = new HashMap<>();
        allCategories = new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.AUTHENTICATION));

        for (Category category : Category.values())
            featuresMap.put(category, new HashSet<>());

        Set<Class<? extends IFeatureNew>> manualFeatureSet;
        Reflections features = new Reflections("de.fraunhofer.iem.swan.features.code");
        manualFeatureSet = features.getSubTypesOf(IFeatureNew.class);
        for(Class<? extends IFeatureNew> featureClass: manualFeatureSet){

            try {
                IFeatureNew NewFeature = featureClass.getDeclaredConstructor().newInstance();
                ((WeightedFeature) NewFeature).setWeight(5);
                addFeature(NewFeature,allCategories);
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException e) {
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
