/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.actions;

import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import de.fraunhofer.iem.swan.assist.util.Constants;
import java.io.File;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Creates process to run SWAN on a separate thread.
 */
public class RunSwanAnalysisImpl {

    private static HashMap<String, String> parameters;
    private Project project;
    HashMap<String, String> results;

    /**
     * Initializes builder.
     *
     * @param project Project on which the plugin is being used with
     * @param param   Parameters that will be used as program arguments
     */
    RunSwanAnalysisImpl(Project project, HashMap<String, String> param) {

        this.project = project;
        parameters = param;
        results = new HashMap<>();
    }

    /**
     * Sets up process to run the application and also send notification to subscribers when finished.
     */
    public void run() {

        ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");

        File outputFolder = new File(parameters.get(Constants.OUTPUT_DIRECTORY));

        if (!outputFolder.exists())
            outputFolder.mkdir();

        ProgressManager.getInstance().run(new RunSwanTask(project, "Detecting SRMs", true,
                PerformInBackgroundOption.ALWAYS_BACKGROUND, parameters));
    }
}