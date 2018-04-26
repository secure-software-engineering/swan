package de.fraunhofer.iem.mois.assist.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.fraunhofer.iem.mois.assist.data.JSONFileLoader;
import de.fraunhofer.iem.mois.assist.data.JSONWriter;
import de.fraunhofer.iem.mois.assist.ui.SummaryToolWindow;
import de.fraunhofer.iem.mois.assist.util.Constants;
import icons.PluginIcons;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

public class ExportAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final String FILE_EXTENSION = ".json";
        String filePath = "";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setSelectedFile(new File("projectmethods.json"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        int returnValue = fileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath();

            if(!filePath.endsWith(FILE_EXTENSION)){
                filePath+= FILE_EXTENSION;
            }

            JSONWriter exportFile = new JSONWriter();

            try {
                exportFile.writeToJsonFile(JSONFileLoader.getMethods(),filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(AnActionEvent event) {

        //Disable/Enable action button
        if (SummaryToolWindow.FILE_SELECTED)
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}
