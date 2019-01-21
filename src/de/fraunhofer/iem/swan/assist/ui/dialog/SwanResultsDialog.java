package de.fraunhofer.iem.swan.assist.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.swan.assist.comm.FileSelectedNotifier;
import de.fraunhofer.iem.swan.assist.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Provides results after SWAN finishes executing.
 *
 * @author Oshando Johnson
 */

public class SwanResultsDialog extends DialogWrapper {

    private Project project;
    private JPanel contentPane;
    private JTextField filePath;
    private JButton selectFile;
    private JTextField logPath;
    private JTextArea logText;
    private JScrollPane scrollPane;

    public SwanResultsDialog(Project project, HashMap<String, String> values) {

        super(project);
        setTitle("SWAN Results");

        this.project = project;

        filePath.setText(values.get(Constants.SWAN_OUTPUT_FILE));
        logPath.setText(values.get(Constants.SWAN_OUTPUT_LOG));

        File logs = new File(values.get(Constants.SWAN_OUTPUT_LOG));

        try {
            logText.setText(new String(Files.readAllBytes(Paths.get(logPath.getText()))));
            logText.setCaretPosition(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        init();

        selectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
                fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String path = selectedFile.getAbsoluteFile().toString();
                    filePath.setText(path);
                    logPath.setText(path.replace(Constants.OUTPUT_JSON_SUFFIX, Constants.SWAN_LOG_SUFFIX));

                    try {
                        logText.setText(new String(Files.readAllBytes(Paths.get(logPath.getText()))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void doOKAction() {

        if (isOKActionEnabled()) {
            MessageBus messageBus = project.getMessageBus();
            FileSelectedNotifier publisher = messageBus.syncPublisher(FileSelectedNotifier.UPDATED_FILE_NOTIFIER_TOPIC);
            publisher.notifyFileChange(filePath.getText());
            super.doOKAction();
        }
    }

    @NotNull
    @Override
    protected Action[] createActions() {


        return super.createActions();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
