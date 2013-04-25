/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests.minitutorials.broadcastingmessages;

import java.util.ArrayList;
import java.util.List;

public class Broadcaster {

    private static List<BroadcastListener> listeners = new ArrayList<BroadcastListener>();

    public synchronized static void register(BroadcastListener listener) {
        listeners.add(listener);
    }

    public synchronized static void unregister(BroadcastListener listener) {
        listeners.remove(listener);
    }

    private synchronized static List<BroadcastListener> getListeners() {
        List<BroadcastListener> listenerCopy = new ArrayList<BroadcastListener>();
        listenerCopy.addAll(listeners);
        return listenerCopy;
    }

    public static void broadcast(final String message) {
        // Make a copy of the listener list while synchronized, can't be
        // synchronized while firing the event or we would have to fire each
        // event in a separate thread.
        final List<BroadcastListener> listenerCopy = getListeners();

        // We spawn another thread to avoid potential deadlocks with
        // multiple UIs locked simultaneously
        Thread eventThread = new Thread() {
            @Override
            public void run() {
                for (BroadcastListener listener : listenerCopy) {
                    listener.receiveBroadcast(message);
                }
            }
        };
        eventThread.start();
    }

    public interface BroadcastListener {
        public void receiveBroadcast(String message);
    }

}
