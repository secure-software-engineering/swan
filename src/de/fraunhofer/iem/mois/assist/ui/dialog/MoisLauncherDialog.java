package de.fraunhofer.iem.mois.assist.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.util.Constants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;

/**
 * Dialog to lauch configuration window for MOIS before running the application.
 * @author Oshando Johnson
 */

public class MoisLauncherDialog extends JDialog {

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

    private boolean confirm = false;
    private HashMap<String, String> parameters = new HashMap<String, String>();

    private String defaultTrainDirectory;
    private String moisJarDirecory;

    public MoisLauncherDialog(Window window, Project project, boolean modal) {

        super((Frame) window, modal);
        setTitle("Launch MOIS");
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        File configurationFile = new File(JSONFileLoader.getConfigurationFile(true));

        defaultTrainDirectory = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getPath() +  Constants.MOIS_TRAIN_DIR_NAME;
        moisJarDirecory = Objects.requireNonNull(getClass().getClassLoader().getResource(Constants.MOIS_JAR_NAME)).getPath();

        sourceDirTextbox.setText(project.getBasePath());
        configDir.setText(configurationFile.getAbsolutePath());
        outputDir.setText(configurationFile.getParent() + File.separator + Constants.OUTPUT_DIR_NAME);

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

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {

        //set path for jar directory
        parameters.put(Constants.MOIS_JAR_DIR, moisJarDirecory);

        //ensure that required fields are populated
        if (sourceDirTextbox.getText().isEmpty() || configDir.getText().isEmpty()) {

            Messages.showWarningDialog(Constants.LAUNCHER_PATH_NOT_SELECTED, "Missing Path");
        } else {
            parameters.put(Constants.MOIS_SOURCE_DIR, sourceDirTextbox.getText());
            parameters.put(Constants.MOIS_CONFIG_FILE, configDir.getText());
        }

        //Check if advanced parameters are configured
        if (advancedCheckbox.isSelected() && (trainingTextbox.getText().isEmpty() || outputDir.getText().isEmpty())) {

            Messages.showWarningDialog(Constants.LAUNCHER_PATH_NOT_SELECTED, "Missing Path");

        } else if (!advancedCheckbox.isSelected()) {
            parameters.put(Constants.MOIS_TRAIN_DIR, defaultTrainDirectory);
            parameters.put(Constants.MOIS_OUTPUT_DIR, outputDir.getText());
            dispose();
        } else {
            parameters.put(Constants.MOIS_TRAIN_DIR, trainingTextbox.getText());
            parameters.put(Constants.MOIS_OUTPUT_DIR, outputDir.getText());
            dispose();
        }

        confirm = true;
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    //Accept users file or folder selection and send return value
    private String fileSelector(int selectionMethod, String path) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(selectionMethod);
        fileChooser.setSelectedFile(new File(path));

        if (selectionMethod == JFileChooser.FILES_ONLY)
            fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile().getPath();
        else
            return path;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public boolean isConfirmed() {
        return confirm;
    }
}
