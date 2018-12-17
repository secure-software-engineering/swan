package de.fraunhofer.iem.mois.assist.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.MethodNotifier;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.ui.CategoryRenderer;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.data.Category;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.Set;

/**
 * Dialog that shows current configuration for method and allows updates.
 *
 * @author Oshando Johnson
 */
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
    private JButton buttonProperty;
    private Project project;
    private MethodWrapper method;
    private Category selectedCategory;
    private DefaultListModel<Category> selectedModel, availableModel;

    public MethodDialog(MethodWrapper m, Project project, Set<Category> availableCategories) {

        method = m;

        this.project = project;

        typeRadioButton.setSelected(true);

        methodSignature.setText(method.getSignature(false));
        methodSignature.setToolTipText(method.getSignature(true));
        methodTypes.setText(StringUtils.join(method.getTypesList(true), ", "));
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

                    selectedModel.removeElement(selectedCategory);
                    method.getCategories().remove(selectedCategory);
                    methodTypes.setText(StringUtils.join(method.getTypesList(true), ", "));
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

        buttonProperty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                MethodPropertiesDialog detailsDialog = new MethodPropertiesDialog(method);
                detailsDialog.setTitle("Method Details");
                detailsDialog.pack();
                detailsDialog.setSize(550, 350);
                detailsDialog.setLocationRelativeTo(null);
                detailsDialog.setVisible(true);
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

        if (method.getCategories().size() == 0) {

            Messages.showMessageDialog(Constants.NO_CATEGORY_SELECTED, "Category Selection", Messages.getInformationIcon());
        } else {
            //Notify Summary Tool window that new method was added
            MessageBus messageBus = project.getMessageBus();

            MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.METHOD_UPDATED_ADDED_TOPIC);
            publisher.afterAction(method);

            dispose();
        }
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
