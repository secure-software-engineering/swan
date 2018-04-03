package de.fraunhofer.iem.mois.assist.ui;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.FileSelectedNotifier;
import de.fraunhofer.iem.mois.assist.util.Constants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class SusiResultsDialog extends JDialog {

    private Project project;
    private JPanel contentPane;
    private JButton buttonLoad;
    private JButton buttonCancel;
    private JTextField filePath;
    private JButton selectFile;
    private JTextField logPath;
    private JTextArea logText;
    private JLabel outputMessage;

    SusiResultsDialog(Project project, HashMap<String, String> values) {

        setContentPane(contentPane);
        setModal(true);
        setTitle("Susi Results");
        getRootPane().setDefaultButton(buttonLoad);

        this.project = project;

        filePath.setText(values.get(Constants.SUSI_OUTPUT_FILE));
        logPath.setText(values.get(Constants.SUSI_OUTPUT_LOG));
        outputMessage.setText(values.get(Constants.SUSI_OUTPUT_MESSAGE));

        File logs = new File(values.get(Constants.SUSI_OUTPUT_LOG));

        try {
            logText.setText(new String(Files.readAllBytes(Paths.get(logPath.getText()))));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                    logPath.setText(path.replace(Constants.OUTPUT_JSON_SUFFIX, Constants.SUSI_LOG_SUFFIX));

                    try {
                        logText.setText(new String(Files.readAllBytes(Paths.get(logPath.getText()))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        buttonLoad.addActionListener(new ActionListener() {
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

        MessageBus messageBus = project.getMessageBus();
        FileSelectedNotifier publisher = messageBus.syncPublisher(FileSelectedNotifier.UPDATED_FILE_NOTIFIER_TOPIC);
        publisher.notifyFileChange(filePath.getText());

        dispose();
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
