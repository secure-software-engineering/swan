/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.sun.javafx.PlatformUtil;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.util.Constants;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Dialog to launch configuration window for SWAN before running the application.
 */

public class SwanLauncherDialog extends DialogWrapper {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField sourceDirTextbox;
    private JButton sourceBtn;
    private JTextField outputDir;
    private JButton outputBtn;
    private JCheckBox sourceCheckBox;
    private JCheckBox advancedCheckbox;
    private JTextField trainingTextbox;
    private JButton trainButton;
    private JPanel advancedPanel;
    private HashMap<String, String> parameters = new HashMap<String, String>();
    private String defaultTrainDirectory;
    private String swanJarDirecory;
    private ResourceBundle resourceBundle;
    private Properties config;

    /**
     * Initializes dialog to launch SWAN.
     * @param project Active project in the editor
     * @param modal Modal setting for dialog
     */
    public SwanLauncherDialog(Project project, boolean modal) {

        super(project, modal);
        resourceBundle = ResourceBundle.getBundle("dialog_messages");
        setTitle(resourceBundle.getString("Launcher.Title"));

        config = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(getClass().getClassLoader().getResource("").getPath()+"config.properties");
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

        File configurationFile = new File(JSONFileLoader.getConfigurationFile(true));

        defaultTrainDirectory = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getPath() + config.getProperty("train_dir_name");

        String jarDir = Objects.requireNonNull(getClass().getClassLoader().getResource(config.getProperty("swan_jar_name"))).getPath();

        if (PlatformUtil.isWindows() || System.getProperty("os.name").toLowerCase().contains("win"))
            swanJarDirecory = jarDir.substring(1);
        else
            swanJarDirecory = jarDir;

        sourceDirTextbox.setText(project.getBasePath());
        outputDir.setText(configurationFile.getParent() + File.separator + config.getProperty("output_dir_name"));

        for (Component component : advancedPanel.getComponents()) {
            component.setEnabled(advancedCheckbox.isSelected());
        }

        init();
        /*
         * Action Listeners for buttons
         */

        sourceCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //restore default values
                if (sourceCheckBox.isSelected()) {
                    sourceDirTextbox.setText(project.getBasePath());
                }

                sourceBtn.setEnabled(!sourceCheckBox.isSelected());
                sourceDirTextbox.setEditable(!sourceCheckBox.isSelected());
            }
        });

        advancedCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (Component component : advancedPanel.getComponents()) {
                    component.setEnabled(advancedCheckbox.isSelected());
                }

                if (advancedCheckbox.isSelected()) {
                    trainingTextbox.setText("");
                    outputDir.setText(configurationFile.getParent() + File.separator + config.getProperty("output_dir_name"));
                }
            }
        });

        sourceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                sourceDirTextbox.setText(fileSelector(JFileChooser.DIRECTORIES_ONLY, sourceDirTextbox.getText()));
            }
        });

        outputBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                outputDir.setText(fileSelector(JFileChooser.DIRECTORIES_ONLY, outputDir.getText()));
            }
        });

        trainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                trainingTextbox.setText(fileSelector(JFileChooser.DIRECTORIES_ONLY, trainingTextbox.getText()));
            }
        });
    }

    @Override
    protected void doOKAction() {

        if (isOKActionEnabled()) {
            //set path for jar directory
            parameters.put(Constants.SWAN_JAR_DIR, swanJarDirecory);

            //ensure that required fields are populated
            if (sourceDirTextbox.getText().trim().isEmpty()) {

                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder(resourceBundle.getString("Messages.Error.PathNotFound"), MessageType.ERROR, null)
                        .createBalloon()
                        .show(JBPopupFactory.getInstance().guessBestPopupLocation(sourceDirTextbox), Balloon.Position.below);
            } else if (advancedCheckbox.isSelected() && (trainingTextbox.getText().isEmpty() || outputDir.getText().isEmpty())) {

                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder(resourceBundle.getString("Messages.Error.PathNotFound"), MessageType.ERROR, null)
                        .createBalloon()
                        .show(JBPopupFactory.getInstance().guessBestPopupLocation(advancedCheckbox), Balloon.Position.below);
            } else if (!advancedCheckbox.isSelected()) {

                parameters.put(Constants.SWAN_SOURCE_DIR, sourceDirTextbox.getText());
                parameters.put(Constants.SWAN_TRAIN_DIR, defaultTrainDirectory);
                parameters.put(Constants.SWAN_OUTPUT_DIR, outputDir.getText());
                parameters.put(Constants.SWAN_OUTPUT_LOG, config.getProperty("log_suffix"));
                parameters.put(Constants.SWAN_OUTPUT_FILE, parameters.get(Constants.SWAN_OUTPUT_DIR) + File.separator  + config.getProperty("output_json_suffix"));
                super.doOKAction();
            } else {

                parameters.put(Constants.SWAN_SOURCE_DIR, sourceDirTextbox.getText());
                parameters.put(Constants.SWAN_TRAIN_DIR, trainingTextbox.getText());
                parameters.put(Constants.SWAN_OUTPUT_DIR, outputDir.getText());
                parameters.put(Constants.SWAN_OUTPUT_FILE, parameters.get(Constants.SWAN_OUTPUT_DIR) + File.separator  + config.getProperty("output_json_suffix"));
                super.doOKAction();
            }
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    /**
     * Accept users file or folder selection and send return value
     * @param selectionMethod Selection method that should be used by File chooser
     * @param path Default path
     * @return Returns default path or path selected by the user.
     */
    private String fileSelector(int selectionMethod, String path) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(selectionMethod);
        fileChooser.setSelectedFile(new File(path));

        if (selectionMethod == JFileChooser.FILES_ONLY)
            fileChooser.setFileFilter(new FileNameExtensionFilter(resourceBundle.getString("FileChooser.FileDescription"), resourceBundle.getString("FileChooser.FileExtension")));

        int returnValue = fileChooser.showOpenDialog(this.contentPane);

        if (returnValue == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile().getPath();
        else
            return path;
    }

    /**
     * Returns parameters
     * @return HashMap of parameters used to run SWAN,
     */
    public HashMap<String, String> getParameters() {
        return parameters;
    }
}
