package de.fraunhofer.iem.swan.assist.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import de.fraunhofer.iem.swan.assist.data.InfoBank;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.data.CWE;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.ResourceBundle;

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

        ResourceBundle resourceBundle = ResourceBundle.getBundle("dialog_messages");
        //Table Columns
        Object[] columnNames = {resourceBundle.getString("Properties.Category"),resourceBundle.getString("Properties.Value") };
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

        //TODO show description in textArea or description field
        //description.setContentType("text/html");
        description.setBorder(null);
        //description.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        description.setOpaque(true);

        setTitle(resourceBundle.getString("Properties.Title"));
        init();

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {

                if (table.getValueAt(table.getSelectedRow(), 0).toString().contains(Constants.FILTER_CWE)) {

                    InfoBank info = new InfoBank();
                    ArrayList<CWE> cwe = info.getCWEDetails(table.getValueAt(table.getSelectedRow(), 1).toString());

                    StringBuilder cweDescription = new StringBuilder();

                    for (CWE entry : cwe) {
                        cweDescription.append("<b>CWE" + entry.getId() + " " + entry.getName() + "</b><p><b>Description: </b>" + entry.getShortName() + "</p>");
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
