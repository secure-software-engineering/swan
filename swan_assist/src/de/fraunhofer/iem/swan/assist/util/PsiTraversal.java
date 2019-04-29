package de.fraunhofer.iem.swan.assist.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.impl.ActionMenuItem;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Methods to find PSI methods using the PSI object or event.
 *
 * @author Oshando Johnson on 12.12.18
 */
public class PsiTraversal {

    private static List<String> getParameters(PsiMethod psiMethod) {

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
        return parameters;
    }

    public static boolean isFromEditor(AnActionEvent anActionEvent) {

        if (anActionEvent.getInputEvent().getSource() instanceof ActionMenuItem) {

            ActionMenuItem menuItem = (ActionMenuItem) anActionEvent.getInputEvent().getSource();

            return (menuItem.getPlace().equals("EditorPopup") && anActionEvent.getData(LangDataKeys.PSI_FILE) instanceof PsiJavaFile);
        }
        return false;
    }

    public static MethodWrapper getMethodAtOffset(AnActionEvent anActionEvent, boolean createIfNotFound) {

        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        PsiJavaFile javaFile = (PsiJavaFile) anActionEvent.getData(LangDataKeys.PSI_FILE);
        //Get offset for element
        int offset = editor.getCaretModel().getOffset();

        if (javaFile != null) {
            PsiElement element = javaFile.findElementAt(offset);

            if (element != null) {

                PsiMethod psiMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

                if (psiMethod != null) {

                    if (JSONFileLoader.methodExists(getMethodSignature(psiMethod))) {
                        return JSONFileLoader.getMethod(getMethodSignature(psiMethod));
                    } else if (createIfNotFound) {

                        //Determine method return type
                        String returnType = psiMethod.getReturnType().getCanonicalText();

                        //Get class
                        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiMethod, PsiClass.class);

                        MethodWrapper method = new MethodWrapper(psiMethod.getName(), PsiTraversal.getParameters(psiMethod), returnType, psiClass.getQualifiedName());

                        return method;
                    } else {
                        return null;
                    }
                }
            }
        }

        return null;
    }


    public static String getMethodSignature(PsiMethod psiMethod) {

        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiMethod, PsiClass.class);

        String returnType ="";
        if(psiMethod.getReturnType()!=null){
            if(psiMethod.getReturnType() instanceof PsiPrimitiveType)
                returnType = ((PsiPrimitiveType) psiMethod.getReturnType()).getName();
            else
                returnType = psiMethod.getReturnType().getCanonicalText();
        }

        return  returnType+ " " +
                psiClass.getQualifiedName() + "." + psiMethod.getName() + " (" +
                StringUtils.join(getParameters(psiMethod), ", ") + ")";
    }
}
