/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.data;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import de.fraunhofer.iem.swan.Parser;
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.data.Method;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Parses JSON file and returns methods.
 */
public class JSONFileParser {

    private String congFilePath;

    /**
     * Initializes parser with file path.
     * @param path Path of configuration file
     */
    public JSONFileParser(String path) {
        congFilePath = path;
    }

    /**
     * Default constructor
     */
    public JSONFileParser() {

    }

    /**
     * Returns file path for configuration file
     * @return File path as a string.
     */
    public String getCongFilePath() {
        return congFilePath;
    }

    /**
     * Set configuration file path
     * @param congFilePath Configuration file path
     */
    public void setCongFilePath(String congFilePath) {
        this.congFilePath = congFilePath;
    }

    /**
     * Parses file and returns method.
     * @return HashMap of methods
     */
    public HashMap<String, MethodWrapper> parseJSONFileMap() {

        Parser parser = new Parser(congFilePath);
        parser.parse(congFilePath);

       return parseMethods(parser.methods());
    }

    /**
     * Parses file and returns method.
     * @return HashMap of methods
     */
    public HashMap<String, MethodWrapper> parseJSONFileStream(InputStreamReader streamReader) {

        Parser parser = new Parser();
        parser.parseStream(streamReader);
        return parseMethods(parser.methods());
    }

    private HashMap<String, MethodWrapper> parseMethods(Set<Method> methodsSet){

        ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");
        HashMap<String, MethodWrapper> methods = new HashMap<String, MethodWrapper>();

        try {
            for (Method method :methodsSet ) {

                MethodWrapper methodWrapper = new MethodWrapper(method);

                if(method.getDiscovery().equals(Constants.TRAINING_METHOD))
                    methodWrapper.setTrainingMethod(true);
                else
                    methodWrapper.setTrainingMethod(false);

                methods.put(methodWrapper.getSignature(true), methodWrapper);
            }
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification("SWAN_Assist", resource.getString("Messages.Title.FileLoadError"), resource.getString("Messages.Error.FileLoadError"), NotificationType.ERROR));
        }

        return methods;
    }
}

