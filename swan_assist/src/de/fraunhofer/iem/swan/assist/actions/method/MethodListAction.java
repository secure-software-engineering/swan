package de.fraunhofer.iem.swan.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;

/**
 * @author Oshando Johnson on 14.12.18
 */
public class MethodListAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getProject();

        ToolWindowManager.getInstance(project).getToolWindow("SWAN_Assist").show(null);
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
