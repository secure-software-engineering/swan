/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.ui.dialog;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import de.fraunhofer.iem.swan.assist.data.JSONFileLoader;
import de.fraunhofer.iem.swan.assist.util.Constants;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Dialog to launch configuration window for SWAN before running the application.
 */

public class PluginSettingsDialog extends DialogWrapper {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField sourceDirTextbox;
    private JButton sourceBtn;
    private JTextField outputDir;
    private JButton outputBtn;
    private JTextField trainingTextbox;
    private JButton trainButton;
    private JPanel trainingPanel;
    private JCheckBox trainingPathCheckbox;
    private HashMap<String, String> parameters = new HashMap<String, String>();
    private ResourceBundle resourceBundle;
    private Properties config;
    private Project project;

    /**
     * Initializes dialog to launch SWAN.
     *
     * @param project Active project in the editor
     * @param modal   Modal setting for dialog
     */
    public PluginSettingsDialog(Project project, boolean modal) {

        super(project, modal);
        this.project = project;
        resourceBundle = ResourceBundle.getBundle("dialog_messages");
        setTitle(resourceBundle.getString("Launcher.Title"));

        config = new Properties();
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

        File configurationFile = new File(JSONFileLoader.getConfigurationFile(true));

        //Use value for source path, if available. Otherwise set the default path
        if (!PropertiesComponent.getInstance(project).isValueSet(Constants.SOURCE_DIRECTORY))
            sourceDirTextbox.setText(project.getBasePath());
        else
            sourceDirTextbox.setText(PropertiesComponent.getInstance(project).getValue(Constants.SOURCE_DIRECTORY));

        //Use value for the output path, if available. Otherwise set the default path
        if (!PropertiesComponent.getInstance(project).isValueSet(Constants.OUTPUT_DIRECTORY))
            outputDir.setText(project.getBasePath() + File.separator + config.getProperty("output_dir_name"));
        else
            outputDir.setText(PropertiesComponent.getInstance(project).getValue(Constants.OUTPUT_DIRECTORY));

        //Set value for using training path
        if (PropertiesComponent.getInstance(project).isValueSet(Constants.DEFAULT_TRAINING_PATH)) {

            trainingPathCheckbox.setSelected(PropertiesComponent.getInstance(project).getBoolean(Constants.DEFAULT_TRAINING_PATH));
        }

        for (Component component : trainingPanel.getComponents()) {
            component.setEnabled(!trainingPathCheckbox.isSelected());
        }

        if(PropertiesComponent.getInstance(project).isValueSet(Constants.TRAIN_DIRECTORY)){
            trainingTextbox.setText(PropertiesComponent.getInstance(project).getValue(Constants.TRAIN_DIRECTORY));
        }

        init();
        /*
         * Action Listeners for buttons
         */


        trainingPathCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println(trainingPathCheckbox.isSelected());
                PropertiesComponent.getInstance(project).setValue(Constants.DEFAULT_TRAINING_PATH, trainingPathCheckbox.isSelected());

                for (Component component : trainingPanel.getComponents()) {
                    component.setEnabled(!trainingPathCheckbox.isSelected());
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

            //ensure that required fields are populated
            if (sourceDirTextbox.getText().trim().isEmpty() || outputDir.getText().isEmpty()) {

                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder(resourceBundle.getString("Messages.Error.PathNotFound"), MessageType.ERROR, null)
                        .createBalloon()
                        .show(JBPopupFactory.getInstance().guessBestPopupLocation(sourceDirTextbox), Balloon.Position.below);
            } else if (!trainingPathCheckbox.isSelected() && trainingTextbox.getText().isEmpty()) {

                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder(resourceBundle.getString("Messages.Error.PathNotFound"), MessageType.ERROR, null)
                        .createBalloon()
                        .show(JBPopupFactory.getInstance().guessBestPopupLocation(trainingPathCheckbox), Balloon.Position.below);
            } else if (!trainingPathCheckbox.isSelected()) {

                parameters.put(Constants.TRAIN_DIRECTORY, trainingTextbox.getText());
                PropertiesComponent.getInstance(project).setValue(Constants.TRAIN_DIRECTORY, trainingTextbox.getText());

                setParameters();
            } else {

                parameters.put(Constants.TRAIN_DIRECTORY, config.getProperty("swan_default_param_value"));
                setParameters();
            }
        }
    }

    private void setParameters() {
        parameters.put(Constants.SOURCE_DIRECTORY, sourceDirTextbox.getText());
        parameters.put(Constants.OUTPUT_DIRECTORY, outputDir.getText());
        parameters.put(Constants.OUTPUT_LOG, config.getProperty("log_suffix"));
        parameters.put(Constants.OUTPUT_FILE, parameters.get(Constants.OUTPUT_DIRECTORY) + File.separator + config.getProperty("output_json_suffix"));

        PropertiesComponent.getInstance(project).setValue(Constants.OUTPUT_DIRECTORY, outputDir.getText());
        PropertiesComponent.getInstance(project).setValue(Constants.SOURCE_DIRECTORY, sourceDirTextbox.getText());

        super.doOKAction();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    /**
     * Accept users file or folder selection and send return value
     *
     * @param selectionMethod Selection method that should be used by File chooser
     * @param path            Default path
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
     *
     * @return HashMap of parameters used to run SWAN,
     */
    public HashMap<String, String> getParameters() {
        return parameters;
    }
}
