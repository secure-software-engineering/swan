package de.fraunhofer.iem.devassist.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import de.fraunhofer.iem.devassist.analysis.SecucheckBackgroundTask;
import de.fraunhofer.iem.devassist.comm.SecucheckNotifier;
import de.fraunhofer.iem.devassist.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class RunSecucheckAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        if (!PropertiesComponent.getInstance(project).isTrueValue(Constants.SWAN_SETTINGS)) {
            e.getActionManager().getAction("Dev_Assist.SettingsAction").actionPerformed(e);
            //TODO Run SWAN if the tool has been configured
        } else {
            runSecucheck(project);
        }
    }

    public void runSecucheck(Project project) {

        File outputFolder = new File(Objects.requireNonNull(PropertiesComponent.getInstance(project).getValue(Constants.OUTPUT_DIRECTORY)));

        if (!outputFolder.exists())
            outputFolder.mkdir();

        ProgressManager.getInstance().run(new SecucheckBackgroundTask(project, "Running Taint Analysis", true,
                PerformInBackgroundOption.ALWAYS_BACKGROUND));

        SecucheckNotifier publisher = project.getMessageBus().syncPublisher(SecucheckNotifier.START_SECUCHECK_PROCESS_TOPIC);
        publisher.launchSecuCheck();
    }
}
