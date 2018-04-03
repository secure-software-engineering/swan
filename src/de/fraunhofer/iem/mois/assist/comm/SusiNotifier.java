package de.fraunhofer.iem.mois.assist.comm;

import com.intellij.util.messages.Topic;

import java.util.HashMap;

public interface SusiNotifier {

    Topic<SusiNotifier> START_SUSI_PROCESS_TOPIC = Topic.create("Start Susi",SusiNotifier.class);
    Topic<SusiNotifier> END_SUSI_PROCESS_TOPIC = Topic.create("Start Susi",SusiNotifier.class);

    void launchSusi(HashMap<String,String> values);
}
