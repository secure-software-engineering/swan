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
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.assist.util.PsiTraversal;

/**
 * Action to show additional properties for a method.
 *
 * @author Oshando Johnson
 */

public class MethodPropertiesAction extends AnAction {

    private MethodWrapper method;

    public MethodPropertiesAction() {
        this.method = null;
    }

    public MethodPropertiesAction(MethodWrapper method) {
        super("Properties");
        this.method = method;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        //Get all the required data from data keys
        final Project project = anActionEvent.getProject();

        if (PsiTraversal.isFromEditor(anActionEvent))
            method = PsiTraversal.getMethodAtOffset(anActionEvent, false);

        if (method != null) {
            MethodPropertiesDialog detailsDialog = new MethodPropertiesDialog(project, method);
            detailsDialog.show();
        } else {
            final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(Constants.METHOD_NOT_FOUND, MessageType.INFO, null)
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
