package de.fraunhofer.iem.mois.assist.ui;

import de.fraunhofer.iem.mois.assist.data.CWE;
import de.fraunhofer.iem.mois.assist.data.InfoBank;
import de.fraunhofer.iem.mois.assist.data.Method;
import de.fraunhofer.iem.mois.assist.util.Constants;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.*;
import java.util.ArrayList;

public class MethodPropertiesDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable table;
    private JTextPane description;

    public MethodPropertiesDialog(Method method) {

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        //Table Columns
        Object[] columnNames = {Constants.TABLE_HEADER_PROPERTY, Constants.TABLE_HEADER_VALUE};
        //Table data
        Object[][] values = {{Constants.RETURN_TYPE_LABEL, method.getReturnType(true)},
                {Constants.METHOD_NAME_LABEL, method.getClassName(true)},
                {Constants.PARAMETERS, method.getParameter(true)},
                {Constants.SECURITY_LEVEL_LABEL, method.getSecLevel()},
                {Constants.DISCOVERY_LABEL, method.getDiscovery()},
                {Constants.FRAMEWORK_LABEL, method.getFramework()},
                {Constants.CWE_LABEL, StringUtils.join(method.getCWEList(), ", ")},
                {Constants.TYPE_LABEL, StringUtils.join(method.getTypesList(), ", ")},
                {Constants.COMMENT_LABEL, method.getComment()},};

        TableModel tableModel = new DefaultTableModel(values, columnNames);

        table.setModel(tableModel);

        //TODO show description in textArea or description field
        //    description.setContentType("text/html");
        description.setBorder(null);
        //  description.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        description.setOpaque(true);


        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {

                if (table.getValueAt(table.getSelectedRow(), 0).toString().contains(Constants.FILTER_CWE)) {

                    InfoBank info = new InfoBank();
                    ArrayList<CWE> cwe = info.getCWEDetails(table.getValueAt(table.getSelectedRow(), 1).toString());

                    StringBuilder cweDescription = new StringBuilder();

                    for (CWE entry : cwe) {
                        cweDescription.append("<br/><b>CWE" + entry.getCweId() + " " + entry.getName() + "</b><p><b>Description: </b>" + entry.getDescription() + "</p>");
                    }

                    description.setText("<html>" + cweDescription + "</html>");
                }
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
