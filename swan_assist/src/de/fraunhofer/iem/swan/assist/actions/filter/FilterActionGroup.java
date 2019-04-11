package de.fraunhofer.iem.swan.assist.actions.filter;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.util.Constants;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action group for filters that can be applied to list of methods.
 * @author Oshando Johnson
 */

public class FilterActionGroup extends ActionGroup {

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {

        return new AnAction[]{
                new FilterAction(Constants.FILE_FILTER),
                new Separator(),
                new CategoryActionGroup(Constants.FILTER_TYPE, true),
                new CategoryActionGroup(Constants.FILTER_CWE, true),
                new Separator(),
                new FilterAction(Constants.TRAIN_FILTER),
                new FilterAction(Constants.DELETED_FILTER),
                new Separator(),
                new FilterAction(Constants.CLEAR_FILTER)};
    }

    @Override
    public boolean isPopup() {
        return true;
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
