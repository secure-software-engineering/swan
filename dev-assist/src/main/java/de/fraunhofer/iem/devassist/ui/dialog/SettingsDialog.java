/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.devassist.ui.dialog;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import de.fraunhofer.iem.devassist.data.JSONFileLoader;
import de.fraunhofer.iem.devassist.util.Constants;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Dialog to launch configuration window for SWAN before running the application.
 */

public class SettingsDialog extends DialogWrapper {

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
    private JRadioButton wekaRadioButton;
    private JRadioButton mekaRadioButton;
    private ButtonGroup toolkitButtonGroup;
    private JPanel toolkitPanel;
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
    public SettingsDialog(Project project, boolean modal) {

        super(project, modal);
        trainingPathCheckbox.setVisible(false);
        trainingPanel.setVisible(false);
        toolkitPanel.setVisible(false);
        this.project = project;
        resourceBundle = ResourceBundle.getBundle("dialog_messages");
        setTitle(resourceBundle.getString("SettingsDialog.Title"));

        mekaRadioButton.setActionCommand("meka");
        wekaRadioButton.setActionCommand("weka");

        toolkitButtonGroup = new ButtonGroup();
        toolkitButtonGroup.add(mekaRadioButton);
        toolkitButtonGroup.add(wekaRadioButton);

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
        sourceDirTextbox.setText(PropertiesComponent.getInstance(project).getValue(Constants.SOURCE_DIRECTORY,
                project.getBasePath() + "/target/classes"));


        //Use value for the output path, if available. Otherwise set the default path
        outputDir.setText(PropertiesComponent.getInstance(project).getValue(Constants.OUTPUT_DIRECTORY,
                project.getBasePath() + File.separator + config.getProperty("output_dir_name")));


        //Set value for using training path
        trainingPathCheckbox.setSelected(PropertiesComponent.getInstance(project).getBoolean(Constants.DEFAULT_TRAINING_PATH, false));

        for (Component component : trainingPanel.getComponents()) {
            component.setEnabled(trainingPathCheckbox.isSelected());
        }

        //Set value for using configuration file
//        configurationPathCheckbox.setSelected(PropertiesComponent.getInstance(project).getBoolean(Constants.PROJECT_CONFIGURATION_FILE, false));
//
//        for (Component component : configurationFilePanel.getComponents()) {
//            component.setEnabled(configurationPathCheckbox.isSelected());
//        }


        if (PropertiesComponent.getInstance(project).isValueSet(Constants.TRAIN_DIRECTORY)) {
            trainingTextbox.setText(PropertiesComponent.getInstance(project).getValue(Constants.TRAIN_DIRECTORY));
        }

//        if (PropertiesComponent.getInstance(project).isValueSet(Constants.CONFIGURATION_FILE)) {
//            configFileTextbox.setText(PropertiesComponent.getInstance(project).getValue(Constants.CONFIGURATION_FILE));
//        }

        init();
        /*
         * Action Listeners for buttons
         */


        trainingPathCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                PropertiesComponent.getInstance(project).setValue(Constants.DEFAULT_TRAINING_PATH, trainingPathCheckbox.isSelected());

                for (Component component : trainingPanel.getComponents()) {
                    component.setEnabled(trainingPathCheckbox.isSelected());
                }
            }
        });

        /*configurationPathCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                PropertiesComponent.getInstance(project).setValue(Constants.PROJECT_CONFIGURATION_FILE, configurationPathCheckbox.isSelected());

                for (Component component : configurationFilePanel.getComponents()) {
                    component.setEnabled(configurationPathCheckbox.isSelected());
                }
            }
        }); */

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

        /*configButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                configFileTextbox.setText(fileSelector(JFileChooser.FILES_ONLY, configFileTextbox.getText()));
            }
        });*/
    }

    @Override
    protected void doOKAction() {
        if (isOKActionEnabled()) {

            //ensure that required fields are populated
            if (sourceDirTextbox.getText().trim().isEmpty()
                    || !(new File(sourceDirTextbox.getText().trim()).exists())
                    || (FileUtils.listFiles(new File(sourceDirTextbox.getText()), new String[]{"class", "jar"}, true)).size() == 0) {

                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder(resourceBundle.getString("Messages.Error.PathNotFound"), MessageType.ERROR, null)
                        .createBalloon()
                        .show(JBPopupFactory.getInstance().guessBestPopupLocation(sourceDirTextbox), Balloon.Position.below);
            } else if (outputDir.getText().isEmpty()) {
                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder(resourceBundle.getString("Messages.Error.PathNotFound"), MessageType.ERROR, null)
                        .createBalloon()
                        .show(JBPopupFactory.getInstance().guessBestPopupLocation(outputDir), Balloon.Position.below);
            } else if (trainingPathCheckbox.isSelected() && trainingTextbox.getText().isEmpty()) {

                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder(resourceBundle.getString("Messages.Error.PathNotFound"), MessageType.ERROR, null)
                        .createBalloon()
                        .show(JBPopupFactory.getInstance().guessBestPopupLocation(trainingPathCheckbox), Balloon.Position.below);
            } else {
                setParameters();
                //Notification analysisCompleted = new Notification(Constants.PLUGIN_GROUP_DISPLAY_ID, "Starting Analysis", "Analysis completed", NotificationType.INFORMATION);
                //analysisCompleted.notify();
            }
        }
    }

    private void setParameters() {

        //Check if option to use default training libs folder is selected
        if (trainingPathCheckbox.isSelected()) {
            parameters.put(Constants.TRAIN_DIRECTORY, trainingTextbox.getText());
            PropertiesComponent.getInstance(project).setValue(Constants.TRAIN_DIRECTORY, trainingTextbox.getText());
        } else {
            parameters.put(Constants.TRAIN_DIRECTORY, config.getProperty("swan_default_param_value"));
        }

        //Check if option to use default configuration file is selected
        /* if (configurationPathCheckbox.isSelected()) {
            parameters.put(Constants.CONFIGURATION_FILE, configFileTextbox.getText());
        } else */
        //set the configuration file to default
        parameters.put(Constants.CONFIGURATION_FILE, config.getProperty("swan_default_param_value"));
        parameters.put(Constants.SOURCE_DIRECTORY, sourceDirTextbox.getText());
        parameters.put(Constants.OUTPUT_DIRECTORY, outputDir.getText());
        parameters.put(Constants.OUTPUT_LOG, config.getProperty("log_suffix"));
        parameters.put(Constants.OUTPUT_FILE, parameters.get(Constants.OUTPUT_DIRECTORY) + File.separator + config.getProperty("output_json_suffix"));

        PropertiesComponent.getInstance(project).setValue(Constants.OUTPUT_DIRECTORY, outputDir.getText());
        PropertiesComponent.getInstance(project).setValue(Constants.SOURCE_DIRECTORY, sourceDirTextbox.getText());
        PropertiesComponent.getInstance(project).setValue(Constants.LAST_SRM_LIST, parameters.get(Constants.OUTPUT_DIRECTORY) + File.separator + config.getProperty("output_json_suffix"));
        PropertiesComponent.getInstance(project).setValue(Constants.TOOLKIT, toolkitButtonGroup.getSelection().getActionCommand());

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
