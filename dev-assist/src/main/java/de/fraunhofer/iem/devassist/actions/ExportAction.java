/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.devassist.actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import de.fraunhofer.iem.devassist.data.JSONFileLoader;
import de.fraunhofer.iem.devassist.data.JSONWriter;
import de.fraunhofer.iem.devassist.util.Constants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;

/**
 * Action to export updated configuration file.
 */

public class ExportAction extends AnAction {
    /**
     * Obtains list of methods and creates new JSON file in the location specified by the user.
     *
     * @param anActionEvent source event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        final String FILE_EXTENSION = ".json";
        String filePath = "";

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);

        File projectPath;

        if (project.getBasePath() == null)
            projectPath = FileSystemView.getFileSystemView().getDefaultDirectory();
        else
            projectPath = new File(project.getBasePath());

        JFileChooser fileChooser = new JFileChooser(projectPath);
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setSelectedFile(new File(project.getName() + "-methods.json"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        int returnValue = fileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath();

            if (!filePath.endsWith(FILE_EXTENSION)) {
                filePath += FILE_EXTENSION;
            }

            JSONWriter exportFile = new JSONWriter();
            //TODO deal with exception
            try {
                exportFile.writeToJsonFile(JSONFileLoader.getMethods(), filePath);

                String notificationContent = JSONFileLoader.getMethods().size() + " methods exported to: " + filePath;
                NotificationGroupManager.getInstance().getNotificationGroup("Plugin-1").createNotification(notificationContent, NotificationType.INFORMATION).notify(project);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Controls whether the action is enabled or disabled
     *
     * @param event source  event
     */
    @Override
    public void update(@NotNull AnActionEvent event) {

        //Disable/Enable action button
        if (JSONFileLoader.isFileSelected())
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}
