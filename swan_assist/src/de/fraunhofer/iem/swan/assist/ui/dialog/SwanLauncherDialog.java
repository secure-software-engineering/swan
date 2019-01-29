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
import java.util.HashMap;
import java.util.Objects;

/**
 * Dialog to lauch configuration window for SWAN before running the application.
 *
 * @author Oshando Johnson
 */

public class SwanLauncherDialog extends DialogWrapper {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField sourceDirTextbox;
    private JButton sourceBtn;
    private JTextField configDir;
    private JButton configBtn;
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

    public SwanLauncherDialog(Project project, boolean modal) {

        super(project, modal);
        setTitle("Launch SWAN");

        File configurationFile = new File(JSONFileLoader.getConfigurationFile(true));

        defaultTrainDirectory = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getPath() + Constants.SWAN_TRAIN_DIR_NAME;

        String jarDir = Objects.requireNonNull(getClass().getClassLoader().getResource(Constants.SWAN_JAR_NAME)).getPath();

        if (PlatformUtil.isWindows() || System.getProperty("os.name").toLowerCase().contains("win"))
            swanJarDirecory = jarDir.substring(1);
        else
            swanJarDirecory = jarDir;

        sourceDirTextbox.setText(project.getBasePath());
        configDir.setText(configurationFile.getAbsolutePath());
        outputDir.setText(configurationFile.getParent() + File.separator + Constants.OUTPUT_DIR_NAME);

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
                    configDir.setText(configurationFile.toString());
                }

                sourceBtn.setEnabled(!sourceCheckBox.isSelected());
                configBtn.setEnabled(!sourceCheckBox.isSelected());
                sourceDirTextbox.setEditable(!sourceCheckBox.isSelected());
                configDir.setEditable(!sourceCheckBox.isSelected());
            }
        });

        advancedCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                advancedPanel.setVisible(advancedCheckbox.isSelected());

                if (advancedCheckbox.isSelected()) {

                    contentPane.setPreferredSize(new Dimension(650, 250));
                    pack();
                    trainingTextbox.setText("");
                    outputDir.setText(configurationFile.getParent() + File.separator + Constants.OUTPUT_DIR_NAME);
                } else {
                    contentPane.setPreferredSize(new Dimension(650, 180));
                    pack();
                }
            }
        });


        sourceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                sourceDirTextbox.setText(fileSelector(JFileChooser.DIRECTORIES_ONLY, sourceDirTextbox.getText()));
            }
        });

        configBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                configDir.setText(fileSelector(JFileChooser.FILES_ONLY, configDir.getText()));
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
            if (sourceDirTextbox.getText().trim().isEmpty() || configDir.getText().trim().isEmpty()) {

                JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(Constants.LAUNCHER_PATH_NOT_SELECTED, MessageType.ERROR, null)
                        .createBalloon()
                        .show(JBPopupFactory.getInstance().guessBestPopupLocation(sourceDirTextbox), Balloon.Position.below);
            } else if (advancedCheckbox.isSelected() && (trainingTextbox.getText().isEmpty() || outputDir.getText().isEmpty())) {

                JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(Constants.LAUNCHER_PATH_NOT_SELECTED, MessageType.ERROR, null)
                        .createBalloon()
                        .show(JBPopupFactory.getInstance().guessBestPopupLocation(advancedCheckbox), Balloon.Position.below);
            } else if (!advancedCheckbox.isSelected()) {

                parameters.put(Constants.SWAN_SOURCE_DIR, sourceDirTextbox.getText());
                parameters.put(Constants.SWAN_CONFIG_FILE, configDir.getText());
                parameters.put(Constants.SWAN_TRAIN_DIR, defaultTrainDirectory);
                parameters.put(Constants.SWAN_OUTPUT_DIR, outputDir.getText());
                super.doOKAction();
            } else {

                parameters.put(Constants.SWAN_SOURCE_DIR, sourceDirTextbox.getText());
                parameters.put(Constants.SWAN_CONFIG_FILE, configDir.getText());
                parameters.put(Constants.SWAN_TRAIN_DIR, trainingTextbox.getText());
                parameters.put(Constants.SWAN_OUTPUT_DIR, outputDir.getText());
                super.doOKAction();
            }
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    //Accept users file or folder selection and send return value
    private String fileSelector(int selectionMethod, String path) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(selectionMethod);
        fileChooser.setSelectedFile(new File(path));

        if (selectionMethod == JFileChooser.FILES_ONLY)
            fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        int returnValue = fileChooser.showOpenDialog(this.contentPane);

        if (returnValue == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile().getPath();
        else
            return path;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }
}
