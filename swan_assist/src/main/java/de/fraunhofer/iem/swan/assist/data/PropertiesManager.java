/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.data;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import de.fraunhofer.iem.swan.assist.ui.dialog.PluginSettingsDialog;

import java.io.File;
import java.util.HashMap;

import static de.fraunhofer.iem.swan.assist.util.Constants.OUTPUT_DIRECTORY;

public class PropertiesManager {

    /**
     * Verifies existing plugin settings and prompt user to update settings if file is missing.
     * @param project project for which settings should be applied to
     * @return the project's output path
     */
    public static String setProjectOutputPath(Project project){
        //Set output directory for plugin, if not set
        if (!PropertiesComponent.getInstance(project).isValueSet(OUTPUT_DIRECTORY)) {

            showSettingsDialog(project);
        } else {
            File trainFile = new File(PropertiesComponent.getInstance(project).getValue(OUTPUT_DIRECTORY));

            if (!trainFile.exists())
                showSettingsDialog(project);
        }
        return PropertiesComponent.getInstance(project).getValue(OUTPUT_DIRECTORY);
    }

    /**
     * Displays Plugin settings dialog.
     * @param project project for which the settings should be configured
     */
    private static void showSettingsDialog(Project project) {

        //Launch SWAN Properties Dialog
        PluginSettingsDialog dialog = new PluginSettingsDialog(project, true);
        dialog.show();

        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {

            HashMap<String, String> swanParameters = dialog.getParameters();

            File outputFolder = new File(swanParameters.get(OUTPUT_DIRECTORY));

            if(!outputFolder.exists())
                outputFolder.mkdir();

            PropertiesComponent.getInstance(project).setValue(OUTPUT_DIRECTORY, swanParameters.get(OUTPUT_DIRECTORY));
        }
    }
}
