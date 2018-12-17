package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.impl.ActionMenuItem;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.EditorPopupHandler;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.ui.SummaryToolWindow;
import de.fraunhofer.iem.mois.assist.ui.dialog.MethodPropertiesDialog;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.assist.util.PsiTraversal;
import org.jetbrains.jsonProtocol.JsonField;

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
            MethodPropertiesDialog detailsDialog = new MethodPropertiesDialog(method);
            detailsDialog.setTitle(Constants.TITLE_METHOD_PROPERTIES);
            detailsDialog.pack();
            detailsDialog.setSize(550, 350);
            detailsDialog.setLocationRelativeTo(null);
            detailsDialog.setVisible(true);
        } else {
            Messages.showMessageDialog(project, Constants.METHOD_NOT_FOUND, "Method Selection", Messages.getInformationIcon());
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
