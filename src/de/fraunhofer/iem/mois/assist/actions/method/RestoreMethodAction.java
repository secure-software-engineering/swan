package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.MethodNotifier;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.ui.SummaryToolWindow;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.assist.util.PsiTraversal;

/**
 * Action to restore a method that was deleted after rerunning MOIS.
 *
 * @author Oshando Johnson
 */

public class RestoreMethodAction extends AnAction {

    private MethodWrapper method;


    public RestoreMethodAction() {
    }

    public RestoreMethodAction(MethodWrapper method) {
        super("Restore");
        this.method = method;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);

        if (PsiTraversal.isFromEditor(anActionEvent))
            method = PsiTraversal.getMethodAtOffset(anActionEvent, false);

        if (method != null) {
            //Notify Summary Tool window that new method was restored
            MessageBus messageBus = project.getMessageBus();
            MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.METHOD_UPDATED_ADDED_TOPIC);
            publisher.afterAction(method);

            Messages.showMessageDialog(project, Constants.MSG_METHOD_RESTORED, Constants.TITLE_RESTORE_METHOD, Messages.getInformationIcon());
        } else {
            final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(Constants.METHOD_NOT_FOUND, MessageType.INFO, null)
                    .createBalloon()
                    .show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.below);
        }
    }

    @Override
    public void update(AnActionEvent event) {

        if (SummaryToolWindow.RESTORE_METHOD && JSONFileLoader.isFileSelected())
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}
