/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions.suggest;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.SuggestNotifier;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.ui.dialog.SwanLauncherDialog;
import de.fraunhofer.iem.swan.assist.util.Constants;

import java.io.File;
import java.util.HashMap;

public class SuggestAction extends AnAction {

    /**
     * Obtains suggested methods from SWAN and loads them in a dialog for user classification.
     *
     * @param e source event
     */
    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();

        //Set output directory for plugin, if not set
        if (!PropertiesComponent.getInstance(project).isValueSet(Constants.SWAN_OUTPUT_DIR)) {

            setProjectPath(project);
        } else {
            File trainFile = new File(PropertiesComponent.getInstance(project).getValue(Constants.SWAN_OUTPUT_DIR));

            if (!trainFile.exists())
                setProjectPath(project);
        }

        SuggestThread suggestThread = new SuggestThread(project,
                "/Users/oshando/Projects/IdeaProjects/mois-evaluation/mois-rq3/resources/gxa-methods-r.json",
                "/Users/oshando/Projects/IdeaProjects/mois-evaluation/mois-executor/project-jars/gxa"
        );
        suggestThread.start();

        SuggestNotifier suggestNotifier = messageBus.syncPublisher(SuggestNotifier.SUGGEST_METHOD_TOPIC);
        suggestNotifier.startSuggestMethod();
    }

    private void setProjectPath(Project project) {

        //Launch SWAN Properties Dialog
        SwanLauncherDialog dialog = new SwanLauncherDialog(project, true);
        dialog.show();

        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {

            HashMap<String, String> swanParameters = dialog.getParameters();

            File outputFolder = new File(swanParameters.get(Constants.SWAN_OUTPUT_DIR));

            if(!outputFolder.exists())
                outputFolder.mkdir();

            PropertiesComponent.getInstance(project).setValue(Constants.SWAN_OUTPUT_DIR, swanParameters.get(Constants.SWAN_OUTPUT_DIR));




            System.out.println("Set project path: : "+ swanParameters.get(Constants.SWAN_OUTPUT_DIR));
        }
    }

    /**
     * Controls whether the action is enabled or disabled
     *
     * @param event source  event
     */
    @Override
    public void update(AnActionEvent event) {

        //Disable/Enable action button
        if (JSONFileLoader.isFileSelected())
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}
