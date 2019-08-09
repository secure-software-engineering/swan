/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.MethodNotifier;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.ui.CategoryRenderer;
import de.fraunhofer.iem.swan.data.Category;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Dialog that shows current configuration for method and allows updates.
 */
public class MethodDialog extends DialogWrapper {

    private JPanel contentPane;
    private JList selectedList;
    private JList availableList;
    private JRadioButton cweRadioButton;
    private JRadioButton typeRadioButton;
    private JTextField methodTypes;
    private JTextField methodCwes;
    private JComboBox signatureCbx;
    private JButton buttonProperty;
    private Project project;
    private MethodWrapper method;
    private Category selectedCategory;
    private DefaultListModel<Category> selectedModel, availableModel;
    private ResourceBundle resourceBundle;
    private Set<Category> availableCategories;
    private Set<Category> selectedCategories;
    private HashMap<String, Set<Category>> originalMethods;
    private HashMap<String, MethodWrapper> methods;

    /**
     * Initializes dialog to show method properties
     * @param methods Method data that will be shown in dialog
     * @param signature Method Signature
     * @param project Active project
     * @param categories List of all possible method categories
     */
    public MethodDialog(HashMap<String, MethodWrapper> methods, String signature, Project project, Set<Category> categories) {

        super(project);

        this.project = project;
        availableCategories = new HashSet<>(categories);
        selectedList.setCellRenderer(new CategoryRenderer());
        availableList.setCellRenderer(new CategoryRenderer());

        originalMethods = new HashMap<>();

        this.methods = methods;
        for (MethodWrapper methodWrapper : methods.values())
            signatureCbx.addItem(methodWrapper);

        updateFields(signature, categories);

        resourceBundle = ResourceBundle.getBundle("dialog_messages");

        switch (method.getStatus()) {

            case SUGGESTED:
                setTitle(resourceBundle.getString("MethodDialog.SuggestTitle"));
                break;
            case NEW:
                setTitle(resourceBundle.getString("MethodDialog.AddTitle"));
                break;
            default:
                setTitle(resourceBundle.getString("MethodDialog.UpdateTitle"));
                break;
        }

        setModal(true);
        setSize(550, 350);
        init();

        signatureCbx.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JComboBox comboBox = (JComboBox) e.getSource();

                MethodWrapper selectedItem = (MethodWrapper) comboBox.getSelectedItem();

                updateFields(selectedItem.getSignature(true), categories);
            }
        });

        typeRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cweRadioButton.setSelected(false);
                selectedModel.clear();
                selectedModel = addCategoriesToModel(method.getCategories(), false);
                selectedList.setModel(selectedModel);

                availableModel.clear();
                availableModel = addCategoriesToModel(availableCategories, false);
                availableList.setModel(availableModel);
            }
        });

        cweRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                typeRadioButton.setSelected(false);
                selectedModel.clear();
                selectedModel = addCategoriesToModel(method.getCategories(), true);
                selectedList.setModel(selectedModel);

                availableModel.clear();
                availableModel = addCategoriesToModel(availableCategories, true);
                availableList.setModel(availableModel);
            }
        });

        //Listener for mouse click events in the methodsList
        availableList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {

                    //Obtain selected method
                    JList value = (JList) e.getSource();
                    selectedCategory = (Category) value.getSelectedValue();
                    availableModel.removeElement(selectedCategory);
                    availableCategories.remove(selectedCategory);

                    selectedModel.addElement(selectedCategory);
                    method.getCategories().add(selectedCategory);
                    methodTypes.setText(StringUtils.join(method.getTypesList(true), ", "));
                    methodCwes.setText(StringUtils.join(method.getCWEList(), ", "));
                }
            }
        });

        //Listener for mouse click events in the methodsList
        selectedList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {
                    //Obtain selected method
                    JList value = (JList) e.getSource();
                    selectedCategory = (Category) value.getSelectedValue();

                    if (method.getTypesList(false).size() == 1 && !selectedCategory.isCwe()) {
                        JBPopupFactory.getInstance()
                                .createHtmlTextBalloonBuilder(resourceBundle.getString("Messages.Error.CategoryNotSelected"), MessageType.ERROR, null)
                                .createBalloon()
                                .show(JBPopupFactory.getInstance().guessBestPopupLocation((JComponent) selectedList), Balloon.Position.below);
                    } else {

                        selectedModel.removeElement(selectedCategory);
                        method.getCategories().remove(selectedCategory);
                        methodTypes.setText(StringUtils.join(method.getTypesList(true), ", "));
                        methodCwes.setText(StringUtils.join(method.getCWEList(), ", "));

                        availableModel.addElement(selectedCategory);
                        availableCategories.add(selectedCategory);
                    }
                }
            }
        });

       /* buttonProperty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                MethodPropertiesDialog detailsDialog = new MethodPropertiesDialog(method);
                detailsDialog.setTitle("Method Details");
                detailsDialog.pack();
                detailsDialog.setSize(550, 350);
                detailsDialog.setLocationRelativeTo(null);
                detailsDialog.setVisible(true);
            }
        });*/
    }

    /**
     * Loads method details in the dialog
     * @param signature Method signature
     * @param categories List of all possible method categories
     */
    private void updateFields(String signature, Set<Category> categories) {

        //Clone categories for method
        method = methods.get(signature);
        originalMethods.put(method.getSignature(true), copyCategories(method.getCategories()));

        signatureCbx.setSelectedItem(method);
        signatureCbx.setToolTipText(method.getSignature(true));

        availableCategories = new HashSet<>(categories);

        methodTypes.setText(StringUtils.join(method.getTypesList(true), ", "));
        methodCwes.setText(StringUtils.join(method.getCWEList(), ", "));
        typeRadioButton.setSelected(true);

        selectedCategories = new HashSet<>(method.getCategories());

        for (Category category : selectedCategories) {

            if (availableCategories.contains(category)) {
                availableCategories.remove(category);
            }
        }

        selectedModel = addCategoriesToModel(selectedCategories, false);
        selectedList.setModel(selectedModel);

        availableModel = addCategoriesToModel(availableCategories, false);
        availableList.setModel(availableModel);

    }

    @Override
    public void doCancelAction() {

        for (String signature : originalMethods.keySet()) {

            methods.get(signature).setCategories(originalMethods.get(signature));
        }
        super.doCancelAction();

    }

    /**
     * Deep copy of method categories
     * @param categories Method categories
     * @return New Set of method categories
     */
    private Set<Category> copyCategories(Set<Category> categories) {

        Set<Category> copy = new HashSet<>();

        for (Category category : categories) {
            copy.add(category);
        }
        return copy;
    }

    @Override
    protected void doOKAction() {

        if (isOKActionEnabled()) {
            if (method.getStatus() == MethodWrapper.MethodStatus.SUGGESTED) {

                boolean categorySelected = true;

                for (MethodWrapper method : methods.values()) {
                    if (method.getCategories().isEmpty()) {
                        categorySelected = false;
                        showErrorMessage("Messages.Error.SuggestedCategoryNotSelected", signatureCbx);
                        break;
                    }
                }

                if (categorySelected) {
                    MessageBus messageBus = project.getMessageBus();

                    MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.ADD_UPDATE_DELETE_METHOD);
                    publisher.afterSuggestAction(new ArrayList<>(methods.values()));
                    super.doOKAction();
                }
            } else if (method.getCategories().size() == 0) {

                showErrorMessage("Messages.Error.CategoryNotSelected", selectedList);
            } else {
                //Notify Summary Tool window that new method was added
                MessageBus messageBus = project.getMessageBus();

                MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.ADD_UPDATE_DELETE_METHOD);
                publisher.addNewExistingMethod(method);

                super.doOKAction();
            }
        }
    }

    /**
     * Shows error message
     * @param message Message
     * @param location Where on screen the message should be shown
     */
    private void showErrorMessage(String message, JComponent location) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(resourceBundle.getString(message), MessageType.ERROR, null)
                .createBalloon()
                .show(JBPopupFactory.getInstance().guessBestPopupLocation(location), Balloon.Position.below);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    /**
     * Add categories to the List model
     * @param categories Method categories
     * @param showCwe Condition to show CWEs or not
     * @return return List Model for List
     */
    private DefaultListModel<Category> addCategoriesToModel(Set<Category> categories, boolean showCwe) {

        DefaultListModel<Category> model = new DefaultListModel<Category>();

        for (Category category : categories) {

            if (category.isCwe() == showCwe)
                model.addElement(category);
        }

        return model;
    }
}
