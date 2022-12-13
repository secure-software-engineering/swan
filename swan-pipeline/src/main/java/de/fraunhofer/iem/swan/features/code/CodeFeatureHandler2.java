package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CodeFeatureHandler2 {
    private Map<Category, Set<IFeatureNew>> featuresMap;
    private static final Logger logger = LoggerFactory.getLogger(CodeFeatureHandler.class);
    public Map<Category, Set<IFeatureNew>> features() {
        return featuresMap;
    }

    public CodeFeatureHandler2() {

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
        featuresMap = new HashMap<>();

        for (Category category : Category.values())
            featuresMap.put(category, new HashSet<>());

        IFeatureNew MethodNameStartWithKeyword = new MethodNameStartsWithKeywordFeature();
        ((WeightedFeature) MethodNameStartWithKeyword).setWeight(5);
        addFeature(MethodNameStartWithKeyword,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.AUTHENTICATION)));

        IFeatureNew MethodNameContainsKeyword = new KeywordsInMethodNameFeature();
        ((WeightedFeature) MethodNameContainsKeyword).setWeight(5);
        addFeature(MethodNameContainsKeyword,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.AUTHENTICATION)));

        IFeatureNew ReturnType = new ReturnTypeOfMethodFeature();
        ((WeightedFeature) ReturnType).setWeight(5);
        addFeature(ReturnType,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.AUTHENTICATION)));

        IFeatureNew ClassNameContainsKeyword = new KeywordsInClassName();
        ((WeightedFeature) ClassNameContainsKeyword).setWeight(5);
        addFeature(ClassNameContainsKeyword,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.AUTHENTICATION)));

        IFeatureNew MethodInvocationName = new MethodsInvokedFeature();
        ((WeightedFeature) MethodInvocationName).setWeight(5);
        addFeature(MethodInvocationName,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.AUTHENTICATION)));

    }

    private void getFeaturesSize() {
        int count = 0;

        HashMap<String, Integer> featuresCount = new HashMap();

        for (Category c : featuresMap.keySet()) {
            int features = featuresMap.get(c).size();
            featuresCount.put(c.toString(), features);
            count += features;
        }
        logger.info("Created {} code features,  distribution of methods={}", count, featuresCount);
    }
}
