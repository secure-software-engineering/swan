/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.devassist.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import de.fraunhofer.iem.devassist.analysis.SwanBackgroundTask;
import de.fraunhofer.iem.devassist.comm.SwanNotifier;
import de.fraunhofer.iem.devassist.data.JSONFileLoader;
import de.fraunhofer.iem.devassist.util.Constants;
import de.fraunhofer.iem.swan.data.Method;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * Action runs SWAN using the configuration provided in the SettingsDialog. After which thread is created to run SWAN.
 */
public class RunSwanAction extends AnAction {

    protected Set<Method> methods = new HashSet<Method>();

    /**
     * Obtains application parameters from user, exports updated JSON file and starts thread to run SWAN.
     *
     * @param anActionEvent source event
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);

        Properties config = new Properties();
        InputStream input = null;

        try {
            input = getClass().getClassLoader().getResourceAsStream("config.properties");
            config.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!PropertiesComponent.getInstance(project).isTrueValue(Constants.SWAN_SETTINGS)) {
            anActionEvent.getActionManager().getAction("Dev_Assist.SettingsAction").actionPerformed(anActionEvent);
            //TODO Run SWAN if the tool has been configured
        } else {
            runSwan(project);
        }
    }

    public void runSwan(Project project) {

        File outputFolder = new File(Objects.requireNonNull(PropertiesComponent.getInstance(project).getValue(Constants.OUTPUT_DIRECTORY)));

        if (!outputFolder.exists())
            outputFolder.mkdir();

        ProgressManager.getInstance().run(new SwanBackgroundTask(project, "Detecting SRMs", true,
                PerformInBackgroundOption.ALWAYS_BACKGROUND));

        SwanNotifier publisher = project.getMessageBus().syncPublisher(SwanNotifier.START_SWAN_PROCESS_TOPIC);
        publisher.launchSwan(null);
    }

    /**
     * Controls whether the action is enabled or disabled
     *
     * @param event source  event
     */
    @Override
    public void update(AnActionEvent event) {

        //Disable/Enable action button
        if (JSONFileLoader.isReloading())
            event.getPresentation().setEnabled(false);
        else
            event.getPresentation().setEnabled(true);
    }
}