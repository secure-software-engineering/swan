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
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.SuggestNotifier;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.PropertiesManager;
import de.fraunhofer.iem.swan.assist.util.Constants;

public class SuggestAction extends AnAction {

    /**
     * Obtains suggested methods from SWAN and loads them in a dialog for user classification.
     * @param e source event
     */
    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();

        //Set output directory for plugin, if not set
        PropertiesManager.setProjectOutputPath(project);

        SuggestThread suggestThread = new SuggestThread(project,
                PropertiesComponent.getInstance(project).getValue(Constants.CONFIGURATION_FILE),
                PropertiesComponent.getInstance(project).getValue(Constants.SOURCE_DIRECTORY));
        suggestThread.start();

        SuggestNotifier suggestNotifier = messageBus.syncPublisher(SuggestNotifier.SUGGEST_METHOD_TOPIC);
        suggestNotifier.startSuggestMethod();
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
