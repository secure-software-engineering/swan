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
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.ui.dialog.MethodPropertiesDialog;
import de.fraunhofer.iem.swan.assist.util.PsiTraversal;

import java.util.ResourceBundle;

/**
 * Action opens a dialog and shows additional method properties.
 */

public class MethodPropertiesAction extends AnAction {

    private MethodWrapper method;

    public MethodPropertiesAction() {
        this.method = null;
    }

    /**
     * Initializes action using the method.
     * @param method The properties of this method will be loaded.
     */
    public MethodPropertiesAction(MethodWrapper method) {
        super("Properties");
        this.method = method;
    }

    /**
     * Obtains method, if not provided, and then launches dialog that shows properties.
     * @param anActionEvent source event
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        //Get all the required data from data keys
        final Project project = anActionEvent.getProject();
        ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");

        if (PsiTraversal.isFromEditor(anActionEvent))
            method = PsiTraversal.getMethodAtOffset(anActionEvent, false);

        if (method != null) {
            MethodPropertiesDialog detailsDialog = new MethodPropertiesDialog(project, method);
            detailsDialog.show();
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
