package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.fraunhofer.iem.mois.assist.data.Method;
import de.fraunhofer.iem.mois.assist.ui.MethodPropertiesDialog;

public class MethodPropertiesAction extends AnAction{

    private Method method;

    MethodPropertiesAction(Method method){
        super("Properties");
        this.method = method;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        MethodPropertiesDialog detailsDialog = new MethodPropertiesDialog(method);
        detailsDialog.setTitle("Method Details");
        detailsDialog.pack();
        detailsDialog.setSize(550, 350);
        detailsDialog.setLocationRelativeTo(null);
        detailsDialog.setVisible(true);
    }
}
