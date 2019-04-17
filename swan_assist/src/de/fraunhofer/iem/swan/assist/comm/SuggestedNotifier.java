package de.fraunhofer.iem.swan.assist.comm;

import com.intellij.util.messages.Topic;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;

import java.util.ArrayList;

/**
 * @author Oshando Johnson on 2019-04-16
 */
public interface SuggestedNotifier {

    Topic<SuggestedNotifier> METHOD_SUGGESTED_TOPIC = Topic.create("Method Suggested",SuggestedNotifier.class);

    //This method will be executed after the action is performed
    void afterAction(ArrayList<MethodWrapper> methods);
}
