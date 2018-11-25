package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.MethodNotifier;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.util.Constants;

import javax.swing.*;

/**
 * Action to delete a method.
 * @author Oshando Johnson
 */


public class DeleteMethodAction extends AnAction {

    private MethodWrapper deleteMethod;

    DeleteMethodAction(MethodWrapper method){
        super("Delete");
        deleteMethod = method;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);

        int confirmation = JOptionPane.showConfirmDialog(null, Constants.CONFIRM_METHOD_DELETION, Constants.TITLE_DELETE_METHOD, JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {

            MessageBus messageBus = project.getMessageBus();
            MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.METHOD_REMOVED_TOPIC);
            publisher.afterAction(deleteMethod);
        }
    }
}
