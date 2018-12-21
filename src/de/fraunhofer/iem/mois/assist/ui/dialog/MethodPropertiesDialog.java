package de.fraunhofer.iem.mois.assist.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import de.fraunhofer.iem.mois.assist.data.InfoBank;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.data.CWE;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Shows additional properties of a method.
 *
 * @author Oshando Johnson
 */

public class MethodPropertiesDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTable table;
    private JTextPane description;

    public MethodPropertiesDialog(Project project, MethodWrapper method) {

        super(project);

        //Table Columns
        Object[] columnNames = {Constants.TABLE_HEADER_PROPERTY, Constants.TABLE_HEADER_VALUE};
        //Table data
        Object[][] values = {{Constants.RETURN_TYPE_LABEL, method.getReturnType(true)},
                {Constants.METHOD_NAME_LABEL, method.getMethodName(true)},
                {Constants.PARAMETER_LABEL, method.getParameter(true)},
                {Constants.SECURITY_LEVEL_LABEL, method.getMethod().getSecLevel()},
                {Constants.DISCOVERY_LABEL, method.getMethod().getDiscovery()},
                {Constants.FRAMEWORK_LABEL, method.getMethod().getFramework()},
                {Constants.CWE_LABEL, StringUtils.join(method.getCWEList(), ", ")},
                {Constants.TYPE_LABEL, StringUtils.join(method.getTypesList(true), ", ")},
                {Constants.COMMENT_LABEL, method.getMethod().getComment()},
        };

        TableModel tableModel = new DefaultTableModel(values, columnNames);

        table.setModel(tableModel);

        //TODO show description in textArea or description field
        //description.setContentType("text/html");
        description.setBorder(null);
        //description.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        description.setOpaque(true);

        setTitle(Constants.TITLE_METHOD_PROPERTIES);
        init();

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {

                if (table.getValueAt(table.getSelectedRow(), 0).toString().contains(Constants.FILTER_CWE)) {

                    InfoBank info = new InfoBank();
                    ArrayList<CWE> cwe = info.getCWEDetails(table.getValueAt(table.getSelectedRow(), 1).toString());

                    StringBuilder cweDescription = new StringBuilder();

                    for (CWE entry : cwe) {
                        cweDescription.append("<br/><b>CWE" + entry.getId() + " " + entry.getName() + "</b><p><b>Description: </b>" + entry.getLink() + "</p>");
                    }

                    description.setText("<html>" + cweDescription + "</html>");
                }
            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
