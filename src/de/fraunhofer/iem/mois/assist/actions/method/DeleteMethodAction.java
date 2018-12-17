package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.MethodNotifier;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.assist.util.PsiTraversal;

import javax.swing.*;

/**
 * Action to delete a method.
 *
 * @author Oshando Johnson
 */


public class DeleteMethodAction extends AnAction {

    private MethodWrapper deleteMethod;

    public DeleteMethodAction() {
        super("Delete");
        deleteMethod = null;
    }

    public DeleteMethodAction(MethodWrapper method) {
        super("Delete");
        deleteMethod = method;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getProject();

        if (PsiTraversal.isFromEditor(anActionEvent))
            deleteMethod = PsiTraversal.getMethodAtOffset(anActionEvent, false);

        if (deleteMethod != null) {
            int confirmation = JOptionPane.showConfirmDialog(null, Constants.CONFIRM_METHOD_DELETION, Constants.TITLE_DELETE_METHOD, JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {

                MessageBus messageBus = project.getMessageBus();
                MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.METHOD_REMOVED_TOPIC);
                publisher.afterAction(deleteMethod);
            }
        } else
            Messages.showMessageDialog(project, Constants.METHOD_NOT_FOUND, "Method Selection", Messages.getInformationIcon());
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
