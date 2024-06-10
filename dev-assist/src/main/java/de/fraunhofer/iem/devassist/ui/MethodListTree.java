/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.devassist.ui;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.devassist.actions.method.MethodActionGroup;
import de.fraunhofer.iem.devassist.comm.*;
import de.fraunhofer.iem.devassist.data.JSONFileLoader;
import de.fraunhofer.iem.devassist.data.MethodWrapper;
import de.fraunhofer.iem.devassist.data.TrainingFileManager;
import de.fraunhofer.iem.devassist.ui.dialog.MethodDialog;
import de.fraunhofer.iem.devassist.util.Constants;
import de.fraunhofer.iem.devassist.util.Formatter;
import de.fraunhofer.iem.devassist.util.Pair;
import de.fraunhofer.iem.devassist.util.PsiTraversal;
import de.fraunhofer.iem.swan.data.Category;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
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
    public static boolean TREE_EXPANDED;


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
        TREE_EXPANDED = true;

        suggestedMethodsList = new HashSet<>();

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

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

                        if (object instanceof Pair) {

                            Pair classPair = (Pair) object;
                            String classname = classPair.getKey().toString();
                            String simpleClassname = classname.substring(classname.lastIndexOf(".") + 1);
                            Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(simpleClassname + ".java", GlobalSearchScope.allScope(project));

                            boolean methodFound = false;

                            for (VirtualFile file : files) {

                                PsiJavaFile psiJavaFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(file);

                                if ((psiJavaFile.getPackageName() + "." + simpleClassname).contentEquals(classname)) {

                                    methodFound = true;

                                    OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(project, psiJavaFile.getVirtualFile());
                                    fileDescriptor.navigateInEditor(project, true);
                                }
                            }

                            if (!methodFound) {
                                JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(resource.getString("Messages.Error.ClassNotFound"), MessageType.ERROR, null)
                                        .createBalloon()
                                        .show(JBPopupFactory.getInstance().guessBestPopupLocation((JComponent) e.getComponent()), Balloon.Position.below);
                            }

                        } else if (object instanceof MethodWrapper) {

                            MethodWrapper method = (MethodWrapper) object;

                            //Get PSI element location
                            Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(method.getFileName(), GlobalSearchScope.allScope(project));
                            String methodSignature = method.getSignature(true);

                            if (methodSignature.contains("<init>"))
                                methodSignature = Formatter.trimProperty(method.getClassName(false));

                            boolean methodFound = false;

                            for (VirtualFile file : files) {

                                PsiJavaFile psiJavaFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(file);

                                for (PsiClass psiClass : psiJavaFile.getClasses()) {
                                    for (PsiMethod psiMethod : psiClass.getMethods()) {

                                        if (Objects.equals(PsiTraversal.getMethodSignature(psiMethod), methodSignature)) {
                                            methodFound = true;

                                            OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(project, file, psiMethod.getTextOffset());
                                            fileDescriptor.navigateInEditor(project, true);
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

                                PropertiesComponent.getInstance(project).setValue(Constants.LAST_SRM_LIST, fileName);
                                JSONFileLoader.setConfigurationFile(fileName, project);
                                JSONFileLoader.loadInitialFile();
                            }
                        });
                    }

                    @Override
                    public void onFinished() {
                        super.onFinished();
                        loadMethods();
                        DaemonCodeAnalyzer.getInstance(project).restart();
                    }
                });
            }

            @Override
            public void loadUpdatedFile(String fileName) {

                ProgressManager.getInstance().run(new Task.Backgroundable(project, resource.getString("Status.ImportFile")) {
                    @Override
                    public void run(@NotNull ProgressIndicator progressIndicator) {

                        ApplicationManager.getApplication().runReadAction(new Runnable() {
                            public void run() {
                                JSONFileLoader.loadUpdatedFile(fileName, project);
                            }
                        });
                    }

                    @Override
                    public void onFinished() {
                        super.onFinished();
                        loadMethods();
                        DaemonCodeAnalyzer.getInstance(project).restart();
                    }
                });
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

                if (trainingFileManager.exportNew(suggestedMethods, PropertiesComponent.getInstance(project).getValue(Constants.OUTPUT_DIRECTORY))) {

                    PropertiesComponent.getInstance(project).setValue(Constants.TRAIN_FILE_SUGGESTED, trainingFileManager.getTrainingFile());

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

                DefaultMutableTreeNode root = (DefaultMutableTreeNode) node.getParent();

                if (root != null) {
                    Pair classPair = (Pair) root.getUserObject();

                    int childCount = (int) classPair.getValue() - 1;

                    if (childCount == 0) {
                        treeModel.removeNodeFromParent(root);
                    } else {
                        root.setUserObject(new Pair<>(classPair.getKey(), childCount));
                        treeModel.removeNodeFromParent(node);
                    }
                }

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
            }
        });

        //Update notification button when SWAN completes running
        bus.connect().subscribe(SwanNotifier.END_SWAN_PROCESS_TOPIC, new SwanNotifier() {
            @Override
            public void launchSwan(HashMap<String, String> values) {

                JSONFileLoader.setReloading(false);

                NotificationGroupManager.getInstance()
                        .getNotificationGroup("SRM Notification")
                        .createNotification(values.get(Constants.ANALYSIS_RESULT), NotificationType.INFORMATION)
                        .notify(project);

                ConfigurationFileNotifier fileNotifier = bus.syncPublisher(ConfigurationFileNotifier.FILE_NOTIFIER_TOPIC);
                fileNotifier.loadUpdatedFile(values.get(Constants.OUTPUT_FILE));
            }
        });

        //Update notification button when SWAN completes running
        bus.connect().subscribe(SecucheckNotifier.END_SECUCHECK_PROCESS_TOPIC, new SecucheckNotifier() {
            @Override
            public void launchSecuCheck() {
                String notificationContent = "Taint analysis results exported successfully.";
                NotificationGroupManager.getInstance()
                        .getNotificationGroup("Analysis Notification")
                        .createNotification(notificationContent, NotificationType.INFORMATION)
                        .addAction(new NotificationAction("Open Results") {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {

                                File results = new File(Objects.requireNonNull(PropertiesComponent.getInstance(project).getValue(Constants.LAST_SARIF_FILE)));
                                Optional<VirtualFile> file = FilenameIndex
                                        .getVirtualFilesByName(results.getName(),
                                                GlobalSearchScope.projectScope(project)).stream().findFirst();

                                file.ifPresent(virtualFile -> new OpenFileDescriptor(project, virtualFile).navigate(true));
                            }
                        })
                        .notify(project);
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

                                MethodDialog dialog = new MethodDialog(suggestedMethods, (String) suggestedMethods.keySet().toArray()[0], project, JSONFileLoader.getAllCategories());
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
                else if (TREE_FILTERS.contains(value)) {
                    TREE_FILTERS.remove(value);
                } else {
                    TREE_FILTERS.add(value);
                }
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


        bus.connect().subscribe(ExpandNotifier.EXPAND_COLLAPSE_LIST, new ExpandNotifier() {
            @Override
            public void expandTree(boolean expand) {

                TREE_EXPANDED = !TREE_EXPANDED;

                expandErrors(TREE_EXPANDED);
            }
        });
    }

    /**
     * Searches if a method already exists in the Tree.
     *
     * @param root  root object of tree
     * @param query the method that is being searched for
     * @return returns the node if it's found
     */
    private DefaultMutableTreeNode searchNode(DefaultMutableTreeNode root, String query) {

        Enumeration e = root.breadthFirstEnumeration();

        while (e.hasMoreElements()) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();

            if (node.getUserObject() instanceof MethodWrapper) {

                MethodWrapper methodWrapper = (MethodWrapper) node.getUserObject();
                if (query.equals(methodWrapper.getSignature(true))) {
                    return node;
                }
            } else if (node.getUserObject() instanceof Pair) {

                Pair classPair = (Pair) node.getUserObject();
                String classname = classPair.getKey().toString();

                if (classname.equals(query))
                    return node;
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

        DefaultMutableTreeNode root = searchNode((DefaultMutableTreeNode) getModel().getRoot(), method.getClassName(true));

        if (root == null) {
            root = (DefaultMutableTreeNode) getModel().getRoot();

            DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(new Pair<>(method.getClassName(true), 1));
            classNode.add(addCategoriesToNode(method));

            treeModel.insertNodeInto(classNode, root, root.getChildCount());
        } else {

            if (root.getUserObject() instanceof Pair) {

                Pair rootClass = (Pair) root.getUserObject();
                root.setUserObject(new Pair<>(method.getClassName(true), (int) rootClass.getValue() + 1));
            }

            DefaultMutableTreeNode newMethodNode = new DefaultMutableTreeNode(method);
            treeModel.insertNodeInto(addCategoriesToNode(newMethodNode, method), root, root.getChildCount());
        }
    }

    /**
     * Loads methods from file and uses them to create tree object.
     */
    private void loadMethods() {

        TreeMap<String, ArrayList<MethodWrapper>> methods = JSONFileLoader.getMethodsForTree(TREE_FILTERS, currentFile, project);

        if (!methods.isEmpty()) {

            DefaultMutableTreeNode root = new DefaultMutableTreeNode("<html>" + "<b>Security Relevant Methods</b></html>");

            DefaultMutableTreeNode currentProject = new DefaultMutableTreeNode("Project");
            DefaultMutableTreeNode standardSrms = new DefaultMutableTreeNode("Standard");


            int methodCount = 0;
            int totalMethods = 0;
            for (String classname : methods.keySet()) {

                methodCount = methods.get(classname).size();
                totalMethods += methodCount;

                DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(new Pair<>(classname, methodCount));

                ArrayList<MethodWrapper> sortedList = methods.get(classname);
                Collections.sort(sortedList);
                for (MethodWrapper method : sortedList) {

                    classNode.add(addCategoriesToNode(method));

                    if (method.getMethod().isKnown()) {
                        method.setTrainingMethod(true);
                        standardSrms.add(classNode);
                    } else {
                        currentProject.add(classNode);
                    }
                }
            }

            String pattern = "###,###";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);

            currentProject.setUserObject("<html><b>" + project.getName() + "</b> <font color='gray'>(" + currentProject.getLeafCount() + " in " + currentProject.getChildCount() + " classes)</font></html>");
            root.add(currentProject);

            standardSrms.setUserObject("<html><b>Known SRMs</b> <font color='gray'>(" + standardSrms.getLeafCount() + " in " + standardSrms.getChildCount() + " classes)</font></html>");
            root.add(standardSrms);

            treeModel.setRoot(root);
            TREE_EXPANDED = false;
        } else {
            treeModel.setRoot(null);
            getEmptyText().setText(resource.getString("Messages.Notification.NoFilterResults"));
        }
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
     * @param expand flag for whether the tree should be expanded or not
     */
    private void expandErrors(boolean expand) {

        TreeNode root = (TreeNode) this.getModel().getRoot();
        expandAll(this, new TreePath(root), expand);
    }

    /**
     * @param tree   that should be expanded
     * @param path   expansion path
     * @param expand flag for whether the tree should be expanded or not
     */
    private void expandAll(Tree tree, TreePath path, boolean expand) {
        TreeNode node = (TreeNode) path.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            Enumeration enumeration = node.children();

            while (enumeration.hasMoreElements()) {
                TreeNode treeNode = (TreeNode) enumeration.nextElement();
                TreePath treePath = path.pathByAddingChild(treeNode);

                expandAll(tree, treePath, expand);
            }
        }

        if (expand) {
            tree.expandPath(path);
        } else {
            tree.collapsePath(path);
        }
    }
}
