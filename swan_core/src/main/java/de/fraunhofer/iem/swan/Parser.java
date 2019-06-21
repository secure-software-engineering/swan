package de.fraunhofer.iem.swan;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.fraunhofer.iem.swan.data.RelevantPart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iem.swan.IFeature.Type;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Constants;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.AbstractSootFeature;
import soot.SootMethod;

/**
 * Loads methods from file or jars.
 *
 * @author Lisa Nguyen Quang Do
 */

public class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    protected final String trainingCp;
    protected Set<Method> methods = new HashSet<Method>();
    private Set<String> cwes = new HashSet<String>();

    public Parser() {
        this.trainingCp = null;
    }

    public Parser(String trainingCp) {
        this.trainingCp = trainingCp;
    }

    public Set<Method> methods() {
        return methods;
    }

    public void loadTrainingSet(Set<String> trainingSetFiles) throws IOException {
        loadMethods(trainingSetFiles);
        prefilterInterfaces();
        Util.createSubclassAnnotations(methods, trainingCp);
        methods = Util.sanityCheck(methods, new HashSet<Method>());
        Util.printStatistics("Training set complete", methods);
    }

    public void resetMethods() {
        for (Method m : methods)
            m.setCategoryClassified(null);
    }

    /**
     * Removes all interfaces from the given set of methods and returns the purged
     * set.
     *
     * @param methods The set of methods from which to remove the interfaces.
     * @return The purged set of methods.
     */
    private void prefilterInterfaces() {
        Set<Method> purgedMethods = new HashSet<Method>(methods.size());
        for (Method am : methods) {
            AbstractSootFeature asf = new AbstractSootFeature(trainingCp) {
                @Override
                public Type appliesInternal(Method method) {
                    SootMethod sm = getSootMethod(method);
                    if (sm == null)
                        return Type.NOT_SUPPORTED;

                    if (sm.isAbstract())
                        return Type.FALSE;
                    else
                        return Type.TRUE;
                }
            };
            Type t = asf.applies(am);
            if (t == Type.TRUE) {
                purgedMethods.add(am);
            } else
                System.out.println("Purged method: " + am.getSignature());

        }
        logger.info(methods.size() + " methods purged down to "
                + purgedMethods.size() + ".");
        this.methods = purgedMethods;
    }

    public void loadMethods(Set<String> trainingSetFiles) throws IOException {

        parse(trainingSetFiles);
        Set<Method> methodsWithoutDuplicates = new HashSet<Method>();

        for (Method am : methods) {
            if (methodsWithoutDuplicates.contains(am)) {
                // Merge the methods
                for (Method amOrig : methodsWithoutDuplicates)
                    if (am.equals(amOrig)) {
                        // Merge the classification
                        amOrig.addCategoriesTrained(am.getCategoriesTrained());
                        break;
                    }
            } else {
                methodsWithoutDuplicates.add(am);
            }
        }

        methods = methodsWithoutDuplicates;
        logger.info(
                "Collected " + methods.size() + " methods from the training set.");
    }

    protected void parse(Set<String> files) {
        for (String file : files)
            parse(file);
    }

    public void parse(String fileName) {
        try {
            parseStream(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void parseStream(InputStreamReader streamReader) {

        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(streamReader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = (JSONObject) obj;

        // get the lists in the file (4 types of files)
        JSONArray methodsList = (JSONArray) jsonObject.get("methods");
        // JSONArray cwesList = (JSONArray) jsonObject.get("cwes");

        // create all cwes
        // if (cwesList != null) loadMethodsFromJsonArray(cwesList, "cwes");
        if (methodsList != null) loadMethodsFromJsonArray(methodsList, "methods");
    }

    @SuppressWarnings("unchecked")
    private void loadMethodsFromJsonArray(JSONArray array, String type) {

        if (type.equals("methods")) {
            Iterator<JSONObject> iterator = array.iterator();
            while (iterator.hasNext()) {
                JSONObject jsonObj = iterator.next();

                String methodName = (String) jsonObj.get(Constants.NAME);
                String returnType = (String) jsonObj.get(Constants.RETURN_TYPE);
                String discovery = (String) jsonObj.get(Constants.DISCOVERY);
                String framework = (String) jsonObj.get(Constants.FRAMEWORK);
                String link = (String) jsonObj.get(Constants.LINK);
                String comment = (String) jsonObj.get(Constants.COMMENT);
                String secLevel = (String) jsonObj.get(Constants.SECURITY_LEVEL);

                JSONObject jsonObjDataIn = (JSONObject) jsonObj.get(Constants.DATA_IN);
                JSONObject jsonObjDataOut = (JSONObject) jsonObj.get(Constants.DATA_OUT);

                List<String> params = new ArrayList<String>();

                if (jsonObj.get(Constants.PARAMETERS) != null) {
                    JSONArray parameters = (JSONArray) jsonObj.get(Constants.PARAMETERS);

                    Iterator<String> p = parameters.iterator();

                    while (p.hasNext()) {
                        params.add(p.next().toString());
                    }
                }

                Method m;
                String temp = getNameOfClass(methodName)
                        .substring(getNameOfClass(methodName).lastIndexOf(".") + 1);
                if (getNameOfMethod(methodName).equals(temp))
                    m = new Method("<init>", params, returnType,
                            getNameOfClass(methodName));
                else
                    m = new Method(getNameOfMethod(methodName), params, returnType,
                            getNameOfClass(methodName));

                // parse the relevantPart objects for dataIn and dataOut
                boolean retDataIn = (boolean) jsonObjDataIn.get(Constants.RETURN_TYPE);
                boolean retDataOut = (boolean) jsonObjDataOut.get(Constants.RETURN_TYPE);

                m.getDataIn().setReturnValue(retDataIn);
                m.getDataOut().setReturnValue(retDataOut);

                if (jsonObjDataIn.get(Constants.PARAMETERS) != null) {
                    JSONArray parameters1 = (JSONArray) jsonObjDataIn.get(Constants.PARAMETERS);

                    Iterator<Long> p1 = parameters1.iterator();

                    while (p1.hasNext()) {
                        m.getDataIn().getParameterIndeces()
                                .add((Integer.parseInt(p1.next().toString())));
                    }
                }

                if (jsonObjDataOut.get(Constants.PARAMETERS) != null) {
                    JSONArray parameters2 = (JSONArray) jsonObjDataOut.get(Constants.PARAMETERS);

                    Iterator<Long> p2 = parameters2.iterator();
                    while (p2.hasNext()) {
                        m.getDataOut().getParameterIndeces()
                                .add((Integer.parseInt(p2.next().toString())));
                    }
                }

                Method.SecLevel securityLevel = Method.SecLevel.NEUTRAL;
                if (secLevel == null)
                    securityLevel = Method.SecLevel.NEUTRAL;
                else {
                    switch (secLevel) {
                        case Constants.AUTH_SAFE:
                            securityLevel = Method.SecLevel.HIGH;
                            break;
                        case Constants.AUTH_UNSAFE:
                            securityLevel = Method.SecLevel.LOW;
                            break;
                        default:
                            securityLevel = Method.SecLevel.NEUTRAL;
                            break;
                    }
                }

                // add the other String values
                m.setComment(comment);
                m.setFramework(framework);
                m.setLink(link);
                m.setDiscovery(discovery);
                m.setSecLevel(securityLevel);

                // link the cwes
                if (jsonObj.get(Constants.CWE) != null) {
                    JSONArray cweList = (JSONArray) jsonObj.get(Constants.CWE);
                    Iterator<String> p = cweList.iterator();
                    while (p.hasNext()) {
                        String cweId = p.next().toString();
                        Category cweCategory = Category.getCategoryForCWE(cweId);
                        if (cweCategory != null) {
                            m.addCategoryTrained(cweCategory);
                            cwes.add(cweId);
                        } else
                            System.err.println("CWE category does not exist: " + cweId);
                    }
                }


                // Note by Ingo Budde:
                // I modified the following lines to get rid of soot-infoflow dependency.
                // It seems that the SourceSinkDefinition is not used here.

                // SourceSinkDefinition ssd = new SourceSinkDefinition(m);
                // Method method = (Method) ssd.getMethod();
                Method method = m;

                // parse the correct types
                if (jsonObj.get(Constants.TYPE) != null) {
                    JSONArray types = (JSONArray) jsonObj.get(Constants.TYPE);
                    Iterator<String> p = types.iterator();
                    while (p.hasNext()) {
                        String t = p.next().toString();
                        switch (t) {
                            case Constants.SOURCE:
                                m.addCategoryTrained(Category.SOURCE);
                                break;
                            case Constants.SINK:
                                m.addCategoryTrained(Category.SINK);
                                break;
                            case Constants.SANITIZER:
                                m.addCategoryTrained(Category.SANITIZER);
                                break;
                            case Constants.AUTHENTICATION:
                                switch (securityLevel) {
                                    case HIGH:
                                        m.addCategoryTrained(Category.AUTHENTICATION_TO_HIGH);
                                        break;
                                    case LOW:
                                        m.addCategoryTrained(Category.AUTHENTICATION_TO_LOW);
                                        break;
                                    case NEUTRAL:
                                        m.addCategoryTrained(Category.AUTHENTICATION_NEUTRAL);
                                        break;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                methods.add(method);
            }
        }

        // if (type.equals("cwes")) {
        // Iterator<JSONObject> iterator2 = array.iterator();
        // JSONObject jsonObj2 = iterator2.next();
        // while (iterator2.hasNext()) {
        // String i = (String) jsonObj2.get("id");
        // String n = (String) jsonObj2.get("name");
        // String s = (String) jsonObj2.get("short");
        // if (s == null) s = "";
        // String l = (String) jsonObj2.get("link");
        // CWE newCWE = new CWE(i, n, s, l);
        // cwes.add(newCWE);
        // jsonObj2 = iterator2.next();
        // }
        // }
    }

    private String getNameOfMethod(String name) {
        String[] names = name.split("\\.");
        return names[names.length - 1];
    }

    private String getNameOfClass(String name) {
        return name.substring(0, name.lastIndexOf("."));
    }

    public Set<String> cwe() {
        return cwes;
    }

    private RelevantPart extractDataInOutObject(JSONObject dataObject) {

        JSONArray parameterArray = (JSONArray) dataObject.get(Constants.PARAMETERS);

        List<Integer> parameters = new ArrayList<>();

        for (Object param : parameterArray) {

            if (param instanceof Integer)
                parameters.add((Integer) param);
        }
        return new RelevantPart((boolean) dataObject.get(Constants.RETURN_TYPE), parameters);
    }

}
