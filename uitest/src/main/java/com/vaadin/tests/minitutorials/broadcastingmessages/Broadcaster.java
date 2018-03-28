package com.vaadin.tests.minitutorials.broadcastingmessages;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Broadcaster {

    private static final List<BroadcastListener> listeners = new CopyOnWriteArrayList<>();

    public static void register(BroadcastListener listener) {
        listeners.add(listener);
    }

    public static void unregister(BroadcastListener listener) {
        listeners.remove(listener);
    }

    public static void broadcast(final String message) {
        for (BroadcastListener listener : listeners) {
            listener.receiveBroadcast(message);
        }
    }

    public interface BroadcastListener {
        public void receiveBroadcast(String message);
    }

}
