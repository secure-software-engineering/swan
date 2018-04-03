package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import de.fraunhofer.iem.mois.assist.data.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MethodActionGroup extends ActionGroup {

    private Method method;

    public MethodActionGroup() {

    }

    public MethodActionGroup(Method method) {

        this.method = method;
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
        return new AnAction[]{
                new UpdateMethodAction(method),
                new RestoreMethodAction(method),
                new DeleteMethodAction(method),
                new Separator(),
                new MethodPropertiesAction(method),};
    }

    @Override
    public boolean isPopup() {
        return true;
    }
}
