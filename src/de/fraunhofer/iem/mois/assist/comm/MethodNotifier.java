package de.fraunhofer.iem.mois.assist.comm;

import com.intellij.util.messages.Topic;
import de.fraunhofer.iem.mois.assist.data.Method;

public interface MethodNotifier {

    Topic<MethodNotifier> METHOD_UPDATED_ADDED_TOPIC = Topic.create("Method Updated",MethodNotifier.class);
    Topic<MethodNotifier> METHOD_REMOVED_TOPIC = Topic.create("Method Removed",MethodNotifier.class);

    //This method will be executed after the action is performed
    void afterAction(Method method);
}
