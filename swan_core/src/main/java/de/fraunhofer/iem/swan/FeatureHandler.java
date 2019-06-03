package de.fraunhofer.iem.swan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.IsImplicitMethod;
import de.fraunhofer.iem.swan.features.MethodAnonymousClassFeature;
import de.fraunhofer.iem.swan.features.MethodClassContainsNameFeature;
import de.fraunhofer.iem.swan.features.MethodClassEndsWithNameFeature;
import de.fraunhofer.iem.swan.features.MethodClassModifierFeature;
import de.fraunhofer.iem.swan.features.MethodHasParametersFeature;
import de.fraunhofer.iem.swan.features.MethodHasReturnTypeFeature;
import de.fraunhofer.iem.swan.features.MethodInnerClassFeature;
import de.fraunhofer.iem.swan.features.MethodInvocationClassName;
import de.fraunhofer.iem.swan.features.MethodInvocationName;
import de.fraunhofer.iem.swan.features.MethodIsConstructor;
import de.fraunhofer.iem.swan.features.MethodIsRealSetterFeature;
import de.fraunhofer.iem.swan.features.MethodModifierFeature;
import de.fraunhofer.iem.swan.features.MethodNameContainsFeature;
import de.fraunhofer.iem.swan.features.MethodNameEqualsFeature;
import de.fraunhofer.iem.swan.features.MethodNameStartsWithFeature;
import de.fraunhofer.iem.swan.features.MethodReturnsConstantFeature;
import de.fraunhofer.iem.swan.features.ParamTypeMatchesReturnType;
import de.fraunhofer.iem.swan.features.ParameterContainsTypeOrNameFeature;
import de.fraunhofer.iem.swan.features.ParameterFlowsToReturn;
import de.fraunhofer.iem.swan.features.ParameterToSinkFeature;
import de.fraunhofer.iem.swan.features.ReturnTypeContainsNameFeature;
import de.fraunhofer.iem.swan.features.ReturnTypeFeature;
import de.fraunhofer.iem.swan.features.SourceToReturnFeature;
import de.fraunhofer.iem.swan.features.VoidOnMethodFeature;
import de.fraunhofer.iem.swan.features.MethodClassModifierFeature.ClassModifier;
import de.fraunhofer.iem.swan.features.MethodModifierFeature.Modifier;

/**
 * Handles the learner's features.
 *
 * @author Lisa Nguyen Quang Do, Goran Piskachev
 *
 */

public class FeatureHandler {

  private final String cp;
  private Map<Category, Set<IFeature>> featuresMap;

  public FeatureHandler(String cp) {
    this.cp = cp;
  }

  public Map<Category, Set<IFeature>> features() {
    return featuresMap;
  }

  private void addFeature(IFeature feature,
      Set<Category> categoriesForFeature) {
    for (Category category : categoriesForFeature) {
      Set<IFeature> typeFeatures = featuresMap.get(category);
      typeFeatures.add(feature);
      featuresMap.put(category, typeFeatures);
    }
  }

  /**
   * Initializes the set of features for classifying methods in the categories.
   * 
   * @param disable indicates the id of the feature instance that should be excluded. 
   * Used for the One-at-a-time analysis. 0 is for all features enables. 
   */
  public void initializeFeatures(int disable) {
    featuresMap = new HashMap<Category, Set<IFeature>>();
    for (Category category : Category.values())
      featuresMap.put(category, new HashSet<IFeature>());

    if(disable != 1) {
    	// Implicit method.
        IFeature isImplicitMethod = new IsImplicitMethod();
        addFeature(isImplicitMethod,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.CWE089, Category.CWE862, Category.CWE863, Category.CWE078, Category.CWE306, Category.CWE079, Category.CWE601,
                Category.NONE)));
    }
    if(disable != 2) {
    	// Method in anonymous class.
    	IFeature anonymousClass = new MethodAnonymousClassFeature(true);
        addFeature(anonymousClass,
            new HashSet<>(Arrays.asList(Category.SOURCE,Category.SINK, Category.NONE, Category.CWE078, Category.CWE079, Category.CWE089, Category.CWE306, Category.CWE862, Category.CWE863, Category.CWE601)));
        
    }
    if(disable != 3) {
    	IFeature classNameContainsSaniti = new MethodClassContainsNameFeature(
    	        "Saniti");
    	    addFeature(classNameContainsSaniti,
    	        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 4) {
    	IFeature classNameContainsEncode = new MethodClassContainsNameFeature(
                "Encod");
            addFeature(classNameContainsEncode,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));	
    }
    
    if(disable != 5) {
    	IFeature classNameContainsEscape = new MethodClassContainsNameFeature(
    	        "Escap");
    	    addFeature(classNameContainsEscape,
    	        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    	    
    }
    if(disable != 6) {
    	IFeature classNameContainsValid = new MethodClassContainsNameFeature(
    	        "Valid");
    	    addFeature(classNameContainsValid,
    	        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 7) {
    	IFeature classNameContainsCheck = new MethodClassContainsNameFeature(
    	        "Check");
    	    addFeature(classNameContainsCheck,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
    	            Category.NONE)));
    }
    if(disable != 8) {
    	IFeature classNameContainsVerify = new MethodClassContainsNameFeature(
    	        "Verif");
    	    addFeature(classNameContainsVerify,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
    	            Category.NONE)));
    }
    if(disable != 9) {
    	IFeature classNameContainsAuthen = new MethodClassContainsNameFeature(
    	        "Authen");
    	    addFeature(classNameContainsAuthen,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
    	            Category.NONE)));
    }
    if(disable != 10) {
    	IFeature classNameContainsSecurity = new MethodClassContainsNameFeature(
    	        "Security");
    	    addFeature(classNameContainsSecurity,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
    	            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
    	            Category.AUTHENTICATION_TO_LOW, Category.NONE)));   
    }
    if(disable != 11) {
    	IFeature classNameContainsConnect = new MethodClassContainsNameFeature(
    	        "Connect");
    	    addFeature(classNameContainsConnect,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, 
    	            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 12) {
    	IFeature classNameContainsBind = new MethodClassContainsNameFeature("Bind");
	    addFeature(classNameContainsBind,
	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
	            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
	            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 13) {
    	IFeature classNameContainsOAuth = new MethodClassContainsNameFeature(
    	        "OAuth");
    	    addFeature(classNameContainsOAuth,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
    	            Category.CWE306, Category.NONE)));
    }
    if(disable != 14) {
    	IFeature classNameContainsIO = new MethodClassContainsNameFeature(".io.");
	    addFeature(classNameContainsIO, new HashSet<>(
	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 15) {
    	IFeature classNameContainsWeb = new MethodClassContainsNameFeature("web");
	    addFeature(classNameContainsWeb, new HashSet<>(
	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.CWE079, Category.CWE601)));
    }
    if(disable != 16) {
    	IFeature classNameContainsNet = new MethodClassContainsNameFeature(".net.");
        addFeature(classNameContainsNet, new HashSet<>(
            Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 17) {
    	 IFeature classNameContainsSql = new MethodClassContainsNameFeature("sql");
         addFeature(classNameContainsSql,
             new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                 Category.CWE089, Category.NONE)));
    }
    if(disable != 18) {
    	IFeature classNameContainsManager = new MethodClassContainsNameFeature(
    	        "Manager");
    	    addFeature(classNameContainsManager, new HashSet<>(
    	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 19) {
    	IFeature classNameContainsOutput = new MethodClassContainsNameFeature(
    	        "Output");
    	    addFeature(classNameContainsOutput,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 20) {
    	IFeature classNameContainsInput = new MethodClassContainsNameFeature(
    	        "Input");
    	    addFeature(classNameContainsInput,
    	        new HashSet<>(Arrays.asList(Category.SINK, Category.CWE079, Category.CWE078, Category.CWE089, Category.NONE)));  
    }
    if(disable != 21) {
    	IFeature classNameContainsDatabase = new MethodClassContainsNameFeature(
    	        "database");
    	    addFeature(classNameContainsDatabase,
    	        new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    }
    if(disable != 22) {
    	 IFeature classNameContainsDb = new MethodClassContainsNameFeature("db");
 	    addFeature(classNameContainsDb,
 	        new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    }
    if(disable != 23) {
    	IFeature classNameContainsHibernate = new MethodClassContainsNameFeature(
    	        "hibernate");
    	    addFeature(classNameContainsHibernate,
    	        new HashSet<>(Arrays.asList(Category.CWE089, Category.CWE079, Category.NONE)));
    }
    if(disable != 24) {
    	IFeature classNameContainsCredential = new MethodClassContainsNameFeature(
    	        "credential");
    	    addFeature(classNameContainsCredential,
    	        new HashSet<>(Arrays.asList(Category.CWE078, Category.CWE862,
    	            Category.CWE863, Category.NONE)));
    }
    if(disable != 25) {
    	IFeature classNameContainsProcess = new MethodClassContainsNameFeature(
    	        "process");
    	    addFeature(classNameContainsProcess,
    	        new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    }
    if(disable != 26) {
    	
        IFeature classNameContainsRuntime = new MethodClassContainsNameFeature(
            "runtime");
        addFeature(classNameContainsRuntime,
            new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));        
    }
    if(disable != 27) {
    	IFeature classNameContainsUser = new MethodClassContainsNameFeature("user");
        addFeature(classNameContainsUser, new HashSet<>(
            Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 28) {
    	IFeature classNameContainsJdbc = new MethodClassContainsNameFeature("jdbc");
        addFeature(classNameContainsJdbc,
            new HashSet<>(Arrays.asList(Category.SINK,
                Category.CWE089, Category.NONE)));
    }
    if(disable != 29) {
    	
        IFeature classNameContainsHtml = new MethodClassContainsNameFeature(
                "Html");
            addFeature(classNameContainsHtml,
                new HashSet<>(Arrays.asList(Category.SINK, Category.SOURCE, Category.CWE079, Category.NONE)));    
    }
    if(disable != 30) {
    	IFeature classNameContainsPage = new MethodClassContainsNameFeature(
                "Page");
            addFeature(classNameContainsPage,
                new HashSet<>(Arrays.asList(Category.CWE079, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 31) {
    	IFeature classNameContainsRequest = new MethodClassContainsNameFeature("Request");
        addFeature(classNameContainsRequest, new HashSet<>(
            Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 32) {
    	IFeature classNameContainsHttp = new MethodClassContainsNameFeature("http");
        addFeature(classNameContainsHttp, new HashSet<>(
            Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 33) {
    	
        IFeature classNameContainsUrl = new MethodClassContainsNameFeature(
                "url");
            addFeature(classNameContainsUrl,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 34) {
    	IFeature classNameContainsServlet = new MethodClassContainsNameFeature(
                "servlet");
            addFeature(classNameContainsServlet,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 35) {
    	IFeature classNameContainsResponse = new MethodClassContainsNameFeature(
                "Response");
            addFeature(classNameContainsResponse,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.SINK, Category.NONE)));
    }
    if(disable != 36) {
    	IFeature classNameContainsRedirect = new MethodClassContainsNameFeature(
                "Redirect");
            addFeature(classNameContainsRedirect,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.SINK, Category.NONE)));
    }
    if(disable != 37) {
    	IFeature classNameContainsCss = new MethodClassContainsNameFeature(
                "Css");
            addFeature(classNameContainsCss,
                new HashSet<>(Arrays.asList(Category.CWE079, Category.NONE)));
    }
    if(disable != 38) {
    	 IFeature classNameContainsDom = new MethodClassContainsNameFeature(
                 "Dom");
             addFeature(classNameContainsDom,
                 new HashSet<>(Arrays.asList(Category.CWE079, Category.NONE)));
    }
    if(disable != 39) {
    	// Method class ends 
        IFeature classEndsWithEncoder = new MethodClassEndsWithNameFeature("Encoder");
        addFeature(classEndsWithEncoder, new HashSet<>(Arrays.asList(
                Category.SANITIZER, Category.CWE078, Category.CWE079, Category.CWE862,
                Category.CWE863, Category.CWE089, Category.NONE)));
    }
    if(disable != 40) {
    	IFeature classEndsWithRequest = new MethodClassEndsWithNameFeature("Request");
        addFeature(classEndsWithRequest, new HashSet<>(Arrays.asList(
                Category.SINK, Category.CWE079, Category.CWE089, Category.NONE)));
    }
    if(disable != 41) {
    	IFeature classEndsWithRender = new MethodClassEndsWithNameFeature("Render");
        addFeature(classEndsWithRender, new HashSet<>(Arrays.asList(
                Category.SINK, Category.CWE079, Category.NONE)));
    }
    if(disable != 42) {
    	// Class modifier.
        IFeature isStaticClass = new MethodClassModifierFeature(cp,
            ClassModifier.STATIC);
        addFeature(isStaticClass, new HashSet<>(Arrays.asList(Category.SOURCE,
            Category.SINK, Category.SANITIZER, Category.NONE)));  
    }
    if(disable != 43) {
    	IFeature isPublicClass = new MethodClassModifierFeature(cp,
                ClassModifier.PUBLIC);
            addFeature(isPublicClass, new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.SINK, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 44) {
    	IFeature isFinalClass = new MethodClassModifierFeature(cp,
                ClassModifier.FINAL);
            addFeature(isFinalClass, new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.SINK, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 45) {
    	// Has parameters.
        IFeature hasParamsPerm = new MethodHasParametersFeature();
        addFeature(hasParamsPerm,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SINK, Category.CWE079,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.CWE601, Category.CWE089, Category.NONE)));	
    }
    if(disable != 46) {
    	// Has a return type.
        IFeature hasReturnType = new MethodHasReturnTypeFeature();
        addFeature(hasReturnType,
            new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.CWE601, Category.NONE)));
    }
    if(disable != 47) {
    	IFeature innerClassMethod = new MethodInnerClassFeature(cp, true);
        addFeature(innerClassMethod,
            new HashSet<>(Arrays.asList(
                Category.SINK, Category.SOURCE, Category.NONE, Category.CWE078, Category.CWE079, Category.CWE089, Category.CWE306, Category.CWE862, Category.CWE863, Category.CWE601 )));
    }
    if(disable != 48) {
    	// Call to a method of class.
        IFeature methodInvocationClassNameSaniti = new MethodInvocationClassName(cp,
            "Saniti");
        addFeature(methodInvocationClassNameSaniti,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 49) {
    	IFeature methodInvocationClassNameRegex = new MethodInvocationClassName(cp,
                "regex");
            addFeature(methodInvocationClassNameRegex,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 50) {
    	IFeature methodInvocationClassNameEscape = new MethodInvocationClassName(cp,
                "escap");
            addFeature(methodInvocationClassNameEscape,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 51) {
    	IFeature methodInvocationClassNameIO = new MethodInvocationClassName(cp,
                ".io.");
            addFeature(methodInvocationClassNameIO,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                    Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                    Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    }
    if(disable != 52) {
    	IFeature methodInvocationClassNameEncode = new MethodInvocationClassName(cp,
                "encod");
            addFeature(methodInvocationClassNameEncode,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 53) {
    	IFeature methodInvocationClassNameSQL = new MethodInvocationClassName(cp,
                "sql");
            addFeature(methodInvocationClassNameSQL,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                    Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                    Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    }
    if(disable != 54) {
    	IFeature methodInvocationClassNameDB = new MethodInvocationClassName(cp,
                "db");
            addFeature(methodInvocationClassNameDB,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                    Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                    Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    }
    if(disable != 55) {
    	IFeature methodInvocationClassNameWeb = new MethodInvocationClassName(cp,
                "web");
            addFeature(methodInvocationClassNameWeb, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.CWE079, Category.NONE)));
    }
    if(disable != 56) {
    	IFeature methodInvocationClassNameNet = new MethodInvocationClassName(cp,
                ".net.");
            addFeature(methodInvocationClassNameNet, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 57) {
    	IFeature methodInvocationClassNameLog = new MethodInvocationClassName(cp,
                "Log.");
            addFeature(methodInvocationClassNameLog,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 58) {
    	// Call to a method.
        IFeature methodInvocationNameEscape = new MethodInvocationName(cp, "escap");
        addFeature(methodInvocationNameEscape,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
            }
    if(disable != 59) {
        IFeature methodInvocationNameReplace = new MethodInvocationName(cp,
                "replac");
            addFeature(methodInvocationNameReplace, new HashSet<>(
                Arrays.asList(Category.SINK, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 60) {
    	IFeature methodInvocationNameStrip = new MethodInvocationName(cp, "strip");
        addFeature(methodInvocationNameStrip,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 61) {
    	IFeature methodInvocationNameMatches = new MethodInvocationName(cp,
                "match");
            addFeature(methodInvocationNameMatches,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));   
    }
    if(disable != 62) {
    	IFeature methodInvocationNameEncode = new MethodInvocationName(cp, "encod");
        addFeature(methodInvocationNameEncode,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 63) {
    	IFeature methodInvocationNameRegex = new MethodInvocationName(cp, "regex");
        addFeature(methodInvocationNameRegex,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));	
    }
    if(disable != 64) {
    	IFeature methodInvocationNameCheck = new MethodInvocationName(cp, "check");
        addFeature(methodInvocationNameCheck,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));     
    }
    if(disable != 65) {
    	IFeature methodInvocationNameVerif = new MethodInvocationName(cp, "verif");
        addFeature(methodInvocationNameVerif,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 66) {
    	IFeature methodInvocationNameAuthori = new MethodInvocationName(cp,
                "authori");
            addFeature(methodInvocationNameAuthori,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                    Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 67) {
    	IFeature methodInvocationNameAuthen = new MethodInvocationName(cp,
                "authen");
            addFeature(methodInvocationNameAuthen,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                    Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 68) {
    	IFeature methodInvocationNameLogin = new MethodInvocationName(cp, "login");
        addFeature(methodInvocationNameLogin,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, 
                Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 69) {
    	IFeature methodInvocationNameLogout = new MethodInvocationName(cp,
                "logout");
            addFeature(methodInvocationNameLogout,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_LOW,
                    Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }    
    if(disable != 70) {
    	IFeature methodInvocationNameSecurity = new MethodInvocationName(cp,
                "security");
            addFeature(methodInvocationNameSecurity,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                    Category.NONE)));
    }
    if(disable != 71) {
    	IFeature methodInvocationNameCredential = new MethodInvocationName(cp,
                "credential");
            addFeature(methodInvocationNameCredential,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 72) {
    	IFeature methodInvocationNameBind = new MethodInvocationName(cp, "bind");
        addFeature(methodInvocationNameBind,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));    
    }
    if(disable != 73) {
    	IFeature methodInvocationNameConnect = new MethodInvocationName(cp,
                "connect");
            addFeature(methodInvocationNameConnect,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                    Category.NONE)));
    }
    if(disable != 74) {
    	IFeature methodInvocationNameGet = new MethodInvocationName(cp, "get");
        addFeature(methodInvocationNameGet,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 75) {
    	IFeature methodInvocationNameRead = new MethodInvocationName(cp, "read");
        addFeature(methodInvocationNameRead,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 76) {
    	IFeature methodInvocationNameDecode = new MethodInvocationName(cp, "decod");
        addFeature(methodInvocationNameDecode,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));  
    }
    if(disable != 77) {
    	IFeature methodInvocationNameUnescape = new MethodInvocationName(cp,
                "unescap");
            addFeature(methodInvocationNameUnescape,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 78) {
    	IFeature methodInvocationNameLoad = new MethodInvocationName(cp, "load");
        addFeature(methodInvocationNameLoad,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 79) {
    	IFeature methodInvocationNameRequest = new MethodInvocationName(cp,
                "request");
            addFeature(methodInvocationNameRequest,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 80) {
    	IFeature methodInvocationNameCreate = new MethodInvocationName(cp, "creat");
        addFeature(methodInvocationNameCreate,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE))); 
    }
    if(disable != 81) {
    	IFeature methodInvocationNameOutput = new MethodInvocationName(cp,
                "output");
            addFeature(methodInvocationNameOutput,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 82) {
    	IFeature methodInvocationNameWrit = new MethodInvocationName(cp, "writ");
        addFeature(methodInvocationNameWrit,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 83) {
    	IFeature methodInvocationNameSet = new MethodInvocationName(cp, "set");
        addFeature(methodInvocationNameSet,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 84) {
    	IFeature methodInvocationNameUpdat = new MethodInvocationName(cp, "updat");
        addFeature(methodInvocationNameUpdat,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 85) {
    	IFeature methodInvocationNameSend = new MethodInvocationName(cp, "send");
        addFeature(methodInvocationNameSend,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 86) {
    	IFeature methodInvocationNameHandl = new MethodInvocationName(cp, "handl");
        addFeature(methodInvocationNameHandl,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 87) {
    	IFeature methodInvocationNamePut = new MethodInvocationName(cp, "put");
        addFeature(methodInvocationNamePut,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 88) {
    	IFeature methodInvocationNameAdd = new MethodInvocationName(cp, "log");
        addFeature(methodInvocationNameAdd,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 89) {
    	IFeature methodInvocationNameRun = new MethodInvocationName(cp, "run");
        addFeature(methodInvocationNameRun,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 90) {
    	IFeature methodInvocationNameExecut = new MethodInvocationName(cp,
                "execut");
            addFeature(methodInvocationNameExecut,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 91) {
    	IFeature methodInvocationNameDump = new MethodInvocationName(cp, "dump");
        addFeature(methodInvocationNameDump,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 92) {
    	IFeature methodInvocationNamePrint = new MethodInvocationName(cp, "print");
        addFeature(methodInvocationNamePrint,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 93) {
    	IFeature methodInvocationNamePars = new MethodInvocationName(cp, "pars");
        addFeature(methodInvocationNamePars,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 94) {
            IFeature methodInvocationNameMakedb = new MethodInvocationName(cp,
                "makedb");
            addFeature(methodInvocationNameMakedb,
                new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    }
    if(disable != 95) {
    	IFeature methodInvocationNameExecute = new MethodInvocationName(cp,
                "execute");
            addFeature(methodInvocationNameExecute,
                new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    }
    if(disable != 96) {
    	IFeature methodInvocationNameSaniti = new MethodInvocationName(cp,
                "saniti");
            addFeature(methodInvocationNameSaniti,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 97) {
    	// Constructor.
        IFeature methodIsConstructor = new MethodIsConstructor();
        addFeature(methodIsConstructor,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                Category.SANITIZER, Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_NEUTRAL, Category.CWE078, Category.CWE862,
                Category.CWE863, Category.CWE089, Category.NONE)));
    }
    if(disable != 98) {
    	IFeature realSetter = new MethodIsRealSetterFeature(cp);
        addFeature(realSetter,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 99) {
    	  // Method modifier.
        IFeature isStaticMethod = new MethodModifierFeature(cp, Modifier.STATIC);
        addFeature(isStaticMethod,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 100) {
    	IFeature isPublicMethod = new MethodModifierFeature(cp, Modifier.PUBLIC);
        addFeature(isPublicMethod,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 101) {
    	IFeature isPrivateMethod = new MethodModifierFeature(cp, Modifier.PRIVATE);
        addFeature(isPrivateMethod, new HashSet<>(
            Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 102) {
    	IFeature isFinalMethod = new MethodModifierFeature(cp, Modifier.FINAL);
        addFeature(isFinalMethod, new HashSet<>(Arrays.asList(Category.SOURCE,
            Category.SINK, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 103) {
    	// Method name.
        IFeature methodNameStartsWithGet = new MethodNameStartsWithFeature("get");
        addFeature(methodNameStartsWithGet,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SANITIZER,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.NONE)));    
    }
    if(disable != 104) {
    	IFeature methodNameStartsWithSet = new MethodNameStartsWithFeature("set");
        addFeature(methodNameStartsWithSet,
            new HashSet<>(Arrays.asList(Category.SINK, Category.SANITIZER,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.CWE079, Category.NONE)));
    }
    if(disable != 105) {
    	IFeature methodNameStartsWithPut = new MethodNameStartsWithFeature("put");
        addFeature(methodNameStartsWithPut,
            new HashSet<>(Arrays.asList(Category.SANITIZER,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    }
    if(disable != 106) {
    	IFeature methodNameStartsWithHas = new MethodNameStartsWithFeature("has");
        addFeature(methodNameStartsWithHas,
            new HashSet<>(Arrays.asList(Category.SANITIZER,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    }
    if(disable != 107) {
    	IFeature methodNameStartsWithIs = new MethodNameStartsWithFeature("is");
        addFeature(methodNameStartsWithIs,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.NONE)));
    }
    if(disable != 108) {
    	IFeature methodNameStartsOpen = new MethodNameStartsWithFeature("open");
        addFeature(methodNameStartsOpen,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, 
                Category.NONE)));
    }
    if(disable != 109) {
    	IFeature methodNameStartsClose = new MethodNameStartsWithFeature("close");
        addFeature(methodNameStartsClose,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                 Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 110) {
    	IFeature methodNameStartsCreate = new MethodNameStartsWithFeature("create");
        addFeature(methodNameStartsCreate,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, 
                Category.NONE)));
    }
    if(disable != 111) {
    	IFeature methodNameStartsDelete = new MethodNameStartsWithFeature("delete");
        addFeature(methodNameStartsDelete,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                 Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 112) {
    	  // Method Name Equals. 
        IFeature nameEqualsLog = new MethodNameEqualsFeature("log");
        addFeature(nameEqualsLog,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));    
    }
    if(disable != 113) {
    	IFeature nameEqualsSetHeader = new MethodNameEqualsFeature("setHeader");
        addFeature(nameEqualsSetHeader,
            new HashSet<>(Arrays.asList(Category.SINK, Category.CWE079, Category.NONE)));
    }
    if(disable != 114) {
    	IFeature nameEqualsSendRedirect = new MethodNameEqualsFeature("sendRedirect");
        addFeature(nameEqualsSendRedirect,
            new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 115) {
    	// Method Contains Name. 
        IFeature methodNameContainsSaniti = new MethodNameContainsFeature("saniti");
        addFeature(methodNameContainsSaniti,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.CWE078, Category.CWE079, Category.CWE089, Category.NONE)));
        
    }
    if(disable != 116) {
    	IFeature methodNameContainsEscape = new MethodNameContainsFeature("escape",
                "unescape");
            addFeature(methodNameContainsEscape,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 117) {
    	IFeature methodNameContainsUnescape = new MethodNameContainsFeature(
    	        "unescape");
    	    addFeature(methodNameContainsUnescape,
    	        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 118) {
    	IFeature methodNameContainsReplace = new MethodNameContainsFeature(
    	        "replac");
    	    addFeature(methodNameContainsReplace, new HashSet<>(
    	        Arrays.asList(Category.SINK, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 119) {
    	IFeature methodNameContainsStrip = new MethodNameContainsFeature("strip");
        addFeature(methodNameContainsStrip,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));  
    }
    if(disable != 120) {
    	IFeature methodNameContainsEncode = new MethodNameContainsFeature("encod",
                "encoding");
            addFeature(methodNameContainsEncode,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 121) {
    	IFeature methodNameContainsRegex = new MethodNameContainsFeature("regex");
        addFeature(methodNameContainsRegex,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 122) {
        IFeature methodNameContainsAuthen = new MethodNameContainsFeature("authen");
        addFeature(methodNameContainsAuthen,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.CWE306, Category.NONE)));
    }
    if(disable != 123) {
    	IFeature methodNameContainsCheck = new MethodNameContainsFeature("check");
        addFeature(methodNameContainsCheck,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 124) {
    	IFeature methodNameContainsVerif = new MethodNameContainsFeature("verif");
        addFeature(methodNameContainsVerif,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE, Category.SANITIZER)));
    }
    if(disable != 125) {
    	IFeature methodNameContainsPrivilege = new MethodNameContainsFeature(
                "privilege");
            addFeature(methodNameContainsPrivilege,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                    Category.NONE)));
    }
    if(disable != 126) {
    	IFeature methodNameContainsLogin = new MethodNameContainsFeature("login");
        addFeature(methodNameContainsLogin,
            new HashSet<>(Arrays.asList(
                Category.AUTHENTICATION_TO_HIGH, 
                Category.CWE306, Category.NONE)));
        
    }
    if(disable != 127) {
    	IFeature methodNameContainsNotLoginPage = new MethodNameContainsFeature("",
                "loginpage");
            addFeature(methodNameContainsNotLoginPage,
                new HashSet<>(Arrays.asList(Category.CWE306, Category.CWE862,
                    Category.CWE863, Category.NONE)));
    }
    if(disable != 128) {
    	IFeature methodNameContainsLogout = new MethodNameContainsFeature("logout");
        addFeature(methodNameContainsLogout,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_LOW,
                Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 129) {
    	IFeature methodNameContainsConnect = new MethodNameContainsFeature(
                "connect", "disconnect");
            addFeature(methodNameContainsConnect,
                new HashSet<>(Arrays.asList(
                    Category.AUTHENTICATION_TO_HIGH, 
                    Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 130) {
    	IFeature methodNameContainsDisconnect = new MethodNameContainsFeature(
                "disconnect");
            addFeature(methodNameContainsDisconnect,
                new HashSet<>(Arrays.asList( Category.AUTHENTICATION_TO_LOW,
                    Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 131) {
    	IFeature methodNameContainsBind = new MethodNameContainsFeature("bind",
    	        "unbind");
    	    addFeature(methodNameContainsBind,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, 
    	            Category.NONE)));
    }
    if(disable != 132) {
    	IFeature methodNameContainsUnbind = new MethodNameContainsFeature("unbind");
	    addFeature(methodNameContainsUnbind,
	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
	            Category.AUTHENTICATION_TO_LOW,
	            Category.NONE)));
    }
    if(disable != 133) {
    	IFeature nameContaisRead = new MethodNameContainsFeature("read", "thread");
	    addFeature(nameContaisRead,
	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.CWE601, Category.NONE)));
    }
    if(disable != 134) {
    	IFeature nameContainsLoad = new MethodNameContainsFeature("load",
    	        "payload");
    	    addFeature(nameContainsLoad,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 135) {
    	IFeature nameContainsRequest = new MethodNameContainsFeature("request");
        addFeature(nameContainsRequest,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
        
    }
    if(disable != 136) {
    	IFeature nameContainsCreate = new MethodNameContainsFeature("creat");
        addFeature(nameContainsCreate,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 137) {
    	IFeature nameContainsDecod = new MethodNameContainsFeature("decod");
        addFeature(nameContainsDecod,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 138) {
    	IFeature nameContainsUnescap = new MethodNameContainsFeature("unescap");
        addFeature(nameContainsUnescap,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 139) {
    	IFeature nameContainsPars = new MethodNameContainsFeature("pars");
        addFeature(nameContainsPars, new HashSet<>(
            Arrays.asList(Category.SINK, Category.SOURCE, Category.NONE)));
    }
    if(disable != 140) {
    	IFeature nameContainsStream = new MethodNameContainsFeature("stream");
        addFeature(nameContainsStream, new HashSet<>(
            Arrays.asList(Category.SINK, Category.SOURCE, Category.NONE)));
    }
    if(disable != 141) {
    	IFeature nameContainsRetriev = new MethodNameContainsFeature("retriev");
        addFeature(nameContainsRetriev,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 142) {
    	IFeature nameContainsObject = new MethodNameContainsFeature("Object");
        addFeature(nameContainsObject,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 143) {
    	 IFeature nameContainsName = new MethodNameContainsFeature("Name");
         addFeature(nameContainsName,
             new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 144) {
    	IFeature nameContainsWrit = new MethodNameContainsFeature("writ");
        addFeature(nameContainsWrit,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 145) {
    	IFeature nameContainsUpdat = new MethodNameContainsFeature("updat");
        addFeature(nameContainsUpdat,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 146) {
    	IFeature nameContainsSend = new MethodNameContainsFeature("send");
        addFeature(nameContainsSend,
            new HashSet<>(Arrays.asList(Category.SINK, Category.CWE601, Category.NONE)));
    }
    if(disable != 147) {
    	IFeature nameContainsHandl = new MethodNameContainsFeature("handl");
        addFeature(nameContainsHandl,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 148) {
    	IFeature nameContainsLog = new MethodNameContainsFeature("log");
        addFeature(nameContainsLog,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 149) {
    	IFeature nameContainsRun = new MethodNameContainsFeature("run");
        addFeature(nameContainsRun, new HashSet<>(
            Arrays.asList(Category.SINK, Category.CWE078, Category.NONE)));
    }
    if(disable != 150) {
    	IFeature nameContainsExecut = new MethodNameContainsFeature("execut");
        addFeature(nameContainsExecut,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 151) {
    	IFeature nameContainsExec = new MethodNameContainsFeature("exec");
        addFeature(nameContainsExec,
            new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    }
    if(disable != 152) {
    	IFeature nameContainsCompile = new MethodNameContainsFeature("compile");
        addFeature(nameContainsCompile,
            new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    }
    if(disable != 153) {
    	IFeature nameContainsDump = new MethodNameContainsFeature("dump");
        addFeature(nameContainsDump,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 154) {
    	IFeature nameContainsPrint = new MethodNameContainsFeature("print");
        addFeature(nameContainsPrint,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 155) {
    	IFeature nameContainsExecute = new MethodNameContainsFeature("execute");
        addFeature(nameContainsExecute,
            new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    }
    if(disable != 156) {
    	IFeature nameContainsQuery = new MethodNameContainsFeature("query");
        addFeature(nameContainsQuery,
            new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    }
    if(disable != 157) {
    	IFeature nameContainsRole = new MethodNameContainsFeature("role");
        addFeature(nameContainsRole, new HashSet<>(
            Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE)));	
    }
    if(disable != 158) {
    	IFeature nameContainsAuthori = new MethodNameContainsFeature("authori");
        addFeature(nameContainsAuthori, new HashSet<>(
            Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 159) {
    	IFeature nameContainsRedirect = new MethodNameContainsFeature("redirect");
        addFeature(nameContainsRedirect,
            new HashSet<>(Arrays.asList(Category.SINK, Category.CWE601, Category.NONE)));
    }
    if(disable != 160) {
    	IFeature methodNameContainsGetParameter = new MethodNameContainsFeature("getParameter");
        addFeature(methodNameContainsGetParameter,
            new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 161) {
    	IFeature returnsConstant = new MethodReturnsConstantFeature(cp);
        addFeature(returnsConstant,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 162) {
    	// Parameter types (ParameterContainsTypeOrNameFeature).
        IFeature parameterOfTypeString = new ParameterContainsTypeOrNameFeature(
            "java.lang.String");
        addFeature(parameterOfTypeString,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.CWE089, Category.CWE306,
                Category.CWE078, Category.CWE862, Category.CWE863, Category.CWE079, Category.NONE)));
    }
    if(disable != 163) {
    	IFeature parameterOfTypeCharArray = new ParameterContainsTypeOrNameFeature(
    	        "char[]");
    	    addFeature(parameterOfTypeCharArray,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
    	            Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.NONE)));
    }
    if(disable != 164) {
    	IFeature parameterOfTypeByteArray = new ParameterContainsTypeOrNameFeature(
    	        "byte[]");
    	    addFeature(parameterOfTypeByteArray,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
    	            Category.SANITIZER, Category.NONE)));
    }
    if(disable != 165) {
    	IFeature parameterOfTypeCharSequence = new ParameterContainsTypeOrNameFeature(
    	        "java.lang.CharSequence");
    	    addFeature(parameterOfTypeCharSequence,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
    	            Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.NONE)));    
    }
    if(disable != 166) {
    	IFeature parameterOfTypeStringBuilder = new ParameterContainsTypeOrNameFeature(
    	        "java.lang.StringBuilder");
    	    addFeature(parameterOfTypeStringBuilder, new HashSet<>(
    	        Arrays.asList(Category.SANITIZER, Category.SINK, Category.NONE)));
    }
    if(disable != 167) {
    	IFeature parameterOfTypeIo = new ParameterContainsTypeOrNameFeature(".io.");
	    addFeature(parameterOfTypeIo, new HashSet<>(
	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 168) {
    	IFeature parameterOfTypeWeb = new ParameterContainsTypeOrNameFeature("web");
	    addFeature(parameterOfTypeWeb, new HashSet<>(
	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 169) {
    	IFeature parameterOfTypeSql = new ParameterContainsTypeOrNameFeature("sql");
	    addFeature(parameterOfTypeSql, new HashSet<>(Arrays.asList(Category.SOURCE,
	        Category.SINK, Category.CWE089, Category.NONE)));
    }
    if(disable != 170) {
    	 IFeature parameterOfTypeDb = new ParameterContainsTypeOrNameFeature("db");
 	    addFeature(parameterOfTypeDb, new HashSet<>(
 	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 171) {
    	IFeature parameterOfTypeCredential = new ParameterContainsTypeOrNameFeature(
    	        "credential");
    	    addFeature(parameterOfTypeCredential,
    	        new HashSet<>(Arrays.asList(Category.CWE306, Category.CWE862,
    	            Category.CWE863, Category.CWE078, Category.NONE)));    
    }
    if(disable != 172) {
    	IFeature parameterOfTypeUrl = new ParameterContainsTypeOrNameFeature("url");
	    addFeature(parameterOfTypeUrl, new HashSet<>(
	        Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 173) {
    	// Parameter flows to return value.
        IFeature parameterFlowsToReturn = new ParameterFlowsToReturn(cp);
        addFeature(parameterFlowsToReturn,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 174) {
    	// Parameter to sink.
        
        IFeature paramToSinkSetWrit = new ParameterToSinkFeature(cp, "writ");
        addFeature(paramToSinkSetWrit,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 175) {
    	IFeature paramToSinkSet = new ParameterToSinkFeature(cp, "set");
        addFeature(paramToSinkSet,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 176) {
    	IFeature paramToSinkUpdat = new ParameterToSinkFeature(cp, "updat");
        addFeature(paramToSinkUpdat,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 177) {
    	IFeature paramToSinkSend = new ParameterToSinkFeature(cp, "send");
        addFeature(paramToSinkSend,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 178) {
    	IFeature paramToSinkSetHandl = new ParameterToSinkFeature(cp, "handl");
        addFeature(paramToSinkSetHandl,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 179) {
    	IFeature paramToSinkSetPut = new ParameterToSinkFeature(cp, "put");
        addFeature(paramToSinkSetPut,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
        
    }
   if(disable != 180) {
	   IFeature paramToSinkSetAdd = new ParameterToSinkFeature(cp, "log");
       addFeature(paramToSinkSetAdd,
           new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 181) {
    	IFeature paramToSinkSetRun = new ParameterToSinkFeature(cp, "run");
        addFeature(paramToSinkSetRun,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 182) {
    	IFeature paramToSinkSetExecut = new ParameterToSinkFeature(cp, "execut");
        addFeature(paramToSinkSetExecut,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 183) {
    	IFeature paramToSinkDump = new ParameterToSinkFeature(cp, "dump");
        addFeature(paramToSinkDump,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 184) {
    	IFeature paramToSinkPrint = new ParameterToSinkFeature(cp, "print");
        addFeature(paramToSinkPrint,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 185) {
    	IFeature paramToSinkPars = new ParameterToSinkFeature(cp, "pars");
        addFeature(paramToSinkPars,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
   if(disable != 186) {
	   IFeature paramToSinkStream = new ParameterToSinkFeature(cp, "stream");
       addFeature(paramToSinkStream,
           new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 187) {
    	// Parameter Type matches return 
        IFeature paramTypeMathcesReturn = new ParamTypeMatchesReturnType(cp);
        addFeature(paramTypeMathcesReturn,
            new HashSet<>(Arrays.asList(
                Category.SANITIZER, Category.CWE078, Category.CWE079, Category.CWE862,
                Category.CWE863, Category.CWE089, Category.NONE)));
    }
    if(disable != 188) {
    	// ReturnTypeContainsNameFeature 
        IFeature returnContainsDocument = new ReturnTypeContainsNameFeature(cp, "Document");
        addFeature(returnContainsDocument,
            new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079, Category.NONE)));
    }
    if(disable != 189) {
    	IFeature returnContainsNode = new ReturnTypeContainsNameFeature(cp, "Node");
        addFeature(returnContainsNode,
            new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079, Category.NONE)));
        
    }
    if(disable != 190) {
    	IFeature returnContainsUser = new ReturnTypeContainsNameFeature(cp, "User");
        addFeature(returnContainsUser,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE))); 
    }
    if(disable != 191) {
    	IFeature returnContainsCredential= new ReturnTypeContainsNameFeature(cp, "Credential");
        addFeature(returnContainsCredential,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
   if(disable != 192) {
	   IFeature returnContainsServlet = new ReturnTypeContainsNameFeature(cp, "Servlet");
       addFeature(returnContainsServlet,
           new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079,Category.CWE078, Category.NONE))); 
    }
    if(disable != 193) {
    	IFeature returnContainsRequest = new ReturnTypeContainsNameFeature(cp, "Request");
        addFeature(returnContainsRequest,
            new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079, Category.NONE)));
    }
    if(disable != 194) {
    	// Return types (ReturnTypeFeature).
        IFeature byteArrayReturnType = new ReturnTypeFeature(cp, "byte[]");
        addFeature(byteArrayReturnType,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SOURCE, Category.NONE)));   
    }
    if(disable != 195) {
    	IFeature stringReturnType = new ReturnTypeFeature(cp, "java.lang.String");
        addFeature(stringReturnType,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SOURCE, Category.CWE079, Category.CWE078, Category.CWE089, Category.NONE)));
    }
    if(disable != 196) {
    	IFeature charSequenceReturnType = new ReturnTypeFeature(cp,
                "java.lang.CharSequence");
            addFeature(charSequenceReturnType,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SOURCE, Category.NONE)));
    }
    if(disable != 197) {
    	IFeature booleanReturnType = new ReturnTypeFeature(cp, "boolean");
        addFeature(booleanReturnType,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
   if(disable != 198) {
	   IFeature resultsetReturnType = new ReturnTypeFeature(cp, "java.sql.ResultSet");
       addFeature(resultsetReturnType,
           new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.CWE089)));
    }
    if(disable != 199) {
    	 // Source to return.
        IFeature sourceGetToReturn = new SourceToReturnFeature(cp, "get");
        addFeature(sourceGetToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 200) {
    	IFeature sourceReadToReturn = new SourceToReturnFeature(cp, "read");
        addFeature(sourceReadToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 201) {
    	IFeature sourcDecodeToReturn = new SourceToReturnFeature(cp, "decode");
        addFeature(sourcDecodeToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 202) {
    	IFeature sourceUnescapeToReturn = new SourceToReturnFeature(cp, "unescape");
        addFeature(sourceUnescapeToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 203) {
    	IFeature sourceLoadToReturn = new SourceToReturnFeature(cp, "load");
        addFeature(sourceLoadToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 204) {
    	IFeature sourceRequestToReturn = new SourceToReturnFeature(cp, "request");
        addFeature(sourceRequestToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 205) {
    	IFeature sourceCreateToReturn = new SourceToReturnFeature(cp, "create");
        addFeature(sourceCreateToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 206) {
    	IFeature voidOn = new VoidOnMethodFeature();
        addFeature(voidOn,
            new HashSet<>(Arrays.asList(Category.SINK,Category.CWE079, Category.NONE)));
    }
    System.out.println("Initialized " + getFeaturesSize() + " features.");
  }

  private int getFeaturesSize() {
    int count = 0;
    for (Category c : featuresMap.keySet())
      count += featuresMap.get(c).size();
    return count;
  }

}
