/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.Main;
import de.fraunhofer.iem.swan.assist.comm.SwanNotifier;
import de.fraunhofer.iem.swan.assist.data.JSONFileParser;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.util.Constants;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Creates process to run SWAN on a separate thread.
 */
public class RunSwanAnalysisImpl {

    private static HashMap<String, String> parameters;
    private Project project;
    private long duration;

    /**
     * Initializes builder.
     * @param project Project on which the plugin is being used with
     * @param param Parameters that will be used as program arguments
     */
    RunSwanAnalysisImpl(Project project, HashMap<String, String> param) {

        this.project = project;
        parameters = param;
    }

    /**
     * Sets up process to run the application and also send notification to subscribers when finished.
     */
    public void run() {

        ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");

        File outputFolder = new File(parameters.get(Constants.OUTPUT_DIRECTORY));

        if(!outputFolder.exists())
            outputFolder.mkdir();

        /*File logFile = new File(outputFolder, currentTimestamp + parameters.get(Constants.OUTPUT_LOG));
        try {
            logFile.createNewFile();
            parameters.replace(Constants.OUTPUT_LOG, logFile.getPath());

            FileOutputStream fileOutputStream = new FileOutputStream(logFile.getAbsolutePath());

            System.setOut(new PrintStream(fileOutputStream));

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        ProgressManager.getInstance().run(new Task.Backgroundable(project, resource.getString("Messages.Title.Progress")) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {

                long start = System.currentTimeMillis();

                Main.main(new String[]{parameters.get(Constants.SOURCE_DIRECTORY),
                        parameters.get(Constants.TRAIN_DIRECTORY),
                        parameters.get(Constants.CONFIGURATION_FILE),
                        parameters.get(Constants.OUTPUT_DIRECTORY)});

                 duration = System.currentTimeMillis() - start;
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }

            @Override
            public void onSuccess() {
                super.onSuccess();

                System.out.println("FILE: "+parameters.get(Constants.OUTPUT_FILE));
                //Create copy of file
                copyFile(new File(parameters.get(Constants.OUTPUT_FILE)));

                JSONFileParser parser = new JSONFileParser(parameters.get(Constants.OUTPUT_FILE));
                HashMap<String, MethodWrapper> exportedMethods = parser.parseJSONFileMap();

                HashMap<String, String> results = new HashMap<String, String>();
                results.put(Constants.OUTPUT_FILE, parameters.get(Constants.OUTPUT_FILE));
                results.put(Constants.OUTPUT_LOG, parameters.get(Constants.OUTPUT_LOG));

                int m = (int) (((duration / 1000) / 60) % 60);
                int s = (int) ((duration / 1000) % 60);

                results.put(Constants.ANALYSIS_RESULT, exportedMethods.size() + " methods found in "+m+" mins "+s+ " secs");

                MessageBus messageBus = project.getMessageBus();
                SwanNotifier publisher = messageBus.syncPublisher(SwanNotifier.END_SWAN_PROCESS_TOPIC);
                publisher.launchSwan(results);
            }
        });
    }

    private void copyFile(File original ){

        File copied = new File(
                parameters.get(Constants.OUTPUT_DIRECTORY) + File.separator +getCurrentTimestamp()+ "-config.json");
        try {
            FileUtils.copyFile(original, copied);
        } catch (IOException e) {
            e.printStackTrace();
        }
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