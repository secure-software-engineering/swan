package de.fraunhofer.iem.mois;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.fraunhofer.iem.mois.data.Category;
import de.fraunhofer.iem.mois.features.IsImplicitMethod;
import de.fraunhofer.iem.mois.features.MethodAnonymousClassFeature;
import de.fraunhofer.iem.mois.features.MethodClassContainsNameFeature;
import de.fraunhofer.iem.mois.features.MethodClassEndsWithNameFeature;
import de.fraunhofer.iem.mois.features.MethodClassModifierFeature;
import de.fraunhofer.iem.mois.features.MethodHasParametersFeature;
import de.fraunhofer.iem.mois.features.MethodHasReturnTypeFeature;
import de.fraunhofer.iem.mois.features.MethodInnerClassFeature;
import de.fraunhofer.iem.mois.features.MethodInvocationClassName;
import de.fraunhofer.iem.mois.features.MethodInvocationName;
import de.fraunhofer.iem.mois.features.MethodIsConstructor;
import de.fraunhofer.iem.mois.features.MethodIsRealSetterFeature;
import de.fraunhofer.iem.mois.features.MethodModifierFeature;
import de.fraunhofer.iem.mois.features.MethodNameContainsFeature;
import de.fraunhofer.iem.mois.features.MethodNameEqualsFeature;
import de.fraunhofer.iem.mois.features.MethodNameStartsWithFeature;
import de.fraunhofer.iem.mois.features.MethodReturnsConstantFeature;
import de.fraunhofer.iem.mois.features.ParamTypeMatchesReturnType;
import de.fraunhofer.iem.mois.features.ParameterContainsTypeOrNameFeature;
import de.fraunhofer.iem.mois.features.ParameterFlowsToReturn;
import de.fraunhofer.iem.mois.features.ParameterToSinkFeature;
import de.fraunhofer.iem.mois.features.ReturnTypeContainsNameFeature;
import de.fraunhofer.iem.mois.features.ReturnTypeFeature;
import de.fraunhofer.iem.mois.features.SourceToReturnFeature;
import de.fraunhofer.iem.mois.features.VoidOnMethodFeature;
import de.fraunhofer.iem.mois.features.MethodClassModifierFeature.ClassModifier;
import de.fraunhofer.iem.mois.features.MethodModifierFeature.Modifier;

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
   * @return The generated feature set.
   */
  public void initializeFeatures() {
    featuresMap = new HashMap<Category, Set<IFeature>>();
    for (Category category : Category.values())
      featuresMap.put(category, new HashSet<IFeature>());

    // Has parameters.
    IFeature hasParamsPerm = new MethodHasParametersFeature();
    addFeature(hasParamsPerm,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.SINK, Category.CWE079,
            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_TO_LOW, Category.CWE601, Category.CWE089, Category.NONE)));

    // Has a return type.
    IFeature hasReturnType = new MethodHasReturnTypeFeature();
    addFeature(hasReturnType,
        new HashSet<>(Arrays.asList(Category.SOURCE,
            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_TO_LOW, Category.CWE601, Category.NONE)));

    // Parameter types (ParameterContainsTypeOrNameFeature).
    IFeature parameterOfTypeString = new ParameterContainsTypeOrNameFeature(
        "java.lang.String");
    addFeature(parameterOfTypeString,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.CWE089, Category.CWE306,
            Category.CWE078, Category.CWE862, Category.CWE863, Category.CWE079, Category.NONE)));
    IFeature parameterOfTypeCharArray = new ParameterContainsTypeOrNameFeature(
        "char[]");
    addFeature(parameterOfTypeCharArray,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.NONE)));
    IFeature parameterOfTypeByteArray = new ParameterContainsTypeOrNameFeature(
        "byte[]");
    addFeature(parameterOfTypeByteArray,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.SANITIZER, Category.NONE)));
    IFeature parameterOfTypeCharSequence = new ParameterContainsTypeOrNameFeature(
        "java.lang.CharSequence");
    addFeature(parameterOfTypeCharSequence,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.NONE)));
    IFeature parameterOfTypeStringBuilder = new ParameterContainsTypeOrNameFeature(
        "java.lang.StringBuilder");
    addFeature(parameterOfTypeStringBuilder, new HashSet<>(
        Arrays.asList(Category.SANITIZER, Category.SINK, Category.NONE)));
    IFeature parameterOfTypeIo = new ParameterContainsTypeOrNameFeature(".io.");
    addFeature(parameterOfTypeIo, new HashSet<>(
        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    IFeature parameterOfTypeWeb = new ParameterContainsTypeOrNameFeature("web");
    addFeature(parameterOfTypeWeb, new HashSet<>(
        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    IFeature parameterOfTypeSql = new ParameterContainsTypeOrNameFeature("sql");
    addFeature(parameterOfTypeSql, new HashSet<>(Arrays.asList(Category.SOURCE,
        Category.SINK, Category.CWE089, Category.NONE)));
    IFeature parameterOfTypeDb = new ParameterContainsTypeOrNameFeature("db");
    addFeature(parameterOfTypeDb, new HashSet<>(
        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    IFeature parameterOfTypeCredential = new ParameterContainsTypeOrNameFeature(
        "credential");
    addFeature(parameterOfTypeCredential,
        new HashSet<>(Arrays.asList(Category.CWE306, Category.CWE862,
            Category.CWE863, Category.CWE078, Category.NONE)));
    IFeature parameterOfTypeUrl = new ParameterContainsTypeOrNameFeature("url");
    addFeature(parameterOfTypeUrl, new HashSet<>(
        Arrays.asList(Category.CWE601, Category.NONE)));
    
    // Return types (ReturnTypeFeature).
    IFeature byteArrayReturnType = new ReturnTypeFeature(cp, "byte[]");
    addFeature(byteArrayReturnType,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature stringReturnType = new ReturnTypeFeature(cp, "java.lang.String");
    addFeature(stringReturnType,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.CWE079, Category.CWE078, Category.CWE089, Category.NONE)));
    IFeature charSequenceReturnType = new ReturnTypeFeature(cp,
        "java.lang.CharSequence");
    addFeature(charSequenceReturnType,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature booleanReturnType = new ReturnTypeFeature(cp, "boolean");
    addFeature(booleanReturnType,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature resultsetReturnType = new ReturnTypeFeature(cp, "java.sql.ResultSet");
    addFeature(resultsetReturnType,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE, Category.CWE089)));
    

    // Method modifier.
    IFeature isStaticMethod = new MethodModifierFeature(cp, Modifier.STATIC);
    addFeature(isStaticMethod,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature isPublicMethod = new MethodModifierFeature(cp, Modifier.PUBLIC);
    addFeature(isPublicMethod,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature isFinalMethod = new MethodModifierFeature(cp, Modifier.FINAL);
    addFeature(isFinalMethod, new HashSet<>(Arrays.asList(Category.SOURCE,
        Category.SINK, Category.SANITIZER, Category.NONE)));
    IFeature isPrivateMethod = new MethodModifierFeature(cp, Modifier.PRIVATE);
    addFeature(isPrivateMethod, new HashSet<>(
        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));

    // Class modifier.
    IFeature isStaticClass = new MethodClassModifierFeature(cp,
        ClassModifier.STATIC);
    addFeature(isStaticClass, new HashSet<>(Arrays.asList(Category.SOURCE,
        Category.SINK, Category.SANITIZER, Category.NONE)));
    IFeature isPublicClass = new MethodClassModifierFeature(cp,
        ClassModifier.PUBLIC);
    addFeature(isPublicClass, new HashSet<>(Arrays.asList(Category.SOURCE,
        Category.SINK, Category.SANITIZER, Category.NONE)));
    IFeature isFinalClass = new MethodClassModifierFeature(cp,
        ClassModifier.FINAL);
    addFeature(isFinalClass, new HashSet<>(Arrays.asList(Category.SOURCE,
        Category.SINK, Category.SANITIZER, Category.NONE)));

    // Method name.
    IFeature methodNameStartsWithGet = new MethodNameStartsWithFeature("get");
    addFeature(methodNameStartsWithGet,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SANITIZER,
            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    IFeature methodNameStartsWithSet = new MethodNameStartsWithFeature("set");
    addFeature(methodNameStartsWithSet,
        new HashSet<>(Arrays.asList(Category.SINK, Category.SANITIZER,
            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_TO_LOW, Category.CWE079, Category.NONE)));
    IFeature methodNameStartsWithPut = new MethodNameStartsWithFeature("put");
    addFeature(methodNameStartsWithPut,
        new HashSet<>(Arrays.asList(Category.SANITIZER,
            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    IFeature methodNameStartsWithHas = new MethodNameStartsWithFeature("has");
    addFeature(methodNameStartsWithHas,
        new HashSet<>(Arrays.asList(Category.SANITIZER,
            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    IFeature methodNameStartsWithIs = new MethodNameStartsWithFeature("is");
    addFeature(methodNameStartsWithIs,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.NONE)));
    IFeature methodNameStartsOpen = new MethodNameStartsWithFeature("open");
    addFeature(methodNameStartsOpen,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, 
            Category.NONE)));
    IFeature methodNameStartsClose = new MethodNameStartsWithFeature("close");
    addFeature(methodNameStartsClose,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
             Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature methodNameStartsCreate = new MethodNameStartsWithFeature("create");
    addFeature(methodNameStartsCreate,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, 
            Category.NONE)));
    IFeature methodNameStartsDelete = new MethodNameStartsWithFeature("delete");
    addFeature(methodNameStartsDelete,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
             Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));

    
    // Method Contains Name. 
    IFeature methodNameContainsSaniti = new MethodNameContainsFeature("saniti");
    addFeature(methodNameContainsSaniti,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.CWE078, Category.CWE079, Category.CWE089, Category.NONE)));
    IFeature methodNameContainsEscape = new MethodNameContainsFeature("escape",
        "unescape");
    addFeature(methodNameContainsEscape,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodNameContainsGetParameter = new MethodNameContainsFeature("getParameter");
        addFeature(methodNameContainsGetParameter,
            new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    IFeature methodNameContainsUnescape = new MethodNameContainsFeature(
        "unescape");
    addFeature(methodNameContainsUnescape,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodNameContainsReplace = new MethodNameContainsFeature(
        "replac");
    addFeature(methodNameContainsReplace, new HashSet<>(
        Arrays.asList(Category.SINK, Category.SANITIZER, Category.NONE)));
    IFeature methodNameContainsStrip = new MethodNameContainsFeature("strip");
    addFeature(methodNameContainsStrip,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodNameContainsEncode = new MethodNameContainsFeature("encod",
        "encoding");
    addFeature(methodNameContainsEncode,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodNameContainsRegex = new MethodNameContainsFeature("regex");
    addFeature(methodNameContainsRegex,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));

    IFeature methodNameContainsAuthen = new MethodNameContainsFeature("authen");
    addFeature(methodNameContainsAuthen,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.CWE306, Category.NONE)));
    IFeature methodNameContainsCheck = new MethodNameContainsFeature("check");
    addFeature(methodNameContainsCheck,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature methodNameContainsCredential = new MethodNameContainsFeature(
        "credential");
    addFeature(methodNameContainsCredential,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature methodNameContainsVerif = new MethodNameContainsFeature("verif");
    addFeature(methodNameContainsVerif,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE, Category.SANITIZER)));
    IFeature methodNameContainsPrivilege = new MethodNameContainsFeature(
        "privilege");
    addFeature(methodNameContainsPrivilege,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature methodNameContainsLogin = new MethodNameContainsFeature("login");
    addFeature(methodNameContainsLogin,
        new HashSet<>(Arrays.asList(
            Category.AUTHENTICATION_TO_HIGH, 
            Category.CWE306, Category.NONE)));
    IFeature methodNameContainsNotLoginPage = new MethodNameContainsFeature("",
        "loginpage");
    addFeature(methodNameContainsNotLoginPage,
        new HashSet<>(Arrays.asList(Category.CWE306, Category.CWE862,
            Category.CWE863, Category.NONE)));
    IFeature methodNameContainsLogout = new MethodNameContainsFeature("logout");
    addFeature(methodNameContainsLogout,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_LOW,
            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature methodNameContainsConnect = new MethodNameContainsFeature(
        "connect", "disconnect");
    addFeature(methodNameContainsConnect,
        new HashSet<>(Arrays.asList(
            Category.AUTHENTICATION_TO_HIGH, 
            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature methodNameContainsDisconnect = new MethodNameContainsFeature(
        "disconnect");
    addFeature(methodNameContainsDisconnect,
        new HashSet<>(Arrays.asList( Category.AUTHENTICATION_TO_LOW,
            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature methodNameContainsBind = new MethodNameContainsFeature("bind",
        "unbind");
    addFeature(methodNameContainsBind,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, 
            Category.NONE)));
    IFeature methodNameContainsUnbind = new MethodNameContainsFeature("unbind");
    addFeature(methodNameContainsUnbind,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    

    IFeature nameContaisRead = new MethodNameContainsFeature("read", "thread");
    addFeature(nameContaisRead,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.CWE601, Category.NONE)));
    IFeature nameContainsLoad = new MethodNameContainsFeature("load",
        "payload");
    addFeature(nameContainsLoad,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature nameContainsRequest = new MethodNameContainsFeature("request");
    addFeature(nameContainsRequest,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature nameContainsCreate = new MethodNameContainsFeature("creat");
    addFeature(nameContainsCreate,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature nameContainsDecod = new MethodNameContainsFeature("decod");
    addFeature(nameContainsDecod,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature nameContainsUnescap = new MethodNameContainsFeature("unescap");
    addFeature(nameContainsUnescap,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature nameContainsPars = new MethodNameContainsFeature("pars");
    addFeature(nameContainsPars, new HashSet<>(
        Arrays.asList(Category.SINK, Category.SOURCE, Category.NONE)));
    IFeature nameContainsStream = new MethodNameContainsFeature("stream");
    addFeature(nameContainsStream, new HashSet<>(
        Arrays.asList(Category.SINK, Category.SOURCE, Category.NONE)));
    IFeature nameContainsRetriev = new MethodNameContainsFeature("retriev");
    addFeature(nameContainsRetriev,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature nameContainsObject = new MethodNameContainsFeature("Object");
    addFeature(nameContainsObject,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature nameContainsName = new MethodNameContainsFeature("Name");
    addFeature(nameContainsName,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));

    IFeature nameContainsWrit = new MethodNameContainsFeature("writ");
    addFeature(nameContainsWrit,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature nameContainsUpdat = new MethodNameContainsFeature("updat");
    addFeature(nameContainsUpdat,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature nameContainsSend = new MethodNameContainsFeature("send");
    addFeature(nameContainsSend,
        new HashSet<>(Arrays.asList(Category.SINK, Category.CWE601, Category.NONE)));
    IFeature nameContainsHandl = new MethodNameContainsFeature("handl");
    addFeature(nameContainsHandl,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    
    IFeature nameContainsRun = new MethodNameContainsFeature("run");
    addFeature(nameContainsRun, new HashSet<>(
        Arrays.asList(Category.SINK, Category.CWE078, Category.NONE)));
    IFeature nameContainsExecut = new MethodNameContainsFeature("execut");
    addFeature(nameContainsExecut,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature nameContainsExec = new MethodNameContainsFeature("exec");
    addFeature(nameContainsExec,
        new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    IFeature nameContainsCompile = new MethodNameContainsFeature("compile");
    addFeature(nameContainsCompile,
        new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    IFeature nameContainsRedirect = new MethodNameContainsFeature("redirect");
    addFeature(nameContainsRedirect,
        new HashSet<>(Arrays.asList(Category.SINK, Category.CWE601, Category.NONE)));

    IFeature nameContainsDump = new MethodNameContainsFeature("dump");
    addFeature(nameContainsDump,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature nameContainsPrint = new MethodNameContainsFeature("print");
    addFeature(nameContainsPrint,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));

    IFeature nameContainsExecute = new MethodNameContainsFeature("execute");
    addFeature(nameContainsExecute,
        new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    IFeature nameContainsQuery = new MethodNameContainsFeature("query");
    addFeature(nameContainsQuery,
        new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));

    IFeature nameContainsRole = new MethodNameContainsFeature("role");
    addFeature(nameContainsRole, new HashSet<>(
        Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature nameContainsAuthori = new MethodNameContainsFeature("authori");
    addFeature(nameContainsAuthori, new HashSet<>(
        Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE)));

    // Method Name Equals. 
    IFeature nameEqualsLog = new MethodNameEqualsFeature("log");
    addFeature(nameEqualsLog,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature nameEqualsSendRedirect = new MethodNameEqualsFeature("sendRedirect");
    addFeature(nameEqualsSendRedirect,
        new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    IFeature nameEqualsSetHeader = new MethodNameEqualsFeature("setHeader");
    addFeature(nameEqualsSetHeader,
        new HashSet<>(Arrays.asList(Category.SINK, Category.CWE079, Category.NONE)));
    
    
    // Class name.
    IFeature classNameContainsRedirect = new MethodClassContainsNameFeature(
            "Redirect");
        addFeature(classNameContainsRedirect,
            new HashSet<>(Arrays.asList(Category.CWE601, Category.SINK, Category.NONE)));
    IFeature classNameContainsResponse = new MethodClassContainsNameFeature(
            "Response");
        addFeature(classNameContainsResponse,
            new HashSet<>(Arrays.asList(Category.CWE601, Category.SINK, Category.NONE)));
    IFeature classNameContainsServlet = new MethodClassContainsNameFeature(
            "servlet");
        addFeature(classNameContainsServlet,
            new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    IFeature classNameContainsUrl = new MethodClassContainsNameFeature(
            "url");
        addFeature(classNameContainsUrl,
            new HashSet<>(Arrays.asList(Category.CWE601, Category.NONE)));
    IFeature classNameContainsCss = new MethodClassContainsNameFeature(
            "Css");
        addFeature(classNameContainsCss,
            new HashSet<>(Arrays.asList(Category.CWE079, Category.NONE)));
    IFeature classNameContainsDom = new MethodClassContainsNameFeature(
            "Dom");
        addFeature(classNameContainsDom,
            new HashSet<>(Arrays.asList(Category.CWE079, Category.NONE)));
    IFeature classNameContainsPage = new MethodClassContainsNameFeature(
            "Page");
        addFeature(classNameContainsPage,
            new HashSet<>(Arrays.asList(Category.CWE079, Category.SANITIZER, Category.NONE)));
    IFeature classNameContainsHtml = new MethodClassContainsNameFeature(
            "Html");
        addFeature(classNameContainsHtml,
            new HashSet<>(Arrays.asList(Category.SINK, Category.SOURCE, Category.CWE079, Category.NONE)));
    IFeature classNameContainsSaniti = new MethodClassContainsNameFeature(
        "Saniti");
    addFeature(classNameContainsSaniti,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature classNameContainsEncode = new MethodClassContainsNameFeature(
        "Encod");
    addFeature(classNameContainsEncode,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature classNameContainsEscape = new MethodClassContainsNameFeature(
        "Escap");
    addFeature(classNameContainsEscape,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature classNameContainsValid = new MethodClassContainsNameFeature(
        "Valid");
    addFeature(classNameContainsValid,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    
    IFeature classNameContainsCheck = new MethodClassContainsNameFeature(
        "Check");
    addFeature(classNameContainsCheck,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature classNameContainsVerify = new MethodClassContainsNameFeature(
        "Verif");
    addFeature(classNameContainsVerify,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature classNameContainsAuthen = new MethodClassContainsNameFeature(
        "Authen");
    addFeature(classNameContainsAuthen,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature classNameContainsSecurity = new MethodClassContainsNameFeature(
        "Security");
    addFeature(classNameContainsSecurity,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    IFeature classNameContainsConnect = new MethodClassContainsNameFeature(
        "Connect");
    addFeature(classNameContainsConnect,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, 
            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature classNameContainsBind = new MethodClassContainsNameFeature("Bind");
    addFeature(classNameContainsBind,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature classNameContainsOAuth = new MethodClassContainsNameFeature(
        "OAuth");
    addFeature(classNameContainsOAuth,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.CWE306, Category.NONE)));

    IFeature classNameContainsIO = new MethodClassContainsNameFeature(".io.");
    addFeature(classNameContainsIO, new HashSet<>(
        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    IFeature classNameContainsWeb = new MethodClassContainsNameFeature("web");
    addFeature(classNameContainsWeb, new HashSet<>(
        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE, Category.CWE079, Category.CWE601)));
    IFeature classNameContainsNet = new MethodClassContainsNameFeature(".net.");
    addFeature(classNameContainsNet, new HashSet<>(
        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    IFeature classNameContainsSql = new MethodClassContainsNameFeature("sql");
    addFeature(classNameContainsSql,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.CWE089, Category.NONE)));
    IFeature classNameContainsJdbc = new MethodClassContainsNameFeature("jdbc");
    addFeature(classNameContainsJdbc,
        new HashSet<>(Arrays.asList(Category.SINK,
            Category.CWE089, Category.NONE)));
    IFeature classNameContainsManager = new MethodClassContainsNameFeature(
        "Manager");
    addFeature(classNameContainsManager, new HashSet<>(
        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    IFeature classNameContainsOutput = new MethodClassContainsNameFeature(
        "Output");
    addFeature(classNameContainsOutput,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature classNameContainsInput = new MethodClassContainsNameFeature(
        "Input");
    addFeature(classNameContainsInput,
        new HashSet<>(Arrays.asList(Category.SINK, Category.CWE079, Category.CWE078, Category.CWE089, Category.NONE)));
    IFeature classNameContainsDatabase = new MethodClassContainsNameFeature(
        "database");
    addFeature(classNameContainsDatabase,
        new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    IFeature classNameContainsDb = new MethodClassContainsNameFeature("db");
    addFeature(classNameContainsDb,
        new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));
    IFeature classNameContainsHibernate = new MethodClassContainsNameFeature(
        "hibernate");
    addFeature(classNameContainsHibernate,
        new HashSet<>(Arrays.asList(Category.CWE089, Category.CWE079, Category.NONE)));
    IFeature classNameContainsCredential = new MethodClassContainsNameFeature(
        "credential");
    addFeature(classNameContainsCredential,
        new HashSet<>(Arrays.asList(Category.CWE078, Category.CWE862,
            Category.CWE863, Category.NONE)));
    IFeature classNameContainsProcess = new MethodClassContainsNameFeature(
        "process");
    addFeature(classNameContainsProcess,
        new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    IFeature classNameContainsRuntime = new MethodClassContainsNameFeature(
        "runtime");
    addFeature(classNameContainsRuntime,
        new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));

    IFeature classNameContainsUser = new MethodClassContainsNameFeature("user");
    addFeature(classNameContainsUser, new HashSet<>(
        Arrays.asList(Category.CWE862, Category.CWE863, Category.NONE)));
    
    IFeature classNameContainsRequest = new MethodClassContainsNameFeature("Request");
    addFeature(classNameContainsRequest, new HashSet<>(
        Arrays.asList(Category.CWE601, Category.NONE)));
    
    IFeature classNameContainsHttp = new MethodClassContainsNameFeature("http");
    addFeature(classNameContainsHttp, new HashSet<>(
        Arrays.asList(Category.CWE601, Category.NONE)));

    // Call to a method.
    IFeature methodInvocationNameSaniti = new MethodInvocationName(cp,
        "saniti");
    addFeature(methodInvocationNameSaniti,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodInvocationNameEscape = new MethodInvocationName(cp, "escap");
    addFeature(methodInvocationNameEscape,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodInvocationNameReplace = new MethodInvocationName(cp,
        "replac");
    addFeature(methodInvocationNameReplace, new HashSet<>(
        Arrays.asList(Category.SINK, Category.SANITIZER, Category.NONE)));
    IFeature methodInvocationNameStrip = new MethodInvocationName(cp, "strip");
    addFeature(methodInvocationNameStrip,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodInvocationNameMatches = new MethodInvocationName(cp,
        "match");
    addFeature(methodInvocationNameMatches,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodInvocationNameEncode = new MethodInvocationName(cp, "encod");
    addFeature(methodInvocationNameEncode,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodInvocationNameRegex = new MethodInvocationName(cp, "regex");
    addFeature(methodInvocationNameRegex,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));

    IFeature methodInvocationNameCheck = new MethodInvocationName(cp, "check");
    addFeature(methodInvocationNameCheck,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature methodInvocationNameVerif = new MethodInvocationName(cp, "verif");
    addFeature(methodInvocationNameVerif,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature methodInvocationNameAuthori = new MethodInvocationName(cp,
        "authori");
    addFeature(methodInvocationNameAuthori,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature methodInvocationNameAuthen = new MethodInvocationName(cp,
        "authen");
    addFeature(methodInvocationNameAuthen,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature methodInvocationNameLogin = new MethodInvocationName(cp, "login");
    addFeature(methodInvocationNameLogin,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, 
            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature methodInvocationNameLogout = new MethodInvocationName(cp,
        "logout");
    addFeature(methodInvocationNameLogout,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_LOW,
            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature methodInvocationNameSecurity = new MethodInvocationName(cp,
        "security");
    addFeature(methodInvocationNameSecurity,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature methodInvocationNameCredential = new MethodInvocationName(cp,
        "credential");
    addFeature(methodInvocationNameCredential,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    IFeature methodInvocationNameBind = new MethodInvocationName(cp, "bind");
    addFeature(methodInvocationNameBind,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));
    IFeature methodInvocationNameConnect = new MethodInvocationName(cp,
        "connect");
    addFeature(methodInvocationNameConnect,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.NONE)));

    IFeature methodInvocationNameGet = new MethodInvocationName(cp, "get");
    addFeature(methodInvocationNameGet,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature methodInvocationNameRead = new MethodInvocationName(cp, "read");
    addFeature(methodInvocationNameRead,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature methodInvocationNameDecode = new MethodInvocationName(cp, "decod");
    addFeature(methodInvocationNameDecode,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature methodInvocationNameUnescape = new MethodInvocationName(cp,
        "unescap");
    addFeature(methodInvocationNameUnescape,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature methodInvocationNameLoad = new MethodInvocationName(cp, "load");
    addFeature(methodInvocationNameLoad,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature methodInvocationNameRequest = new MethodInvocationName(cp,
        "request");
    addFeature(methodInvocationNameRequest,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature methodInvocationNameCreate = new MethodInvocationName(cp, "creat");
    addFeature(methodInvocationNameCreate,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature methodInvocationNameOutput = new MethodInvocationName(cp,
        "output");
    addFeature(methodInvocationNameOutput,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));

    IFeature methodInvocationNameSet = new MethodInvocationName(cp, "set");
    addFeature(methodInvocationNameSet,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNameWrit = new MethodInvocationName(cp, "writ");
    addFeature(methodInvocationNameWrit,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNameUpdat = new MethodInvocationName(cp, "updat");
    addFeature(methodInvocationNameUpdat,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNameSend = new MethodInvocationName(cp, "send");
    addFeature(methodInvocationNameSend,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNameHandl = new MethodInvocationName(cp, "handl");
    addFeature(methodInvocationNameHandl,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNamePut = new MethodInvocationName(cp, "put");
    addFeature(methodInvocationNamePut,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNameAdd = new MethodInvocationName(cp, "log");
    addFeature(methodInvocationNameAdd,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNameRun = new MethodInvocationName(cp, "run");
    addFeature(methodInvocationNameRun,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNameExecut = new MethodInvocationName(cp,
        "execut");
    addFeature(methodInvocationNameExecut,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNameDump = new MethodInvocationName(cp, "dump");
    addFeature(methodInvocationNameDump,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNamePrint = new MethodInvocationName(cp, "print");
    addFeature(methodInvocationNamePrint,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature methodInvocationNamePars = new MethodInvocationName(cp, "pars");
    addFeature(methodInvocationNamePars,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));

    IFeature methodInvocationNameExecute = new MethodInvocationName(cp,
        "execute");
    addFeature(methodInvocationNameExecute,
        new HashSet<>(Arrays.asList(Category.CWE078, Category.NONE)));
    IFeature methodInvocationNameMakedb = new MethodInvocationName(cp,
        "makedb");
    addFeature(methodInvocationNameMakedb,
        new HashSet<>(Arrays.asList(Category.CWE089, Category.NONE)));

    // Parameter to sink.
    IFeature paramToSinkSet = new ParameterToSinkFeature(cp, "set");
    addFeature(paramToSinkSet,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkSetWrit = new ParameterToSinkFeature(cp, "writ");
    addFeature(paramToSinkSetWrit,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkSetUpdat = new ParameterToSinkFeature(cp, "updat");
    addFeature(paramToSinkSetUpdat,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkSetHandl = new ParameterToSinkFeature(cp, "handl");
    addFeature(paramToSinkSetHandl,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkSetReplac = new ParameterToSinkFeature(cp, "replac");
    addFeature(paramToSinkSetReplac,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkSetPut = new ParameterToSinkFeature(cp, "put");
    addFeature(paramToSinkSetPut,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkSetAdd = new ParameterToSinkFeature(cp, "log");
    addFeature(paramToSinkSetAdd,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkSetRun = new ParameterToSinkFeature(cp, "run");
    addFeature(paramToSinkSetRun,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkSetExecut = new ParameterToSinkFeature(cp, "execut");
    addFeature(paramToSinkSetExecut,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkDump = new ParameterToSinkFeature(cp, "dump");
    addFeature(paramToSinkDump,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkPrint = new ParameterToSinkFeature(cp, "print");
    addFeature(paramToSinkPrint,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkPars = new ParameterToSinkFeature(cp, "pars");
    addFeature(paramToSinkPars,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    IFeature paramToSinkStream = new ParameterToSinkFeature(cp, "stream");
    addFeature(paramToSinkStream,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));

    // Source to return.
    IFeature sourceGetToReturn = new SourceToReturnFeature(cp, "get");
    addFeature(sourceGetToReturn,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature sourceReadToReturn = new SourceToReturnFeature(cp, "read");
    addFeature(sourceReadToReturn,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature sourcDecodeToReturn = new SourceToReturnFeature(cp, "decode");
    addFeature(sourcDecodeToReturn,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature sourceUnescapeToReturn = new SourceToReturnFeature(cp, "unescape");
    addFeature(sourceUnescapeToReturn,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature sourceLoadToReturn = new SourceToReturnFeature(cp, "load");
    addFeature(sourceLoadToReturn,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature sourceRequestToReturn = new SourceToReturnFeature(cp, "request");
    addFeature(sourceRequestToReturn,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    IFeature sourceCreateToReturn = new SourceToReturnFeature(cp, "create");
    addFeature(sourceCreateToReturn,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));

    // Call to a method of class.
    IFeature methodInvocationClassNameSaniti = new MethodInvocationClassName(cp,
        "Saniti");
    addFeature(methodInvocationClassNameSaniti,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodInvocationClassNameRegex = new MethodInvocationClassName(cp,
        "regex");
    addFeature(methodInvocationClassNameRegex,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodInvocationClassNameEscape = new MethodInvocationClassName(cp,
        "escap");
    addFeature(methodInvocationClassNameEscape,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));
    IFeature methodInvocationClassNameEncode = new MethodInvocationClassName(cp,
        "encod");
    addFeature(methodInvocationClassNameEncode,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));

    IFeature methodInvocationClassNameIO = new MethodInvocationClassName(cp,
        ".io.");
    addFeature(methodInvocationClassNameIO,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    IFeature methodInvocationClassNameSQL = new MethodInvocationClassName(cp,
        "sql");
    addFeature(methodInvocationClassNameSQL,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_TO_LOW, Category.NONE)));
    IFeature methodInvocationClassNameDB = new MethodInvocationClassName(cp,
        "db");
    addFeature(methodInvocationClassNameDB,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.AUTHENTICATION_NEUTRAL, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_TO_LOW, Category.NONE)));

    IFeature methodInvocationClassNameWeb = new MethodInvocationClassName(cp,
        "web");
    addFeature(methodInvocationClassNameWeb, new HashSet<>(
        Arrays.asList(Category.SOURCE, Category.SINK, Category.CWE079, Category.NONE)));
    IFeature methodInvocationClassNameNet = new MethodInvocationClassName(cp,
        ".net.");
    addFeature(methodInvocationClassNameNet, new HashSet<>(
        Arrays.asList(Category.SOURCE, Category.SINK, Category.NONE)));
    IFeature methodInvocationClassNameLog = new MethodInvocationClassName(cp,
        "Log.");
    addFeature(methodInvocationClassNameLog,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));

    // Parameter flows to return value.
    IFeature parameterFlowsToReturn = new ParameterFlowsToReturn(cp);
    addFeature(parameterFlowsToReturn,
        new HashSet<>(Arrays.asList(Category.SANITIZER, Category.NONE)));

    // Implicit method.
    IFeature isImplicitMethod = new IsImplicitMethod();
    addFeature(isImplicitMethod,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.SANITIZER, Category.AUTHENTICATION_NEUTRAL,
            Category.AUTHENTICATION_TO_HIGH, Category.AUTHENTICATION_TO_LOW,
            Category.CWE089, Category.CWE862, Category.CWE863, Category.CWE078, Category.CWE306, Category.CWE079, Category.CWE601,
            Category.NONE)));

    // Constructor.
    IFeature methodIsConstructor = new MethodIsConstructor();
    addFeature(methodIsConstructor,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.SINK,
            Category.SANITIZER, Category.AUTHENTICATION_TO_LOW, Category.AUTHENTICATION_TO_HIGH,
            Category.AUTHENTICATION_NEUTRAL, Category.CWE078, Category.CWE862,
            Category.CWE863, Category.CWE089, Category.NONE)));
    
    // Method class ends 
    IFeature classEndsWithEncoder = new MethodClassEndsWithNameFeature("Encoder");
    addFeature(classEndsWithEncoder, new HashSet<>(Arrays.asList(
            Category.SANITIZER, Category.CWE078, Category.CWE079, Category.CWE862,
            Category.CWE863, Category.CWE089, Category.NONE)));
    IFeature classEndsWithRequest = new MethodClassEndsWithNameFeature("Request");
    addFeature(classEndsWithRequest, new HashSet<>(Arrays.asList(
            Category.SINK, Category.CWE079, Category.CWE089, Category.NONE)));
    IFeature classEndsWithRender = new MethodClassEndsWithNameFeature("Render");
    addFeature(classEndsWithRender, new HashSet<>(Arrays.asList(
            Category.SINK, Category.CWE079, Category.NONE)));
    
    // Parameter Type matches return 
    IFeature paramTypeMathcesReturn = new ParamTypeMatchesReturnType(cp);
    addFeature(paramTypeMathcesReturn,
        new HashSet<>(Arrays.asList(
            Category.SANITIZER, Category.CWE078, Category.CWE079, Category.CWE862,
            Category.CWE863, Category.CWE089, Category.NONE)));
    
    IFeature innerClassMethod = new MethodInnerClassFeature(cp, true);
    addFeature(innerClassMethod,
        new HashSet<>(Arrays.asList(
            Category.SINK, Category.SOURCE, Category.NONE, Category.CWE078, Category.CWE079, Category.CWE089, Category.CWE306, Category.CWE862, Category.CWE863, Category.CWE601 )));
    
    IFeature returnsConstant = new MethodReturnsConstantFeature(cp);
    addFeature(returnsConstant,
        new HashSet<>(Arrays.asList(Category.SOURCE, Category.NONE)));
    
    IFeature anonymousClass = new MethodAnonymousClassFeature(true);
    addFeature(anonymousClass,
        new HashSet<>(Arrays.asList(Category.SOURCE,Category.SINK, Category.NONE, Category.CWE078, Category.CWE079, Category.CWE089, Category.CWE306, Category.CWE862, Category.CWE863, Category.CWE601)));
    
    IFeature realSetter = new MethodIsRealSetterFeature(cp);
    addFeature(realSetter,
        new HashSet<>(Arrays.asList(Category.SINK, Category.NONE)));
    
    IFeature voidOn = new VoidOnMethodFeature();
    addFeature(voidOn,
        new HashSet<>(Arrays.asList(Category.SINK,Category.CWE079, Category.NONE)));
    
    
    // ReturnTypeContainsNameFeature 
    IFeature returnContainsDocument = new ReturnTypeContainsNameFeature(cp, "Document");
    addFeature(returnContainsDocument,
        new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079, Category.NONE)));
    
    IFeature returnContainsNode = new ReturnTypeContainsNameFeature(cp, "Node");
    addFeature(returnContainsNode,
        new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079, Category.NONE)));
    
    IFeature returnContainsUser = new ReturnTypeContainsNameFeature(cp, "User");
    addFeature(returnContainsUser,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    
    IFeature returnContainsCredential= new ReturnTypeContainsNameFeature(cp, "Credential");
    addFeature(returnContainsCredential,
        new HashSet<>(Arrays.asList(Category.AUTHENTICATION_TO_HIGH,Category.CWE306, Category.CWE862, Category.CWE863, Category.NONE)));
    
    IFeature returnContainsServlet = new ReturnTypeContainsNameFeature(cp, "Servlet");
    addFeature(returnContainsServlet,
        new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079,Category.CWE078, Category.NONE)));
    
    IFeature returnContainsRequest = new ReturnTypeContainsNameFeature(cp, "Request");
    addFeature(returnContainsRequest,
        new HashSet<>(Arrays.asList(Category.SOURCE,Category.CWE079, Category.NONE)));
    
    System.out.println("Initialized " + getFeaturesSize() + " features.");
  }

  private int getFeaturesSize() {
    int count = 0;
    for (Category c : featuresMap.keySet())
      count += featuresMap.get(c).size();
    return count;
  }

}
