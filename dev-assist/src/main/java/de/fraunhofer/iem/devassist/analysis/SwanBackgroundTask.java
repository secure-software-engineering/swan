package de.fraunhofer.iem.devassist.analysis;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.devassist.comm.SwanNotifier;
import de.fraunhofer.iem.devassist.util.Constants;
import de.fraunhofer.iem.swan.cli.CliRunner;
import de.fraunhofer.iem.swan.cli.SwanCli;
import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.io.dataset.SrmListUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SwanBackgroundTask extends Task.Backgroundable {

    private Project project;
    private HashMap<String, String> parameters;
    private long duration;

    public SwanBackgroundTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title, boolean canBeCancelled, @Nullable PerformInBackgroundOption backgroundOption) {
        super(project, title, canBeCancelled, backgroundOption);
        this.project = project;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {

        long start = System.currentTimeMillis();

        indicator.setText("Configuring SWAN");
        SwanOptions options = new CliRunner().initializeOptions();

        options.setTestDataDir(Objects.requireNonNull(PropertiesComponent.getInstance(project).getValue(Constants.SOURCE_DIRECTORY)));
        options.setOutputDir(Objects.requireNonNull(PropertiesComponent.getInstance(project).getValue(Constants.OUTPUT_DIRECTORY)));
        options.setToolkit(Objects.requireNonNull(PropertiesComponent.getInstance(project).getValue(Constants.TOOLKIT)).toLowerCase());
        options.setSrmClasses(List.of("all"));
        options.setPhase("predict");
        options.setTrainDataDir("");
        options.setAddKnownSrms(true);


        indicator.setText("Running SWAN");
        SwanCli swan = new SwanCli();

        try {
            swan.run(options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        duration = System.currentTimeMillis() - start;
        int m = (int) (((duration / 1000) / 60) % 60);
        int s = (int) ((duration / 1000) % 60);

        indicator.setText("Exporting SRMs");
        String filename = Objects.requireNonNull(PropertiesComponent.getInstance(project).getValue(Constants.OUTPUT_DIRECTORY)) + File.separator + "srm-" + getCurrentTimestamp() + ".json";
        SrmList srmList = swan.getSwanPipeline().getModelEvaluator().getPredictedSrmList();

        try {

            SrmListUtils.exportFile(srmList, filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, String> results = new HashMap<>();

        PropertiesComponent.getInstance(project).setValue(Constants.LAST_SRM_LIST, filename);
        results.put(Constants.OUTPUT_FILE, filename);
        results.put(Constants.OUTPUT_LOG, "");
        results.put(Constants.ANALYSIS_RESULT, srmList.getMethods().size() + " methods found in " + m + " mins " + s + " secs");

        MessageBus messageBus = project.getMessageBus();
        SwanNotifier publisher = messageBus.syncPublisher(SwanNotifier.END_SWAN_PROCESS_TOPIC);
        publisher.launchSwan(results);
    }

    /**
     * Get the timestamp in a specified format.
     *
     * @return Formatted date
     */
    private String getCurrentTimestamp() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");
        return LocalDateTime.now().format(formatter);
    }

}
