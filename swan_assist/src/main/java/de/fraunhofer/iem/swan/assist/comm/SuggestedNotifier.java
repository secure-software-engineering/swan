/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.comm;

import com.intellij.util.messages.Topic;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;

import java.util.ArrayList;

/**
 * Notifies subscribers of the methods that were suggested by SWAN
 */
public interface SuggestedNotifier {

    Topic<SuggestedNotifier> METHOD_SUGGESTED_TOPIC = Topic.create("Method Suggested",SuggestedNotifier.class);

    /**
     * Sends suggested methods as an array list.
     * @param methods methods suggested by SWAN.
     */
    void afterAction(ArrayList<MethodWrapper> methods);
}
