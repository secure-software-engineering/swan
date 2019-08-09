/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.Main;
import de.fraunhofer.iem.swan.assist.comm.SwanNotifier;
import de.fraunhofer.iem.swan.assist.util.Constants;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Creates process to run SWAN on a separate thread.
 */
public class SwanProcessBuilder extends Thread {

    private static HashMap<String, String> parameters;
    private Project project;

    /**
     * Initializes builder.
     * @param project Project on which the plugin is being used with
     * @param param Parameters that will be used as program arguments
     */
    SwanProcessBuilder(Project project, HashMap<String, String> param) {

        this.project = project;
        parameters = param;
    }

    /**
     * Sets up process to run the application and also send notification to subscribers when finished.
     */
    public void run() {
        super.run();

        ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");

        String currentTimestamp = getCurrentTimestamp();

        File outputFolder = new File(parameters.get(Constants.OUTPUT_DIRECTORY));

        if(!outputFolder.exists())
            outputFolder.mkdir();

        File logFile = new File(outputFolder, currentTimestamp + parameters.get(Constants.OUTPUT_LOG));
        try {
            logFile.createNewFile();
            parameters.replace(Constants.OUTPUT_LOG, logFile.getPath());

            FileOutputStream fileOutputStream = new FileOutputStream(logFile.getAbsolutePath());

            System.setOut(new PrintStream(fileOutputStream));

        } catch (IOException e) {
            e.printStackTrace();
        }

        Main.main(new String[]{parameters.get(Constants.SOURCE_DIRECTORY),
                parameters.get(Constants.TRAIN_DIRECTORY),
                parameters.get(Constants.CONFIGURATION_FILE),
                parameters.get(Constants.OUTPUT_DIRECTORY)});

        System.setOut(System.out);

        HashMap<String, String> results = new HashMap<String, String>();
        results.put(Constants.OUTPUT_FILE, parameters.get(Constants.OUTPUT_FILE));
        results.put(Constants.OUTPUT_LOG, parameters.get(Constants.OUTPUT_LOG));

        MessageBus messageBus = project.getMessageBus();
        SwanNotifier publisher = messageBus.syncPublisher(SwanNotifier.END_SWAN_PROCESS_TOPIC);
        publisher.launchSwan(results);
    }

    /**
     * Get the timestamp in a specified format.
     * @return Formatted date
     */
    private String getCurrentTimestamp() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");
        return LocalDateTime.now().format(formatter);
    }
}