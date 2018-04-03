package de.fraunhofer.iem.mois.assist.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.SusiNotifier;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.JSONWriter;
import de.fraunhofer.iem.mois.assist.ui.SusiLauncherDialog;

import javax.swing.FocusManager;
import java.awt.*;
import java.io.IOException;

public class LaunchSusiAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();

        //Export changes to configuration files
        JSONWriter exportFile = new JSONWriter();

        try {
            exportFile.writeToJsonFile(JSONFileLoader.getMethods(),JSONFileLoader.getConfigurationFile(true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Launch Dialog
        Window activeWindow = FocusManager.getCurrentManager().getActiveWindow();

        SusiLauncherDialog dialog = new SusiLauncherDialog(activeWindow, true);
        dialog.pack();
        dialog.setSize(550, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {

            SusiProcessThread susiProcessThread = new SusiProcessThread(anActionEvent, dialog.getParameters());
            susiProcessThread.start();
            anActionEvent.getPresentation().setEnabled(false);
            SusiNotifier publisher = messageBus.syncPublisher(SusiNotifier.START_SUSI_PROCESS_TOPIC);
            publisher.launchSusi(null);
        }
    }
}