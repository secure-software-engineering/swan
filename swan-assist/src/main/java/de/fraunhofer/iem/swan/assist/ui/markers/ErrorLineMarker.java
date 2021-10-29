/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.ui.markers;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import de.fraunhofer.iem.swan.assist.actions.method.UpdateMethodAction;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.util.PsiTraversal;
import icons.IconUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

/**
 * Queries the list of methods that have been annotated and creates
 * gutter icons for them.
 */
public class ErrorLineMarker implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {

        final GutterIconNavigationHandler<PsiElement> navigationHandler = new GutterIconNavigationHandler<PsiElement>() {
            @Override
            public void navigate(MouseEvent mouseEvent, PsiElement psiElement) {

                ActionManager.getInstance().tryToExecute(new UpdateMethodAction(JSONFileLoader.getMethod(PsiTraversal.getMethodSignature((PsiMethod) psiElement.getParent()))), mouseEvent, null, "Add Method", false);
            }
        };

        if (psiElement instanceof PsiIdentifier && psiElement.getParent() instanceof PsiMethod && JSONFileLoader.isFileSelected()) {

            PsiMethod psiMethod = (PsiMethod) psiElement.getParent();

            String methodSignature = PsiTraversal.getMethodSignature(psiMethod);

            if (JSONFileLoader.methodExists(methodSignature)) {

                MethodWrapper method = JSONFileLoader.getMethod(methodSignature);

                return new LineMarkerInfo<>(psiElement,
                        psiElement.getTextRange(),
                        IconUtils.getNodeIcon(method.getTypesList(false)),
                        Pass.UPDATE_ALL,
                        new TooltipProvider(method.getMarkerMessage()),
                        navigationHandler,
                        GutterIconRenderer.Alignment.LEFT);

            } else
                return null;
        }
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> list, @NotNull Collection<LineMarkerInfo> collection) {

    }
}
