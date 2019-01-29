package de.fraunhofer.iem.swan.assist.comm;

import com.intellij.util.messages.Topic;

import java.util.HashMap;

/**
 * Notification events for SWAN.
 * @author Oshando Johnson
 */

public interface SwanNotifier {

    Topic<SwanNotifier> START_SWAN_PROCESS_TOPIC = Topic.create("Start Swan",SwanNotifier.class);
    Topic<SwanNotifier> END_SWAN_PROCESS_TOPIC = Topic.create("Start Swan",SwanNotifier.class);

    void launchSwan(HashMap<String,String> values);
}
