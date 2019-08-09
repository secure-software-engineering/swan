package de.fraunhofer.iem.swan.assist.ui;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.actions.method.MethodActionGroup;
import de.fraunhofer.iem.swan.assist.comm.FileSelectedNotifier;
import de.fraunhofer.iem.swan.assist.comm.FilterNotifier;
import de.fraunhofer.iem.swan.assist.comm.MethodNotifier;
import de.fraunhofer.iem.swan.assist.comm.SwanNotifier;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.ui.dialog.SwanResultsDialog;
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.assist.util.Formatter;
import de.fraunhofer.iem.swan.assist.util.PsiTraversal;
import de.fraunhofer.iem.swan.data.Category;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * @author Oshando Johnson on 14.12.18
 */
public class MethodListTree extends Tree {

    private DefaultTreeModel treeModel;
    private String currentFile;
    private JBPanel notificationPanel;
    public static ArrayList<String> TREE_FILTERS;
    public static boolean CURRENT_FILE_FILTER;
    public static boolean RESTORE_METHOD;
    public static boolean CURRENT_PROJECT_FILTER;
    private Project project;


    public MethodListTree(Project project) {

        this.project = project;
        MessageBus bus = project.getMessageBus();

        //TreeList panel and listModel creation
        treeModel = new DefaultTreeModel(null);

        //methodTree = new Tree();
        setCellRenderer(new MethodTreeRenderer());
        setModel(treeModel);
        getEmptyText().setText(Constants.TREE_EMPTY);
        setToggleClickCount(0);

        TREE_FILTERS = new ArrayList<>();
        CURRENT_FILE_FILTER = false;
        RESTORE_METHOD = false;
        CURRENT_PROJECT_FILTER = false;

        //TODO exception when no file is open
        Document document = FileEditorManager.getInstance(project).getSelectedTextEditor().getDocument();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        currentFile = Formatter.getFileNameFromPath(virtualFile.getName());

        //Action listener for tree
        addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();

                if (node != null) {

                    Object object = node.getUserObject();

                    if (object instanceof MethodWrapper) {
                        MethodWrapper method = (MethodWrapper) object;


                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if ((SwingUtilities.isRightMouseButton(e) || (e.isControlDown()) && !isEmpty())) {

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                    Object object = node.getUserObject();

                    if (object instanceof MethodWrapper) {

                        MethodWrapper method = (MethodWrapper) object;

                        //   RESTORE_METHOD = method.getUpdateOperation().equals(Constants.METHOD_DELETED);

                        ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("Method", new MethodActionGroup(method));
                        actionPopupMenu.getComponent().show(e.getComponent(), e.getX(), e.getY());
                    }

                } else if (e.getClickCount() == 2) {

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                    Object object = node.getUserObject();

                    if (object instanceof MethodWrapper) {

                        MethodWrapper method = (MethodWrapper) object;

                        //Get PSI element location
                        PsiFile[] files = FilenameIndex.getFilesByName(project, method.getFileName(), GlobalSearchScope.allScope(project));
                        boolean methodFound = false;

                        for (PsiFile file : files) {

                            PsiJavaFile psiJavaFile = (PsiJavaFile) file;

                            for (PsiClass psiClass : psiJavaFile.getClasses()) {
                                for (PsiMethod psiMethod : psiClass.getMethods()) {

                                    if (PsiTraversal.getMethodSignature(psiMethod).equals(method.getSignature(true))) {

                                        methodFound = true;
                                        FileEditorManager.getInstance(project).openFile(psiJavaFile.getVirtualFile(), true, true);
                                        FileEditorManager.getInstance(project).getSelectedTextEditor().getCaretModel().moveToOffset(psiMethod.getTextOffset());
                                    }
                                }
                            }
                        }

                        if (!methodFound) {
                            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(Constants.METHOD_NOT_FOUND_IN_EDITOR, MessageType.ERROR, null)
                                    .createBalloon()
                                    .show(JBPopupFactory.getInstance().guessBestPopupLocation((JComponent) e.getComponent()), Balloon.Position.below);
                        }
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
                DaemonCodeAnalyzer.getInstance(project).restart();
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
            public void afterAction(MethodWrapper newMethod) {

                switch (JSONFileLoader.addMethod(newMethod)) {

                    case JSONFileLoader.EXISTING_METHOD:


                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();

                        if (node == null) {
                            node = searchNode((DefaultMutableTreeNode) treeModel.getRoot(), newMethod.getSignature(true));
                        }

                        if (node != null && node.getChildCount() > 0)
                            node.removeAllChildren();

                        treeModel.nodeStructureChanged(addCategoriesToNode(node, newMethod));

                        break;
                    case JSONFileLoader.NEW_METHOD:
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
                        DefaultMutableTreeNode newMethodNode = new DefaultMutableTreeNode(newMethod);

                        treeModel.insertNodeInto(addCategoriesToNode(newMethodNode, newMethod), root, root.getChildCount());
                        break;
                }

                DaemonCodeAnalyzer.getInstance(project).restart();
            }
        });

        //Connect to project bus and obtain method that was deleted
        bus.connect().subscribe(MethodNotifier.METHOD_REMOVED_TOPIC, new MethodNotifier() {
            @Override
            public void afterAction(MethodWrapper newMethod) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();

                if (node == null) {
                    node = searchNode((DefaultMutableTreeNode) treeModel.getRoot(), newMethod.getSignature(true));
                }

                treeModel.removeNodeFromParent(node);
                JSONFileLoader.removeMethod(newMethod);
                DaemonCodeAnalyzer.getInstance(project).restart();
            }
        });

        //Update Tool Window to notify user that SWAN is running
        bus.connect().subscribe(SwanNotifier.START_SWAN_PROCESS_TOPIC, new SwanNotifier() {
            @Override
            public void launchSwan(HashMap<String, String> values) {

                JSONFileLoader.setReloading(true);
                Notifications.Bus.notify(new Notification(Constants.PLUGIN_GROUP_DISPLAY_ID, "Reloading SWAN", Constants.NOTIFICATION_START_SWAN, NotificationType.INFORMATION));
            }
        });

        //Update notification button when SWAN completes running
        bus.connect().subscribe(SwanNotifier.END_SWAN_PROCESS_TOPIC, new SwanNotifier() {
            @Override
            public void launchSwan(HashMap<String, String> values) {

                JSONFileLoader.setReloading(false);
                NotificationType notificationType = NotificationType.INFORMATION;

                if (!values.get(Constants.SWAN_OUTPUT_MESSAGE).equals(Constants.NOTIFICATION_END_SWAN_SUCCESS)) {
                    notificationType = NotificationType.ERROR;
                }

                String message = "<html>" + values.get(Constants.SWAN_OUTPUT_MESSAGE) + "<br><a href='logs'>View Logs</a> or <a href='load'>Load Changes</a></html>";
                Notifications.Bus.notify(new Notification(Constants.PLUGIN_GROUP_DISPLAY_ID, "Completed", message, notificationType, new NotificationListener() {
                    @Override
                    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent hyperlinkEvent) {

                        if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

                            if (hyperlinkEvent.getDescription().equals("logs")) {

                                SwanResultsDialog resultsDialog = new SwanResultsDialog(project, values);
                                resultsDialog.show();

                            } else if (hyperlinkEvent.getDescription().equals("load")){
                                JSONFileLoader.loadUpdatedFile(values.get(Constants.SWAN_OUTPUT_FILE));
                                loadMethods();
                            }
                        }
                    }
                }));
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

    /**
     * Searches if a method already exists in the Tree.
     *
     * @param root   root object of tree
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
    private void loadMethods() {

        ArrayList<MethodWrapper> methods = JSONFileLoader.getMethods(TREE_FILTERS, currentFile, CURRENT_FILE_FILTER, CURRENT_PROJECT_FILTER, project);

        if (methods.size() > 0) {

            DefaultMutableTreeNode root = new DefaultMutableTreeNode("<html><b>Methods</b> <font color='gray'>[<i>" + JSONFileLoader.getConfigurationFile(false) + "</i>]</font></html>");

            for (MethodWrapper method : methods) {
                root.add(addCategoriesToNode(method));
            }
            treeModel.setRoot(root);
        } else {
            treeModel.setRoot(null);
            getEmptyText().setText(Constants.TREE_FILTERS_EMPTY);
        }
    }
}