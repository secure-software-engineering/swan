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
 * Notification events for methods.
 */

public interface MethodNotifier {

    Topic<MethodNotifier> ADD_UPDATE_DELETE_METHOD = Topic.create("Method Updated", MethodNotifier.class);

    /**
     * Sends notification whenever methods are updated, added or removed.
     *
     * @param method Method that is being modified, added or deleted.
     */

    void removeMethod(MethodWrapper method);

    void restoreMethod(MethodWrapper method);

    void addNewExistingMethod(MethodWrapper method);

    void afterSuggestAction(ArrayList<MethodWrapper> methods);
}
