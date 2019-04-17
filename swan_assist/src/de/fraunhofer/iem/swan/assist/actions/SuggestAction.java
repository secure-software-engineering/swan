package de.fraunhofer.iem.swan.assist.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.ui.dialog.MethodDialog;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Oshando Johnson on 2019-04-11
 */
public class SuggestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here

        HashMap<String,MethodWrapper> suggestedMethods = new HashMap<>();

        MethodWrapper m1 = new MethodWrapper("getPassport", Arrays.asList("java.lang.String"), "java.lang.String", "de.fraunhofer.Test");
        m1.setStatus(MethodWrapper.MethodStatus.SUGGESTED);

        MethodWrapper m2 = new MethodWrapper("deleteApplication", Arrays.asList("java.lang.String"), "void", "de.fraunhofer.Test");
        m2.setStatus(MethodWrapper.MethodStatus.SUGGESTED);

        suggestedMethods.put(m1.getSignature(true),m1);
        suggestedMethods.put(m2.getSignature(true),m2);

        MethodDialog dialog  = new MethodDialog(suggestedMethods, m1.getSignature(true), e.getProject(), JSONFileLoader.getCategories());
        dialog.show();
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
