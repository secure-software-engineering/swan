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
import com.intellij.uiDesigner.core.GridConstraints;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.data.Category;
import jnr.ffi.annotations.In;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Shows additional properties of a method.
 */

public class MethodPropertiesDialog extends DialogWrapper {
    private JPanel contentPane;
    private JBTable propertiesTable;
    private JComboBox signatureCbx;
    private JPanel cweCheckBoxPanel;
    private JPanel srmCheckBoxPanel;
    private JTabbedPane tab;
    private JCheckBox dataInReturnCheckBox;
    private JCheckBox dataOutReturnCheckBox1;
    private JPanel parametersPanel;
    private JPanel dataOutPanel;
    private JPanel dataInPanel;
    private JButton updateButton;
    private List<JCheckBox> dataCheckBoxes;
    private ResourceBundle resourceBundle;


    /**
     * Initializes the dialog using the given arguments
     * @param project Active project in IDE
     * @param methods List of methods in the JComboBox
     * @param signature Signature of the method selected currently
     * @param categories list of SRM and CWE categories
     */
    public MethodPropertiesDialog(HashMap<String, MethodWrapper> methods, String signature, Project project, Set<Category> categories) {

        super(project);
        this.resourceBundle = ResourceBundle.getBundle("dialog_messages");
        updatePropertiesTable(methods.get(signature));
        updateCategoryTab(methods.get(signature), categories);
        for (MethodWrapper methodWrapper : methods.values()){
            signatureCbx.addItem(methodWrapper);
        }
        signatureCbx.setRenderer(new SignatureComboBoxRenderer());
        signatureCbx.setSelectedItem(methods.get(signature));
        updateCategoryTab(methods.get(signature), categories);
        updateParametersPanel(methods.get(signature));

        setSize(400, 400);
        init();

        signatureCbx.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JComboBox comboBox = (JComboBox) e.getSource();

                MethodWrapper selectedItem = (MethodWrapper) comboBox.getSelectedItem();

                updateParametersPanel(selectedItem);
                updatePropertiesTable(selectedItem);
                updateCategoryTab(selectedItem, categories);
            }
        });


    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    private void updateCategoryTab(MethodWrapper method, Set<Category> categories){
        srmCheckBoxPanel.removeAll();
        cweCheckBoxPanel.removeAll();
        srmCheckBoxPanel.setLayout(new GridLayout(2,4));
        cweCheckBoxPanel.setLayout(new GridLayout(7,4));
        Set<Category> methodCategory = method.getCategories();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("dialog_messages");
        for (Category category: categories) {
            JCheckBox checkBox = new JCheckBox(category.toString());
            if(methodCategory.contains(category)){
                checkBox.setSelected(true);
            }
            if(category.isCwe()){
                try {
                    checkBox.setToolTipText(resourceBundle.getString(category.toString() + ".Name"));
                } catch (Exception e){
                    checkBox.setToolTipText(category.toString());
                }
                cweCheckBoxPanel.setPreferredSize(new java.awt.Dimension(200, 200));
                cweCheckBoxPanel.add(checkBox, new GridConstraints());
            }else {
                srmCheckBoxPanel.setPreferredSize(new java.awt.Dimension(200, 200));
                srmCheckBoxPanel.add(checkBox, new GridConstraints());
            }
        }
        cweCheckBoxPanel.revalidate();
        srmCheckBoxPanel.revalidate();
        cweCheckBoxPanel.repaint();
        srmCheckBoxPanel.repaint();
    }


    private void updateParametersPanel(MethodWrapper method){
        // Clear existing data in the panels
        parametersPanel.removeAll();
        dataInPanel.removeAll();
        dataOutPanel.removeAll();
        parametersPanel.setLayout(new GridLayout(0,1));
        dataInPanel.setLayout(new GridLayout(0,1));
        dataOutPanel.setLayout(new GridLayout(0,1));

        // Add the parameter value and the checkboxes to the panels
        List<String> parametersList = method.getParameters(true);
        dataCheckBoxes = new ArrayList<>();
        for(String parameter: parametersList){
            JLabel label = new JLabel(parameter);
            JCheckBox dataIn = new JCheckBox();
            JCheckBox dataOut = new JCheckBox();
            dataCheckBoxes.add(dataIn);
            dataCheckBoxes.add(dataOut);
            parametersPanel.add(label);
            dataInPanel.add(dataIn);
            dataOutPanel.add(dataOut);
        }
        // Revalidate and repaint the panels
        parametersPanel.revalidate();
        parametersPanel.repaint();
        dataInPanel.revalidate();
        dataInPanel.repaint();
        dataOutPanel.revalidate();
        dataOutPanel.repaint();
    }

    private void updatePropertiesTable(MethodWrapper method){
        /**
         *Update the properties table based on the selected method
         **/
        //Table Columns
        Object[] columnNames = {resourceBundle.getString("Properties.Category"), resourceBundle.getString("Properties.Value") };
        //Table data
        Object[][] values = {{resourceBundle.getString("Properties.Return"), method.getReturnType(true)},
                {resourceBundle.getString("Properties.Method"), method.getMethodName(true)},
                {resourceBundle.getString("Properties.Parameters"), StringUtils.join(method.getParameters(true), ", ") },
                //{resourceBundle.getString("Properties.Security"), method.getMethod().getSecLevel()},
                {resourceBundle.getString("Properties.Discovery"), method.getMethod().getDiscovery()},
                {resourceBundle.getString("Properties.Framework"), method.getMethod().getFramework()},
                {resourceBundle.getString("Properties.CWE"), StringUtils.join(method.getCWEList(), ", ")},
                {resourceBundle.getString("Properties.Type"), StringUtils.join(method.getTypesList(true), ", ")},
                {resourceBundle.getString("Properties.Comment"), method.getMethod().getComment()},
        };

        TableModel tableModel = new DefaultTableModel(values, columnNames);

        propertiesTable.setModel(tableModel);
        propertiesTable.getColumnModel().getColumn(0).setMinWidth(10);
        propertiesTable.getColumnModel().getColumn(1).setMinWidth(400);
        propertiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    @Override
    protected void doOKAction() {

        if (isOKActionEnabled()) {
            MethodWrapper selectedMethod = (MethodWrapper) signatureCbx.getSelectedItem();
            JCheckBox[] cweCheckBoxPanelComponents = (JCheckBox[]) cweCheckBoxPanel.getComponents();
            JCheckBox[] srmCheckBoxPanelComponents = (JCheckBox[]) srmCheckBoxPanel.getComponents();
            for (JCheckBox checkBox: cweCheckBoxPanelComponents){
                if(checkBox.isSelected()){

                }
            }
        }
    }
}

/** A custom renderer to display the data-in & data-out properties in the dropdown
* */
class SignatureComboBoxRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof MethodWrapper) {
            MethodWrapper methodWrapper = (MethodWrapper) value;
            List<Integer> dataInParameters = methodWrapper.getMethod().getDataIn().getParameters();
            List<Integer> dataOutParameters = methodWrapper.getMethod().getDataOut().getParameters();
            List<String> parametersList = methodWrapper.getParameters(false);
            String displayText = methodWrapper.getReturnType(false)+" "+methodWrapper.getMethodName(false);
            displayText+= "(";
            for(int i=0; i<parametersList.size(); i++){
                displayText+=parametersList.get(i);
                if(dataInParameters.contains(i)){
                    displayText+='\u2193';
                }
                if(dataOutParameters.contains(i)){
                    displayText+='\u2191';
                }
                if(i!=parametersList.size()-1){
                    displayText+=",";
                }
            }
            displayText+=")";
            setText(displayText);
        }

        return this;
    }
}
