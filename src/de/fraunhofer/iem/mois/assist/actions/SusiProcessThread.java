package de.fraunhofer.iem.mois.assist.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.SusiNotifier;
import de.fraunhofer.iem.mois.assist.util.Constants;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

//Executes Susi with updated configuration file on a separate thread.
public class SusiProcessThread extends Thread {

    private HashMap<String, String> parameters;
    private AnActionEvent anActionEvent;

    SusiProcessThread(AnActionEvent actionEvent, HashMap<String, String> param) {

        anActionEvent = actionEvent;
        parameters = param;
    }

    public void run() {
        super.run();

        String currentTimestamp = getCurrentTimestamp("yyyyMMddHHmmss");

        File logFile = new File(parameters.get(Constants.SUSI_OUTPUT_DIR) + File.separator + currentTimestamp + Constants.SUSI_LOG_SUFFIX);

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar",
                parameters.get(Constants.SUSI_JAR_DIR),
                parameters.get(Constants.SUSI_SOURCE_DIR),
                parameters.get(Constants.SUSI_TRAIN_DIR),
                parameters.get(Constants.SUSI_CONFIG_FILE),
                parameters.get(Constants.SUSI_OUTPUT_DIR) + File.separator + currentTimestamp + Constants.OUTPUT_TEXT_SUFFIX,
                parameters.get(Constants.SUSI_OUTPUT_DIR) + File.separator + currentTimestamp + Constants.OUTPUT_JSON_SUFFIX);

        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

        String completionTimestamp = getCurrentTimestamp("HH:mm:ss");
        String message = Constants.NOTIFICATION_END_SUSI_SUCCESS + completionTimestamp;

/*
        try {
            Process susiProcess = processBuilder.start();
            int result = susiProcess.waitFor();

            int PROCESS_SUCCESS = 0;
            if (result == PROCESS_SUCCESS)
                message = Constants.NOTIFICATION_END_SUSI_SUCCESS + completionTimestamp;

            else
                message = Constants.NOTIFICATION_END_SUSI_FAIL;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        */

        HashMap<String, String> results = new HashMap<String, String>();
        results.put(Constants.SUSI_OUTPUT_FILE, parameters.get(Constants.SUSI_OUTPUT_DIR) + File.separator + currentTimestamp + Constants.OUTPUT_JSON_SUFFIX);
        results.put(Constants.SUSI_OUTPUT_LOG, parameters.get(Constants.SUSI_OUTPUT_DIR) + File.separator + currentTimestamp + Constants.SUSI_LOG_SUFFIX);
        results.put(Constants.SUSI_OUTPUT_MESSAGE, message);

        anActionEvent.getPresentation().setEnabled(true);

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();
        SusiNotifier publisher = messageBus.syncPublisher(SusiNotifier.END_SUSI_PROCESS_TOPIC);
        publisher.launchSusi(results);
    }

    private String getCurrentTimestamp(String dateFormat) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDateTime.now().format(formatter);
    }
}