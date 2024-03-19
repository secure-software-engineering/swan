package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.*;
import de.fraunhofer.iem.swan.features.code.type.MethodClassModifierFeature.ClassModifier;
import de.fraunhofer.iem.swan.features.code.type.MethodModifierFeature.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Handles the learner's features.
 *
 * @author Lisa Nguyen Quang Do, Goran Piskachev
 */

public class CodeBinaryRelevanceFeatureHandler {

    private Map<Category, Set<IFeature>> featuresMap;
    private static final Logger logger = LoggerFactory.getLogger(CodeBinaryRelevanceFeatureHandler.class);
    public Map<Category, Set<IFeature>> features() {
        return featuresMap;
    }

    public CodeBinaryRelevanceFeatureHandler() {

    }

    public void evaluateCodeFeatureData(Set<Method> methodSet) {
        //TODO refactor code features implementation to be similar to the doc features
    }

    private void addFeature(IFeature feature, Set<Category> categoriesForFeature) {

        for (Category category : categoriesForFeature) {
            Set<IFeature> typeFeatures = featuresMap.get(category);
            typeFeatures.add(feature);
            featuresMap.put(category, typeFeatures);
        }
    }

    /**
     * Initializes the set of features for classifying methods in the categories.
     */
    public void initializeFeatures() {

        featuresMap = new HashMap<>();

        for (Category category : Category.values())
            featuresMap.put(category, new HashSet<>());

        // Implicit method.
        IFeature isImplicitMethod = new IsImplicitMethod();
        ((WeightedFeature) isImplicitMethod).setWeight(5);
        addFeature(isImplicitMethod,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.CWE89, Category.CWE862, Category.CWE863, Category.CWE78, Category.CWE306,
                        Category.CWE79, Category.CWE601, Category.NONE, Category.RELEVANT)));

        // Method in anonymous class.
        IFeature anonymousClass = new MethodAnonymousClassFeature(true);
        ((WeightedFeature) anonymousClass).setWeight(8);
        addFeature(anonymousClass,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.RELEVANT,
                        Category.CWE78, Category.CWE79, Category.CWE89, Category.CWE306, Category.CWE862,
                        Category.CWE863, Category.CWE601)));

        IFeature classNameContainsSaniti = new MethodClassContainsNameFeature("Saniti");
        ((WeightedFeature) classNameContainsSaniti).setWeight(2);
        addFeature(classNameContainsSaniti,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsEncode = new MethodClassContainsNameFeature("Encod");
        ((WeightedFeature) classNameContainsEncode).setWeight(-12);
        addFeature(classNameContainsEncode,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsEscape = new MethodClassContainsNameFeature("Escap");
        ((WeightedFeature) classNameContainsEscape).setWeight(0);
        addFeature(classNameContainsEscape,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsValid = new MethodClassContainsNameFeature("Valid");
        ((WeightedFeature) classNameContainsValid).setWeight(-13);
        addFeature(classNameContainsValid,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsCheck = new MethodClassContainsNameFeature("Check");
        ((WeightedFeature) classNameContainsCheck).setWeight(13);
        addFeature(classNameContainsCheck,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsVerify = new MethodClassContainsNameFeature("Verif");
        ((WeightedFeature) classNameContainsVerify).setWeight(-8);
        addFeature(classNameContainsVerify,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsAuthen = new MethodClassContainsNameFeature("Authen");
        ((WeightedFeature) classNameContainsAuthen).setWeight(9);
        addFeature(classNameContainsAuthen,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsSecurity = new MethodClassContainsNameFeature("Security");
        ((WeightedFeature) classNameContainsSecurity).setWeight(1);
        addFeature(classNameContainsSecurity,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsConnect = new MethodClassContainsNameFeature("Connect");
        ((WeightedFeature) classNameContainsConnect).setWeight(9);
        addFeature(classNameContainsConnect,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH,
                        Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsBind = new MethodClassContainsNameFeature("Bind");
        ((WeightedFeature) classNameContainsBind).setWeight(21);
        addFeature(classNameContainsBind,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsOAuth = new MethodClassContainsNameFeature("OAuth");
        ((WeightedFeature) classNameContainsOAuth).setWeight(5);
        addFeature(classNameContainsOAuth,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.CWE306, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsIO = new MethodClassContainsNameFeature(".io.");
        ((WeightedFeature) classNameContainsIO).setWeight(13);
        addFeature(classNameContainsIO, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsWeb = new MethodClassContainsNameFeature("web");
        ((WeightedFeature) classNameContainsWeb).setWeight(15);
        addFeature(classNameContainsWeb, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.RELEVANT, Category.CWE79,
                        Category.CWE601)));

        IFeature classNameContainsNet = new MethodClassContainsNameFeature(".net.");
        ((WeightedFeature) classNameContainsNet).setWeight(9);
        addFeature(classNameContainsNet, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsSql = new MethodClassContainsNameFeature("sql");
        ((WeightedFeature) classNameContainsSql).setWeight(17);
        addFeature(classNameContainsSql,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsManager = new MethodClassContainsNameFeature("Manager");
        ((WeightedFeature) classNameContainsManager).setWeight(-5);
        addFeature(classNameContainsManager, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsOutput = new MethodClassContainsNameFeature("Output");
        ((WeightedFeature) classNameContainsOutput).setWeight(-8);
        addFeature(classNameContainsOutput,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsInput = new MethodClassContainsNameFeature("Input");
        ((WeightedFeature) classNameContainsInput).setWeight(5);
        addFeature(classNameContainsInput,
                new HashSet<>(Arrays.asList(Category.SINK, Category.CWE79, Category.CWE78, Category.CWE89,
                        Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsDatabase = new MethodClassContainsNameFeature("database");
        ((WeightedFeature) classNameContainsDatabase).setWeight(10);
        addFeature(classNameContainsDatabase,
                new HashSet<>(Arrays.asList(Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsDb = new MethodClassContainsNameFeature("db");
        ((WeightedFeature) classNameContainsDb).setWeight(5);
        addFeature(classNameContainsDb,
                new HashSet<>(Arrays.asList(Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsHibernate = new MethodClassContainsNameFeature("hibernate");
        ((WeightedFeature) classNameContainsHibernate).setWeight(-8);
        addFeature(classNameContainsHibernate,
                new HashSet<>(Arrays.asList(Category.CWE89, Category.CWE79, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsCredential = new MethodClassContainsNameFeature("credential");
        ((WeightedFeature) classNameContainsCredential).setWeight(19);
        addFeature(classNameContainsCredential,
                new HashSet<>(Arrays.asList(Category.CWE78, Category.CWE862,
                        Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsProcess = new MethodClassContainsNameFeature("process");
        ((WeightedFeature) classNameContainsProcess).setWeight(13);
        addFeature(classNameContainsProcess,
                new HashSet<>(Arrays.asList(Category.CWE78, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsRuntime = new MethodClassContainsNameFeature("runtime");
        ((WeightedFeature) classNameContainsRuntime).setWeight(17);
        addFeature(classNameContainsRuntime,
                new HashSet<>(Arrays.asList(Category.CWE78, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsUser = new MethodClassContainsNameFeature("user");
        ((WeightedFeature) classNameContainsUser).setWeight(2);
        addFeature(classNameContainsUser, new HashSet<>(
                Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsJdbc = new MethodClassContainsNameFeature("jdbc");
        ((WeightedFeature) classNameContainsJdbc).setWeight(2);
        addFeature(classNameContainsJdbc,
                new HashSet<>(Arrays.asList(Category.SINK,
                        Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsHtml = new MethodClassContainsNameFeature("Html");
        ((WeightedFeature) classNameContainsHtml).setWeight(1);
        addFeature(classNameContainsHtml,
                new HashSet<>(Arrays.asList(Category.SINK, Category.SOURCE, Category.CWE79, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsPage = new MethodClassContainsNameFeature("Page");
        ((WeightedFeature) classNameContainsPage).setWeight(5);
        addFeature(classNameContainsPage,
                new HashSet<>(Arrays.asList(Category.CWE79, Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsRequest = new MethodClassContainsNameFeature("Request");
        ((WeightedFeature) classNameContainsRequest).setWeight(13);
        addFeature(classNameContainsRequest, new HashSet<>(
                Arrays.asList(Category.CWE601, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsHttp = new MethodClassContainsNameFeature("http");
        ((WeightedFeature) classNameContainsHttp).setWeight(-8);
        addFeature(classNameContainsHttp, new HashSet<>(
                Arrays.asList(Category.CWE601, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsUrl = new MethodClassContainsNameFeature("url");
        ((WeightedFeature) classNameContainsUrl).setWeight(13);
        addFeature(classNameContainsUrl,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsServlet = new MethodClassContainsNameFeature("servlet");
        ((WeightedFeature) classNameContainsServlet).setWeight(0);
        addFeature(classNameContainsServlet,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsResponse = new MethodClassContainsNameFeature("Response");
        ((WeightedFeature) classNameContainsResponse).setWeight(7);
        addFeature(classNameContainsResponse,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsRedirect = new MethodClassContainsNameFeature("Redirect");
        ((WeightedFeature) classNameContainsRedirect).setWeight(7);
        addFeature(classNameContainsRedirect,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsCss = new MethodClassContainsNameFeature("Css");
        ((WeightedFeature) classNameContainsCss).setWeight(5);
        addFeature(classNameContainsCss,
                new HashSet<>(Arrays.asList(Category.CWE79, Category.NONE, Category.RELEVANT)));

        IFeature classNameContainsDom = new MethodClassContainsNameFeature("Dom");
        ((WeightedFeature) classNameContainsDom).setWeight(-1);
        addFeature(classNameContainsDom,
                new HashSet<>(Arrays.asList(Category.CWE79, Category.NONE, Category.RELEVANT)));

        // Method class ends
        IFeature classEndsWithEncoder = new MethodClassEndsWithNameFeature("Encoder");
        ((WeightedFeature) classEndsWithEncoder).setWeight(18);
        addFeature(classEndsWithEncoder, new HashSet<>(Arrays.asList(
                Category.SANITIZER, Category.CWE78, Category.CWE79, Category.CWE862,
                Category.CWE863, Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature classEndsWithRequest = new MethodClassEndsWithNameFeature("Request");
        ((WeightedFeature) classEndsWithRequest).setWeight(13);
        addFeature(classEndsWithRequest, new HashSet<>(Arrays.asList(
                Category.SINK, Category.CWE79, Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature classEndsWithRender = new MethodClassEndsWithNameFeature("Render");
        ((WeightedFeature) classEndsWithRender).setWeight(-8);
        addFeature(classEndsWithRender, new HashSet<>(Arrays.asList(
                Category.SINK, Category.CWE79, Category.NONE, Category.RELEVANT)));

        // Class modifier.
        IFeature isStaticClass = new MethodClassModifierFeature(ClassModifier.STATIC);
        ((WeightedFeature) isStaticClass).setWeight(17);
        addFeature(isStaticClass, new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.SINK, Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature isPublicClass = new MethodClassModifierFeature(ClassModifier.PUBLIC);
        ((WeightedFeature) isPublicClass).setWeight(-8);
        addFeature(isPublicClass, new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.SINK, Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature isFinalClass = new MethodClassModifierFeature(ClassModifier.FINAL);
        ((WeightedFeature) isFinalClass).setWeight(12);
        addFeature(isFinalClass, new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.SINK, Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        // Has parameters.
        IFeature hasParamsPerm = new MethodHasParametersFeature();
        ((WeightedFeature) hasParamsPerm).setWeight(2);
        addFeature(hasParamsPerm,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SINK, Category.CWE79,
                        Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.CWE601, Category.CWE89, Category.NONE, Category.RELEVANT)));

        // Has a return type.
        IFeature hasReturnType = new MethodHasReturnTypeFeature();
        ((WeightedFeature) hasReturnType).setWeight(-11);
        addFeature(hasReturnType,
                new HashSet<>(Arrays.asList(Category.SOURCE,
                        Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.CWE601, Category.NONE, Category.RELEVANT)));

        IFeature innerClassMethod = new MethodInnerClassFeature(true);
        ((WeightedFeature) innerClassMethod).setWeight(0);
        addFeature(innerClassMethod,
                new HashSet<>(Arrays.asList(
                        Category.SINK, Category.SOURCE, Category.NONE, Category.RELEVANT, Category.CWE78, Category.CWE79, Category.CWE89, Category.CWE306, Category.CWE862, Category.CWE863, Category.CWE601)));

        // Call to a method of class.
        IFeature methodInvocationClassNameSaniti = new MethodInvocationClassName("Saniti");
        ((WeightedFeature) methodInvocationClassNameSaniti).setWeight(13);
        addFeature(methodInvocationClassNameSaniti,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationClassNameRegex = new MethodInvocationClassName("regex");
        ((WeightedFeature) methodInvocationClassNameRegex).setWeight(0);
        addFeature(methodInvocationClassNameRegex,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationClassNameEscape = new MethodInvocationClassName("escap");
        ((WeightedFeature) methodInvocationClassNameEscape).setWeight(0);
        addFeature(methodInvocationClassNameEscape,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationClassNameIO = new MethodInvocationClassName(".io.");
        ((WeightedFeature) methodInvocationClassNameIO).setWeight(-3);
        addFeature(methodInvocationClassNameIO,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationClassNameEncode = new MethodInvocationClassName("encod");
        ((WeightedFeature) methodInvocationClassNameEncode).setWeight(-1);
        addFeature(methodInvocationClassNameEncode,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationClassNameSQL = new MethodInvocationClassName("sql");
        ((WeightedFeature) methodInvocationClassNameSQL).setWeight(13);
        addFeature(methodInvocationClassNameSQL,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationClassNameDB = new MethodInvocationClassName("db");
        addFeature(methodInvocationClassNameDB,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.NONE, Category.RELEVANT)));
        ((WeightedFeature) methodInvocationClassNameDB).setWeight(5);

        IFeature methodInvocationClassNameWeb = new MethodInvocationClassName("web");
        ((WeightedFeature) methodInvocationClassNameWeb).setWeight(29);
        addFeature(methodInvocationClassNameWeb, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.CWE79, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationClassNameNet = new MethodInvocationClassName(".net.");
        ((WeightedFeature) methodInvocationClassNameNet).setWeight(19);
        addFeature(methodInvocationClassNameNet, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationClassNameLog = new MethodInvocationClassName("Log.");
        ((WeightedFeature) methodInvocationClassNameLog).setWeight(19);
        addFeature(methodInvocationClassNameLog,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        // Call to a method.
        IFeature methodInvocationNameEscape = new MethodInvocationName("escap");
        ((WeightedFeature) methodInvocationNameEscape).setWeight(17);
        addFeature(methodInvocationNameEscape,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameReplace = new MethodInvocationName("replac");
        ((WeightedFeature) methodInvocationNameReplace).setWeight(10);
        addFeature(methodInvocationNameReplace, new HashSet<>(
                Arrays.asList(Category.SINK, Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameStrip = new MethodInvocationName("strip");
        ((WeightedFeature) methodInvocationNameStrip).setWeight(18);
        addFeature(methodInvocationNameStrip,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameMatches = new MethodInvocationName("match");
        ((WeightedFeature) methodInvocationNameMatches).setWeight(-8);
        addFeature(methodInvocationNameMatches,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameEncode = new MethodInvocationName("encod");
        ((WeightedFeature) methodInvocationNameEncode).setWeight(20);
        addFeature(methodInvocationNameEncode,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameRegex = new MethodInvocationName("regex");
        ((WeightedFeature) methodInvocationNameRegex).setWeight(-8);
        addFeature(methodInvocationNameRegex,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameCheck = new MethodInvocationName("check");
        ((WeightedFeature) methodInvocationNameCheck).setWeight(13);
        addFeature(methodInvocationNameCheck,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameVerif = new MethodInvocationName("verif");
        ((WeightedFeature) methodInvocationNameVerif).setWeight(5);
        addFeature(methodInvocationNameVerif,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameAuthori = new MethodInvocationName("authori");
        ((WeightedFeature) methodInvocationNameAuthori).setWeight(13);
        addFeature(methodInvocationNameAuthori,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameAuthen = new MethodInvocationName("authen");
        ((WeightedFeature) methodInvocationNameAuthen).setWeight(29);
        addFeature(methodInvocationNameAuthen,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameLogin = new MethodInvocationName("login");
        ((WeightedFeature) methodInvocationNameLogin).setWeight(10);
        addFeature(methodInvocationNameLogin,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH,
                        Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameLogout = new MethodInvocationName("logout");
        ((WeightedFeature) methodInvocationNameLogout).setWeight(-12);
        addFeature(methodInvocationNameLogout,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_LOW,
                        Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameSecurity = new MethodInvocationName("security");
        ((WeightedFeature) methodInvocationNameSecurity).setWeight(-8);
        addFeature(methodInvocationNameSecurity,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameCredential = new MethodInvocationName("credential");
        ((WeightedFeature) methodInvocationNameCredential).setWeight(0);
        addFeature(methodInvocationNameCredential,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameBind = new MethodInvocationName("bind");
        ((WeightedFeature) methodInvocationNameBind).setWeight(5);
        addFeature(methodInvocationNameBind,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameConnect = new MethodInvocationName("connect");
        ((WeightedFeature) methodInvocationNameConnect).setWeight(-8);
        addFeature(methodInvocationNameConnect,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameGet = new MethodInvocationName("get");
        ((WeightedFeature) methodInvocationNameGet).setWeight(6);
        addFeature(methodInvocationNameGet,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameRead = new MethodInvocationName("read");
        ((WeightedFeature) methodInvocationNameRead).setWeight(-3);
        addFeature(methodInvocationNameRead,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameDecode = new MethodInvocationName("decod");
        ((WeightedFeature) methodInvocationNameDecode).setWeight(-1);
        addFeature(methodInvocationNameDecode,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameUnescape = new MethodInvocationName("unescap");
        ((WeightedFeature) methodInvocationNameUnescape).setWeight(17);
        addFeature(methodInvocationNameUnescape,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameLoad = new MethodInvocationName("load");
        ((WeightedFeature) methodInvocationNameLoad).setWeight(9);
        addFeature(methodInvocationNameLoad,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameRequest = new MethodInvocationName("request");
        ((WeightedFeature) methodInvocationNameRequest).setWeight(14);
        addFeature(methodInvocationNameRequest,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameCreate = new MethodInvocationName("creat");
        ((WeightedFeature) methodInvocationNameCreate).setWeight(13);
        addFeature(methodInvocationNameCreate,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameOutput = new MethodInvocationName("output");
        ((WeightedFeature) methodInvocationNameOutput).setWeight(0);
        addFeature(methodInvocationNameOutput,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameWrit = new MethodInvocationName("writ");
        ((WeightedFeature) methodInvocationNameWrit).setWeight(13);
        addFeature(methodInvocationNameWrit,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameSet = new MethodInvocationName("set");
        ((WeightedFeature) methodInvocationNameSet).setWeight(8);
        addFeature(methodInvocationNameSet,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameUpdat = new MethodInvocationName("updat");
        ((WeightedFeature) methodInvocationNameUpdat).setWeight(-8);
        addFeature(methodInvocationNameUpdat,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameSend = new MethodInvocationName("send");
        ((WeightedFeature) methodInvocationNameSend).setWeight(13);
        addFeature(methodInvocationNameSend,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameHandl = new MethodInvocationName("handl");
        ((WeightedFeature) methodInvocationNameHandl).setWeight(-7);
        addFeature(methodInvocationNameHandl,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNamePut = new MethodInvocationName("put");
        ((WeightedFeature) methodInvocationNamePut).setWeight(-11);
        addFeature(methodInvocationNamePut,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameAdd = new MethodInvocationName("log");
        ((WeightedFeature) methodInvocationNameAdd).setWeight(4);
        addFeature(methodInvocationNameAdd,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameRun = new MethodInvocationName("run");
        ((WeightedFeature) methodInvocationNameRun).setWeight(0);
        addFeature(methodInvocationNameRun,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameExecut = new MethodInvocationName("execut");
        ((WeightedFeature) methodInvocationNameExecut).setWeight(17);
        addFeature(methodInvocationNameExecut,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameDump = new MethodInvocationName("dump");
        ((WeightedFeature) methodInvocationNameDump).setWeight(1);
        addFeature(methodInvocationNameDump,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNamePrint = new MethodInvocationName("print");
        ((WeightedFeature) methodInvocationNamePrint).setWeight(10);
        addFeature(methodInvocationNamePrint,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNamePars = new MethodInvocationName("pars");
        ((WeightedFeature) methodInvocationNamePars).setWeight(12);
        addFeature(methodInvocationNamePars,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameMakedb = new MethodInvocationName("makedb");
        ((WeightedFeature) methodInvocationNameMakedb).setWeight(-8);
        addFeature(methodInvocationNameMakedb,
                new HashSet<>(Arrays.asList(Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameExecute = new MethodInvocationName("execute");
        ((WeightedFeature) methodInvocationNameExecute).setWeight(12);
        addFeature(methodInvocationNameExecute,
                new HashSet<>(Arrays.asList(Category.CWE78, Category.NONE, Category.RELEVANT)));

        IFeature methodInvocationNameSaniti = new MethodInvocationName("saniti");
        ((WeightedFeature) methodInvocationNameSaniti).setWeight(-10);
        addFeature(methodInvocationNameSaniti,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        // Constructor.
        IFeature methodIsConstructor = new MethodIsConstructor();
        ((WeightedFeature) methodIsConstructor).setWeight(0);
        addFeature(methodIsConstructor,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_NEUTRAL, Category.CWE78, Category.CWE862,
                        Category.CWE863, Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature realSetter = new MethodIsRealSetterFeature();
        ((WeightedFeature) realSetter).setWeight(0);
        addFeature(realSetter,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        // Method modifier.
        IFeature isStaticMethod = new de.fraunhofer.iem.swan.features.code.type.MethodModifierFeature(Modifier.STATIC);
        ((WeightedFeature) isStaticMethod).setWeight(3);
        addFeature(isStaticMethod,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature isPublicMethod = new de.fraunhofer.iem.swan.features.code.type.MethodModifierFeature(Modifier.PUBLIC);
        ((WeightedFeature) isPublicMethod).setWeight(10);
        addFeature(isPublicMethod,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature isPrivateMethod = new de.fraunhofer.iem.swan.features.code.type.MethodModifierFeature(Modifier.PRIVATE);
        ((WeightedFeature) isPrivateMethod).setWeight(10);
        addFeature(isPrivateMethod, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature isFinalMethod = new de.fraunhofer.iem.swan.features.code.type.MethodModifierFeature(Modifier.FINAL);
        ((WeightedFeature) isFinalMethod).setWeight(4);
        addFeature(isFinalMethod, new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.SINK, Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        // Method name.
        IFeature methodNameStartsWithGet = new MethodNameStartsWithFeature("get");
        ((WeightedFeature) methodNameStartsWithGet).setWeight(-3);
        addFeature(methodNameStartsWithGet,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SANITIZER,
                        Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.NONE, Category.RELEVANT)));

        IFeature methodNameStartsWithSet = new MethodNameStartsWithFeature("set");
        ((WeightedFeature) methodNameStartsWithSet).setWeight(15);
        addFeature(methodNameStartsWithSet,
                new HashSet<>(Arrays.asList(Category.SINK, Category.SANITIZER,
                        Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.CWE79, Category.NONE, Category.RELEVANT)));

        IFeature methodNameStartsWithPut = new MethodNameStartsWithFeature("put");
        ((WeightedFeature) methodNameStartsWithPut).setWeight(12);
        addFeature(methodNameStartsWithPut,
                new HashSet<>(Arrays.asList(Category.SANITIZER,
                        Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.NONE, Category.RELEVANT)));

        IFeature methodNameStartsWithHas = new MethodNameStartsWithFeature("has");
        ((WeightedFeature) methodNameStartsWithHas).setWeight(-8);
        addFeature(methodNameStartsWithHas,
                new HashSet<>(Arrays.asList(Category.SANITIZER,
                        Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                        Category.AUTHENTICATION_TO_LOW, Category.NONE, Category.RELEVANT)));

        IFeature methodNameStartsWithIs = new MethodNameStartsWithFeature("is");
        ((WeightedFeature) methodNameStartsWithIs).setWeight(21);
        addFeature(methodNameStartsWithIs,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodNameStartsOpen = new MethodNameStartsWithFeature("open");
        ((WeightedFeature) methodNameStartsOpen).setWeight(2);
        addFeature(methodNameStartsOpen,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodNameStartsClose = new MethodNameStartsWithFeature("close");
        ((WeightedFeature) methodNameStartsClose).setWeight(0);
        addFeature(methodNameStartsClose,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodNameStartsCreate = new MethodNameStartsWithFeature("create");
        ((WeightedFeature) methodNameStartsCreate).setWeight(-4);
        addFeature(methodNameStartsCreate,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodNameStartsDelete = new MethodNameStartsWithFeature("delete");
        ((WeightedFeature) methodNameStartsDelete).setWeight(22);
        addFeature(methodNameStartsDelete,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        // Method Name Equals.
        IFeature nameEqualsLog = new MethodNameEqualsFeature("log");
        ((WeightedFeature) nameEqualsLog).setWeight(-8);
        addFeature(nameEqualsLog,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature nameEqualsSetHeader = new MethodNameEqualsFeature("setHeader");
        ((WeightedFeature) nameEqualsSetHeader).setWeight(9);
        addFeature(nameEqualsSetHeader,
                new HashSet<>(Arrays.asList(Category.SINK, Category.CWE79, Category.NONE, Category.RELEVANT)));

        IFeature nameEqualsSendRedirect = new MethodNameEqualsFeature("sendRedirect");
        ((WeightedFeature) nameEqualsSendRedirect).setWeight(-8);
        addFeature(nameEqualsSendRedirect,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE, Category.RELEVANT)));

        // Method Contains Name.
        IFeature methodNameContainsSaniti = new MethodNameContainsFeature("saniti");
        ((WeightedFeature) methodNameContainsSaniti).setWeight(-3);
        addFeature(methodNameContainsSaniti,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.CWE78, Category.CWE79, Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsEscape = new MethodNameContainsFeature("escape", "unescape");
        ((WeightedFeature) methodNameContainsEscape).setWeight(51);
        addFeature(methodNameContainsEscape,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsUnescape = new MethodNameContainsFeature("unescape");
        ((WeightedFeature) methodNameContainsUnescape).setWeight(-3);
        addFeature(methodNameContainsUnescape,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsReplace = new MethodNameContainsFeature("replac");
        ((WeightedFeature) methodNameContainsReplace).setWeight(-3);
        addFeature(methodNameContainsReplace, new HashSet<>(
                Arrays.asList(Category.SINK, Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsStrip = new MethodNameContainsFeature("strip");
        ((WeightedFeature) methodNameContainsStrip).setWeight(65);
        addFeature(methodNameContainsStrip,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsEncode = new MethodNameContainsFeature("encod", "encoding");
        ((WeightedFeature) methodNameContainsEncode).setWeight(21);
        addFeature(methodNameContainsEncode,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsRegex = new MethodNameContainsFeature("regex");
        ((WeightedFeature) methodNameContainsRegex).setWeight(25);
        addFeature(methodNameContainsRegex,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsAuthen = new MethodNameContainsFeature("authen");
        ((WeightedFeature) methodNameContainsAuthen).setWeight(19);
        addFeature(methodNameContainsAuthen,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.CWE306, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsCheck = new MethodNameContainsFeature("check");
        ((WeightedFeature) methodNameContainsCheck).setWeight(0);
        addFeature(methodNameContainsCheck,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsVerif = new MethodNameContainsFeature("verif");
        ((WeightedFeature) methodNameContainsVerif).setWeight(-8);
        addFeature(methodNameContainsVerif,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT, Category.SANITIZER)));

        IFeature methodNameContainsPrivilege = new MethodNameContainsFeature(
                "privilege");
        ((WeightedFeature) methodNameContainsPrivilege).setWeight(9);
        addFeature(methodNameContainsPrivilege,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsLogin = new MethodNameContainsFeature("login");
        ((WeightedFeature) methodNameContainsLogin).setWeight(9);
        addFeature(methodNameContainsLogin,
                new HashSet<>(Arrays.asList(
                        Category.AUTHENTICATION_TO_HIGH,
                        Category.CWE306, Category.NONE, Category.RELEVANT)));


        IFeature methodNameContainsNotLoginPage = new MethodNameContainsFeature("", "loginpage");
        ((WeightedFeature) methodNameContainsNotLoginPage).setWeight(43);
        addFeature(methodNameContainsNotLoginPage,
                new HashSet<>(Arrays.asList(Category.CWE306, Category.CWE862,
                        Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsLogout = new MethodNameContainsFeature("logout");
        ((WeightedFeature) methodNameContainsLogout).setWeight(-8);
        addFeature(methodNameContainsLogout,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_LOW,
                        Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsConnect = new MethodNameContainsFeature("connect", "disconnect");
        ((WeightedFeature) methodNameContainsConnect).setWeight(31);
        addFeature(methodNameContainsConnect,
                new HashSet<>(Arrays.asList(
                        Category.AUTHENTICATION_TO_HIGH,
                        Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsDisconnect = new MethodNameContainsFeature("disconnect");
        ((WeightedFeature) methodNameContainsDisconnect).setWeight(31);
        addFeature(methodNameContainsDisconnect,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_LOW,
                        Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsBind = new MethodNameContainsFeature("bind", "unbind");
        ((WeightedFeature) methodNameContainsBind).setWeight(18);
        addFeature(methodNameContainsBind,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH,
                        Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsUnbind = new MethodNameContainsFeature("unbind");
        ((WeightedFeature) methodNameContainsUnbind).setWeight(4);
        addFeature(methodNameContainsUnbind,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature nameContaisRead = new MethodNameContainsFeature("read", "thread");
        ((WeightedFeature) nameContaisRead).setWeight(-8);
        addFeature(nameContaisRead,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.CWE601, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsLoad = new MethodNameContainsFeature("load", "payload");
        ((WeightedFeature) nameContainsLoad).setWeight(18);
        addFeature(nameContainsLoad,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsRequest = new MethodNameContainsFeature("request");
        ((WeightedFeature) nameContainsRequest).setWeight(9);
        addFeature(nameContainsRequest,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsCreate = new MethodNameContainsFeature("creat");
        ((WeightedFeature) nameContainsCreate).setWeight(6);
        addFeature(nameContainsCreate,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsDecod = new MethodNameContainsFeature("decod");
        ((WeightedFeature) nameContainsDecod).setWeight(15);
        addFeature(nameContainsDecod,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsUnescap = new MethodNameContainsFeature("unescap");
        ((WeightedFeature) nameContainsUnescap).setWeight(-3);
        addFeature(nameContainsUnescap,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsPars = new MethodNameContainsFeature("pars");
        ((WeightedFeature) nameContainsPars).setWeight(36);
        addFeature(nameContainsPars, new HashSet<>(
                Arrays.asList(Category.SINK, Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsStream = new MethodNameContainsFeature("stream");
        ((WeightedFeature) nameContainsStream).setWeight(17);
        addFeature(nameContainsStream, new HashSet<>(
                Arrays.asList(Category.SINK, Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsRetriev = new MethodNameContainsFeature("retriev");
        ((WeightedFeature) nameContainsRetriev).setWeight(-8);
        addFeature(nameContainsRetriev,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsObject = new MethodNameContainsFeature("Object");
        ((WeightedFeature) nameContainsObject).setWeight(-8);
        addFeature(nameContainsObject,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsName = new MethodNameContainsFeature("Name");
        ((WeightedFeature) nameContainsName).setWeight(1);
        addFeature(nameContainsName,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsWrit = new MethodNameContainsFeature("writ");
        ((WeightedFeature) nameContainsWrit).setWeight(0);
        addFeature(nameContainsWrit,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsUpdat = new MethodNameContainsFeature("updat");
        ((WeightedFeature) nameContainsUpdat).setWeight(1);
        addFeature(nameContainsUpdat,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsSend = new MethodNameContainsFeature("send");
        ((WeightedFeature) nameContainsSend).setWeight(-8);
        addFeature(nameContainsSend,
                new HashSet<>(Arrays.asList(Category.SINK, Category.CWE601, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsHandl = new MethodNameContainsFeature("handl");
        ((WeightedFeature) nameContainsHandl).setWeight(16);
        addFeature(nameContainsHandl,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsLog = new MethodNameContainsFeature("log");
        ((WeightedFeature) nameContainsLog).setWeight(-5);
        addFeature(nameContainsLog,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsRun = new MethodNameContainsFeature("run");
        ((WeightedFeature) nameContainsRun).setWeight(0);
        addFeature(nameContainsRun, new HashSet<>(
                Arrays.asList(Category.SINK, Category.CWE78, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsExecut = new MethodNameContainsFeature("execut");
        ((WeightedFeature) nameContainsExecut).setWeight(-8);
        addFeature(nameContainsExecut,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsExec = new MethodNameContainsFeature("exec");
        ((WeightedFeature) nameContainsExec).setWeight(34);
        addFeature(nameContainsExec,
                new HashSet<>(Arrays.asList(Category.CWE78, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsCompile = new MethodNameContainsFeature("compile");
        ((WeightedFeature) nameContainsCompile).setWeight(17);
        addFeature(nameContainsCompile,
                new HashSet<>(Arrays.asList(Category.CWE78, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsDump = new MethodNameContainsFeature("dump");
        ((WeightedFeature) nameContainsDump).setWeight(9);
        addFeature(nameContainsDump,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsPrint = new MethodNameContainsFeature("print");
        ((WeightedFeature) nameContainsPrint).setWeight(9);
        addFeature(nameContainsPrint,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsExecute = new MethodNameContainsFeature("execute");
        ((WeightedFeature) nameContainsExecute).setWeight(8);
        addFeature(nameContainsExecute,
                new HashSet<>(Arrays.asList(Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsQuery = new MethodNameContainsFeature("query");
        ((WeightedFeature) nameContainsQuery).setWeight(17);
        addFeature(nameContainsQuery,
                new HashSet<>(Arrays.asList(Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsRole = new MethodNameContainsFeature("role");
        ((WeightedFeature) nameContainsRole).setWeight(-8);
        addFeature(nameContainsRole, new HashSet<>(
                Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsAuthori = new MethodNameContainsFeature("authori");
        ((WeightedFeature) nameContainsAuthori).setWeight(0);
        addFeature(nameContainsAuthori, new HashSet<>(
                Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature nameContainsRedirect = new MethodNameContainsFeature("redirect");
        ((WeightedFeature) nameContainsRedirect).setWeight(17);
        addFeature(nameContainsRedirect,
                new HashSet<>(Arrays.asList(Category.SINK, Category.CWE601, Category.NONE, Category.RELEVANT)));

        IFeature methodNameContainsGetParameter = new MethodNameContainsFeature("getParameter");
        ((WeightedFeature) methodNameContainsGetParameter).setWeight(-8);
        addFeature(methodNameContainsGetParameter,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE, Category.RELEVANT)));

        IFeature returnsConstant = new MethodReturnsConstantFeature();
        ((WeightedFeature) returnsConstant).setWeight(-4);
        addFeature(returnsConstant,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        // Parameter types (ParameterContainsTypeOrNameFeature).
        IFeature parameterOfTypeString = new ParameterContainsTypeOrNameFeature("java.lang.String");
        ((WeightedFeature) parameterOfTypeString).setWeight(32);
        addFeature(parameterOfTypeString,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.CWE89, Category.CWE306,
                        Category.CWE78, Category.CWE862, Category.CWE863, Category.CWE79, Category.NONE, Category.RELEVANT)));

        IFeature parameterOfTypeCharArray = new ParameterContainsTypeOrNameFeature("char[]");
        ((WeightedFeature) parameterOfTypeCharArray).setWeight(12);
        addFeature(parameterOfTypeCharArray,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.NONE, Category.RELEVANT)));

        IFeature parameterOfTypeByteArray = new ParameterContainsTypeOrNameFeature("byte[]");
        ((WeightedFeature) parameterOfTypeByteArray).setWeight(9);
        addFeature(parameterOfTypeByteArray,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        IFeature parameterOfTypeCharSequence = new ParameterContainsTypeOrNameFeature("java.lang.CharSequence");
        ((WeightedFeature) parameterOfTypeCharSequence).setWeight(13);
        addFeature(parameterOfTypeCharSequence,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                        Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.NONE, Category.RELEVANT)));

        IFeature parameterOfTypeStringBuilder = new ParameterContainsTypeOrNameFeature("java.lang.StringBuilder");
        ((WeightedFeature) parameterOfTypeStringBuilder).setWeight(-8);
        addFeature(parameterOfTypeStringBuilder, new HashSet<>(
                Arrays.asList(Category.SANITIZER, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature parameterOfTypeIo = new ParameterContainsTypeOrNameFeature(".io.");
        ((WeightedFeature) parameterOfTypeIo).setWeight(-3);
        addFeature(parameterOfTypeIo, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature parameterOfTypeWeb = new ParameterContainsTypeOrNameFeature("web");
        ((WeightedFeature) parameterOfTypeWeb).setWeight(22);
        addFeature(parameterOfTypeWeb, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature parameterOfTypeSql = new ParameterContainsTypeOrNameFeature("sql");
        ((WeightedFeature) parameterOfTypeSql).setWeight(2);
        addFeature(parameterOfTypeSql, new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.SINK, Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature parameterOfTypeDb = new ParameterContainsTypeOrNameFeature("db");
        ((WeightedFeature) parameterOfTypeDb).setWeight(12);
        addFeature(parameterOfTypeDb, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature parameterOfTypeCredential = new ParameterContainsTypeOrNameFeature("credential");
        ((WeightedFeature) parameterOfTypeCredential).setWeight(137);
        addFeature(parameterOfTypeCredential,
                new HashSet<>(Arrays.asList(Category.CWE306, Category.CWE862,
                        Category.CWE863, Category.CWE78, Category.NONE, Category.RELEVANT)));

        IFeature parameterOfTypeUrl = new ParameterContainsTypeOrNameFeature("url");
        ((WeightedFeature) parameterOfTypeUrl).setWeight(21);
        addFeature(parameterOfTypeUrl, new HashSet<>(
                Arrays.asList(Category.CWE601, Category.NONE, Category.RELEVANT)));

        // Parameter flows to return value.
        IFeature parameterFlowsToReturn = new ParameterFlowsToReturn();
        ((WeightedFeature) parameterFlowsToReturn).setWeight(-7);
        addFeature(parameterFlowsToReturn,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE, Category.RELEVANT)));

        // Parameter to sink.
        IFeature paramToSinkSetWrit = new ParameterToSinkFeature("writ");
        ((WeightedFeature) paramToSinkSetWrit).setWeight(-4);
        addFeature(paramToSinkSetWrit,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkSet = new ParameterToSinkFeature("set");
        ((WeightedFeature) paramToSinkSet).setWeight(8);
        addFeature(paramToSinkSet,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkUpdat = new ParameterToSinkFeature("updat");
        ((WeightedFeature) paramToSinkUpdat).setWeight(9);
        addFeature(paramToSinkUpdat,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkSend = new ParameterToSinkFeature("send");
        ((WeightedFeature) paramToSinkSend).setWeight(0);
        addFeature(paramToSinkSend,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkSetHandl = new ParameterToSinkFeature("handl");
        ((WeightedFeature) paramToSinkSetHandl).setWeight(5);
        addFeature(paramToSinkSetHandl,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkSetPut = new ParameterToSinkFeature("put");
        ((WeightedFeature) paramToSinkSetPut).setWeight(5);
        addFeature(paramToSinkSetPut,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkSetAdd = new ParameterToSinkFeature("log");
        ((WeightedFeature) paramToSinkSetAdd).setWeight(-11);
        addFeature(paramToSinkSetAdd,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkSetRun = new ParameterToSinkFeature("run");
        ((WeightedFeature) paramToSinkSetRun).setWeight(5);
        addFeature(paramToSinkSetRun,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkSetExecut = new ParameterToSinkFeature("execut");
        ((WeightedFeature) paramToSinkSetExecut).setWeight(-8);
        addFeature(paramToSinkSetExecut,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkDump = new ParameterToSinkFeature("dump");
        ((WeightedFeature) paramToSinkDump).setWeight(0);
        addFeature(paramToSinkDump,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkPrint = new ParameterToSinkFeature("print");
        ((WeightedFeature) paramToSinkPrint).setWeight(9);
        addFeature(paramToSinkPrint,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkPars = new ParameterToSinkFeature("pars");
        ((WeightedFeature) paramToSinkPars).setWeight(5);
        addFeature(paramToSinkPars,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        IFeature paramToSinkStream = new ParameterToSinkFeature("stream");
        ((WeightedFeature) paramToSinkStream).setWeight(5);
        addFeature(paramToSinkStream,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT)));

        // Parameter Type matches return
        IFeature paramTypeMathcesReturn = new ParamTypeMatchesReturnType();
        ((WeightedFeature) paramTypeMathcesReturn).setWeight(-5);
        addFeature(paramTypeMathcesReturn,
                new HashSet<>(Arrays.asList(
                        Category.SANITIZER, Category.CWE78, Category.CWE79, Category.CWE862,
                        Category.CWE863, Category.CWE89, Category.NONE, Category.RELEVANT)));

        // ReturnTypeContainsNameFeature
        IFeature returnContainsDocument = new ReturnTypeContainsNameFeature("Document");
        ((WeightedFeature) returnContainsDocument).setWeight(5);
        addFeature(returnContainsDocument,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.CWE79, Category.NONE, Category.RELEVANT)));

        IFeature returnContainsNode = new ReturnTypeContainsNameFeature("Node");
        ((WeightedFeature) returnContainsNode).setWeight(11);
        addFeature(returnContainsNode,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.CWE79, Category.NONE, Category.RELEVANT)));

        IFeature returnContainsUser = new ReturnTypeContainsNameFeature("User");
        ((WeightedFeature) returnContainsUser).setWeight(-17);
        addFeature(returnContainsUser,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH, Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature returnContainsCredential = new ReturnTypeContainsNameFeature("Credential");
        ((WeightedFeature) returnContainsCredential).setWeight(14);
        addFeature(returnContainsCredential,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH, Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE, Category.RELEVANT)));

        IFeature returnContainsServlet = new ReturnTypeContainsNameFeature("Servlet");
        ((WeightedFeature) returnContainsServlet).setWeight(-11);
        addFeature(returnContainsServlet,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.CWE79, Category.CWE78, Category.NONE, Category.RELEVANT)));

        IFeature returnContainsRequest = new ReturnTypeContainsNameFeature("Request");
        ((WeightedFeature) returnContainsRequest).setWeight(-8);
        addFeature(returnContainsRequest,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.CWE79, Category.NONE, Category.RELEVANT)));

        // Return types (ReturnTypeFeature).
        IFeature byteArrayReturnType = new de.fraunhofer.iem.swan.features.code.type.ReturnTypeFeature("byte[]");
        ((WeightedFeature) byteArrayReturnType).setWeight(-4);
        addFeature(byteArrayReturnType,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature stringReturnType = new de.fraunhofer.iem.swan.features.code.type.ReturnTypeFeature("java.lang.String");
        ((WeightedFeature) stringReturnType).setWeight(27);
        addFeature(stringReturnType,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SOURCE, Category.CWE79, Category.CWE78, Category.CWE89, Category.NONE, Category.RELEVANT)));

        IFeature charSequenceReturnType = new de.fraunhofer.iem.swan.features.code.type.ReturnTypeFeature("java.lang.CharSequence");
        ((WeightedFeature) charSequenceReturnType).setWeight(-8);
        addFeature(charSequenceReturnType,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature booleanReturnType = new de.fraunhofer.iem.swan.features.code.type.ReturnTypeFeature("boolean");
        ((WeightedFeature) booleanReturnType).setWeight(-8);
        addFeature(booleanReturnType,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                        Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                        Category.NONE, Category.RELEVANT)));

        IFeature resultsetReturnType = new de.fraunhofer.iem.swan.features.code.type.ReturnTypeFeature("java.sql.ResultSet");
        ((WeightedFeature) resultsetReturnType).setWeight(21);
        addFeature(resultsetReturnType,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.RELEVANT, Category.CWE89)));

        // Source to return.
        IFeature sourceGetToReturn = new SourceToReturnFeature("get");
        ((WeightedFeature) sourceGetToReturn).setWeight(0);
        addFeature(sourceGetToReturn,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature sourceReadToReturn = new SourceToReturnFeature("read");
        ((WeightedFeature) sourceReadToReturn).setWeight(13);
        addFeature(sourceReadToReturn,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature sourcDecodeToReturn = new SourceToReturnFeature("decode");
        ((WeightedFeature) sourcDecodeToReturn).setWeight(21);
        addFeature(sourcDecodeToReturn,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature sourceUnescapeToReturn = new SourceToReturnFeature("unescape");
        ((WeightedFeature) sourceUnescapeToReturn).setWeight(-8);
        addFeature(sourceUnescapeToReturn,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature sourceLoadToReturn = new SourceToReturnFeature("load");
        ((WeightedFeature) sourceLoadToReturn).setWeight(13);
        addFeature(sourceLoadToReturn,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature sourceRequestToReturn = new SourceToReturnFeature("request");
        ((WeightedFeature) sourceRequestToReturn).setWeight(8);
        addFeature(sourceRequestToReturn,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature sourceCreateToReturn = new SourceToReturnFeature("create");
        ((WeightedFeature) sourceCreateToReturn).setWeight(-8);
        addFeature(sourceCreateToReturn,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE, Category.RELEVANT)));

        IFeature voidOn = new VoidOnMethodFeature();
        ((WeightedFeature) voidOn).setWeight(5);
        addFeature(voidOn,
                new HashSet<>(Arrays.asList(Category.SINK, Category.CWE79, Category.NONE, Category.RELEVANT)));

        getFeaturesSize();
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
