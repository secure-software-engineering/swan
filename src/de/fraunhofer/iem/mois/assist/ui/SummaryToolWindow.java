package de.fraunhofer.iem.mois.assist.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.ui.JBUI;
import de.fraunhofer.iem.mois.assist.actions.method.MethodActionGroup;
import de.fraunhofer.iem.mois.assist.comm.FileSelectedNotifier;
import de.fraunhofer.iem.mois.assist.comm.FilterNotifier;
import de.fraunhofer.iem.mois.assist.comm.MethodNotifier;
import de.fraunhofer.iem.mois.assist.comm.MoisNotifier;
import de.fraunhofer.iem.mois.assist.data.Category;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.Method;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.assist.util.Formatter;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


public class SummaryToolWindow implements ToolWindowFactory {

    private Tree methodTree;
    private DefaultTreeModel treeModel;
    private String currentFile;
    private JBPanel notificationPanel;
    public static ArrayList<String> TREE_FILTERS;
    public static boolean CURRENT_FILE_FILTER;
    public static boolean RESTORE_METHOD;
    public static boolean FILE_SELECTED;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        MessageBus bus = project.getMessageBus();

        JBPanel toolPanel = new JBPanel(new BorderLayout());

        //Toolbar action panel
        final DefaultActionGroup actions = (DefaultActionGroup) ActionManager.getInstance().getAction("MOIS-Assist.ActionBar");
        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("SummaryToolbar", actions, true);
        toolPanel.add(actionToolbar.getComponent(), BorderLayout.PAGE_START);

        //TreeList panel and listModel creation
        treeModel = new DefaultTreeModel(null);

        methodTree = new Tree();
        methodTree.setCellRenderer(new MethodTreeRenderer());
        methodTree.setModel(treeModel);
        methodTree.getEmptyText().setText(Constants.TREE_EMPTY);

        TREE_FILTERS = new ArrayList<>();
        CURRENT_FILE_FILTER = false;
        RESTORE_METHOD = false;
        FILE_SELECTED = false;

        //TODO exception when no file is open
        Document document = FileEditorManager.getInstance(project).getSelectedTextEditor().getDocument();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        currentFile = Formatter.getFileNameFromPath(virtualFile.getName());

        JBScrollPane scrollPane = new JBScrollPane(methodTree);
        toolPanel.add(scrollPane, BorderLayout.CENTER);

        //Tool Window Footer
        notificationPanel = new JBPanel(new BorderLayout());
        notificationPanel.setBorder(JBUI.Borders.empty(4));

        JLabel eventDescription = new JLabel();
        eventDescription.setFont(new Font(eventDescription.getFont().getName(), Font.PLAIN, 11));
        notificationPanel.add(eventDescription, BorderLayout.WEST);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        JPanel wrappingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrappingPanel.setVisible(false);
        wrappingPanel.add(progressBar);
        notificationPanel.add(wrappingPanel);

        JButton eventNotification = new JButton();
        eventNotification.setIcon(PluginIcons.NOTIFICATION_NONE);
        eventNotification.setOpaque(false);
        eventNotification.setBorder(null);
        eventNotification.setToolTipText(Constants.NOTIFICATION_NONE);
        notificationPanel.add(eventNotification, BorderLayout.EAST);

        toolPanel.add(notificationPanel, BorderLayout.PAGE_END);

        //Add Content to ToolWindow
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content toolContent = contentFactory.createContent(toolPanel, "", false);
        toolWindow.getContentManager().addContent(toolContent);

        /*
         *  Action listeners for JTree
         */

        //Action listener for tree
        methodTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) methodTree.getLastSelectedPathComponent();

                if (node != null) {

                    Object object = node.getUserObject();

                    if (object instanceof Method) {
                        Method method = (Method) object;


                    }
                }
            }
        });

        methodTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if ((SwingUtilities.isRightMouseButton(e) || e.isControlDown()) &&!methodTree.isEmpty() ) {

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) methodTree.getLastSelectedPathComponent();
                    Object object = node.getUserObject();

                    if (object instanceof Method) {

                        Method updateMethod = (Method) object;
                        RESTORE_METHOD = updateMethod.getUpdateOperation().equals(Constants.METHOD_DELETED);

                        ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("Method", new MethodActionGroup(updateMethod));
                        actionPopupMenu.getComponent().show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        /*
         *  MessageBus connections that receive and process updates from Publishers.
         */

        //Connect to project bus and subscribe Load File action
        bus.connect().subscribe(FileSelectedNotifier.INITIAL_FILE_NOTIFIER_TOPIC, new FileSelectedNotifier() {
            @Override
            public void notifyFileChange(String fileName) {

                JSONFileLoader.setConfigurationFile(fileName);
                JSONFileLoader.loadInitialFile();

                loadMethods();
            }
        });

        //Connect to project bus and obtain updated configuration file path
        bus.connect().subscribe(FileSelectedNotifier.UPDATED_FILE_NOTIFIER_TOPIC, new FileSelectedNotifier() {
            @Override
            public void notifyFileChange(String fileName) {

                JSONFileLoader.loadUpdatedFile(fileName);

                loadMethods();
            }
        });

        //Connect to project's bus and obtain method that was updated or added
        bus.connect().subscribe(MethodNotifier.METHOD_UPDATED_ADDED_TOPIC, new MethodNotifier() {

            @Override
            public void afterAction(Method newMethod) {

                switch (JSONFileLoader.addMethod(newMethod)) {

                    case JSONFileLoader.EXISTING_METHOD:

                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) methodTree.getLastSelectedPathComponent();
                        node.removeAllChildren();

                        for (Category category : newMethod.getCategories()) {
                            node.add(new DefaultMutableTreeNode(category));
                        }

                        treeModel.nodeStructureChanged(node);
                        break;
                    case JSONFileLoader.NEW_METHOD:
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) methodTree.getModel().getRoot();
                        DefaultMutableTreeNode newMethodNode = new DefaultMutableTreeNode(newMethod);

                        for (Category category : newMethod.getCategories()) {
                            newMethodNode.add(new DefaultMutableTreeNode(category));
                        }

                        treeModel.insertNodeInto(newMethodNode, root, root.getChildCount());
                        break;
                }
            }
        });

        //Connect to project bus and obtain method that was deleted
        bus.connect().subscribe(MethodNotifier.METHOD_REMOVED_TOPIC, new MethodNotifier() {
            @Override
            public void afterAction(Method newMethod) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) methodTree.getLastSelectedPathComponent();

                treeModel.removeNodeFromParent(node);
                JSONFileLoader.removeMethod(newMethod);
            }
        });

        //Update Tool Window to notify user that MOIS is running
        bus.connect().subscribe(MoisNotifier.START_MOIS_PROCESS_TOPIC, new MoisNotifier() {
            @Override
            public void launchMois(HashMap<String, String> values) {

                JLabel notificationMessage = (JLabel) notificationPanel.getComponent(0);
                notificationMessage.setText(Constants.NOTIFICATION_START_MOIS);

                //show progress bar
                notificationPanel.getComponent(1).setVisible(true);
            }
        });

        //Update notification button when MOIS completes running
        bus.connect().subscribe(MoisNotifier.END_MOIS_PROCESS_TOPIC, new MoisNotifier() {
            @Override
            public void launchMois(HashMap<String, String> values) {

                JLabel label = (JLabel) notificationPanel.getComponent(0);
                label.setText(values.get(Constants.MOIS_OUTPUT_MESSAGE));

                //remove progress bar
                notificationPanel.getComponent(1).setVisible(false);

                JButton viewResults = (JButton) notificationPanel.getComponent(2);
                viewResults.setIcon(PluginIcons.NOTIFICATION_NEW);
                viewResults.setToolTipText(Constants.NOTIFICATION_MOIS);

                viewResults.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        MoisResultsDialog resultsDialog = new MoisResultsDialog(project, values);
                        resultsDialog.pack();
                        resultsDialog.setSize(550, 350);
                        resultsDialog.setLocationRelativeTo(null);
                        resultsDialog.setVisible(true);
                    }
                });
            }
        });

        //Connect to project bus and obtain filter for Method Tree
        bus.connect().subscribe(FilterNotifier.FILTER_SELECTED_TOPIC, new FilterNotifier() {
            @Override
            public void updateFilter(String value) {

                loadMethods();
            }
        });

        //Connect to message bus and filter list when a new file is selected or opened
        bus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {

                if (CURRENT_FILE_FILTER) {
                    currentFile = Formatter.getFileNameFromPath(event.getNewFile().getName());

                    loadMethods();
                }
            }
        });
    }

    private void loadMethods() {

        ArrayList<Method> methods = JSONFileLoader.getMethods(TREE_FILTERS, currentFile, CURRENT_FILE_FILTER);

        if (methods.size() > 0) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("<html><b>Methods</b> <font color='gray'>[<i>" + JSONFileLoader.getConfigurationFile(false) + "</i>]</font></html>");

            for (Method method : methods) {

                DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(method);

                for (Category category : method.getCategories()) {
                    methodNode.add(new DefaultMutableTreeNode(category));
                }
                root.add(methodNode);
            }
            treeModel.setRoot(root);
        } else {
            treeModel.setRoot(null);
            methodTree.getEmptyText().setText(Constants.TREE_FILTERS_EMPTY);
        }

    }
}
