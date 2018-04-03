package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.MethodNotifier;
import de.fraunhofer.iem.mois.assist.data.Method;
import de.fraunhofer.iem.mois.assist.util.Constants;

import javax.swing.*;

public class DeleteMethodAction extends AnAction {

    Method deleteMethod;

    DeleteMethodAction(Method method){
        super("Delete");
        deleteMethod = method;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);

        int confirmation = JOptionPane.showConfirmDialog(null, Constants.CONFIRM_METHOD_DELETION, "Delete Method", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {

            MessageBus messageBus = project.getMessageBus();
            MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.METHOD_REMOVED_TOPIC);
            publisher.afterAction(deleteMethod);
        }
    }
}
