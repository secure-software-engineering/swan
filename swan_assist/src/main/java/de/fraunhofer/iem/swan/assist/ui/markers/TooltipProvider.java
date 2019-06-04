/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.ui.markers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.Function;

/**
 * Creates tooltip for line markers
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