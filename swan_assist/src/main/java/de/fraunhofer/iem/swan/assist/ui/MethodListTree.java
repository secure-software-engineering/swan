/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.ui;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
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
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.actions.method.MethodActionGroup;
import de.fraunhofer.iem.swan.assist.comm.*;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.assist.data.TrainingFileManager;
import de.fraunhofer.iem.swan.assist.ui.dialog.MethodDialog;
import de.fraunhofer.iem.swan.assist.ui.dialog.SwanResultsDialog;
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.assist.util.Formatter;
import de.fraunhofer.iem.swan.assist.util.PsiTraversal;
import de.fraunhofer.iem.swan.data.Category;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Manages content and interactions of method list tree
 */
public class MethodListTree extends Tree {

    private DefaultTreeModel treeModel;
    private String currentFile;
    public static ArrayList<Pair<String, String>> TREE_FILTERS;
    public static boolean RESTORE_METHOD;
    public static Set<String> suggestedMethodsList;
    private Project project;
    private ResourceBundle resource;

    /**
     * Initialises method list tree
     *
     * @param project Active project in IDE
     */
    public MethodListTree(Project project) {

        this.project = project;
        MessageBus bus = project.getMessageBus();

        resource = ResourceBundle.getBundle("dialog_messages");

        //TreeList panel and listModel creation
        treeModel = new DefaultTreeModel(null);
        setCellRenderer(new MethodTreeRenderer());
        setModel(treeModel);
        getEmptyText().setText(resource.getString("Messages.Notification.EmptyTree"));
        setToggleClickCount(0);

        TREE_FILTERS = new ArrayList<>();
        RESTORE_METHOD = false;
        currentFile = "";

        suggestedMethodsList = new HashSet<>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if ((SwingUtilities.isRightMouseButton(e) || (e.isControlDown()) && !isEmpty())) {

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();

                    if (node != null) {

                        Object object = node.getUserObject();

                        if (object instanceof MethodWrapper) {

                            MethodWrapper method = (MethodWrapper) object;

                            RESTORE_METHOD = method.getUpdateOperation().equals(Constants.METHOD_DELETED);

                            ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("Method", new MethodActionGroup(method));
                            actionPopupMenu.getComponent().show(e.getComponent(), e.getX(), e.getY());
                        }
                    }

                } else if (e.getClickCount() == 2) {

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                    if (node != null) {
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

                                        if (Objects.equals(PsiTraversal.getMethodSignature(psiMethod), method.getSignature(true))) {

                                            methodFound = true;
                                            FileEditorManager.getInstance(project).openFile(psiJavaFile.getVirtualFile(), true, true);
                                            FileEditorManager.getInstance(project).getSelectedTextEditor().getCaretModel().moveToOffset(psiMethod.getTextOffset());
                                        }
                                    }
                                }
                            }

                            if (!methodFound) {
                                JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(resource.getString("Messages.Error.MethodNotFound"), MessageType.ERROR, null)
                                        .createBalloon()
                                        .show(JBPopupFactory.getInstance().guessBestPopupLocation((JComponent) e.getComponent()), Balloon.Position.below);
                            }
                        }
                    }

                }
            }
        });

        /*
         *  MessageBus connections that receive and process updates from Publishers.
         */

        //Connect to project bus and subscribe Load File action
        bus.connect().subscribe(ConfigurationFileNotifier.FILE_NOTIFIER_TOPIC, new ConfigurationFileNotifier() {
            @Override
            public void loadInitialFile(String fileName) {

                ProgressManager.getInstance().run(new Task.Backgroundable(project, resource.getString("Status.ImportFile")) {
                    @Override
                    public void run(@NotNull ProgressIndicator progressIndicator) {

                        ApplicationManager.getApplication().runReadAction(new Runnable() {
                            public void run() {
                                JSONFileLoader.setConfigurationFile(fileName, project);
                                JSONFileLoader.loadInitialFile();
                                loadMethods();
                            }
                        });
                    }
                });

                DaemonCodeAnalyzer.getInstance(project).restart();
            }

            @Override
            public void loadUpdatedFile(String fileName) {

                ProgressManager.getInstance().run(new Task.Backgroundable(project, resource.getString("Status.ImportFile")) {
                    @Override
                    public void run(@NotNull ProgressIndicator progressIndicator) {

                        ApplicationManager.getApplication().runReadAction(new Runnable() {
                            public void run() {
                                JSONFileLoader.loadUpdatedFile(fileName, project);
                                loadMethods();
                            }
                        });
                    }
                });

                DaemonCodeAnalyzer.getInstance(project).restart();
            }
        });

        //Connect to project's bus and obtain method that was updated or added
        bus.connect().subscribe(MethodNotifier.ADD_UPDATE_DELETE_METHOD, new MethodNotifier() {

            @Override
            public void addNewExistingMethod(MethodWrapper newMethod) {

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

                        addNode(newMethod);
                        break;
                }

                DaemonCodeAnalyzer.getInstance(project).restart();
            }

            @Override
            public void afterSuggestAction(ArrayList<MethodWrapper> methods) {

                HashMap<String, MethodWrapper> suggestedMethods = new HashMap<>();

                for (MethodWrapper method : methods) {
                    JSONFileLoader.addMethod(method);
                    addNode(method);
                    suggestedMethods.put(method.getSignature(true), method);
                }

                TrainingFileManager trainingFileManager = new TrainingFileManager(project);

                if(trainingFileManager.exportNew(suggestedMethods, PropertiesComponent.getInstance(project).getValue(Constants.OUTPUT_DIRECTORY))){

                    PropertiesComponent.getInstance(project).setValue(Constants.TRAIN_FILE_SUGGESTED,trainingFileManager.getTrainingFile());

                    NotificationType notificationType = NotificationType.INFORMATION;
                    Notifications.Bus.notify(new Notification(Constants.PLUGIN_GROUP_DISPLAY_ID, resource.getString("Messages.Title.Suggest.NewTrainingFile"), PropertiesComponent.getInstance(project).getValue(Constants.TRAIN_FILE_SUGGESTED), notificationType));
                }

                DaemonCodeAnalyzer.getInstance(project).restart();
            }

            @Override
            public void removeMethod(MethodWrapper newMethod) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();

                if (node == null) {
                    node = searchNode((DefaultMutableTreeNode) treeModel.getRoot(), newMethod.getSignature(true));
                }

                treeModel.removeNodeFromParent(node);
                JSONFileLoader.removeMethod(newMethod);
                DaemonCodeAnalyzer.getInstance(project).restart();
            }

            @Override
            public void restoreMethod(MethodWrapper method) {

            }
        });

        //Update Tool Window to notify user that SWAN is running
        bus.connect().subscribe(SwanNotifier.START_SWAN_PROCESS_TOPIC, new SwanNotifier() {
            @Override
            public void launchSwan(HashMap<String, String> values) {

                JSONFileLoader.setReloading(true);
                Notifications.Bus.notify(new Notification(Constants.PLUGIN_GROUP_DISPLAY_ID, resource.getString("Messages.Title.RefreshStarted"), resource.getString("Messages.Notification.RefreshStarted"), NotificationType.INFORMATION));
            }
        });

        //Update notification button when SWAN completes running
        bus.connect().subscribe(SwanNotifier.END_SWAN_PROCESS_TOPIC, new SwanNotifier() {
            @Override
            public void launchSwan(HashMap<String, String> values) {

                JSONFileLoader.setReloading(false);
                NotificationType notificationType = NotificationType.INFORMATION;

                String message = "<html>"
                        + resource.getString("Messages.Notification.Completed")
                        + "<br><a href='logs'>View Logs</a> or <a href='load'>Load Changes</a></html>";

                Notifications.Bus.notify(new Notification(Constants.PLUGIN_GROUP_DISPLAY_ID, resource.getString("Messages.Notification.Title.Completed"), message, notificationType, new NotificationListener() {
                    @Override
                    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent hyperlinkEvent) {

                        if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

                            if (hyperlinkEvent.getDescription().equals("logs")) {

                                SwanResultsDialog resultsDialog = new SwanResultsDialog(project, values);
                                resultsDialog.show();

                            } else if (hyperlinkEvent.getDescription().equals("load")) {

                                ConfigurationFileNotifier fileNotifier = bus.syncPublisher(ConfigurationFileNotifier.FILE_NOTIFIER_TOPIC);
                                fileNotifier.loadUpdatedFile(values.get(Constants.OUTPUT_FILE));
                            }
                        }
                    }
                }));
            }
        });


        //Notify user that Suggest method process started or that the methods were generated
        bus.connect().subscribe(SuggestNotifier.SUGGEST_METHOD_TOPIC, new SuggestNotifier() {

            @Override
            public void startSuggestMethod() {
                JSONFileLoader.setReloading(true);
                Notifications.Bus.notify(new Notification(Constants.PLUGIN_GROUP_DISPLAY_ID, resource.getString("Messages.Title.SuggestStarted"), resource.getString("Messages.Notification.SuggestStarted"), NotificationType.INFORMATION));

            }

            @Override
            public void endSuggestMethod(Set<MethodWrapper> values) {
                JSONFileLoader.setReloading(false);

                for (MethodWrapper method : values)
                    suggestedMethodsList.add(method.getSignature(true));

                NotificationType notificationType = NotificationType.INFORMATION;

                String message = "<html>" + resource.getString("Messages.Notification.Suggest.Completed") + "</html>";
                Notifications.Bus.notify(new Notification(Constants.PLUGIN_GROUP_DISPLAY_ID, resource.getString("Messages.Title.Suggest.Completed"), message, notificationType, new NotificationListener() {
                    @Override
                    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent hyperlinkEvent) {

                        if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

                            if (hyperlinkEvent.getDescription().equals(resource.getString("Messages.Notification.Suggest.Link"))) {

                                HashMap<String, MethodWrapper> suggestedMethods = new HashMap<>();

                                for (MethodWrapper method : values) {
                                    suggestedMethods.put(method.getSignature(true), method);
                                }

                                MethodDialog dialog = new MethodDialog(suggestedMethods, (String) suggestedMethods.keySet().toArray()[0], project, JSONFileLoader.getCategories());
                                dialog.show();
                            }
                        }
                    }
                }));
            }
        });

        //Connect to project bus and obtain filter for Method Tree
        bus.connect().subscribe(FilterNotifier.FILTER_SELECTED_TOPIC, new FilterNotifier() {
            @Override
            public void updateFilter(Pair<String, String> value) {

                if (value.equals(Constants.CLEAR_FILTER))
                    TREE_FILTERS.clear();
                else if (TREE_FILTERS.contains(value))
                    TREE_FILTERS.remove(value);
                else
                    TREE_FILTERS.add(value);

                if (TREE_FILTERS.contains(Constants.FILE_FILTER)) {

                    try {

                        Document document = FileEditorManager.getInstance(project).getSelectedTextEditor().getDocument();
                        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
                        currentFile = Formatter.getFileNameFromPath(Objects.requireNonNull(virtualFile).getName());
                    } catch (NullPointerException e) {

                        currentFile = "";
                    }
                }
                loadMethods();
            }
        });

        //Connect to message bus and filter list when a new file is selected or opened
        bus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {

                if (TREE_FILTERS.contains(Constants.FILE_FILTER)) {
                    if (event.getNewFile() != null) {
                        currentFile = Formatter.getFileNameFromPath(event.getNewFile().getName());
                    } else
                        currentFile = "";

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
     * Add new node to tree
     *
     * @param method New method to be added
     */
    private void addNode(MethodWrapper method) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
        DefaultMutableTreeNode newMethodNode = new DefaultMutableTreeNode(method);

        treeModel.insertNodeInto(addCategoriesToNode(newMethodNode, method), root, root.getChildCount());
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

        ArrayList<MethodWrapper> methods = JSONFileLoader.getMethods(TREE_FILTERS, currentFile, project);

        if (methods.size() > 0) {

            DefaultMutableTreeNode root = new DefaultMutableTreeNode("<html><b>Methods</b> <font color='gray'>[<i>" + JSONFileLoader.getConfigurationFile(false) + "</i>]</font></html>");

            for (MethodWrapper method : methods) {
                root.add(addCategoriesToNode(method));
            }
            treeModel.setRoot(root);
        } else {
            treeModel.setRoot(null);
            getEmptyText().setText(resource.getString("Messages.Notification.NoFilterResults"));
        }
    }
}
