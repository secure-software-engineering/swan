package de.fraunhofer.iem.mois.assist.actions.method;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.ui.SummaryToolWindow;
import de.fraunhofer.iem.mois.assist.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Action to add a new method by selecting a class\category.
 *
 * @author Oshando Johnson
 */

public class AddMethodAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        //Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        //Get offset for element
        int offset = editor.getCaretModel().getOffset();

        PsiJavaFile psiFile;

        if (e.getData(LangDataKeys.PSI_FILE) instanceof PsiJavaFile) {

            psiFile = (PsiJavaFile) e.getData(LangDataKeys.PSI_FILE);

            if (psiFile != null) {

                PsiElement element = psiFile.findElementAt(offset);

                if (element != null) {

                    PsiMethod psiMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

                    if (psiMethod != null) {

                        //Determine method return type
                        String returnType = psiMethod.getReturnType().getCanonicalText();

                        //Obtain parameters
                        List<String> parameters = new ArrayList<String>();
                        for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {

                            PsiClass psiClass = PsiTypesUtil.getPsiClass(psiParameter.getType());

                            if (psiClass != null) {
                                parameters.add(psiClass.getQualifiedName());
                            } else if (psiParameter.getType() instanceof PsiArrayType) {


                                PsiArrayType psiArrayType = (PsiArrayType) psiParameter.getType();
                                parameters.add(psiArrayType.getCanonicalText());
                            } else
                                parameters.add(psiParameter.getType().getCanonicalText());

                        }

                        //Get class
                        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiMethod, PsiClass.class);

                        MethodWrapper method = new MethodWrapper(psiMethod.getName(), parameters, returnType, psiClass.getQualifiedName());

                        if (!JSONFileLoader.methodExists(method.getSignature(true))) {
                            method.setNewMethod(true);
                        } else {
                            method = JSONFileLoader.getMethod(method.getSignature(true));
                        }

                        ActionManager.getInstance().tryToExecute(new UpdateMethodAction(method), e.getInputEvent(), null, "Add Method", false);

                    } else {
                        Messages.showMessageDialog(project, Constants.METHOD_NOT_FOUND, "Method Selection", Messages.getInformationIcon());
                    }
                } else {
                    Messages.showMessageDialog(project, Constants.ELEMENT_NOT_SELECTED, "Method Selection", Messages.getInformationIcon());
                }
            }
        } else {
            Messages.showMessageDialog(project, Constants.NOT_JAVA_FILE, "Incorrect File", Messages.getInformationIcon());
        }
    }

    @Override
    public void update(AnActionEvent event) {

        //Disable/Enable action button
        if (SummaryToolWindow.CONFIG_FILE_SELECTED)
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}
