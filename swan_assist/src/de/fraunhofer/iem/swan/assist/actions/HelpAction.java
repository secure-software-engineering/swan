package de.fraunhofer.iem.swan.assist.actions;


import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import de.fraunhofer.iem.swan.assist.util.Constants;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

/**
 * Action to open help page or resources.
 *
 * @author Oshando Johnson
 */

public class HelpAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");
        URI helpUri = null;

        try {
            helpUri = new URI(Constants.HELP_LINK);
            Desktop.getDesktop().browse(helpUri);
        } catch (IOException | URISyntaxException exception) {

            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(resource.getString("Messages.Error.URINotFound"), MessageType.INFO, null)
                    .createBalloon()
                    .show(JBPopupFactory.getInstance().guessBestPopupLocation(e.getDataContext()), Balloon.Position.below);

            Notifications.Bus.notify(new Notification(Constants.PLUGIN_GROUP_DISPLAY_ID,
                    resource.getString("Messages.Error.PageNotFound"),
                    resource.getString("Messages.Error.URINotFound"),
                    NotificationType.ERROR));
        }
    }
}

