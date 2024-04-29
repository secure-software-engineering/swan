package de.fraunhofer.iem.devassist.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.devassist.ui.dialog.SettingsDialog;
import de.fraunhofer.iem.devassist.util.Constants;
import de.fraunhofer.iem.swan.data.Method;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Action opens dialog for user to set parameters for running SWAN. After which thread is created to run SWAN.
 */
public class SettingsAction extends AnAction {

    protected Set<Method> methods = new HashSet<Method>();

    /**
     * Obtains application parameters from user, exports updated JSON file and starts thread to run SWAN.
     *
     * @param anActionEvent source event
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();

        Properties config = new Properties();
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

        //Launch Dialog
        SettingsDialog dialog = new SettingsDialog(project, true);
        dialog.show();

        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {

            PropertiesComponent.getInstance(project).setValue(Constants.SWAN_SETTINGS, true);

            HashMap<String, String> settings = dialog.getParameters();

            for (String property : settings.keySet())
                PropertiesComponent.getInstance(project).setValue(property, settings.get(property));
        }
    }

    /**
     * Controls whether the action is enabled or disabled
     *
     * @param event source  event
     */
    @Override
    public void update(AnActionEvent event) {

    }
}