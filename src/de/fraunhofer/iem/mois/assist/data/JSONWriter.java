package de.fraunhofer.iem.mois.assist.data;

import com.sun.tools.internal.jxc.ap.Const;
import de.fraunhofer.iem.mois.assist.util.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JSONWriter {

    /* Outputs results as a JSON file. */
    public void writeToJsonFile(ArrayList<Method> methods, String outputPath) throws IOException {

        Map<Category, Integer> counters = new HashMap<Category, Integer>();
        FileWriter wr = null;

        wr = new FileWriter(outputPath);

        int count = 0;
        JSONArray arr = new JSONArray();

        HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

        for (Method method : methods) {

            if (!method.getUpdateOperation().equals(Constants.METHOD_DELETED)) {

                JSONObject obj = new JSONObject();

                obj.put(Constants.NAME, method.getClassName(true));
                obj.put(Constants.RETURN_TYPE, method.getReturnType(true));
                obj.put(Constants.PARAMETERS, method.getParameters());

                //DataIn object
                JSONArray dataInParams = new JSONArray();
                dataInParams.addAll(method.getDataIn().getParameterIndices());

                JSONObject dataIn = new JSONObject();
                dataIn.put(Constants.RETURN_TYPE, method.getDataIn().getReturnValue());
                dataIn.put(Constants.PARAMETERS, dataInParams);

                //DataOut object
                JSONArray dataOutParams = new JSONArray();
                dataOutParams.addAll(method.getDataOut().getParameterIndices());

                JSONObject dataOut = new JSONObject();
                dataOut.put(Constants.RETURN_TYPE, method.getDataOut().getReturnValue());
                dataOut.put(Constants.PARAMETERS, dataOutParams);

                obj.put(Constants.DATA_IN, dataIn);
                obj.put(Constants.DATA_OUT, dataOut);

                obj.put(Constants.SECURITY_LEVEL, method.getSecLevel());
                obj.put(Constants.DISCOVERY, method.getDiscovery());
                obj.put(Constants.FRAMEWORK, method.getFramework());
                obj.put(Constants.LINK, method.getLink());
                obj.put(Constants.COMMENT, method.getComment());

                JSONArray types = new JSONArray();
                JSONArray cwes = new JSONArray();

                for (Category category : method.getCategories()) {

                    if (category.isCwe())
                        cwes.add(category.toString());
                    else
                        types.add(category.toString());

                    if (counters.containsKey(category)) {
                        int counter = counters.get(category);
                        counters.put(category, ++counter);
                    } else
                        counters.put(category, 1);
                }
                obj.put(Constants.TYPE, types);
                obj.put(Constants.CWE, cwes);


                map.put("method" + count, obj);
                arr.add(map.get("method" + count));
            }
        }

        JSONObject parent = new JSONObject();
        parent.put(Constants.METHOD, arr);

        wr.write(parent.toJSONString());
        wr.flush();
        wr.close();
    }
}
