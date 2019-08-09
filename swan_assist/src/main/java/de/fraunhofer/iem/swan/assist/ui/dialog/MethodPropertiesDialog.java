/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.table.JBTable;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.ResourceBundle;

/**
 * Shows additional properties of a method.
 */

public class MethodPropertiesDialog extends DialogWrapper {
    private JPanel contentPane;
    private JBTable table;

    /**
     * Initializes the dialog using the given arguments
     * @param project Active project in IDE
     * @param method Method for which properties should be loaded
     */
    public MethodPropertiesDialog(Project project, MethodWrapper method) {

        super(project);

        ResourceBundle resourceBundle = ResourceBundle.getBundle("dialog_messages");
        //Table Columns
        Object[] columnNames = {resourceBundle.getString("Properties.Category"), resourceBundle.getString("Properties.Value") };
        //Table data
        Object[][] values = {{resourceBundle.getString("Properties.Return"), method.getReturnType(true)},
                {resourceBundle.getString("Properties.Method"), method.getMethodName(true)},
                {resourceBundle.getString("Properties.Parameters"), method.getParameter(true)},
                {resourceBundle.getString("Properties.Security"), method.getMethod().getSecLevel()},
                {resourceBundle.getString("Properties.Discovery"), method.getMethod().getDiscovery()},
                {resourceBundle.getString("Properties.Framework"), method.getMethod().getFramework()},
                {resourceBundle.getString("Properties.CWE"), StringUtils.join(method.getCWEList(), ", ")},
                {resourceBundle.getString("Properties.Type"), StringUtils.join(method.getTypesList(true), ", ")},
                {resourceBundle.getString("Properties.Comment"), method.getMethod().getComment()},
        };

        TableModel tableModel = new DefaultTableModel(values, columnNames);

        table.setModel(tableModel);
        table.getColumnModel().getColumn(0).setMinWidth(10);
        table.getColumnModel().getColumn(1).setMinWidth(300);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        init();
        setTitle(resourceBundle.getString("Properties.Title"));
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
