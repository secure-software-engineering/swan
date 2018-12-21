package de.fraunhofer.iem.mois.assist.actions;


import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import de.fraunhofer.iem.mois.assist.util.Constants;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Action to open help page or resources.
 *
 * @author Oshando Johnson
 */

public class HelpAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getData(PlatformDataKeys.PROJECT);
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        URI helpUri = null;
        try {
            helpUri = new URI(Constants.HELP_LINK);
        } catch (URISyntaxException e1) {
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(Constants.URI_NOT_FOUND, MessageType.INFO, null)
                    .createBalloon()
                    .show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.below);
        }

        try {
            Desktop.getDesktop().browse(helpUri);
        } catch (IOException e1) {

            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(Constants.URI_NOT_FOUND, MessageType.INFO, null)
                    .createBalloon()
                    .show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.below);
            Notifications.Bus.notify(new Notification("AssistMois", "Page Not Found", Constants.URI_NOT_FOUND, NotificationType.ERROR));
        }
    }
}

