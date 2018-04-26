package de.fraunhofer.iem.mois.assist.comm;

import com.intellij.util.messages.Topic;

import java.util.HashMap;

public interface MoisNotifier {

    Topic<MoisNotifier> START_MOIS_PROCESS_TOPIC = Topic.create("Start Mois",MoisNotifier.class);
    Topic<MoisNotifier> END_MOIS_PROCESS_TOPIC = Topic.create("Start Mois",MoisNotifier.class);

    void launchMois(HashMap<String,String> values);
}
