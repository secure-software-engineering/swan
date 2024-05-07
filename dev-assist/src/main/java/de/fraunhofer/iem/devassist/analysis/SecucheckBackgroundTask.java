package de.fraunhofer.iem.devassist.analysis;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.aidevassist.sa.cli.AIDevAssistCli;
import de.fraunhofer.iem.aidevassist.sa.cli.CliRunner;
import de.fraunhofer.iem.devassist.comm.SecucheckNotifier;
import de.fraunhofer.iem.devassist.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import picocli.CommandLine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
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

        indicator.setText("Configuring SecuCheck");

        String[] args = new String[]{
                "--analysis", "0",
                Objects.requireNonNull(PropertiesComponent.getInstance(project).getValue(Constants.SOURCE_DIRECTORY)),
                "--output", Objects.requireNonNull(PropertiesComponent.getInstance(project).getValue(Constants.OUTPUT_DIRECTORY)),
                "--inclusion", "*",
                "--entry", "*"};

        CliRunner cliRunner = new CliRunner(args);
        CommandLine.ParseResult cmd = new CommandLine(cliRunner).parseArgs(args);

        indicator.setText("Running analysis");

        if (cmd.errors().isEmpty()) {
            AIDevAssistCli cli = new AIDevAssistCli();
            cli.run(cliRunner.createOptions());
            PropertiesComponent.getInstance(project).setValue(Constants.LAST_SARIF_FILE, cli.getResults().getResultFile());
        }

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