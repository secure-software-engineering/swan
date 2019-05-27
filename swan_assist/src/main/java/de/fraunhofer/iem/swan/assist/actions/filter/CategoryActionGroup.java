/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions.filter;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.util.Formatter;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.assist.util.Constants;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Manages group of categories that is used for filtering.
 */
public class CategoryActionGroup extends ActionGroup {

    /**
     * Creates new category action group.
     */
    public CategoryActionGroup() {
    }

    /**
     * Creates new category action group.
     *
     * @param name  the name of the category
     * @param popup whether or not the group is a popup menu
     */
    public CategoryActionGroup(String name, boolean popup) {
        super(name, popup);
    }

    /**
     * Obtains all supported types and CWEs and builds filter groups.
     *
     * @param anActionEvent event that triggered the action
     * @return An array of categories that should be added to the menu.
     */
    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {

        ArrayList<FilterAction> cweFilter = new ArrayList<FilterAction>();
        ArrayList<FilterAction> typeFilter = new ArrayList<FilterAction>();

        if (JSONFileLoader.isFileSelected()) {

            for (Category category : JSONFileLoader.getCategories()) {

                if (category.isCwe())
                    cweFilter.add(new FilterAction(new Pair<>(Constants.FILTER_CWE, Formatter.toTitleCase(category.toString()))));
                else
                    typeFilter.add(new FilterAction(new Pair<>(Constants.FILTER_TYPE, Formatter.toTitleCase(category.toString()))));
            }
        }

        if (this.toString().contains(Constants.FILTER_CWE))
            return cweFilter.toArray(new FilterAction[cweFilter.size()]);
        else
            return typeFilter.toArray(new FilterAction[typeFilter.size()]);
    }

    @Override
    public boolean isPopup() {
        return true;
    }
}
