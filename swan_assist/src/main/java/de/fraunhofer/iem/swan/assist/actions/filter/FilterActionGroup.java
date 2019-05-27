/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

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

    /**
     * Creates list of filters.
     * @param anActionEvent source event
     * @return An array of filters that can be applied to the list
     */
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

    /**
     * Controls whether the action is enabled or disabled
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
