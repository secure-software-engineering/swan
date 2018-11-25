package de.fraunhofer.iem.mois.assist.comm;

import com.intellij.util.messages.Topic;

/**
 * Notification events for filters.
 * @author Oshando Johnson
 */

public interface FilterNotifier {

    Topic<FilterNotifier> FILTER_SELECTED_TOPIC = Topic.create("Filter Selected",FilterNotifier.class);

    void updateFilter(String value);
}
