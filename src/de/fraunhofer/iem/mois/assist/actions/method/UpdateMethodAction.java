package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.ui.MethodDialog;
import de.fraunhofer.iem.mois.assist.util.Constants;

/**
 * Action to add update the selected method.
 *
 * @author Oshando Johnson
 */

public class UpdateMethodAction extends AnAction {

    MethodWrapper method;

    public UpdateMethodAction(MethodWrapper method) {
        super("Update");
        this.method = method;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);

        MethodDialog dialog = new MethodDialog(method, project, JSONFileLoader.getCategories());

        if (method.isNewMethod())
            dialog.setTitle(Constants.TITLE_ADD_METHOD);
        else
            dialog.setTitle(Constants.TITLE_UPDATE_METHOD);

        dialog.pack();
        dialog.setSize(550, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
