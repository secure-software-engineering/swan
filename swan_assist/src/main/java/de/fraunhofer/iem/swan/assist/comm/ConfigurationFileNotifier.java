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
public interface ConfigurationFileNotifier {

    Topic<ConfigurationFileNotifier> FILE_NOTIFIER_TOPIC = Topic.create("Configuration file selected", ConfigurationFileNotifier.class);

    /**
     * This method will be executed after a file as been selected
     * @param fileName Name of configuration file
     */
    void loadUpdatedFile(String fileName);

    void loadInitialFile(String fileName);

}
