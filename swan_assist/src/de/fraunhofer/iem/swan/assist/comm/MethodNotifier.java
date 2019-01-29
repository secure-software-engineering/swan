package de.fraunhofer.iem.swan.assist.comm;

import com.intellij.util.messages.Topic;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;

/**
 * Notification events for methods.
 * @author Oshando Johnson
 */

public interface MethodNotifier {

    Topic<MethodNotifier> METHOD_UPDATED_ADDED_TOPIC = Topic.create("Method Updated",MethodNotifier.class);
    Topic<MethodNotifier> METHOD_REMOVED_TOPIC = Topic.create("Method Removed",MethodNotifier.class);

    //This method will be executed after the action is performed
    void afterAction(MethodWrapper method);
}
