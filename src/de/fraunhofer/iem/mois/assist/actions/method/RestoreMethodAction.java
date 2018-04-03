package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.fraunhofer.iem.mois.assist.data.Method;
import de.fraunhofer.iem.mois.assist.ui.SummaryToolWindow;

public class RestoreMethodAction extends AnAction {

    private Method method;

    RestoreMethodAction(Method method){
        super("Restore");
        this.method = method;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

    }

    @Override
    public void update(AnActionEvent event){

        if(SummaryToolWindow.RESTORE_METHOD)
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}
