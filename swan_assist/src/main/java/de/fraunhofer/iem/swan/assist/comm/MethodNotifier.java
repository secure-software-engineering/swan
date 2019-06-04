/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.comm;

import com.intellij.util.messages.Topic;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;

/**
 * Notification events for methods.
 */

public interface MethodNotifier {

    Topic<MethodNotifier> METHOD_UPDATED_ADDED_TOPIC = Topic.create("Method Updated",MethodNotifier.class);
    Topic<MethodNotifier> METHOD_REMOVED_TOPIC = Topic.create("Method Removed",MethodNotifier.class);

    /**
     * Sends notification whenever methods are updated, added or removed.
     * @param method Method that is being modified, added or deleted.
     */
    void afterAction(MethodWrapper method);
}
