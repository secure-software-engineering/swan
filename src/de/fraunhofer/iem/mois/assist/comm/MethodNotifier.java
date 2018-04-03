package de.fraunhofer.iem.mois.assist.comm;

import com.intellij.util.messages.Topic;
import de.fraunhofer.iem.mois.assist.data.Method;

public interface MethodNotifier {

    Topic<MethodNotifier> METHOD_ADDED_TOPIC = Topic.create("Method Added",MethodNotifier.class);
    Topic<MethodNotifier> METHOD_SELECTED_TOPIC = Topic.create("Method Selected",MethodNotifier.class);
    Topic<MethodNotifier> METHOD_UPDATED_TOPIC = Topic.create("Method Updated",MethodNotifier.class);
    Topic<MethodNotifier> METHOD_REMOVED_TOPIC = Topic.create("Method Removed",MethodNotifier.class);
    Topic<MethodNotifier> METHOD_RESTORED_TOPIC = Topic.create("Method RESTORED",MethodNotifier.class);

    //This method will be executed after the action is performed
    void afterAction(Method method);
}
