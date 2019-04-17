package de.fraunhofer.iem.swan.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.ui.dialog.MethodDialog;

import java.util.HashMap;

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

        MethodDialog dialog = null;

        if (method.getStatus()== MethodWrapper.MethodStatus.NEW){

            HashMap<String,MethodWrapper> methods = new HashMap<>();
            methods.put(method.getSignature(true),method);
            dialog = new MethodDialog(methods, method.getSignature(true), project, JSONFileLoader.getCategories());
        } else
            dialog = new MethodDialog(JSONFileLoader.getAllMethods(), method.getSignature(true), project, JSONFileLoader.getCategories());

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
