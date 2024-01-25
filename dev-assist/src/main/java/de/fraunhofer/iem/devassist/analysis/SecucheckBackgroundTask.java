package de.fraunhofer.iem.devassist.analysis;

import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.devassist.comm.SecucheckNotifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SecucheckBackgroundTask extends Task.Backgroundable {

    private Project project;
    private HashMap<String, String> parameters;
    private long duration;

    public SecucheckBackgroundTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title, boolean canBeCancelled, @Nullable PerformInBackgroundOption backgroundOption) {
        super(project, title, canBeCancelled, backgroundOption);
        this.project = project;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {

        long start = System.currentTimeMillis();

        //TODO add implementation to run SecuCheck
        indicator.setText("Generating fluentTQL Specifications");

        indicator.setText("Configuring SecuCheck");

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        indicator.setText("Configuring analysis");

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        indicator.setText("Exporting analysis results");

        MessageBus messageBus = project.getMessageBus();
        SecucheckNotifier publisher = messageBus.syncPublisher(SecucheckNotifier.END_SECUCHECK_PROCESS_TOPIC);
        publisher.launchSecuCheck();
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