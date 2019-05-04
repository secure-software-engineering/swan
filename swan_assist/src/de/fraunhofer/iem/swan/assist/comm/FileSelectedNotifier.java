/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.comm;

import com.intellij.util.messages.Topic;

/**
 * Notifier for file operation events.
 */
public interface FileSelectedNotifier {

    Topic<FileSelectedNotifier> INITIAL_FILE_NOTIFIER_TOPIC = Topic.create("Configuration file selected",FileSelectedNotifier.class);
    Topic<FileSelectedNotifier> UPDATED_FILE_NOTIFIER_TOPIC = Topic.create("Updated configuration file selected",FileSelectedNotifier.class);

    /**
     * This method will be executed after a file as been selected
     * @param fileName Name of configuration file
     */
    void notifyFileChange(String fileName);

}
