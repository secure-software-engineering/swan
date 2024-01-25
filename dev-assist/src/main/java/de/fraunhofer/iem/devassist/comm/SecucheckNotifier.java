/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.devassist.comm;

import com.intellij.util.messages.Topic;

/**
 * Provides notifications about SWAN.
 */
public interface SecucheckNotifier {

    Topic<SecucheckNotifier> START_SECUCHECK_PROCESS_TOPIC = Topic.create("Start SecuCheck", SecucheckNotifier.class);
    Topic<SecucheckNotifier> END_SECUCHECK_PROCESS_TOPIC = Topic.create("Stop SecuCheck", SecucheckNotifier.class);

    /**
     * Provides settings used to start SWAN or to access program logs/reports.
     */
    void launchSecuCheck();
}
