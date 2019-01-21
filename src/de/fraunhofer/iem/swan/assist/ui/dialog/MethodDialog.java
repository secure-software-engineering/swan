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
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.data.Category;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;
import java.util.Set;

/**
 * Dialog that shows current configuration for method and allows updates.
 *
 * @author Oshando Johnson
 */
public class MethodDialog extends DialogWrapper {

    private JPanel contentPane;
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

        super(project);

        method = m;
        this.project = project;

        if (method.isNewMethod())
            setTitle(Constants.TITLE_ADD_METHOD);
        else
            setTitle(Constants.TITLE_UPDATE_METHOD);

        methodSignature.setText(method.getSignature(false));
        methodSignature.setToolTipText(method.getSignature(true));
        methodTypes.setText(StringUtils.join(method.getTypesList(true), ", "));
        methodCwes.setText(StringUtils.join(method.getCWEList(), ", "));
        typeRadioButton.setSelected(true);

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

        setModal(true);
        setSize(550, 350);
        init();

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

    @Override
    protected void doOKAction() {

        if (isOKActionEnabled()) {
            if (method.getCategories().size() == 0) {
                JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(Constants.NO_CATEGORY_SELECTED, MessageType.ERROR, null)
                        .createBalloon()
                        .show(JBPopupFactory.getInstance().guessBestPopupLocation((JComponent) selectedList), Balloon.Position.below);
            } else {
                //Notify Summary Tool window that new method was added
                MessageBus messageBus = project.getMessageBus();

                MethodNotifier publisher = messageBus.syncPublisher(MethodNotifier.METHOD_UPDATED_ADDED_TOPIC);
                publisher.afterAction(method);

                dispose();
            }
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
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
