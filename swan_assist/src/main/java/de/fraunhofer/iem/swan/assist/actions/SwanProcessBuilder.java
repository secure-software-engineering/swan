/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.SwanNotifier;
import de.fraunhofer.iem.swan.assist.util.Constants;

import java.io.File;
import java.io.IOException;
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

        String currentTimestamp = getCurrentTimestamp("yyyy-MM-dd-HHmmss");

        File outputFolder = new File(parameters.get(Constants.SWAN_OUTPUT_DIR));
        outputFolder.mkdirs();

        File logFile = new File(outputFolder, currentTimestamp + parameters.get(Constants.SWAN_OUTPUT_LOG));
        try {
            logFile.createNewFile();
            parameters.replace(Constants.SWAN_OUTPUT_LOG, logFile.getPath());

        } catch (IOException e) {
            e.printStackTrace();
        }

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar",
                parameters.get(Constants.SWAN_JAR_DIR),
                parameters.get(Constants.SWAN_SOURCE_DIR),
                parameters.get(Constants.SWAN_TRAIN_DIR),
                parameters.get(Constants.SWAN_CONFIG_FILE),
                parameters.get(Constants.SWAN_OUTPUT_DIR));

        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

        String message;

        try {
            Process swanProcess = processBuilder.start();
            int result = swanProcess.waitFor();

            if (result == 0)
                message = resource.getString("Messages.Notification.Success");
            else
                message = resource.getString("Messages.Notification.Failure");

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
            message = resource.getString("Messages.Notification.Failure");
        }

        HashMap<String, String> results = new HashMap<String, String>();
        results.put(Constants.SWAN_OUTPUT_FILE, parameters.get(Constants.SWAN_OUTPUT_FILE));
        results.put(Constants.SWAN_OUTPUT_LOG, parameters.get(Constants.SWAN_OUTPUT_LOG));
        results.put(Constants.SWAN_OUTPUT_MESSAGE, message);

        MessageBus messageBus = project.getMessageBus();
        SwanNotifier publisher = messageBus.syncPublisher(SwanNotifier.END_SWAN_PROCESS_TOPIC);
        publisher.launchSwan(results);
    }

    /**
     * Get the timestamp in a specified format.
     * @param dateFormat Date format that should be used.
     * @return Formatted date
     */
    private String getCurrentTimestamp(String dateFormat) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDateTime.now().format(formatter);
    }
}