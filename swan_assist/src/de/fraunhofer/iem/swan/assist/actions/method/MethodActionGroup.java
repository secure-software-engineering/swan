/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions.method;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Groups action that are used to manage methods.
 */

public class MethodActionGroup extends ActionGroup {

    private MethodWrapper methodWrapper;
    /**
     * Initializes action group
     */
    public MethodActionGroup() {

    }

    /**
     * Initializes action using method
     * @param method method that the actions can be applied to
     */
    public MethodActionGroup(MethodWrapper method) {

        this.methodWrapper = method;
    }

    /**
     * Returns all actions that can be used on on the method.
     * @param anActionEvent source event
     * @return array of possible actions
     */
    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
        return new AnAction[]{
                new UpdateMethodAction(methodWrapper),
                new RestoreMethodAction(methodWrapper),
                new DeleteMethodAction(methodWrapper),
                new Separator(),
                new MethodPropertiesAction(methodWrapper)};
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
