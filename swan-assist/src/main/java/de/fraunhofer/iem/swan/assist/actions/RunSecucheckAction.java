package de.fraunhofer.iem.swan.assist.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import de.fraunhofer.iem.swan.assist.analysis.SecucheckBackgroundTask;
import de.fraunhofer.iem.swan.assist.comm.SecucheckNotifier;
import org.jetbrains.annotations.NotNull;

public class RunSecucheckAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        ProgressManager.getInstance().run(new SecucheckBackgroundTask(project, "Running Taint Analysis with SecuCheck", true,
                PerformInBackgroundOption.ALWAYS_BACKGROUND));

        SecucheckNotifier publisher = project.getMessageBus().syncPublisher(SecucheckNotifier.START_SECUCHECK_PROCESS_TOPIC);
        publisher.launchSecuCheck();

    }
}
