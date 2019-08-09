package de.fraunhofer.iem.swan.assist.comm;

import com.intellij.util.messages.Topic;
import de.fraunhofer.iem.swan.assist.data.MethodWrapper;
import de.fraunhofer.iem.swan.data.Method;

import java.util.HashMap;
import java.util.Set;

/**
 * Sends notification when suggested methods are computed.
 */
public interface SuggestNotifier {

    Topic<SuggestNotifier> SUGGEST_METHOD_TOPIC = Topic.create("Start suggest",SuggestNotifier.class);

    /**
     * Sends notification that suggest method process started.
     */
    void startSuggestMethod();

    /**
     * After the suggest method process ends, the suggested methods are passed.
     * @param values Program arguments or results.
     */
    void endSuggestMethod(Set<MethodWrapper> values);

}
