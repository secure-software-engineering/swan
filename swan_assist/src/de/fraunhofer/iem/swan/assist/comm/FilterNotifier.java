package de.fraunhofer.iem.swan.assist.comm;

import com.intellij.util.messages.Topic;
import javafx.util.Pair;

/**
 * Notification events for filters.
 * @author Oshando Johnson
 */

public interface FilterNotifier {

    Topic<FilterNotifier> FILTER_SELECTED_TOPIC = Topic.create("Filter Selected",FilterNotifier.class);

    void updateFilter(Pair<String, String> value);
}
