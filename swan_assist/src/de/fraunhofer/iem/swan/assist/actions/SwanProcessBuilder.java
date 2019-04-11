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
 * Creates process to run SWAN.
 *
 * @author Oshando Johnson
 */


//Executes SWAN with updated configuration file on a separate thread.
public class SwanProcessBuilder extends Thread {

    private static HashMap<String, String> parameters;
    private Project project;

    SwanProcessBuilder(Project project, HashMap<String, String> param) {

        this.project = project;
        parameters = param;
    }

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

    private String getCurrentTimestamp(String dateFormat) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDateTime.now().format(formatter);
    }
}