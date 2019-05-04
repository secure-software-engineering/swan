/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions.filter;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.FilterNotifier;
import de.fraunhofer.iem.swan.assist.ui.MethodListTree;
import icons.PluginIcons;
import javafx.util.Pair;

/**
 * Plugin action to filter method list.
 */

public class FilterAction extends AnAction {

    private Pair<String, String> filterPair;

    /**
     * Initializes action.
     */
    public FilterAction() {

    }

    /**
     * Initializes action using the key-value pair.
     * @param filter name and type of the filter
     */
    public FilterAction(Pair<String, String> filter) {
        super(filter.getValue());

        filterPair = filter;
    }

    /**
     * Listener for filter actions.
     * @param e source event
     */
    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();

        FilterNotifier filterNotifier = messageBus.syncPublisher(FilterNotifier.FILTER_SELECTED_TOPIC);
        filterNotifier.updateFilter(filterPair);
    }

    /**
     * Controls whether the action is enabled or disabled
     * @param event source  event
     */
    @Override
    public void update(AnActionEvent event) {

        //Set/unset icon for filters
        if ( MethodListTree.TREE_FILTERS.contains(filterPair))
            event.getPresentation().setIcon(PluginIcons.SELECTED);
        else
            event.getPresentation().setIcon(null);
    }
}