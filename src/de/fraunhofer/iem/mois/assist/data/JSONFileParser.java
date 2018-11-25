package de.fraunhofer.iem.mois.assist.data;

import com.intellij.openapi.ui.Messages;
import de.fraunhofer.iem.mois.Parser;
import de.fraunhofer.iem.mois.assist.util.Constants;

import java.util.HashMap;

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


    public HashMap<String, MethodWrapper> parseJSONFileMap() {

        HashMap<String, MethodWrapper> methods = new HashMap<String, MethodWrapper>();
        Parser parser = new Parser(null);

        try {
            for (de.fraunhofer.iem.mois.data.Method method : parser.parseFile(congFilePath)) {

                MethodWrapper methodWrapper = new MethodWrapper(method);
                methods.put(methodWrapper.getSignature(true),methodWrapper );
            }
        } catch (Exception e) {
            Messages.showWarningDialog(Constants.FILE_LOAD_ERROR, "File Load Error");
        }

        return methods;
    }
}

