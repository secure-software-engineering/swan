package de.fraunhofer.iem.mois.assist.ui;

import com.intellij.openapi.ui.Messages;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.util.Constants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;


public class SusiLauncherDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField sourceDir;
    private JButton sourceBtn;
    private JTextField configDir;
    private JButton configBtn;
    private JTextField outputDir;
    private JButton outputBtn;

    private boolean confirm = false;
    private HashMap<String, String> parameters = new HashMap<String, String>();

    public SusiLauncherDialog(Window window, boolean modal) {

        super((Frame) window, modal);
        setTitle("Launch Susi");
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        String configurationFile = JSONFileLoader.getConfigurationFile(true);

        //TODO remove hard coded values
        configDir.setText(configurationFile);
        sourceDir.setText("/Users/oshando/IdeaProjects/iem-attract/02_code/SuSi4Attract/target-spring");
        outputDir.setText("/Users/oshando/IdeaProjects/iem-attract/02_code/susi/SuSi/output");

        /*
         * Action Listeners for buttons
         */

        sourceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                sourceDir.setText(fileSelector(JFileChooser.DIRECTORIES_ONLY));
            }
        });

        configBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                configDir.setText(fileSelector(JFileChooser.FILES_ONLY));
            }
        });

        outputBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                outputDir.setText(fileSelector(JFileChooser.DIRECTORIES_ONLY));
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

        dispose();

        parameters.put(Constants.SUSI_SOURCE_DIR, sourceDir.getText());
        parameters.put(Constants.SUSI_TRAIN_DIR, Constants.SUSI_TRAIN_DIR_PATH);
        parameters.put(Constants.SUSI_JAR_DIR, Constants.SUSI_JAR_PATH);
        parameters.put(Constants.SUSI_CONFIG_FILE, configDir.getText());
        parameters.put(Constants.SUSI_OUTPUT_DIR, outputDir.getText());

        confirm = true;
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    //Accept users file or folder selection and send return value
    private String fileSelector(int selectionMethod) {

        String filePath = "";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(selectionMethod);
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fileChooser.getCurrentDirectory();
            filePath = selectedFile.getAbsoluteFile().toString();

        } else
            Messages.showMessageDialog(this.getParent(), Constants.FILE_NOT_SELECTED, "File Selection", Messages.getInformationIcon());

        return filePath;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public boolean isConfirmed() {
        return confirm;
    }
}
