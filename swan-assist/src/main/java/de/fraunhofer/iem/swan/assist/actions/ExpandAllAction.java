package de.fraunhofer.iem.swan.assist.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.ExpandNotifier;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.ui.MethodListTree;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

/**
 * @author Oshando Johnson on 2020-01-07
 */
public class ExpandAllAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        ExpandNotifier publisher = messageBus.syncPublisher(ExpandNotifier.EXPAND_COLLAPSE_LIST);
        publisher.expandTree(true);

    }

    /**
     * Controls whether the action is enabled or disabled
     * @param event source  event
     */
    @Override
    public void update(@NotNull AnActionEvent event) {

        //Disable/Enable action button
        if (JSONFileLoader.isFileSelected())
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);


        //Disable/Enable action button
        if (MethodListTree.TREE_EXPANDED){
            event.getPresentation().setIcon(AllIcons.Actions.Collapseall);
            event.getPresentation().setText("Collapse Tree");
        } else{
            event.getPresentation().setIcon(AllIcons.Actions.Expandall);
            event.getPresentation().setText("Expand Tree");
        }
    }

}
