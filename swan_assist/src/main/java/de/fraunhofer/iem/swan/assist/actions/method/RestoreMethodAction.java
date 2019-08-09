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
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.MethodNotifier;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.ui.MethodListTree;
import de.fraunhofer.iem.swan.assist.util.PsiTraversal;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * Action to restore a method that was deleted after rerunning SWAN.
 */

public class RestoreMethodAction extends AnAction {

    private MethodWrapper method;

    /**
     * Initializes action
     */
    public RestoreMethodAction() {
    }

    /**
     * Initializes action using method.
     * @param method Method that will be restored.
     */
    public RestoreMethodAction(MethodWrapper method) {
        super("Restore");
        this.method = method;
    }

    /**
     * Obtains method, if not provided, and then sends notification to restore method.
     * @param anActionEvent source event
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");

        if (PsiTraversal.isFromEditor(anActionEvent))
            method = PsiTraversal.getMethodAtOffset(anActionEvent, false);

        if (method != null) {

            int confirmation = JOptionPane.showConfirmDialog(null, resource.getString("Messages.Confirmation.RestoreMethod"), resource.getString("Messages.Title.RestoreMethod"), JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {

                //Notify Summary Tool window that new method was restored
                method.setUpdateOperation(null);
                MessageBus messageBus = project.getMessageBus();
                MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.ADD_UPDATE_DELETE_METHOD);
                publisher.restoreMethod(method);
            }
        }
    }

    /**
     * Controls whether the action is enabled or disabled
     * @param event source  event
     */
    @Override
    public void update(AnActionEvent event) {

        if (MethodListTree.RESTORE_METHOD && JSONFileLoader.isFileSelected())
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}
