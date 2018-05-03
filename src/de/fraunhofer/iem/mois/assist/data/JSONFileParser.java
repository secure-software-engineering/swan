package de.fraunhofer.iem.mois.assist.data;

import com.intellij.openapi.ui.Messages;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.assist.util.Formatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JSONFileParser {

    private String congFilePath;

    JSONFileParser(String path) {
        congFilePath = path;
    }

    public String getCongFilePath() {
        return congFilePath;
    }

    public void setCongFilePath(String congFilePath) {
        this.congFilePath = congFilePath;
    }


    public HashMap<String, Method> parseJSONFileMap() {

        Object obj = null;

        try {
            JSONParser jsonParser = new JSONParser();
            obj = jsonParser.parse(new FileReader(congFilePath));
        } catch (Exception e) {
            Messages.showWarningDialog(Constants.FILE_LOAD_ERROR, "File Load Error");
        }

        JSONObject jsonObject = (JSONObject) obj;
        JSONArray array = (JSONArray) jsonObject.get(Constants.METHOD);

        HashMap<String, Method> methods = new HashMap<String, Method>();

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


            Method method = new Method(methodName, returnType, discovery, framework, link, comment, secLevel);

            // parse the correct types
            if (jsonObj.get(Constants.TYPE) != null) {

                JSONArray types = (JSONArray) jsonObj.get(Constants.TYPE);

                if (types.isEmpty())
                    continue;

                else {
                    for (String type : (Iterable<String>) types) {
                        switch (Formatter.capitalizeFirstCharacter(type)) {
                            case Constants.SOURCE:
                                method.addCategory(Category.SOURCE);
                                break;
                            case Constants.SINK:
                                method.addCategory(Category.SINK);
                                break;
                            case Constants.SANITIZER:
                                method.addCategory(Category.SANITIZER);
                                break;
                            case Constants.AUTHENTICATION:
                                method.addCategory(Category.AUTHENTICATION);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            Method.SecLevel securityLevel = Method.SecLevel.NEUTRAL;
            if (secLevel == null)
                securityLevel = Method.SecLevel.NEUTRAL;
            else {
                switch (secLevel) {
                    case "high":
                        securityLevel = Method.SecLevel.HIGH;
                        break;
                    case "low":
                        securityLevel = Method.SecLevel.LOW;
                        break;
                    default:
                        securityLevel = Method.SecLevel.NEUTRAL;
                        break;
                }
            }

            //Import function parameters
            List<String> params = new ArrayList<String>();

            if (jsonObj.get(Constants.PARAMETERS) != null) {
                JSONArray parameters = (JSONArray) jsonObj.get(Constants.PARAMETERS);

                Iterator<String> p = parameters.iterator();

                while (p.hasNext()) {
                    params.add(p.next().toString());
                }
            }

            method.addParameter(params);

            // link the cwes
            if (jsonObj.get(Constants.CWE) != null) {
                JSONArray cweList = (JSONArray) jsonObj.get(Constants.CWE);
                Iterator<String> p = cweList.iterator();

                while (p.hasNext()) {
                    String cweId = p.next().toString();
                    Category cweCategory = Category.getCategoryForCWE(cweId);
                    if (cweCategory != null) {
                        method.addCategory(cweCategory);
                    }
                    //TODO manage else case: System.err.println("CWE category does not exist: " + cweId);
                }
            }

            // parse dataIn and dataOut
            if (jsonObjDataIn.get(Constants.PARAMETERS) != null) {
                method.setDataIn(extractDataInOutObject(jsonObjDataIn));
            }

            if (jsonObjDataOut.get(Constants.PARAMETERS) != null) {
                method.setDataOut(extractDataInOutObject(jsonObjDataOut));
            }

            methods.put(method.getSignature(true), method);
        }

        return methods;
    }

    private DataInOut extractDataInOutObject(JSONObject dataObject) {

        JSONArray parameterArray = (JSONArray) dataObject.get(Constants.PARAMETERS);

        List<Integer> parameters = new ArrayList<>();

        for (Object param : parameterArray) {

            if (param instanceof Integer)
                parameters.add((Integer) param);
        }
        return new DataInOut((boolean) dataObject.get(Constants.RETURN_TYPE), parameters);
    }
}

