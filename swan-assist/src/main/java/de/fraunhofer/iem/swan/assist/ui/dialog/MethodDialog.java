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
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.MethodNotifier;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.RelevantPart;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

/**
 * Shows additional properties of a method.
 */

public class MethodDialog extends DialogWrapper {

    private JPanel contentPane;
    private JBTable propertiesTable;
    private JComboBox signatureCbx;
    private JPanel cweCheckBoxPanel;
    private JPanel srmCheckBoxPanel;
    private JTabbedPane tab;
    private JCheckBox dataInReturnCheckBox;
    private JCheckBox dataOutReturnCheckBox;
    private JPanel parametersPanel;
    MethodWrapper previousItem;
    private JPanel dataOutPanel;
    private JPanel dataInPanel;
    private JPanel parametersTabPanel;
    private List<JCheckBox> dataInCheckBoxes, dataOutCheckBoxes;
    private ResourceBundle resourceBundle;
    private MethodWrapper method;
    private Project project;


    /**
     * Initializes the dialog using the given arguments
     *
     * @param project    Active project in IDE
     * @param methods    List of methods in the JComboBox
     * @param signature  Signature of the method selected currently
     * @param categories list of SRM and CWE categories
     */
    public MethodDialog(HashMap<String, MethodWrapper> methods, String signature, Project project, Set<Category> categories) {
        super(project);
        this.project = project;
        this.resourceBundle = ResourceBundle.getBundle("dialog_messages");

        resourceBundle = ResourceBundle.getBundle("dialog_messages");
        setTitle(resourceBundle.getString("MethodDialog.Title"));


        for (MethodWrapper methodWrapper : methods.values()) {
            signatureCbx.addItem(methodWrapper);
        }
        signatureCbx.setRenderer(new SignatureComboBoxRenderer());
        signatureCbx.setSelectedItem(methods.get(signature));
        previousItem = methods.get(signature);

        method = methods.get(signature);
        updateCategoryTab(methods.get(signature), categories);
        updateParametersTab(methods.get(signature));
        updatePropertiesTab(methods.get(signature));

        setSize(550, 350);
        setAutoAdjustable(true);
        setModal(true);
        init();

        signatureCbx.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JComboBox comboBox = (JComboBox) e.getSource();

                MethodWrapper selectedItem = (MethodWrapper) comboBox.getSelectedItem();
                updateMethodData(previousItem);
                previousItem = selectedItem;
                updateParametersTab(selectedItem);
                updatePropertiesTab(selectedItem);
                updateCategoryTab(selectedItem, categories);
            }
        });
    }

    private void updateMethodData(MethodWrapper previousItem) {

        //Update the SRM & CWE categories for the selected methods
        Set<Category> srmSet = new HashSet<>();
        Set<Category> cweSet = new HashSet<>();
        for (Component component : srmCheckBoxPanel.getComponents()) {
            JCheckBox checkBox = (JCheckBox) component;
            if (checkBox.isSelected()) {
                srmSet.add(Category.fromText(checkBox.getName()));
            }
        }
        for (Component component : cweCheckBoxPanel.getComponents()) {
            JCheckBox checkBox = (JCheckBox) component;
            if (checkBox.isSelected()) {
                cweSet.add(Category.getCategoryForCWE(checkBox.getName()));
            }
        }
        srmSet.addAll(cweSet);
        previousItem.setCategories(srmSet);

        // Update the dataIn, dataOut & return fields for the selected methods
        if (previousItem.getParameters(true).size() != 0) {
            List<Integer> dataInList = new ArrayList<>();
            List<Integer> dataOutList = new ArrayList<>();
            for (JCheckBox checkbox : dataInCheckBoxes) {
                if (checkbox.isSelected())
                    dataInList.add(Integer.valueOf(checkbox.getName()));
            }
            for (JCheckBox checkBox : dataOutCheckBoxes) {
                if (checkBox.isSelected())
                    dataOutList.add(Integer.valueOf(checkBox.getName()));
            }
            previousItem.getMethod().setDataIn(new RelevantPart(dataInReturnCheckBox.isSelected(), dataInList));
            previousItem.getMethod().setDataOut(new RelevantPart(dataOutReturnCheckBox.isSelected(), dataOutList));
        }

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }


    // Display the SRM and CWE categories for the selected method
    private void updateCategoryTab(MethodWrapper method, Set<Category> categories) {

        srmCheckBoxPanel.removeAll();
        cweCheckBoxPanel.removeAll();
        srmCheckBoxPanel.setLayout(new GridLayout(1, 3));
        cweCheckBoxPanel.setLayout(new GridLayout(3, 3));

        Set<Category> methodCategory = method.getCategories();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("dialog_messages");

        for (Category category : categories) {

            JCheckBox checkBox = new JCheckBox(category.toString());
            checkBox.setName(category.name());

            if (methodCategory.contains(category)) {
                checkBox.setSelected(true);
            }
            if (category.isCwe()) {
                try {
                    checkBox.setToolTipText(resourceBundle.getString(category + ".Name"));
                } catch (Exception e) {
                    checkBox.setToolTipText(category.toString());
                }
                cweCheckBoxPanel.add(checkBox);
            } else {
                //srmCheckBoxPanel.setPreferredSize(new java.awt.Dimension(200, 200));
                srmCheckBoxPanel.add(checkBox);
            }
        }
        cweCheckBoxPanel.revalidate();
        srmCheckBoxPanel.revalidate();
        cweCheckBoxPanel.repaint();
        srmCheckBoxPanel.repaint();
    }


    // Populate the parameters tab with the details of the selected method
    private void updateParametersTab(MethodWrapper method) {
        // Clear existing data in the panels
        parametersPanel.removeAll();
        dataInPanel.removeAll();
        dataOutPanel.removeAll();
        parametersPanel.setLayout(new GridLayout(0, 1));
        dataInPanel.setLayout(new GridLayout(0, 1));
        dataOutPanel.setLayout(new GridLayout(0, 1));

        // Add the parameter value and the checkboxes to the panels
        List<String> parametersList = method.getParameters(true);
        dataInCheckBoxes = new ArrayList<>();
        dataOutCheckBoxes = new ArrayList<>();
        for (int i = 0; i < parametersList.size(); i++) {
            String parameter = parametersList.get(i);
            JLabel label = new JLabel(parameter);
            JCheckBox dataIn = new JCheckBox();
            JCheckBox dataOut = new JCheckBox();
            dataIn.setName(String.valueOf(i));
            dataOut.setName(String.valueOf(i));
            dataIn.setSelected(isDataIn(method, parameter, i));
            dataOut.setSelected(isDataOut(method, parameter, i));
            dataInCheckBoxes.add(dataIn);
            dataOutCheckBoxes.add(dataOut);

            // Add the checkboxes to the respective panels
            parametersPanel.add(label);
            dataInPanel.add(dataIn);
            dataOutPanel.add(dataOut);
        }
        dataInReturnCheckBox.setSelected(method.getMethod().getDataIn().getReturnValue());
        dataOutReturnCheckBox.setSelected(method.getMethod().getDataOut().getReturnValue());
        if (parametersList.size() == 0) {
            parametersTabPanel.setVisible(false);
            dataInReturnCheckBox.setVisible(false);
            dataOutReturnCheckBox.setVisible(false);
        } else {
            parametersTabPanel.setVisible(true);
            dataInReturnCheckBox.setVisible(true);
            dataOutReturnCheckBox.setVisible(true);
        }
        // Revalidate and repaint the panels
        parametersPanel.revalidate();
        parametersPanel.repaint();
        dataInPanel.revalidate();
        dataInPanel.repaint();
        dataOutPanel.revalidate();
        dataOutPanel.repaint();
    }

    private boolean isDataOut(MethodWrapper method, String parameter, int i) {
        List<Integer> dataOutList = method.getMethod().getDataOut().getParameters();
        List<String> parametersList = method.getParameters(true);

        if (parametersList.get(i) == parameter && dataOutList.contains(i)) {
            return true;
        }
        return false;
    }

    private boolean isDataIn(MethodWrapper method, String parameter, int i) {
        List<Integer> dataInList = method.getMethod().getDataIn().getParameters();
        List<String> parametersList = method.getParameters(true);

        return parametersList.get(i).equals(parameter) && dataInList.contains(i);
    }

    // Populate the properties tab with the details of the selected method
    private void updatePropertiesTab(MethodWrapper method) {
        /**
         *Update the properties table based on the selected method
         **/
        //Table Columns
        Object[] columnNames = {resourceBundle.getString("Properties.Category"), resourceBundle.getString("Properties.Value")};
        //Table data
        Object[][] values = {{resourceBundle.getString("Properties.Return"), method.getReturnType(true)},
                {resourceBundle.getString("Properties.Method"), method.getMethodName(true)},
                {resourceBundle.getString("Properties.Parameters"), StringUtils.join(method.getParameters(true), ", ")},
                //{resourceBundle.getString("Properties.Security"), method.getMethod().getSecLevel()},
                {resourceBundle.getString("Properties.Discovery"), method.getMethod().getDiscovery()},
                {resourceBundle.getString("Properties.Framework"), method.getMethod().getFramework()},
                {resourceBundle.getString("Properties.CWE"), StringUtils.join(method.getCWEList(), ", ")},
                {resourceBundle.getString("Properties.Type"), StringUtils.join(method.getTypesList(true), ", ")},
                {resourceBundle.getString("Properties.Comment"), method.getMethod().getComment()},
        };

        TableModel tableModel = new DefaultTableModel(values, columnNames);

        propertiesTable.setModel(tableModel);

        propertiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    @Override
    protected void doOKAction() {

        if (isOKActionEnabled()) {
            updateMethodData((MethodWrapper) signatureCbx.getSelectedItem());

            if (method.getStatus().equals(MethodWrapper.MethodStatus.NEW)) {
                //Notify Summary Tool window that new method was added
                MessageBus messageBus = project.getMessageBus();

                MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.ADD_UPDATE_DELETE_METHOD);
                publisher.addNewExistingMethod(method);
                super.doOKAction();
            }
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

/**
 * A custom renderer to display the data-in & data-out properties in the dropdown
 */
class SignatureComboBoxRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof MethodWrapper) {
            MethodWrapper methodWrapper = (MethodWrapper) value;
            List<Integer> dataInParameters = methodWrapper.getMethod().getDataIn().getParameters();
            List<Integer> dataOutParameters = methodWrapper.getMethod().getDataOut().getParameters();
            List<String> parametersList = methodWrapper.getParameters(false);
            String displayText = methodWrapper.getReturnType(false) + " " + methodWrapper.getMethodName(false);
            displayText += "(";
            for (int i = 0; i < parametersList.size(); i++) {
                displayText += parametersList.get(i);
                if (dataInParameters.contains(i)) {
                    displayText += '\u2193';
                }
                if (dataOutParameters.contains(i)) {
                    displayText += '\u2191';
                }
                if (i != parametersList.size() - 1) {
                    displayText += ",";
                }
            }
            displayText += ")";
            setText(displayText);
        }

        return this;
    }
}
