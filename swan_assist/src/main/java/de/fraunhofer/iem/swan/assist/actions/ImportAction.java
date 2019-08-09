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
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.ConfigurationFileNotifier;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * Action to import configuration file.
 */
public class ImportAction extends AnAction {

    /**
     * Allows user to select configuration file that is then loaded. 
     * @param e source event
     */
    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        File projectPath;

        if (project.getBasePath() == null)
            projectPath = FileSystemView.getFileSystemView().getDefaultDirectory();
        else
            projectPath = new File(project.getBasePath());

        JFileChooser fileChooser = new JFileChooser(projectPath);
        FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("JSON Files", "json");
        fileChooser.setFileFilter(jsonFilter);

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fileChooser.getSelectedFile();

            MessageBus messageBus = project.getMessageBus();
            ConfigurationFileNotifier publisher = messageBus.syncPublisher(ConfigurationFileNotifier.FILE_NOTIFIER_TOPIC);
            publisher.loadInitialFile(selectedFile.getAbsoluteFile().toString());
        }
    }
}
