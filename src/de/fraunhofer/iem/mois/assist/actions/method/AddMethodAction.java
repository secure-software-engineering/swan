package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.assist.util.PsiTraversal;

/**
 * Action to add a new method by selecting a class\category.
 *
 * @author Oshando Johnson
 */

public class AddMethodAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        //Get all the required data from data keys
        final Project project = e.getProject();

        MethodWrapper method = PsiTraversal.getMethodAtOffset(e, true);

        if (method != null) {

            ActionManager.getInstance().tryToExecute(new UpdateMethodAction(method), e.getInputEvent(), null, "Add Method", false);
        } else {
            Messages.showMessageDialog(project, Constants.ELEMENT_NOT_SELECTED, "Method Selection", Messages.getInformationIcon());
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
