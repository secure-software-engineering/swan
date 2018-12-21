package de.fraunhofer.iem.mois.assist.data;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import de.fraunhofer.iem.mois.Parser;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.data.Method;

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
            for (Method method : parser.parseFile(congFilePath)) {

                MethodWrapper methodWrapper = new MethodWrapper(method);
                methods.put(methodWrapper.getSignature(true),methodWrapper );
            }
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification("AssistMois", "File Load Error", Constants.FILE_LOAD_ERROR, NotificationType.ERROR));
        }

        return methods;
    }
}

