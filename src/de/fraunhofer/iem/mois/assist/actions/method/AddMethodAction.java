package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.Method;
import de.fraunhofer.iem.mois.assist.ui.MethodDialog;
import de.fraunhofer.iem.mois.assist.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddMethodAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        //Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        PsiJavaFile java = (PsiJavaFile) e.getData(LangDataKeys.PSI_FILE);

        //Obtain selected method
        //TODO use signature to prevent problems with overloaded methods
        final SelectionModel selectionModel = editor.getSelectionModel();

        CaretModel caretModel = editor.getCaretModel();
        Caret caret = caretModel.getCurrentCaret();

        Method newMethod;
        boolean methodFound = false;

        if (!JSONFileLoader.isFileSelected())
            Messages.showMessageDialog(project, Constants.FILE_NOT_SELECTED, "Method Selection", Messages.getInformationIcon());
        else if (!selectionModel.hasSelection())
            Messages.showMessageDialog(project, Constants.METHOD_NOT_SELECTED, "Method Selection", Messages.getInformationIcon());
        else {

            //Obtain methods in class
            for (PsiClass psiClass : java.getClasses()) {
                for (PsiMethod psiMethod : psiClass.getMethods()) {

                    if (psiMethod.getName().equals(selectionModel.getSelectedText())) {

                        methodFound = true;

                        //Determine method return type
                        String returnType = psiMethod.getReturnType().getCanonicalText();

                        //Obtain parameters
                        List<String> parameters = new ArrayList<String>();
                        for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters())
                            parameters.add(psiParameter.getTypeElement().getType().getCanonicalText());

                        newMethod = new Method(psiClass.getQualifiedName() + "." + psiMethod.getName(), returnType, "", "", "", "", "");
                        newMethod.addParameter(parameters);
                        newMethod.setNewMethod(true);

                        MethodDialog methodDialog = new MethodDialog(newMethod, project, JSONFileLoader.getCategories());
                        methodDialog.setTitle("Add Method");
                        methodDialog.pack();
                        methodDialog.setSize(550,350);
                        methodDialog.setLocationRelativeTo(null);
                        methodDialog.setVisible(true);
                    }
                }
            }

            if (!methodFound)
                Messages.showMessageDialog(project, Constants.METHOD_NOT_FOUND, "Method Selection", Messages.getInformationIcon());
        }
    }
}
