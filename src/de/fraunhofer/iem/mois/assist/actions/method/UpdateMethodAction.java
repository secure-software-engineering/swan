package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.Method;
import de.fraunhofer.iem.mois.assist.ui.MethodDialog;

public class UpdateMethodAction extends AnAction {

    Method method;
    public UpdateMethodAction(Method method){
        super("Update");
        this.method = method;
    }
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);


        MethodDialog dialog = new MethodDialog(method, project, JSONFileLoader.getCategories());
        dialog.setTitle("Update Method");
        dialog.pack();
        dialog.setSize(550, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
