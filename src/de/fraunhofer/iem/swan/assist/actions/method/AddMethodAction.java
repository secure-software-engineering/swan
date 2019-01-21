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
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.assist.util.PsiTraversal;

/**
 * Action to add a new method by selecting a class\category.
 *
 * @author Oshando Johnson
 */

public class AddMethodAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        MethodWrapper method = PsiTraversal.getMethodAtOffset(e, true);

        if (method != null) {

            ActionManager.getInstance().tryToExecute(new UpdateMethodAction(method), e.getInputEvent(), null, "Add Method", false);
        } else {
            final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(Constants.ELEMENT_NOT_SELECTED, MessageType.INFO, null)
                    .createBalloon()
                    .show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.below);
        }
    }

    @Override
    public void update(AnActionEvent event) {

        //Disable/Enable action button
        if (JSONFileLoader.isFileSelected())
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}
