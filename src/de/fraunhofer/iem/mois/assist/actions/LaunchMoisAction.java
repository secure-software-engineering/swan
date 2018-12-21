package de.fraunhofer.iem.mois.assist.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.MoisNotifier;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.JSONWriter;
import de.fraunhofer.iem.mois.assist.ui.dialog.MoisLauncherDialog;

import javax.swing.FocusManager;
import java.awt.*;
import java.io.IOException;

/**
 * Action to start load dialog for configuring MOIS before running.
 *
 * @author Oshando Johnson
 */


public class LaunchMoisAction extends AnAction {

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
        MoisLauncherDialog dialog = new MoisLauncherDialog(project, true);
        dialog.show();

        if (dialog.getExitCode()==DialogWrapper.OK_EXIT_CODE) {

            MoisProcessBuilder processBuilder = new MoisProcessBuilder(project, dialog.getParameters());
            processBuilder.start();

            MoisNotifier publisher = messageBus.syncPublisher(MoisNotifier.START_MOIS_PROCESS_TOPIC);
            publisher.launchMois(null);
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