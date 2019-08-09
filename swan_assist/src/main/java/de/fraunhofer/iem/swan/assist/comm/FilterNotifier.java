/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 *
 * Contributors:
 * Oshando Johnson (oshando.johnson@iem.fraunhofer.de ) - initial implementation
 ******************************************************************************/

package de.fraunhofer.iem.swan.assist.comm;

import com.intellij.util.messages.Topic;
import javafx.util.Pair;

/**
 * Notification events for filters.
 */

public interface FilterNotifier {

    Topic<FilterNotifier> FILTER_SELECTED_TOPIC = Topic.create("Filter Selected",FilterNotifier.class);

    /**
     * Sends the filter that was selected to the subscribers
     * @param value Filter that was selected
     */
    void updateFilter(Pair<String, String> value);
}
