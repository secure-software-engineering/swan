package de.fraunhofer.iem.mois.assist.data;

import de.fraunhofer.iem.mois.data.CWE;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

public class InfoBank {

    ArrayList<CWE> methods;
    private String congFile = this.getClass().getClassLoader().getResource("infobank.json").getPath();

    //Import configuration details from JSON file
    public ArrayList<CWE> getCWEDetails(String cweQuery) {

        Object obj = null;

        try {

            JSONParser jsonParser = new JSONParser();
            obj = jsonParser.parse(new FileReader(congFile));

        } catch (Exception e) {

        }

        JSONObject jsonObject = (JSONObject) obj;
        JSONArray array = (JSONArray) jsonObject.get("cwes");

        methods = new ArrayList<CWE>();

        Iterator<JSONObject> iterator = array.iterator();

        while (iterator.hasNext()) {
            JSONObject jsonObj = iterator.next();


            String cweId = (String) jsonObj.get("ID").toString();

            if (cweQuery.contains(cweId)) {
                String cweName = (String) jsonObj.get("Name");
                String cweDescription = (String) jsonObj.get("Description");

                CWE cwe = new CWE(cweId, cweName, cweDescription, "");

                methods.add(cwe);
            }

        }
        return methods;
    }
}
