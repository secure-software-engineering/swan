/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.comm;

import com.intellij.util.messages.Topic;

import java.util.HashMap;

/**
 * Provides notifications about SWAN.
 */
public interface SwanNotifier {

    Topic<SwanNotifier> START_SWAN_PROCESS_TOPIC = Topic.create("Start Swan",SwanNotifier.class);
    Topic<SwanNotifier> END_SWAN_PROCESS_TOPIC = Topic.create("Stop Swan",SwanNotifier.class);

    /**
     * Provides settings used to start SWAN or to access program logs/reports.
     * @param values Program arguments or results.
     */
    void launchSwan(HashMap<String,String> values);
}
