package de.fraunhofer.iem.swan.assist.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.SwanNotifier;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.JSONWriter;
import de.fraunhofer.iem.swan.assist.ui.dialog.SwanLauncherDialog;

import java.io.IOException;

/**
 * Action to start load dialog for configuring SWAN before running.
 *
 * @author Oshando Johnson
 */


public class LaunchSwanAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();

        //Export changes to configuration files
        JSONWriter exportFile = new JSONWriter();

        try {
            exportFile.writeToJsonFile(JSONFileLoader.getMethods(), JSONFileLoader.getConfigurationFile(true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Launch Dialog
        SwanLauncherDialog dialog = new SwanLauncherDialog(project, true);
        dialog.show();

        if (dialog.getExitCode()==DialogWrapper.OK_EXIT_CODE) {

            SwanProcessBuilder processBuilder = new SwanProcessBuilder(project, dialog.getParameters());
            processBuilder.start();

            SwanNotifier publisher = messageBus.syncPublisher(SwanNotifier.START_SWAN_PROCESS_TOPIC);
            publisher.launchSwan(null);
        }
    }

    @Override
    public void update(AnActionEvent event) {

        //Disable/Enable action button
        if (JSONFileLoader.isReloading() || !JSONFileLoader.isFileSelected())
            event.getPresentation().setEnabled(false);
        else
            event.getPresentation().setEnabled(true);
    }
}