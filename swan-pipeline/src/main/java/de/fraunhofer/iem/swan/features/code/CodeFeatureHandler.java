package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CodeFeatureHandler {
    private Map<Category, Set<IFeatureNew>> featuresMap;

    private HashSet<Category> allCategories;
    private static final Logger logger = LoggerFactory.getLogger(CodeFeatureHandler3.class);
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

        IFeatureNew MethodAccessModifier = new MethodAccessModifierFeature();
        ((WeightedFeature) MethodAccessModifier).setWeight(5);
        addFeature(MethodAccessModifier,allCategories);

        IFeatureNew ClassAccessModifier = new ClassAccessModifierFeature();
        ((WeightedFeature) ClassAccessModifier).setWeight(5);
        addFeature(ClassAccessModifier,allCategories);

        IFeatureNew ClassModifier = new ClassModifierFeature();
        ((WeightedFeature) ClassModifier).setWeight(5);
        addFeature(ClassModifier,allCategories);

        IFeatureNew MethodModifier = new MethodModifierFeature();
        ((WeightedFeature) MethodModifier).setWeight(5);
        addFeature(MethodModifier,allCategories);

        IFeatureNew MethodReturnType = new MethodReturnTypeFeature();
        ((WeightedFeature) MethodReturnType).setWeight(5);
        addFeature(MethodReturnType,allCategories);

        IFeatureNew MethodNameStartWithKeyword = new MethodNameStartsWithKeywordFeature();
        ((WeightedFeature) MethodNameStartWithKeyword).setWeight(5);
        addFeature(MethodNameStartWithKeyword, allCategories);

        IFeatureNew MethodNameKeywordsCount = new MethodNameKeywordsCountFeature();
        ((WeightedFeature) MethodNameKeywordsCount).setWeight(5);
        addFeature(MethodNameKeywordsCount,allCategories);

        IFeatureNew ClassNameKeywordsCount = new ClassNameKeywordsCountFeature();
        ((WeightedFeature) ClassNameKeywordsCount).setWeight(5);
        addFeature(ClassNameKeywordsCount,allCategories);

        IFeatureNew MethodsInvokedCount = new MethodsInvokedCountFeature();
        ((WeightedFeature) MethodsInvokedCount).setWeight(5);
        addFeature(MethodsInvokedCount,allCategories);

        IFeatureNew ParametersCount = new ParametersCountFeature();
        ((WeightedFeature) ParametersCount).setWeight(5);
        addFeature(ParametersCount,allCategories);

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
