/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions.method;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.util.PsiTraversal;

import java.util.ResourceBundle;

/**
 * Action to add a new method from the editor.
 */

public class AddMethodAction extends AnAction {

    /**
     * After add action is selected, the method properties are obtained using PSI and the new method is created.
     * A dialog appears where the user can select the types and categories. Notifiation is then created for new method.
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {

        MethodWrapper method = PsiTraversal.getMethodAtOffset(e, true);

        if (method != null) {

            method.setStatus(MethodWrapper.MethodStatus.NEW);
            ActionManager.getInstance().tryToExecute(new UpdateMethodAction(method), e.getInputEvent(), null, "Add Method", false);
        } else {
            final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

            ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(resource.getString("Messages.Error.ElementNotSelected"), MessageType.INFO, null)
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
