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
 * Groups action that can be used to manage methods.
 * @author Oshando Johnson
 */

public class MethodActionGroup extends ActionGroup {

    private MethodWrapper methodWrapper;

    public MethodActionGroup() {

    }

    public MethodActionGroup(MethodWrapper method) {

        this.methodWrapper = method;
    }

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

    @Override
    public void update(AnActionEvent event) {

        //Disable/Enable action button
        if (JSONFileLoader.isFileSelected())
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}
