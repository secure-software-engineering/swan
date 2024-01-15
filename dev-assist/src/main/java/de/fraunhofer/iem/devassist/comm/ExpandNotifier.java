package de.fraunhofer.iem.devassist.comm;

import com.intellij.util.messages.Topic;

/**
 * @author Oshando Johnson on 2020-01-07
 */
public interface ExpandNotifier {

    Topic<ExpandNotifier> EXPAND_COLLAPSE_LIST = Topic.create("Expand list", ExpandNotifier.class);

    void expandTree(boolean expand);
}
