package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.ui.MethodPropertiesDialog;
import de.fraunhofer.iem.mois.assist.util.Constants;

/**
 * Action to show additional properties for a method.
 * @author Oshando Johnson
 */

public class MethodPropertiesAction extends AnAction{

    private MethodWrapper method;

    MethodPropertiesAction(MethodWrapper method){
        super("Properties");
        this.method = method;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        MethodPropertiesDialog detailsDialog = new MethodPropertiesDialog(method);
        detailsDialog.setTitle(Constants.TITLE_METHOD_PROPERTIES);
        detailsDialog.pack();
        detailsDialog.setSize(550, 350);
        detailsDialog.setLocationRelativeTo(null);
        detailsDialog.setVisible(true);
    }
}
