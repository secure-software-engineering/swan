package de.fraunhofer.iem.mois.assist.comm;

import com.intellij.util.messages.Topic;

/**
 * Notifier for file operation events.
 * @author Oshando Johnson
 */
public interface FileSelectedNotifier {

    Topic<FileSelectedNotifier> INITIAL_FILE_NOTIFIER_TOPIC = Topic.create("Configuration file selected",FileSelectedNotifier.class);
    Topic<FileSelectedNotifier> UPDATED_FILE_NOTIFIER_TOPIC = Topic.create("Updated configuration file selected",FileSelectedNotifier.class);

    //This method will be executed after a file as been selected
    void notifyFileChange(String fileName);

}
