package de.fraunhofer.iem.mois.assist.actions.filter;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.FilterNotifier;
import de.fraunhofer.iem.mois.assist.ui.SummaryToolWindow;
import de.fraunhofer.iem.mois.assist.util.Constants;
import icons.PluginIcons;
import javafx.util.Pair;

/**
 * Action to filter list of methods.
 *
 * @author Oshando Johnson
 */

public class FilterAction extends AnAction {

    private Pair<String, String> filterPair;

    public FilterAction() {

    }

    public FilterAction(Pair<String, String> filter) {
        super(filter.getValue());

        filterPair = filter;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();

        switch (filterPair.getKey()) {

            case Constants.FILTER_CURRENT_FILE_KEY:

                SummaryToolWindow.CURRENT_FILE_FILTER = !SummaryToolWindow.CURRENT_FILE_FILTER;
                SummaryToolWindow.CURRENT_PROJECT_FILTER = false;
                break;
            case Constants.FILTER_CURRENT_PROJECT_KEY:
                SummaryToolWindow.CURRENT_FILE_FILTER = false;
                SummaryToolWindow.CURRENT_PROJECT_FILTER = !SummaryToolWindow.CURRENT_PROJECT_FILTER;
                break;
            case Constants.FILTER_CLEAR_KEY:

                SummaryToolWindow.TREE_FILTERS.clear();
                break;
            default:

                if (SummaryToolWindow.TREE_FILTERS.contains(filterPair.getValue()))
                    SummaryToolWindow.TREE_FILTERS.remove(filterPair.getValue());
                else
                    SummaryToolWindow.TREE_FILTERS.add(filterPair.getValue());
                break;
        }

        FilterNotifier filterNotifier = messageBus.syncPublisher(FilterNotifier.FILTER_SELECTED_TOPIC);
        filterNotifier.updateFilter(filterPair.getValue());
    }


    @Override
    public void update(AnActionEvent event) {

        //Set/unset icon for filters
        if (SummaryToolWindow.TREE_FILTERS.contains(filterPair.getValue()) || (filterPair.getKey().equals(Constants.FILTER_CURRENT_FILE_KEY) && SummaryToolWindow.CURRENT_FILE_FILTER) || (filterPair.getKey().equals(Constants.FILTER_CURRENT_PROJECT_KEY) && SummaryToolWindow.CURRENT_PROJECT_FILTER))
            event.getPresentation().setIcon(PluginIcons.SELECTED);
        else
            event.getPresentation().setIcon(null);
    }
}