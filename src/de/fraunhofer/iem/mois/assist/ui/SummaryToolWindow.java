package de.fraunhofer.iem.mois.assist.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
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
import de.fraunhofer.iem.mois.assist.actions.method.UpdateMethodAction;
import de.fraunhofer.iem.mois.assist.comm.FileSelectedNotifier;
import de.fraunhofer.iem.mois.assist.comm.FilterNotifier;
import de.fraunhofer.iem.mois.assist.comm.MethodNotifier;
import de.fraunhofer.iem.mois.assist.comm.MoisNotifier;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.MethodWrapper;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.assist.util.Formatter;
import de.fraunhofer.iem.mois.data.Category;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Tool Window implementation for the Plugin.
 *
 * @author Oshando Johnson
 */

public class SummaryToolWindow implements ToolWindowFactory {

    private Tree methodTree;
    private DefaultTreeModel treeModel;
    private String currentFile;
    private JBPanel notificationPanel;
    public static ArrayList<String> TREE_FILTERS;
    public static boolean CURRENT_FILE_FILTER;
    public static boolean RESTORE_METHOD;
    public static boolean CONFIG_FILE_SELECTED;
    public static boolean CURRENT_PROJECT_FILTER;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        MessageBus bus = project.getMessageBus();

        JBPanel toolPanel = new JBPanel(new BorderLayout());

        //Toolbar action panel
        final DefaultActionGroup actions = (DefaultActionGroup) ActionManager.getInstance().getAction("AssistMOIS.ActionBar");
        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("SummaryToolbar", actions, true);
        toolPanel.add(actionToolbar.getComponent(), BorderLayout.PAGE_START);

        //TreeList panel and listModel creation
        treeModel = new DefaultTreeModel(null);

        methodTree = new Tree();
        methodTree.setCellRenderer(new MethodTreeRenderer());
        methodTree.setModel(treeModel);
        methodTree.getEmptyText().setText(Constants.TREE_EMPTY);
        methodTree.setToggleClickCount(0);

        TREE_FILTERS = new ArrayList<>();
        CURRENT_FILE_FILTER = false;
        RESTORE_METHOD = false;
        CONFIG_FILE_SELECTED = false;
        CURRENT_PROJECT_FILTER = false;

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

                    if (object instanceof MethodWrapper) {
                        MethodWrapper method = (MethodWrapper) object;


                    }
                }
            }
        });

        methodTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if ((SwingUtilities.isRightMouseButton(e) || (e.isControlDown()) && !methodTree.isEmpty())) {

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) methodTree.getLastSelectedPathComponent();
                    Object object = node.getUserObject();

                    if (object instanceof MethodWrapper) {

                        MethodWrapper method = (MethodWrapper) object;

                        //   RESTORE_METHOD = method.getUpdateOperation().equals(Constants.METHOD_DELETED);

                        ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("Method", new MethodActionGroup(method));
                        actionPopupMenu.getComponent().show(e.getComponent(), e.getX(), e.getY());
                    }

                } else if (e.getClickCount() == 2) {

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) methodTree.getLastSelectedPathComponent();
                    Object object = node.getUserObject();

                    if (object instanceof MethodWrapper) {

                        MethodWrapper method = (MethodWrapper) object;
                        ActionManager.getInstance().tryToExecute(new UpdateMethodAction(method), e, null, null, false);
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
                loadMethods(project);
            }
        });

        //Connect to project bus and obtain updated configuration file path
        bus.connect().subscribe(FileSelectedNotifier.UPDATED_FILE_NOTIFIER_TOPIC, new FileSelectedNotifier() {
            @Override
            public void notifyFileChange(String fileName) {

                JSONFileLoader.loadUpdatedFile(fileName);
                loadMethods(project);
            }
        });

        //Connect to project's bus and obtain method that was updated or added
        bus.connect().subscribe(MethodNotifier.METHOD_UPDATED_ADDED_TOPIC, new MethodNotifier() {

            @Override
            public void afterAction(MethodWrapper newMethod) {

                switch (JSONFileLoader.addMethod(newMethod)) {

                    case JSONFileLoader.EXISTING_METHOD:

                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) methodTree.getLastSelectedPathComponent();

                        if (node == null) {
                            node = searchNode((DefaultMutableTreeNode) treeModel.getRoot(), newMethod.getSignature(true));
                        }

                        if (node!=null && node.getChildCount() > 0)
                            node.removeAllChildren();

                        treeModel.nodeStructureChanged(addCategoriesToNode(node, newMethod));

                        break;
                    case JSONFileLoader.NEW_METHOD:
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) methodTree.getModel().getRoot();
                        DefaultMutableTreeNode newMethodNode = new DefaultMutableTreeNode(newMethod);

                        treeModel.insertNodeInto(addCategoriesToNode(newMethodNode, newMethod), root, root.getChildCount());
                        break;
                }
            }
        });

        //Connect to project bus and obtain method that was deleted
        bus.connect().subscribe(MethodNotifier.METHOD_REMOVED_TOPIC, new MethodNotifier() {
            @Override
            public void afterAction(MethodWrapper newMethod) {

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

                loadMethods(project);
            }
        });

        //Connect to message bus and filter list when a new file is selected or opened
        bus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {

                if (CURRENT_FILE_FILTER) {
                    currentFile = Formatter.getFileNameFromPath(event.getNewFile().getName());

                    loadMethods(project);
                }
            }
        });
    }

    /**
     * Searches if a method already exists in the Tree.
     *
     * @param root root object of tree
     * @param method the method that is being searched for
     * @return returns the node if it's found
     */
    private DefaultMutableTreeNode searchNode(DefaultMutableTreeNode root, String method) {

        Enumeration e = root.breadthFirstEnumeration();

        while (e.hasMoreElements()) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();

            if (node.getUserObject() instanceof MethodWrapper) {

                MethodWrapper methodWrapper = (MethodWrapper) node.getUserObject();

                if (method.equals(methodWrapper.getSignature(true))) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Extracts categories from method and adds them to the DefaultMutableTreeNode node.
     *
     * @param method the method that is being added to the tree
     * @return the node object with the categories as children
     */
    private DefaultMutableTreeNode addCategoriesToNode(MethodWrapper method) {
        return addCategoriesToNode(new DefaultMutableTreeNode(method), method);
    }

    /**
     * Extracts categories from method and adds them to the DefaultMutableTreeNode node.
     *
     * @param node   method to be added to tree
     * @param method the method that is being added to the tree
     * @return the node object with the categories as children
     */
    private DefaultMutableTreeNode addCategoriesToNode(DefaultMutableTreeNode node, MethodWrapper method) {

        for (Category category : method.getCategories()) {
            if (category.isCwe())
                node.add(new DefaultMutableTreeNode(category));
        }

        return node;
    }

    /**
     * Loads methods from file and uses them to create tree object.
     */
    private void loadMethods(Project project) {

        ArrayList<MethodWrapper> methods = JSONFileLoader.getMethods(TREE_FILTERS, currentFile, CURRENT_FILE_FILTER, CURRENT_PROJECT_FILTER, project);

        if (methods.size() > 0) {

            DefaultMutableTreeNode root = new DefaultMutableTreeNode("<html><b>Methods</b> <font color='gray'>[<i>" + JSONFileLoader.getConfigurationFile(false) + "</i>]</font></html>");

            for (MethodWrapper method : methods) {
                root.add(addCategoriesToNode(method));
            }
            treeModel.setRoot(root);
        } else {
            treeModel.setRoot(null);
            methodTree.getEmptyText().setText(Constants.TREE_FILTERS_EMPTY);
        }
    }
}
