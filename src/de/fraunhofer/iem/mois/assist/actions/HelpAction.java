package de.fraunhofer.iem.mois.assist.actions;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import de.fraunhofer.iem.mois.assist.util.Constants;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HelpAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getData(PlatformDataKeys.PROJECT);

        URI helpUri = null;
        try {
            helpUri = new URI(Constants.HELP_LINK);
        } catch (URISyntaxException e1) {
            Messages.showMessageDialog(project, Constants.URI_NOT_FOUND, "Page Not Found", Messages.getInformationIcon());
        }

        try {
            Desktop.getDesktop().browse(helpUri);
        } catch (IOException e1) {
            Messages.showMessageDialog(project, Constants.URI_NOT_FOUND, "Page Not Found", Messages.getInformationIcon());
        }
    }
}

