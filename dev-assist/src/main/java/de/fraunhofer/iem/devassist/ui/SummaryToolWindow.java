/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.devassist.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.devassist.comm.ConfigurationFileNotifier;
import de.fraunhofer.iem.devassist.util.Constants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;

/**
 * Tool Window implementation for the Plugin.
 */
public class SummaryToolWindow implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        JBPanel toolPanel = new JBPanel(new BorderLayout());

        //Toolbar action panel
        final DefaultActionGroup actions = (DefaultActionGroup) ActionManager.getInstance().getAction("Dev_Assist.ActionBar");
        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("SummaryToolbar", actions, true);
        actionToolbar.setTargetComponent(toolPanel);

        if (toolWindow.getAnchor().isHorizontal()) {
            actionToolbar.setOrientation(SwingConstants.VERTICAL);
            toolPanel.add(actionToolbar.getComponent(), BorderLayout.LINE_START);
        } else {
            actionToolbar.setOrientation(SwingConstants.HORIZONTAL);
            toolPanel.add(actionToolbar.getComponent(), BorderLayout.PAGE_START);
        }

        //Add method list tree to tool window
        toolPanel.add(new JBScrollPane(new MethodListTree(project)), BorderLayout.CENTER);

        //If a configuration file was already selected, load it
        if (PropertiesComponent.getInstance(project).isValueSet(Constants.LAST_SRM_LIST)) {

            File configFile = new File(Objects.requireNonNull(PropertiesComponent.getInstance(project).getValue(Constants.LAST_SRM_LIST)));

            if (configFile.exists()) {

                MessageBus messageBus = project.getMessageBus();
                ConfigurationFileNotifier publisher = messageBus.syncPublisher(ConfigurationFileNotifier.FILE_NOTIFIER_TOPIC);
                publisher.loadInitialFile(PropertiesComponent.getInstance(project).getValue(Constants.LAST_SRM_LIST));

                actionToolbar.updateActionsImmediately();
            }
        }

        //Add Content to ToolWindow
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content toolContent = contentFactory.createContent(toolPanel, "", false);
        toolWindow.getContentManager().addContent(toolContent);
    }
}
