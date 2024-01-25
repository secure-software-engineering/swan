package de.fraunhofer.iem.devassist.comm;

import com.intellij.util.messages.Topic;
import de.fraunhofer.iem.devassist.data.MethodWrapper;

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
