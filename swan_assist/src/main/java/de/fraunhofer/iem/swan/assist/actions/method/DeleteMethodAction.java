/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.MethodNotifier;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.util.PsiTraversal;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * Action to delete a method from the list.
 */

public class DeleteMethodAction extends AnAction {

    private MethodWrapper deleteMethod;

    /**
     * Initializes the action
     */
    public DeleteMethodAction() {
        super("Delete");
        deleteMethod = null;
    }

    /**
     * Intitializes the action
     * @param method that will be deleted
     */
    public DeleteMethodAction(MethodWrapper method) {
        super("Delete");
        deleteMethod = method;
    }

    /**
     * Obtain method, if it was not passed, and then send notification about the method that should be deleted.
     * @param anActionEvent source event
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getProject();
        ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");

        if (PsiTraversal.isFromEditor(anActionEvent))
            deleteMethod = PsiTraversal.getMethodAtOffset(anActionEvent, false);

        if (deleteMethod != null) {
            int confirmation = JOptionPane.showConfirmDialog(null, resource.getString("Messages.Confirmation.DeleteMethod"), resource.getString("Messages.Title.DeleteMethod"), JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {

                MessageBus messageBus = project.getMessageBus();
                MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.ADD_UPDATE_DELETE_METHOD);
                publisher.removeMethod(deleteMethod);
            }
        } else {
            final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(resource.getString("Messages.Error.MethodNotFound"), MessageType.INFO, null)
                    .createBalloon()
                    .show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.below);
        }
    }

    /**
     * Controls whether the action is enabled or disabled
     * @param event source  event
     */
    @Override
    public void update(AnActionEvent event) {

        //Disable/Enable action button
        if (JSONFileLoader.isFileSelected())
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}
