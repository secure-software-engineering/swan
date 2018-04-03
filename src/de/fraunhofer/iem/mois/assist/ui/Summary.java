package de.fraunhofer.iem.mois.assist.ui;

import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Summary extends JBPanel {
    private JButton loadButton;
    private JButton refreshButton;
    private JList list1;
    private JComboBox comboBox1;
    private JButton addButton;
    private JTextArea textArea1;
    private JPanel toolPanel;
    private JTree tree1;
    private JButton button1;
    private JProgressBar progressBar1;

    public Summary() {

        setLayout(new BorderLayout());
        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });

        add(list1,BorderLayout.CENTER);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
