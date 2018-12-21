package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.ui.dialog.MethodDialog;

/**
 * Action to add update the selected method.
 *
 * @author Oshando Johnson
 */

public class UpdateMethodAction extends AnAction {

    private MethodWrapper method;

    public UpdateMethodAction(MethodWrapper method) {
        super("Update");
        this.method = method;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getProject();

        MethodDialog dialog = new MethodDialog(method, project, JSONFileLoader.getCategories());
        dialog.show();
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
