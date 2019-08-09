/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import de.fraunhofer.iem.swan.assist.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Provides results after SWAN finishes executing.
 */

public class SwanResultsDialog extends DialogWrapper {

    private Project project;
    private JPanel contentPane;
    private JTextField filePath;
    private JButton selectFile;
    private JTextField logPath;
    private JTextArea logText;
    private JScrollPane scrollPane;

    /**
     * Initializes dialog with provided arguments
     * @param project Active project in IDE
     * @param values HashMap of output file, logs and other values for the SWAN run.
     */
    public SwanResultsDialog(Project project, HashMap<String, String> values) {

        super(project);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("dialog_messages");
        setTitle(resourceBundle.getString("Results.Title"));

        Properties config = new Properties();
        InputStream input = null;

        try {
            input = getClass().getClassLoader().getResourceAsStream("config.properties");
            config.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        this.project = project;

        filePath.setText(values.get(Constants.OUTPUT_FILE));
        logPath.setText(values.get(Constants.OUTPUT_LOG));

        File logs = new File(values.get(Constants.OUTPUT_LOG));

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
                fileChooser.setFileFilter(new FileNameExtensionFilter(resourceBundle.getString("FileChooser.FileDescription"), resourceBundle.getString("FileChooser.FileExtension")));

                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String path = selectedFile.getAbsoluteFile().toString();
                    filePath.setText(path);
                    logPath.setText(path.replace(config.getProperty("output_json_suffix"), config.getProperty("log_suffix")));

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
