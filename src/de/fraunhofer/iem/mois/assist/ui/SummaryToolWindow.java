package de.fraunhofer.iem.mois.assist.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import de.fraunhofer.iem.mois.assist.util.Constants;
import de.fraunhofer.iem.mois.assist.util.Formatter;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Tool Window implementation for the Plugin.
 *
 * @author Oshando Johnson
 */

public class SummaryToolWindow implements ToolWindowFactory {

    private String currentFile;
    private JBPanel notificationPanel;
    public static ArrayList<String> TREE_FILTERS;
    public static boolean CURRENT_FILE_FILTER;
    public static boolean RESTORE_METHOD;
    public static boolean CURRENT_PROJECT_FILTER;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        JBPanel toolPanel = new JBPanel(new BorderLayout());

        //Toolbar action panel
        final DefaultActionGroup actions = (DefaultActionGroup) ActionManager.getInstance().getAction("AssistMOIS.ActionBar");
        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("SummaryToolbar", actions, true);
        toolPanel.add(actionToolbar.getComponent(), BorderLayout.PAGE_START);

        TREE_FILTERS = new ArrayList<>();
        CURRENT_FILE_FILTER = false;
        RESTORE_METHOD = false;
        CURRENT_PROJECT_FILTER = false;

        //TODO exception when no file is open
        Document document = FileEditorManager.getInstance(project).getSelectedTextEditor().getDocument();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        currentFile = Formatter.getFileNameFromPath(virtualFile.getName());

        //Add method list tree to tool window
        toolPanel.add(new JBScrollPane(new MethodListTree(project)), BorderLayout.CENTER);

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
    }
}
