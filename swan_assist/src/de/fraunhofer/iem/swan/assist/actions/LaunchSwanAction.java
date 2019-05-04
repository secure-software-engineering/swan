/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.SwanNotifier;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.JSONFileParser;
import de.fraunhofer.iem.swan.assist.data.JSONWriter;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.ui.dialog.SwanLauncherDialog;
import de.fraunhofer.iem.swan.assist.util.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

/**
 * Action opens dialog for user to set parameters for running SWAN. After which thread is created to run SWAN.
 */
public class LaunchSwanAction extends AnAction {

    /**
     * Obtains application parameters from user, exports updated JSON file and starts thread to run SWAN.
     * @param anActionEvent source event
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();

        Properties config = new Properties();
        InputStream input = null;

        try {
            input = getClass().getClassLoader().getResourceAsStream("config.properties");
            config.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Launch Dialog
        SwanLauncherDialog dialog = new SwanLauncherDialog(project, true);
        dialog.show();

        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {

            HashMap<String, String> swanParameters = dialog.getParameters();

            //Merge current list with training methods
            HashMap<String, MethodWrapper> methods = JSONFileLoader.getAllMethods();

            //Load training methods
            String trainingFile = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getPath() + config.getProperty("train_config_file");
            JSONFileParser fileParser = new JSONFileParser(trainingFile);
            HashMap<String, MethodWrapper> trainingMethods = fileParser.parseJSONFileMap();

            HashMap<String, MethodWrapper> mergedMethods = new HashMap<>(methods);
            mergedMethods.putAll(trainingMethods);

            //Export changes to configuration files
            JSONWriter exportFile = new JSONWriter();
            String newConfigFile = swanParameters.get(Constants.SWAN_OUTPUT_DIR) + File.separator + config.getProperty("input_json_suffix");
            try {
                exportFile.writeToJsonFile(new ArrayList<>(mergedMethods.values()), newConfigFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            swanParameters.put(Constants.SWAN_CONFIG_FILE, newConfigFile);

            SwanProcessBuilder processBuilder = new SwanProcessBuilder(project, dialog.getParameters());
            processBuilder.start();

            SwanNotifier publisher = messageBus.syncPublisher(SwanNotifier.START_SWAN_PROCESS_TOPIC);
            publisher.launchSwan(null);
        }
    }

    /**
     * Controls whether the action is enabled or disabled
     * @param event source  event
     */
    @Override
    public void update(AnActionEvent event) {

        //Disable/Enable action button
        if (JSONFileLoader.isReloading() || !JSONFileLoader.isFileSelected())
            event.getPresentation().setEnabled(false);
        else
            event.getPresentation().setEnabled(true);
    }
}