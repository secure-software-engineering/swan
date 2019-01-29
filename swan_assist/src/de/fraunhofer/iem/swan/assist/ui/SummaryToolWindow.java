package de.fraunhofer.iem.swan.assist.ui;

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
import de.fraunhofer.iem.swan.assist.util.Formatter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

/**
 * Tool Window implementation for the Plugin.
 *
 * @author Oshando Johnson
 */

public class SummaryToolWindow implements ToolWindowFactory {

    private String currentFile;
    public static ArrayList<String> TREE_FILTERS;
    public static boolean CURRENT_FILE_FILTER;
    public static boolean RESTORE_METHOD;
    public static boolean CURRENT_PROJECT_FILTER;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        JBPanel toolPanel = new JBPanel(new BorderLayout());

        //Toolbar action panel
        final DefaultActionGroup actions = (DefaultActionGroup) ActionManager.getInstance().getAction("SWAN_Assist.ActionBar");
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

        //Add Content to ToolWindow
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content toolContent = contentFactory.createContent(toolPanel, "", false);
        toolWindow.getContentManager().addContent(toolContent);
    }
}
