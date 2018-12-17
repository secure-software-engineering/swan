package de.fraunhofer.iem.mois.assist.ui.markers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.Function;

/**
 * @author Oshando Johnson on 12.12.18
 */
public class TooltipProvider implements Function<PsiElement, String> {

    private final String toolTip;

    TooltipProvider(String text) {
        toolTip = text;
    }

    public String fun(PsiElement psiElement) {
        return toolTip;
    }
}