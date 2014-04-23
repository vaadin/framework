/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.event;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.util.ReflectTools;

/**
 * A class that contains events, listeners and handlers specific to the
 * {@link UI} class.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface UIEvents {

    /**
     * A {@link PollListener} receives and handles {@link PollEvent PollEvents}
     * fired by {@link PollNotifier PollNotifiers}.
     * 
     * @since 7.2
     * @author Vaadin Ltd
     */
    public interface PollListener extends Serializable {
        public static final Method POLL_METHOD = ReflectTools.findMethod(
                PollListener.class, "poll", PollEvent.class);

        /**
         * A poll request has been received by the server.
         * 
         * @param event
         *            poll event
         */
        public void poll(PollEvent event);
    }

    /**
     * An event that is fired whenever a client polls the server for
     * asynchronous UI updates.
     * 
     * @since 7.2
     * @author Vaadin Ltd
     */
    public static class PollEvent extends Component.Event {
        public PollEvent(UI ui) {
            super(ui);
        }

        /**
         * Get the {@link UI} instance that received the poll request.
         * 
         * @return the {@link UI} that received the poll request. Never
         *         <code>null</code>.
         */
        public UI getUI() {
            /*
             * This cast is safe to make, since this class' constructor
             * constrains the source to be a UI instance.
             */
            return (UI) getComponent();
        }
    }

    /**
     * The interface for adding and removing {@link PollEvent} listeners.
     * <p>
     * By implementing this interface, a class publicly announces that it is
     * able to send {@link PollEvent PollEvents} whenever the client sends a
     * periodic poll message to the client, to check for asynchronous
     * server-side modifications.
     * 
     * @since 7.2
     * @see UI#setPollInterval(int)
     */
    public interface PollNotifier extends Serializable {
        /**
         * Add a poll listener.
         * <p>
         * The listener is called whenever the client polls the server for
         * asynchronous UI updates.
         * 
         * @see UI#setPollInterval(int)
         * @see #removePollListener(PollListener)
         * @param listener
         *            the {@link PollListener} to add
         */
        public void addPollListener(PollListener listener);

        /**
         * Remove a poll listener.
         * 
         * @see #addPollListener(PollListener)
         * @param listener
         *            the listener to be removed
         */
        public void removePollListener(PollListener listener);
    }

}
