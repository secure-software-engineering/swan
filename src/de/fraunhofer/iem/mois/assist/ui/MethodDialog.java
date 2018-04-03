package de.fraunhofer.iem.mois.assist.ui;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.MethodNotifier;
import de.fraunhofer.iem.mois.assist.data.Category;
import de.fraunhofer.iem.mois.assist.data.Method;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.Set;

public class MethodDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList selectedList;
    private JList availableList;
    private JRadioButton cweRadioButton;
    private JRadioButton typeRadioButton;
    private JTextField methodTypes;
    private JTextField methodCwes;
    private JTextField methodSignature;
    private Project project;
    private Method addMethod;
    private Category selectedCategory;
    private DefaultListModel<Category> selectedModel, availableModel;

    public MethodDialog(Method method, Project project, Set<Category> availableCategories) {

        addMethod = method;
        this.project = project;

        typeRadioButton.setSelected(true);

        methodSignature.setText(method.getReturnType(false) + " " + method.getClassName(false) + " (" + method.getParameter(false) + ")");
        methodTypes.setText(StringUtils.join(method.getTypesList(), ", "));
        methodCwes.setText(StringUtils.join(method.getCWEList(), ", "));

        for (Category category : method.getCategories()) {

            if (availableCategories.contains(category)) {
                availableCategories.remove(category);
            }
        }

        selectedModel = addCategoriesToModel(method.getCategories(), false);
        selectedList.setCellRenderer(new CategoryRenderer());
        selectedList.setModel(selectedModel);

        availableModel = addCategoriesToModel(availableCategories, false);
        availableList.setCellRenderer(new CategoryRenderer());
        availableList.setModel(availableModel);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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
                    methodTypes.setText(StringUtils.join(method.getTypesList(), ", "));
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

                    selectedModel.removeElement(selectedCategory);
                    method.getCategories().remove(selectedCategory);
                    methodTypes.setText(StringUtils.join(method.getTypesList(), ", "));
                    methodCwes.setText(StringUtils.join(method.getCWEList(), ", "));

                    availableModel.addElement(selectedCategory);
                    availableCategories.add(selectedCategory);
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

        //Notify Summary Tool window that new method was added
        MessageBus messageBus = project.getMessageBus();

        if (addMethod.isNewMethod()) {
            MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.METHOD_ADDED_TOPIC);
            publisher.afterAction(addMethod);
        } else {

            MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.METHOD_UPDATED_TOPIC);
            publisher.afterAction(addMethod);
        }

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    //Add categories to the List model
    private DefaultListModel<Category> addCategoriesToModel(Set<Category> categories, boolean showCwe) {

        DefaultListModel<Category> model = new DefaultListModel<Category>();

        for (Category category : categories) {

            if (category.isCwe() == showCwe)
                model.addElement(category);
        }

        return model;
    }
}
