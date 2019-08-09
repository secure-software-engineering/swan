package de.fraunhofer.iem.swan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.features.*;
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
        ((WeightedFeature)isImplicitMethod).setWeight(5);
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
        ((WeightedFeature)anonymousClass).setWeight(8);
        addFeature(anonymousClass,
            new HashSet<>(Arrays.asList(Category.SOURCE,Category.SINK, Category.NONE, Category.CWE078, Category.CWE079, Category.CWE089, Category.CWE306, Category.CWE862, Category.CWE863, Category.CWE601)));
        
    }
    if(disable != 3) {
    	IFeature classNameContainsSaniti = new MethodClassContainsNameFeature(
    	        "Saniti");
        ((WeightedFeature)classNameContainsSaniti).setWeight(2);
    	    addFeature(classNameContainsSaniti,
    	        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 4) {
    	IFeature classNameContainsEncode = new MethodClassContainsNameFeature(
                "Encod");
        ((WeightedFeature)classNameContainsEncode).setWeight(-12);
            addFeature(classNameContainsEncode,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));	
    }
    
    if(disable != 5) {
    	IFeature classNameContainsEscape = new MethodClassContainsNameFeature(
    	        "Escap");
        ((WeightedFeature)classNameContainsEscape).setWeight(0);
    	    addFeature(classNameContainsEscape,
    	        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    	    
    }
    if(disable != 6) {
    	IFeature classNameContainsValid = new MethodClassContainsNameFeature(
    	        "Valid");
        ((WeightedFeature)classNameContainsValid).setWeight(-13);
    	    addFeature(classNameContainsValid,
    	        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 7) {
    	IFeature classNameContainsCheck = new MethodClassContainsNameFeature(
    	        "Check");
        ((WeightedFeature)classNameContainsCheck).setWeight(13);
    	    addFeature(classNameContainsCheck,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
    	            Category.NONE)));
    }
    if(disable != 8) {
    	IFeature classNameContainsVerify = new MethodClassContainsNameFeature(
    	        "Verif");
        ((WeightedFeature)classNameContainsVerify).setWeight(-8);
    	    addFeature(classNameContainsVerify,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
    	            Category.NONE)));
    }
    if(disable != 9) {
    	IFeature classNameContainsAuthen = new MethodClassContainsNameFeature(
    	        "Authen");
        ((WeightedFeature)classNameContainsAuthen).setWeight(9);
    	    addFeature(classNameContainsAuthen,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
    	            Category.NONE)));
    }
    if(disable != 10) {
    	IFeature classNameContainsSecurity = new MethodClassContainsNameFeature(
    	        "Security");
        ((WeightedFeature)classNameContainsSecurity).setWeight(1);
    	    addFeature(classNameContainsSecurity,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
    	            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
    	            Category.AUTHENTICATION_TO_LOW, Category.NONE)));   
    }
    if(disable != 11) {
    	IFeature classNameContainsConnect = new MethodClassContainsNameFeature(
    	        "Connect");
        ((WeightedFeature)classNameContainsConnect).setWeight(9);
    	    addFeature(classNameContainsConnect,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, 
    	            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 12) {
    	IFeature classNameContainsBind = new MethodClassContainsNameFeature("Bind");
        ((WeightedFeature)classNameContainsBind).setWeight(21);
	    addFeature(classNameContainsBind,
	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
	            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
	            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 13) {
    	IFeature classNameContainsOAuth = new MethodClassContainsNameFeature(
    	        "OAuth");
        ((WeightedFeature)classNameContainsOAuth).setWeight(5);
    	    addFeature(classNameContainsOAuth,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
    	            Category.CWE306, Category.NONE)));
    }
    if(disable != 14) {
    	IFeature classNameContainsIO = new MethodClassContainsNameFeature(".io.");
        ((WeightedFeature)classNameContainsIO).setWeight(13);
	    addFeature(classNameContainsIO, new HashSet<>(
	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 15) {
    	IFeature classNameContainsWeb = new MethodClassContainsNameFeature("web");
        ((WeightedFeature)classNameContainsWeb).setWeight(15);
	    addFeature(classNameContainsWeb, new HashSet<>(
	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.CWE079, Category.CWE601)));
    }
    if(disable != 16) {
    	IFeature classNameContainsNet = new MethodClassContainsNameFeature(".net.");
        ((WeightedFeature)classNameContainsNet).setWeight(9);
        addFeature(classNameContainsNet, new HashSet<>(
            Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 17) {
    	 IFeature classNameContainsSql = new MethodClassContainsNameFeature("sql");
        ((WeightedFeature)classNameContainsSql).setWeight(17);
         addFeature(classNameContainsSql,
             new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                 Category.CWE089, Category.NONE)));
    }
    if(disable != 18) {
    	IFeature classNameContainsManager = new MethodClassContainsNameFeature(
    	        "Manager");
        ((WeightedFeature)classNameContainsManager).setWeight(-5);
    	    addFeature(classNameContainsManager, new HashSet<>(
    	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 19) {
    	IFeature classNameContainsOutput = new MethodClassContainsNameFeature(
    	        "Output");
        ((WeightedFeature)classNameContainsOutput).setWeight(-8);
    	    addFeature(classNameContainsOutput,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 20) {
    	IFeature classNameContainsInput = new MethodClassContainsNameFeature(
    	        "Input");
        ((WeightedFeature)classNameContainsInput).setWeight(5);
    	    addFeature(classNameContainsInput,
    	        new HashSet<>(Arrays.asList(Category.SINK, Category.CWE079, Category.CWE078, Category.CWE089, Category.NONE)));  
    }
    if(disable != 21) {
    	IFeature classNameContainsDatabase = new MethodClassContainsNameFeature(
    	        "database");
        ((WeightedFeature)classNameContainsDatabase).setWeight(10);
    	    addFeature(classNameContainsDatabase,
    	        new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    }
    if(disable != 22) {
    	 IFeature classNameContainsDb = new MethodClassContainsNameFeature("db");
        ((WeightedFeature)classNameContainsDb).setWeight(5);
 	    addFeature(classNameContainsDb,
 	        new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    }
    if(disable != 23) {
    	IFeature classNameContainsHibernate = new MethodClassContainsNameFeature(
    	        "hibernate");
        ((WeightedFeature)classNameContainsHibernate).setWeight(-8);
    	    addFeature(classNameContainsHibernate,
    	        new HashSet<>(Arrays.asList(Category.CWE089, Category.CWE079, Category.NONE)));
    }
    if(disable != 24) {
    	IFeature classNameContainsCredential = new MethodClassContainsNameFeature(
    	        "credential");
        ((WeightedFeature)classNameContainsCredential).setWeight(19);
    	    addFeature(classNameContainsCredential,
    	        new HashSet<>(Arrays.asList(Category.CWE078, Category.CWE862,
    	            Category.CWE863, Category.NONE)));
    }
    if(disable != 25) {
    	IFeature classNameContainsProcess = new MethodClassContainsNameFeature(
    	        "process");
        ((WeightedFeature)classNameContainsProcess).setWeight(13);
    	    addFeature(classNameContainsProcess,
    	        new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    }
    if(disable != 26) {
    	
        IFeature classNameContainsRuntime = new MethodClassContainsNameFeature(
            "runtime");
        ((WeightedFeature)classNameContainsRuntime).setWeight(17);
        addFeature(classNameContainsRuntime,
            new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));        
    }
    if(disable != 27) {
    	IFeature classNameContainsUser = new MethodClassContainsNameFeature("user");
        ((WeightedFeature)classNameContainsUser).setWeight(2);
        addFeature(classNameContainsUser, new HashSet<>(
            Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 28) {
    	IFeature classNameContainsJdbc = new MethodClassContainsNameFeature("jdbc");
        ((WeightedFeature)classNameContainsJdbc).setWeight(2);
        addFeature(classNameContainsJdbc,
            new HashSet<>(Arrays.asList(Category.SINK,
                Category.CWE089, Category.NONE)));
    }
    if(disable != 29) {
    	
        IFeature classNameContainsHtml = new MethodClassContainsNameFeature(
                "Html");
        ((WeightedFeature)classNameContainsHtml).setWeight(1);
            addFeature(classNameContainsHtml,
                new HashSet<>(Arrays.asList(Category.SINK, Category.SOURCE, Category.CWE079, Category.NONE)));    
    }
    if(disable != 30) {
    	IFeature classNameContainsPage = new MethodClassContainsNameFeature(
                "Page");
        ((WeightedFeature)classNameContainsPage).setWeight(5);
            addFeature(classNameContainsPage,
                new HashSet<>(Arrays.asList(Category.CWE079, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 31) {
    	IFeature classNameContainsRequest = new MethodClassContainsNameFeature("Request");
        ((WeightedFeature)classNameContainsRequest).setWeight(13);
        addFeature(classNameContainsRequest, new HashSet<>(
            Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 32) {
    	IFeature classNameContainsHttp = new MethodClassContainsNameFeature("http");
        ((WeightedFeature)classNameContainsHttp).setWeight(-8);
        addFeature(classNameContainsHttp, new HashSet<>(
            Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 33) {
    	
        IFeature classNameContainsUrl = new MethodClassContainsNameFeature(
                "url");
        ((WeightedFeature)classNameContainsUrl).setWeight(13);
            addFeature(classNameContainsUrl,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 34) {
    	IFeature classNameContainsServlet = new MethodClassContainsNameFeature(
                "servlet");
        ((WeightedFeature)classNameContainsServlet).setWeight(0);
            addFeature(classNameContainsServlet,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 35) {
    	IFeature classNameContainsResponse = new MethodClassContainsNameFeature(
                "Response");
        ((WeightedFeature)classNameContainsResponse).setWeight(7);
            addFeature(classNameContainsResponse,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.SINK, Category.NONE)));
    }
    if(disable != 36) {
    	IFeature classNameContainsRedirect = new MethodClassContainsNameFeature(
                "Redirect");
        ((WeightedFeature)classNameContainsRedirect).setWeight(7);
            addFeature(classNameContainsRedirect,
                new HashSet<>(Arrays.asList(Category.CWE601, Category.SINK, Category.NONE)));
    }
    if(disable != 37) {
    	IFeature classNameContainsCss = new MethodClassContainsNameFeature(
                "Css");
        ((WeightedFeature)classNameContainsCss).setWeight(5);
            addFeature(classNameContainsCss,
                new HashSet<>(Arrays.asList(Category.CWE079, Category.NONE)));
    }
    if(disable != 38) {
    	 IFeature classNameContainsDom = new MethodClassContainsNameFeature(
                 "Dom");
        ((WeightedFeature)classNameContainsDom).setWeight(-1);
             addFeature(classNameContainsDom,
                 new HashSet<>(Arrays.asList(Category.CWE079, Category.NONE)));
    }
    if(disable != 39) {
    	// Method class ends 
        IFeature classEndsWithEncoder = new MethodClassEndsWithNameFeature("Encoder");
        ((WeightedFeature)classEndsWithEncoder).setWeight(18);
        addFeature(classEndsWithEncoder, new HashSet<>(Arrays.asList(
                Category.SANITIZER, Category.CWE078, Category.CWE079, Category.CWE862,
                Category.CWE863, Category.CWE089, Category.NONE)));
    }
    if(disable != 40) {
    	IFeature classEndsWithRequest = new MethodClassEndsWithNameFeature("Request");
        ((WeightedFeature)classEndsWithRequest).setWeight(13);
        addFeature(classEndsWithRequest, new HashSet<>(Arrays.asList(
                Category.SINK, Category.CWE079, Category.CWE089, Category.NONE)));
    }
    if(disable != 41) {
    	IFeature classEndsWithRender = new MethodClassEndsWithNameFeature("Render");
        ((WeightedFeature)classEndsWithRender).setWeight(-8);
        addFeature(classEndsWithRender, new HashSet<>(Arrays.asList(
                Category.SINK, Category.CWE079, Category.NONE)));
    }
    if(disable != 42) {
    	// Class modifier.
        IFeature isStaticClass = new MethodClassModifierFeature(cp,
            ClassModifier.STATIC);
        ((WeightedFeature)isStaticClass).setWeight(17);
        addFeature(isStaticClass, new HashSet<>(Arrays.asList(Category.SOURCE,
            Category.SINK, Category.SANITIZER, Category.NONE)));  
    }
    if(disable != 43) {
    	IFeature isPublicClass = new MethodClassModifierFeature(cp,
                ClassModifier.PUBLIC);
        ((WeightedFeature)isPublicClass).setWeight(-8);
            addFeature(isPublicClass, new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.SINK, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 44) {
    	IFeature isFinalClass = new MethodClassModifierFeature(cp,
                ClassModifier.FINAL);
        ((WeightedFeature)isFinalClass).setWeight(12);
            addFeature(isFinalClass, new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.SINK, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 45) {
    	// Has parameters.
        IFeature hasParamsPerm = new MethodHasParametersFeature();
        ((WeightedFeature)hasParamsPerm).setWeight(2);
        addFeature(hasParamsPerm,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SINK, Category.CWE079,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.CWE601, Category.CWE089, Category.NONE)));	
    }
    if(disable != 46) {
    	// Has a return type.
        IFeature hasReturnType = new MethodHasReturnTypeFeature();
        ((WeightedFeature)hasReturnType).setWeight(-11);
        addFeature(hasReturnType,
            new HashSet<>(Arrays.asList(Category.SOURCE,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.CWE601, Category.NONE)));
    }
    if(disable != 47) {
    	IFeature innerClassMethod = new MethodInnerClassFeature(cp, true);
        ((WeightedFeature)innerClassMethod).setWeight(0);
        addFeature(innerClassMethod,
            new HashSet<>(Arrays.asList(
                Category.SINK, Category.SOURCE, Category.NONE, Category.CWE078, Category.CWE079, Category.CWE089, Category.CWE306, Category.CWE862, Category.CWE863, Category.CWE601 )));
    }
    if(disable != 48) {
    	// Call to a method of class.
        IFeature methodInvocationClassNameSaniti = new MethodInvocationClassName(cp,
            "Saniti");
        ((WeightedFeature)methodInvocationClassNameSaniti).setWeight(13);
        addFeature(methodInvocationClassNameSaniti,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 49) {
    	IFeature methodInvocationClassNameRegex = new MethodInvocationClassName(cp,
                "regex");
        ((WeightedFeature)methodInvocationClassNameRegex).setWeight(0);
            addFeature(methodInvocationClassNameRegex,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 50) {
    	IFeature methodInvocationClassNameEscape = new MethodInvocationClassName(cp,
                "escap");
        ((WeightedFeature)methodInvocationClassNameEscape).setWeight(0);
            addFeature(methodInvocationClassNameEscape,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 51) {
    	IFeature methodInvocationClassNameIO = new MethodInvocationClassName(cp,
                ".io.");
        ((WeightedFeature)methodInvocationClassNameIO).setWeight(-3);
            addFeature(methodInvocationClassNameIO,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                    Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                    Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    }
    if(disable != 52) {
    	IFeature methodInvocationClassNameEncode = new MethodInvocationClassName(cp,
                "encod");
        ((WeightedFeature)methodInvocationClassNameEncode).setWeight(-1);
            addFeature(methodInvocationClassNameEncode,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 53) {
    	IFeature methodInvocationClassNameSQL = new MethodInvocationClassName(cp,
                "sql");
        ((WeightedFeature)methodInvocationClassNameSQL).setWeight(13);
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
        ((WeightedFeature)methodInvocationClassNameDB).setWeight(5);
    }
    if(disable != 55) {
    	IFeature methodInvocationClassNameWeb = new MethodInvocationClassName(cp,
                "web");
        ((WeightedFeature)methodInvocationClassNameWeb).setWeight(29);
            addFeature(methodInvocationClassNameWeb, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.CWE079, Category.NONE)));
    }
    if(disable != 56) {
    	IFeature methodInvocationClassNameNet = new MethodInvocationClassName(cp,
                ".net.");
        ((WeightedFeature)methodInvocationClassNameNet).setWeight(19);
            addFeature(methodInvocationClassNameNet, new HashSet<>(
                Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 57) {
    	IFeature methodInvocationClassNameLog = new MethodInvocationClassName(cp,
                "Log.");
        ((WeightedFeature)methodInvocationClassNameLog).setWeight(19);
            addFeature(methodInvocationClassNameLog,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 58) {
    	// Call to a method.
        IFeature methodInvocationNameEscape = new MethodInvocationName(cp, "escap");
        ((WeightedFeature)methodInvocationNameEscape).setWeight(17);
        addFeature(methodInvocationNameEscape,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
            }
    if(disable != 59) {
        IFeature methodInvocationNameReplace = new MethodInvocationName(cp,
                "replac");
        ((WeightedFeature)methodInvocationNameReplace).setWeight(10);
            addFeature(methodInvocationNameReplace, new HashSet<>(
                Arrays.asList(Category.SINK, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 60) {
    	IFeature methodInvocationNameStrip = new MethodInvocationName(cp, "strip");
        ((WeightedFeature)methodInvocationNameStrip).setWeight(18);
        addFeature(methodInvocationNameStrip,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 61) {
    	IFeature methodInvocationNameMatches = new MethodInvocationName(cp,
                "match");
        ((WeightedFeature)methodInvocationNameMatches).setWeight(-8);
            addFeature(methodInvocationNameMatches,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));   
    }
    if(disable != 62) {
    	IFeature methodInvocationNameEncode = new MethodInvocationName(cp, "encod");
        ((WeightedFeature)methodInvocationNameEncode).setWeight(20);
        addFeature(methodInvocationNameEncode,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 63) {
    	IFeature methodInvocationNameRegex = new MethodInvocationName(cp, "regex");
        ((WeightedFeature)methodInvocationNameRegex).setWeight(-8);
        addFeature(methodInvocationNameRegex,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));	
    }
    if(disable != 64) {
    	IFeature methodInvocationNameCheck = new MethodInvocationName(cp, "check");
        ((WeightedFeature)methodInvocationNameCheck).setWeight(13);
        addFeature(methodInvocationNameCheck,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));     
    }
    if(disable != 65) {
    	IFeature methodInvocationNameVerif = new MethodInvocationName(cp, "verif");
        ((WeightedFeature)methodInvocationNameVerif).setWeight(5);
        addFeature(methodInvocationNameVerif,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 66) {
    	IFeature methodInvocationNameAuthori = new MethodInvocationName(cp,
                "authori");
        ((WeightedFeature)methodInvocationNameAuthori).setWeight(13);
            addFeature(methodInvocationNameAuthori,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                    Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 67) {
    	IFeature methodInvocationNameAuthen = new MethodInvocationName(cp,
                "authen");
        ((WeightedFeature)methodInvocationNameAuthen).setWeight(29);
            addFeature(methodInvocationNameAuthen,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                    Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 68) {
    	IFeature methodInvocationNameLogin = new MethodInvocationName(cp, "login");
        ((WeightedFeature)methodInvocationNameLogin).setWeight(10);
        addFeature(methodInvocationNameLogin,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, 
                Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 69) {
    	IFeature methodInvocationNameLogout = new MethodInvocationName(cp,
                "logout");
        ((WeightedFeature)methodInvocationNameLogout).setWeight(-12);
            addFeature(methodInvocationNameLogout,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_LOW,
                    Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }    
    if(disable != 70) {
    	IFeature methodInvocationNameSecurity = new MethodInvocationName(cp,
                "security");
        ((WeightedFeature)methodInvocationNameSecurity).setWeight(-8);
            addFeature(methodInvocationNameSecurity,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                    Category.NONE)));
    }
    if(disable != 71) {
    	IFeature methodInvocationNameCredential = new MethodInvocationName(cp,
                "credential");
        ((WeightedFeature)methodInvocationNameCredential).setWeight(0);
            addFeature(methodInvocationNameCredential,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 72) {
    	IFeature methodInvocationNameBind = new MethodInvocationName(cp, "bind");
        ((WeightedFeature)methodInvocationNameBind).setWeight(5);
        addFeature(methodInvocationNameBind,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));    
    }
    if(disable != 73) {
    	IFeature methodInvocationNameConnect = new MethodInvocationName(cp,
                "connect");
        ((WeightedFeature)methodInvocationNameConnect).setWeight(-8);
            addFeature(methodInvocationNameConnect,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                    Category.NONE)));
    }
    if(disable != 74) {
    	IFeature methodInvocationNameGet = new MethodInvocationName(cp, "get");
        ((WeightedFeature)methodInvocationNameGet).setWeight(6);
        addFeature(methodInvocationNameGet,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 75) {
    	IFeature methodInvocationNameRead = new MethodInvocationName(cp, "read");
        ((WeightedFeature)methodInvocationNameRead).setWeight(-3);
        addFeature(methodInvocationNameRead,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 76) {
    	IFeature methodInvocationNameDecode = new MethodInvocationName(cp, "decod");
        ((WeightedFeature)methodInvocationNameDecode).setWeight(-1);
        addFeature(methodInvocationNameDecode,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));  
    }
    if(disable != 77) {
    	IFeature methodInvocationNameUnescape = new MethodInvocationName(cp,
                "unescap");
        ((WeightedFeature)methodInvocationNameUnescape).setWeight(17);
            addFeature(methodInvocationNameUnescape,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 78) {
    	IFeature methodInvocationNameLoad = new MethodInvocationName(cp, "load");
        ((WeightedFeature)methodInvocationNameLoad).setWeight(9);
        addFeature(methodInvocationNameLoad,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 79) {
    	IFeature methodInvocationNameRequest = new MethodInvocationName(cp,
                "request");
        ((WeightedFeature)methodInvocationNameRequest).setWeight(14);
            addFeature(methodInvocationNameRequest,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 80) {
    	IFeature methodInvocationNameCreate = new MethodInvocationName(cp, "creat");
        ((WeightedFeature)methodInvocationNameCreate).setWeight(13);
        addFeature(methodInvocationNameCreate,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE))); 
    }
    if(disable != 81) {
    	IFeature methodInvocationNameOutput = new MethodInvocationName(cp,
                "output");
        ((WeightedFeature)methodInvocationNameOutput).setWeight(0);
            addFeature(methodInvocationNameOutput,
                new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 82) {
    	IFeature methodInvocationNameWrit = new MethodInvocationName(cp, "writ");
        ((WeightedFeature)methodInvocationNameWrit).setWeight(13);
        addFeature(methodInvocationNameWrit,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 83) {
    	IFeature methodInvocationNameSet = new MethodInvocationName(cp, "set");
        ((WeightedFeature)methodInvocationNameSet).setWeight(8);
        addFeature(methodInvocationNameSet,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 84) {
    	IFeature methodInvocationNameUpdat = new MethodInvocationName(cp, "updat");
        ((WeightedFeature)methodInvocationNameUpdat).setWeight(-8);
        addFeature(methodInvocationNameUpdat,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 85) {
    	IFeature methodInvocationNameSend = new MethodInvocationName(cp, "send");
        ((WeightedFeature)methodInvocationNameSend).setWeight(13);
        addFeature(methodInvocationNameSend,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 86) {
    	IFeature methodInvocationNameHandl = new MethodInvocationName(cp, "handl");
        ((WeightedFeature)methodInvocationNameHandl).setWeight(-7);
        addFeature(methodInvocationNameHandl,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 87) {
    	IFeature methodInvocationNamePut = new MethodInvocationName(cp, "put");
        ((WeightedFeature)methodInvocationNamePut).setWeight(-11);
        addFeature(methodInvocationNamePut,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 88) {
    	IFeature methodInvocationNameAdd = new MethodInvocationName(cp, "log");
        ((WeightedFeature)methodInvocationNameAdd).setWeight(4);
        addFeature(methodInvocationNameAdd,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 89) {
    	IFeature methodInvocationNameRun = new MethodInvocationName(cp, "run");

        ((WeightedFeature)methodInvocationNameRun).setWeight(0);
        addFeature(methodInvocationNameRun,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 90) {
    	IFeature methodInvocationNameExecut = new MethodInvocationName(cp,
                "execut");
    	((WeightedFeature)methodInvocationNameExecut).setWeight(17);
            addFeature(methodInvocationNameExecut,
                new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 91) {
    	IFeature methodInvocationNameDump = new MethodInvocationName(cp, "dump");
        ((WeightedFeature)methodInvocationNameDump).setWeight(1);
        addFeature(methodInvocationNameDump,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 92) {
    	IFeature methodInvocationNamePrint = new MethodInvocationName(cp, "print");
        ((WeightedFeature)methodInvocationNamePrint).setWeight(10);
        addFeature(methodInvocationNamePrint,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 93) {
    	IFeature methodInvocationNamePars = new MethodInvocationName(cp, "pars");
        ((WeightedFeature)methodInvocationNamePars).setWeight(12);
        addFeature(methodInvocationNamePars,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 94) {
            IFeature methodInvocationNameMakedb = new MethodInvocationName(cp,
                "makedb");
        ((WeightedFeature)methodInvocationNameMakedb).setWeight(-8);
            addFeature(methodInvocationNameMakedb,
                new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    }
    if(disable != 95) {
    	IFeature methodInvocationNameExecute = new MethodInvocationName(cp,
                "execute");
        ((WeightedFeature)methodInvocationNameExecute).setWeight(12);
            addFeature(methodInvocationNameExecute,
                new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    }
    if(disable != 96) {
    	IFeature methodInvocationNameSaniti = new MethodInvocationName(cp,
                "saniti");
        ((WeightedFeature)methodInvocationNameSaniti).setWeight(-10);
            addFeature(methodInvocationNameSaniti,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 97) {
    	// Constructor.
        IFeature methodIsConstructor = new MethodIsConstructor();
        ((WeightedFeature)methodIsConstructor).setWeight(0);
        addFeature(methodIsConstructor,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                Category.SANITIZER, Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_NEUTRAL, Category.CWE078, Category.CWE862,
                Category.CWE863, Category.CWE089, Category.NONE)));
    }
    if(disable != 98) {
    	IFeature realSetter = new MethodIsRealSetterFeature(cp);
        ((WeightedFeature)realSetter).setWeight(0);
        addFeature(realSetter,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 99) {
    	  // Method modifier.
        IFeature isStaticMethod = new MethodModifierFeature(cp, Modifier.STATIC);
        ((WeightedFeature)isStaticMethod).setWeight(3);
        addFeature(isStaticMethod,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 100) {
    	IFeature isPublicMethod = new MethodModifierFeature(cp, Modifier.PUBLIC);
        ((WeightedFeature)isPublicMethod).setWeight(10);
        addFeature(isPublicMethod,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 101) {
    	IFeature isPrivateMethod = new MethodModifierFeature(cp, Modifier.PRIVATE);
        ((WeightedFeature)isPrivateMethod).setWeight(10);
        addFeature(isPrivateMethod, new HashSet<>(
            Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 102) {
    	IFeature isFinalMethod = new MethodModifierFeature(cp, Modifier.FINAL);
        ((WeightedFeature)isFinalMethod).setWeight(4);
        addFeature(isFinalMethod, new HashSet<>(Arrays.asList(Category.SOURCE,
            Category.SINK, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 103) {
    	// Method name.
        IFeature methodNameStartsWithGet = new MethodNameStartsWithFeature("get");
        ((WeightedFeature)methodNameStartsWithGet).setWeight(-3);
        addFeature(methodNameStartsWithGet,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SANITIZER,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.NONE)));    
    }
    if(disable != 104) {
    	IFeature methodNameStartsWithSet = new MethodNameStartsWithFeature("set");
        ((WeightedFeature)methodNameStartsWithSet).setWeight(15);
        addFeature(methodNameStartsWithSet,
            new HashSet<>(Arrays.asList(Category.SINK, Category.SANITIZER,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.CWE079, Category.NONE)));
    }
    if(disable != 105) {
    	IFeature methodNameStartsWithPut = new MethodNameStartsWithFeature("put");
        ((WeightedFeature)methodNameStartsWithPut).setWeight(12);
        addFeature(methodNameStartsWithPut,
            new HashSet<>(Arrays.asList(Category.SANITIZER,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    }
    if(disable != 106) {
    	IFeature methodNameStartsWithHas = new MethodNameStartsWithFeature("has");
        ((WeightedFeature)methodNameStartsWithHas).setWeight(-8);
        addFeature(methodNameStartsWithHas,
            new HashSet<>(Arrays.asList(Category.SANITIZER,
                Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
                Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    }
    if(disable != 107) {
    	IFeature methodNameStartsWithIs = new MethodNameStartsWithFeature("is");
        ((WeightedFeature)methodNameStartsWithIs).setWeight(21);
        addFeature(methodNameStartsWithIs,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.NONE)));
    }
    if(disable != 108) {
    	IFeature methodNameStartsOpen = new MethodNameStartsWithFeature("open");
        ((WeightedFeature)methodNameStartsOpen).setWeight(2);
        addFeature(methodNameStartsOpen,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, 
                Category.NONE)));
    }
    if(disable != 109) {
    	IFeature methodNameStartsClose = new MethodNameStartsWithFeature("close");
        ((WeightedFeature)methodNameStartsClose).setWeight(0);
        addFeature(methodNameStartsClose,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                 Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 110) {
    	IFeature methodNameStartsCreate = new MethodNameStartsWithFeature("create");
        ((WeightedFeature)methodNameStartsCreate).setWeight(-4);
        addFeature(methodNameStartsCreate,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, 
                Category.NONE)));
    }
    if(disable != 111) {
    	IFeature methodNameStartsDelete = new MethodNameStartsWithFeature("delete");
        ((WeightedFeature)methodNameStartsDelete).setWeight(22);
        addFeature(methodNameStartsDelete,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                 Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 112) {
    	  // Method Name Equals. 
        IFeature nameEqualsLog = new MethodNameEqualsFeature("log");
        ((WeightedFeature)nameEqualsLog).setWeight(-8);
        addFeature(nameEqualsLog,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));    
    }
    if(disable != 113) {
    	IFeature nameEqualsSetHeader = new MethodNameEqualsFeature("setHeader");
        ((WeightedFeature)nameEqualsSetHeader).setWeight(9);
        addFeature(nameEqualsSetHeader,
            new HashSet<>(Arrays.asList(Category.SINK, Category.CWE079, Category.NONE)));
    }
    if(disable != 114) {
    	IFeature nameEqualsSendRedirect = new MethodNameEqualsFeature("sendRedirect");
        ((WeightedFeature)nameEqualsSendRedirect).setWeight(-8);
        addFeature(nameEqualsSendRedirect,
            new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 115) {
    	// Method Contains Name. 
        IFeature methodNameContainsSaniti = new MethodNameContainsFeature("saniti");
        ((WeightedFeature)methodNameContainsSaniti).setWeight(-3);
        addFeature(methodNameContainsSaniti,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.CWE078, Category.CWE079, Category.CWE089, Category.NONE)));
        
    }
    if(disable != 116) {
    	IFeature methodNameContainsEscape = new MethodNameContainsFeature("escape",
                "unescape");
        ((WeightedFeature)methodNameContainsEscape).setWeight(51);
            addFeature(methodNameContainsEscape,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 117) {
    	IFeature methodNameContainsUnescape = new MethodNameContainsFeature(
    	        "unescape");
        ((WeightedFeature)methodNameContainsUnescape).setWeight(-3);
    	    addFeature(methodNameContainsUnescape,
    	        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 118) {
    	IFeature methodNameContainsReplace = new MethodNameContainsFeature(
    	        "replac");
        ((WeightedFeature)methodNameContainsReplace).setWeight(-3);
    	    addFeature(methodNameContainsReplace, new HashSet<>(
    	        Arrays.asList(Category.SINK, Category.SANITIZER, Category.NONE)));
    }
    if(disable != 119) {
    	IFeature methodNameContainsStrip = new MethodNameContainsFeature("strip");
        ((WeightedFeature)methodNameContainsStrip).setWeight(65);
        addFeature(methodNameContainsStrip,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));  
    }
    if(disable != 120) {
    	IFeature methodNameContainsEncode = new MethodNameContainsFeature("encod",
                "encoding");
        ((WeightedFeature)methodNameContainsEncode).setWeight(21);
            addFeature(methodNameContainsEncode,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 121) {
    	IFeature methodNameContainsRegex = new MethodNameContainsFeature("regex");
        ((WeightedFeature)methodNameContainsRegex).setWeight(25);
        addFeature(methodNameContainsRegex,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 122) {
        IFeature methodNameContainsAuthen = new MethodNameContainsFeature("authen");
        ((WeightedFeature)methodNameContainsAuthen).setWeight(19);
        addFeature(methodNameContainsAuthen,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.CWE306, Category.NONE)));
    }
    if(disable != 123) {
    	IFeature methodNameContainsCheck = new MethodNameContainsFeature("check");
        ((WeightedFeature)methodNameContainsCheck).setWeight(0);
        addFeature(methodNameContainsCheck,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
    if(disable != 124) {
    	IFeature methodNameContainsVerif = new MethodNameContainsFeature("verif");
        ((WeightedFeature)methodNameContainsVerif).setWeight(-8);
        addFeature(methodNameContainsVerif,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE, Category.SANITIZER)));
    }
    if(disable != 125) {
    	IFeature methodNameContainsPrivilege = new MethodNameContainsFeature(
                "privilege");
        ((WeightedFeature)methodNameContainsPrivilege).setWeight(9);
            addFeature(methodNameContainsPrivilege,
                new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                    Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                    Category.NONE)));
    }
    if(disable != 126) {
    	IFeature methodNameContainsLogin = new MethodNameContainsFeature("login");
        ((WeightedFeature)methodNameContainsLogin).setWeight(9);
        addFeature(methodNameContainsLogin,
            new HashSet<>(Arrays.asList(
                Category.AUTHENTICATION_TO_HIGH, 
                Category.CWE306, Category.NONE)));
        
    }
    if(disable != 127) {
    	IFeature methodNameContainsNotLoginPage = new MethodNameContainsFeature("",
                "loginpage");
        ((WeightedFeature)methodNameContainsNotLoginPage).setWeight(43);
            addFeature(methodNameContainsNotLoginPage,
                new HashSet<>(Arrays.asList(Category.CWE306, Category.CWE862,
                    Category.CWE863, Category.NONE)));
    }
    if(disable != 128) {
    	IFeature methodNameContainsLogout = new MethodNameContainsFeature("logout");
        ((WeightedFeature)methodNameContainsLogout).setWeight(-8);
        addFeature(methodNameContainsLogout,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_LOW,
                Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 129) {
    	IFeature methodNameContainsConnect = new MethodNameContainsFeature(
                "connect", "disconnect");
        ((WeightedFeature)methodNameContainsConnect).setWeight(31);
            addFeature(methodNameContainsConnect,
                new HashSet<>(Arrays.asList(
                    Category.AUTHENTICATION_TO_HIGH, 
                    Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 130) {
    	IFeature methodNameContainsDisconnect = new MethodNameContainsFeature(
                "disconnect");
        ((WeightedFeature)methodNameContainsDisconnect).setWeight(31);
            addFeature(methodNameContainsDisconnect,
                new HashSet<>(Arrays.asList( Category.AUTHENTICATION_TO_LOW,
                    Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 131) {
    	IFeature methodNameContainsBind = new MethodNameContainsFeature("bind",
    	        "unbind");
        ((WeightedFeature)methodNameContainsBind).setWeight(18);
    	    addFeature(methodNameContainsBind,
    	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, 
    	            Category.NONE)));
    }
    if(disable != 132) {
    	IFeature methodNameContainsUnbind = new MethodNameContainsFeature("unbind");
        ((WeightedFeature)methodNameContainsUnbind).setWeight(4);
	    addFeature(methodNameContainsUnbind,
	        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
	            Category.AUTHENTICATION_TO_LOW,
	            Category.NONE)));
    }
    if(disable != 133) {
    	IFeature nameContaisRead = new MethodNameContainsFeature("read", "thread");
        ((WeightedFeature)nameContaisRead).setWeight(-8);
	    addFeature(nameContaisRead,
	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.CWE601, Category.NONE)));
    }
    if(disable != 134) {
    	IFeature nameContainsLoad = new MethodNameContainsFeature("load",
    	        "payload");
        ((WeightedFeature)nameContainsLoad).setWeight(18);
    	    addFeature(nameContainsLoad,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 135) {
    	IFeature nameContainsRequest = new MethodNameContainsFeature("request");
        ((WeightedFeature)nameContainsRequest).setWeight(9);
        addFeature(nameContainsRequest,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
        
    }
    if(disable != 136) {
    	IFeature nameContainsCreate = new MethodNameContainsFeature("creat");
        ((WeightedFeature)nameContainsCreate).setWeight(6);
        addFeature(nameContainsCreate,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 137) {
    	IFeature nameContainsDecod = new MethodNameContainsFeature("decod");
        ((WeightedFeature)nameContainsDecod).setWeight(15);
        addFeature(nameContainsDecod,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 138) {
    	IFeature nameContainsUnescap = new MethodNameContainsFeature("unescap");
        ((WeightedFeature)nameContainsUnescap).setWeight(-3);
        addFeature(nameContainsUnescap,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 139) {
    	IFeature nameContainsPars = new MethodNameContainsFeature("pars");
        ((WeightedFeature)nameContainsPars).setWeight(36);
        addFeature(nameContainsPars, new HashSet<>(
            Arrays.asList(Category.SINK, Category.SOURCE, Category.NONE)));
    }
    if(disable != 140) {
    	IFeature nameContainsStream = new MethodNameContainsFeature("stream");
        ((WeightedFeature)nameContainsStream).setWeight(17);
        addFeature(nameContainsStream, new HashSet<>(
            Arrays.asList(Category.SINK, Category.SOURCE, Category.NONE)));
    }
    if(disable != 141) {
    	IFeature nameContainsRetriev = new MethodNameContainsFeature("retriev");
        ((WeightedFeature)nameContainsRetriev).setWeight(-8);
        addFeature(nameContainsRetriev,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 142) {
    	IFeature nameContainsObject = new MethodNameContainsFeature("Object");
        ((WeightedFeature)nameContainsObject).setWeight(-8);
        addFeature(nameContainsObject,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 143) {
    	 IFeature nameContainsName = new MethodNameContainsFeature("Name");
        ((WeightedFeature)nameContainsName).setWeight(1);
         addFeature(nameContainsName,
             new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 144) {
    	IFeature nameContainsWrit = new MethodNameContainsFeature("writ");
        ((WeightedFeature)nameContainsWrit).setWeight(0);
        addFeature(nameContainsWrit,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 145) {
    	IFeature nameContainsUpdat = new MethodNameContainsFeature("updat");
        ((WeightedFeature)nameContainsUpdat).setWeight(1);
        addFeature(nameContainsUpdat,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 146) {
    	IFeature nameContainsSend = new MethodNameContainsFeature("send");
        ((WeightedFeature)nameContainsSend).setWeight(-8);
        addFeature(nameContainsSend,
            new HashSet<>(Arrays.asList(Category.SINK, Category.CWE601, Category.NONE)));
    }
    if(disable != 147) {
    	IFeature nameContainsHandl = new MethodNameContainsFeature("handl");
        ((WeightedFeature)nameContainsHandl).setWeight(16);
        addFeature(nameContainsHandl,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 148) {
    	IFeature nameContainsLog = new MethodNameContainsFeature("log");
        ((WeightedFeature)nameContainsLog).setWeight(-5);
        addFeature(nameContainsLog,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 149) {
    	IFeature nameContainsRun = new MethodNameContainsFeature("run");
        ((WeightedFeature)nameContainsRun).setWeight(0);
        addFeature(nameContainsRun, new HashSet<>(
            Arrays.asList(Category.SINK, Category.CWE078, Category.NONE)));
    }
    if(disable != 150) {
    	IFeature nameContainsExecut = new MethodNameContainsFeature("execut");
        ((WeightedFeature)nameContainsExecut).setWeight(-8);
        addFeature(nameContainsExecut,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 151) {
    	IFeature nameContainsExec = new MethodNameContainsFeature("exec");
        ((WeightedFeature)nameContainsExec).setWeight(34);
        addFeature(nameContainsExec,
            new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    }
    if(disable != 152) {
    	IFeature nameContainsCompile = new MethodNameContainsFeature("compile");
        ((WeightedFeature)nameContainsCompile).setWeight(17);
        addFeature(nameContainsCompile,
            new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    }
    if(disable != 153) {
    	IFeature nameContainsDump = new MethodNameContainsFeature("dump");
        ((WeightedFeature)nameContainsDump).setWeight(9);
        addFeature(nameContainsDump,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 154) {
    	IFeature nameContainsPrint = new MethodNameContainsFeature("print");
        ((WeightedFeature)nameContainsPrint).setWeight(9);
        addFeature(nameContainsPrint,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 155) {
    	IFeature nameContainsExecute = new MethodNameContainsFeature("execute");
        ((WeightedFeature)nameContainsExecute).setWeight(8);
        addFeature(nameContainsExecute,
            new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    }
    if(disable != 156) {
    	IFeature nameContainsQuery = new MethodNameContainsFeature("query");
        ((WeightedFeature)nameContainsQuery).setWeight(17);
        addFeature(nameContainsQuery,
            new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    }
    if(disable != 157) {
    	IFeature nameContainsRole = new MethodNameContainsFeature("role");
        ((WeightedFeature)nameContainsRole).setWeight(-8);
        addFeature(nameContainsRole, new HashSet<>(
            Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE)));	
    }
    if(disable != 158) {
    	IFeature nameContainsAuthori = new MethodNameContainsFeature("authori");
        ((WeightedFeature)nameContainsAuthori).setWeight(0);
        addFeature(nameContainsAuthori, new HashSet<>(
            Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE)));
    }
    if(disable != 159) {
    	IFeature nameContainsRedirect = new MethodNameContainsFeature("redirect");
        ((WeightedFeature)nameContainsRedirect).setWeight(17);
        addFeature(nameContainsRedirect,
            new HashSet<>(Arrays.asList(Category.SINK, Category.CWE601, Category.NONE)));
    }
    if(disable != 160) {
    	IFeature methodNameContainsGetParameter = new MethodNameContainsFeature("getParameter");
        ((WeightedFeature)methodNameContainsGetParameter).setWeight(-8);
        addFeature(methodNameContainsGetParameter,
            new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 161) {
    	IFeature returnsConstant = new MethodReturnsConstantFeature(cp);
        ((WeightedFeature)returnsConstant).setWeight(-4);
        addFeature(returnsConstant,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 162) {
    	// Parameter types (ParameterContainsTypeOrNameFeature).
        IFeature parameterOfTypeString = new ParameterContainsTypeOrNameFeature(
            "java.lang.String");
        ((WeightedFeature)parameterOfTypeString).setWeight(32);
        addFeature(parameterOfTypeString,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
                Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.CWE089, Category.CWE306,
                Category.CWE078, Category.CWE862, Category.CWE863, Category.CWE079, Category.NONE)));
    }
    if(disable != 163) {
    	IFeature parameterOfTypeCharArray = new ParameterContainsTypeOrNameFeature(
    	        "char[]");
        ((WeightedFeature)parameterOfTypeCharArray).setWeight(12);
    	    addFeature(parameterOfTypeCharArray,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
    	            Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.NONE)));
    }
    if(disable != 164) {
    	IFeature parameterOfTypeByteArray = new ParameterContainsTypeOrNameFeature(
    	        "byte[]");
        ((WeightedFeature)parameterOfTypeByteArray).setWeight(9);
    	    addFeature(parameterOfTypeByteArray,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
    	            Category.SANITIZER, Category.NONE)));
    }
    if(disable != 165) {
    	IFeature parameterOfTypeCharSequence = new ParameterContainsTypeOrNameFeature(
    	        "java.lang.CharSequence");
        ((WeightedFeature)parameterOfTypeCharSequence).setWeight(13);
    	    addFeature(parameterOfTypeCharSequence,
    	        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
    	            Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
    	            Category.AUTHENTICATION_TO_HIGH, Category.NONE)));    
    }
    if(disable != 166) {
    	IFeature parameterOfTypeStringBuilder = new ParameterContainsTypeOrNameFeature(
    	        "java.lang.StringBuilder");
        ((WeightedFeature)parameterOfTypeStringBuilder).setWeight(-8);
    	    addFeature(parameterOfTypeStringBuilder, new HashSet<>(
    	        Arrays.asList(Category.SANITIZER, Category.SINK, Category.NONE)));
    }
    if(disable != 167) {
    	IFeature parameterOfTypeIo = new ParameterContainsTypeOrNameFeature(".io.");
        ((WeightedFeature)parameterOfTypeIo).setWeight(-3);
	    addFeature(parameterOfTypeIo, new HashSet<>(
	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 168) {
    	IFeature parameterOfTypeWeb = new ParameterContainsTypeOrNameFeature("web");
        ((WeightedFeature)parameterOfTypeWeb).setWeight(22);
	    addFeature(parameterOfTypeWeb, new HashSet<>(
	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 169) {
    	IFeature parameterOfTypeSql = new ParameterContainsTypeOrNameFeature("sql");
        ((WeightedFeature)parameterOfTypeSql).setWeight(2);
	    addFeature(parameterOfTypeSql, new HashSet<>(Arrays.asList(Category.SOURCE,
	        Category.SINK, Category.CWE089, Category.NONE)));
    }
    if(disable != 170) {
    	 IFeature parameterOfTypeDb = new ParameterContainsTypeOrNameFeature("db");
        ((WeightedFeature)parameterOfTypeDb).setWeight(12);
 	    addFeature(parameterOfTypeDb, new HashSet<>(
 	        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    }
    if(disable != 171) {
    	IFeature parameterOfTypeCredential = new ParameterContainsTypeOrNameFeature(
    	        "credential");
        ((WeightedFeature)parameterOfTypeCredential).setWeight(137);
    	    addFeature(parameterOfTypeCredential,
    	        new HashSet<>(Arrays.asList(Category.CWE306, Category.CWE862,
    	            Category.CWE863, Category.CWE078, Category.NONE)));    
    }
    if(disable != 172) {
    	IFeature parameterOfTypeUrl = new ParameterContainsTypeOrNameFeature("url");
        ((WeightedFeature)parameterOfTypeUrl).setWeight(21);
	    addFeature(parameterOfTypeUrl, new HashSet<>(
	        Arrays.asList(Category.CWE601, Category.NONE)));
    }
    if(disable != 173) {
    	// Parameter flows to return value.
        IFeature parameterFlowsToReturn = new ParameterFlowsToReturn(cp);
        ((WeightedFeature)parameterFlowsToReturn).setWeight(-7);
        addFeature(parameterFlowsToReturn,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    }
    if(disable != 174) {
    	// Parameter to sink.
        
        IFeature paramToSinkSetWrit = new ParameterToSinkFeature(cp, "writ");
        ((WeightedFeature)paramToSinkSetWrit).setWeight(-4);
        addFeature(paramToSinkSetWrit,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 175) {
    	IFeature paramToSinkSet = new ParameterToSinkFeature(cp, "set");
        ((WeightedFeature)paramToSinkSet).setWeight(8);
        addFeature(paramToSinkSet,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 176) {
    	IFeature paramToSinkUpdat = new ParameterToSinkFeature(cp, "updat");
        ((WeightedFeature)paramToSinkUpdat).setWeight(9);
        addFeature(paramToSinkUpdat,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 177) {
    	IFeature paramToSinkSend = new ParameterToSinkFeature(cp, "send");
        ((WeightedFeature)paramToSinkSend).setWeight(0);
        addFeature(paramToSinkSend,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 178) {
    	IFeature paramToSinkSetHandl = new ParameterToSinkFeature(cp, "handl");
        ((WeightedFeature)paramToSinkSetHandl).setWeight(5);
        addFeature(paramToSinkSetHandl,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 179) {
    	IFeature paramToSinkSetPut = new ParameterToSinkFeature(cp, "put");
        ((WeightedFeature)paramToSinkSetPut).setWeight(5);
        addFeature(paramToSinkSetPut,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
        
    }
   if(disable != 180) {
	   IFeature paramToSinkSetAdd = new ParameterToSinkFeature(cp, "log");
       ((WeightedFeature)paramToSinkSetAdd).setWeight(-11);
       addFeature(paramToSinkSetAdd,
           new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 181) {
    	IFeature paramToSinkSetRun = new ParameterToSinkFeature(cp, "run");
        ((WeightedFeature)paramToSinkSetRun).setWeight(5);
        addFeature(paramToSinkSetRun,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 182) {
    	IFeature paramToSinkSetExecut = new ParameterToSinkFeature(cp, "execut");
        ((WeightedFeature)paramToSinkSetExecut).setWeight(-8);
        addFeature(paramToSinkSetExecut,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 183) {
    	IFeature paramToSinkDump = new ParameterToSinkFeature(cp, "dump");
        ((WeightedFeature)paramToSinkDump).setWeight(0);
        addFeature(paramToSinkDump,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 184) {
    	IFeature paramToSinkPrint = new ParameterToSinkFeature(cp, "print");
        ((WeightedFeature)paramToSinkPrint).setWeight(9);
        addFeature(paramToSinkPrint,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 185) {
    	IFeature paramToSinkPars = new ParameterToSinkFeature(cp, "pars");
        ((WeightedFeature)paramToSinkPars).setWeight(5);
        addFeature(paramToSinkPars,
            new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
   if(disable != 186) {
	   IFeature paramToSinkStream = new ParameterToSinkFeature(cp, "stream");
       ((WeightedFeature)paramToSinkStream).setWeight(5);
       addFeature(paramToSinkStream,
           new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    }
    if(disable != 187) {
    	// Parameter Type matches return 
        IFeature paramTypeMathcesReturn = new ParamTypeMatchesReturnType(cp);
        ((WeightedFeature)paramTypeMathcesReturn).setWeight(-5);
        addFeature(paramTypeMathcesReturn,
            new HashSet<>(Arrays.asList(
                Category.SANITIZER, Category.CWE078, Category.CWE079, Category.CWE862,
                Category.CWE863, Category.CWE089, Category.NONE)));
    }
    if(disable != 188) {
    	// ReturnTypeContainsNameFeature 
        IFeature returnContainsDocument = new ReturnTypeContainsNameFeature(cp, "Document");
        ((WeightedFeature)returnContainsDocument).setWeight(5);
        addFeature(returnContainsDocument,
            new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079, Category.NONE)));
    }
    if(disable != 189) {
    	IFeature returnContainsNode = new ReturnTypeContainsNameFeature(cp, "Node");
        ((WeightedFeature)returnContainsNode).setWeight(11);
        addFeature(returnContainsNode,
            new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079, Category.NONE)));
        
    }
    if(disable != 190) {
    	IFeature returnContainsUser = new ReturnTypeContainsNameFeature(cp, "User");
        ((WeightedFeature)returnContainsUser).setWeight(-17);
        addFeature(returnContainsUser,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE))); 
    }
    if(disable != 191) {
    	IFeature returnContainsCredential= new ReturnTypeContainsNameFeature(cp, "Credential");
        ((WeightedFeature)returnContainsCredential).setWeight(14);
        addFeature(returnContainsCredential,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    }
   if(disable != 192) {
	   IFeature returnContainsServlet = new ReturnTypeContainsNameFeature(cp, "Servlet");
       ((WeightedFeature)returnContainsServlet).setWeight(-11);
       addFeature(returnContainsServlet,
           new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079,Category.CWE078, Category.NONE))); 
    }
    if(disable != 193) {
    	IFeature returnContainsRequest = new ReturnTypeContainsNameFeature(cp, "Request");
        ((WeightedFeature)returnContainsRequest).setWeight(-8);
        addFeature(returnContainsRequest,
            new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079, Category.NONE)));
    }
    if(disable != 194) {
    	// Return types (ReturnTypeFeature).
        IFeature byteArrayReturnType = new ReturnTypeFeature(cp, "byte[]");
        ((WeightedFeature)byteArrayReturnType).setWeight(-4);
        addFeature(byteArrayReturnType,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SOURCE, Category.NONE)));   
    }
    if(disable != 195) {
    	IFeature stringReturnType = new ReturnTypeFeature(cp, "java.lang.String");
        ((WeightedFeature)stringReturnType).setWeight(27);
        addFeature(stringReturnType,
            new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SOURCE, Category.CWE079, Category.CWE078, Category.CWE089, Category.NONE)));
    }
    if(disable != 196) {
    	IFeature charSequenceReturnType = new ReturnTypeFeature(cp,
                "java.lang.CharSequence");
        ((WeightedFeature)charSequenceReturnType).setWeight(-8);
            addFeature(charSequenceReturnType,
                new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SOURCE, Category.NONE)));
    }
    if(disable != 197) {
    	IFeature booleanReturnType = new ReturnTypeFeature(cp, "boolean");
        ((WeightedFeature)booleanReturnType).setWeight(-8);
        addFeature(booleanReturnType,
            new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
                Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
                Category.NONE)));
    }
   if(disable != 198) {
	   IFeature resultsetReturnType = new ReturnTypeFeature(cp, "java.sql.ResultSet");
       ((WeightedFeature)resultsetReturnType).setWeight(21);
       addFeature(resultsetReturnType,
           new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.CWE089)));
    }
    if(disable != 199) {
    	 // Source to return.
        IFeature sourceGetToReturn = new SourceToReturnFeature(cp, "get");
        ((WeightedFeature)sourceGetToReturn).setWeight(0);
        addFeature(sourceGetToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 200) {
    	IFeature sourceReadToReturn = new SourceToReturnFeature(cp, "read");
        ((WeightedFeature)sourceReadToReturn).setWeight(13);
        addFeature(sourceReadToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 201) {
    	IFeature sourcDecodeToReturn = new SourceToReturnFeature(cp, "decode");
        ((WeightedFeature)sourcDecodeToReturn).setWeight(21);
        addFeature(sourcDecodeToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 202) {
    	IFeature sourceUnescapeToReturn = new SourceToReturnFeature(cp, "unescape");
        ((WeightedFeature)sourceUnescapeToReturn).setWeight(-8);
        addFeature(sourceUnescapeToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 203) {
    	IFeature sourceLoadToReturn = new SourceToReturnFeature(cp, "load");
        ((WeightedFeature)sourceLoadToReturn).setWeight(13);
        addFeature(sourceLoadToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 204) {
    	IFeature sourceRequestToReturn = new SourceToReturnFeature(cp, "request");
        ((WeightedFeature)sourceRequestToReturn).setWeight(8);
        addFeature(sourceRequestToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 205) {
    	IFeature sourceCreateToReturn = new SourceToReturnFeature(cp, "create");
        ((WeightedFeature)sourceCreateToReturn).setWeight(-8);
        addFeature(sourceCreateToReturn,
            new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    }
    if(disable != 206) {
    	IFeature voidOn = new VoidOnMethodFeature();
        ((WeightedFeature)voidOn).setWeight(5);
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
