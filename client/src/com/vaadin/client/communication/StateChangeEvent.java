/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.client.communication;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;

public class StateChangeEvent extends
        AbstractServerConnectorEvent<StateChangeHandler> {
    /**
     * Type of this event, used by the event bus.
     */
    public static final Type<StateChangeHandler> TYPE = new Type<StateChangeHandler>();

    private Set<String> changedProperties;

    @Override
    public Type<StateChangeHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Creates a new state change event.
     * 
     * @param connector
     *            the event whose state has changed
     * @param changedProperties
     *            a set of names of the changed properties
     */
    public StateChangeEvent(ServerConnector connector,
            Set<String> changedProperties) {
        setConnector(connector);
        this.changedProperties = changedProperties;
    }

    @Override
    public void dispatch(StateChangeHandler listener) {
        listener.onStateChanged(this);
    }

    /**
     * Event handler that gets notified whenever any part of the state has been
     * updated by the server.
     * 
     * @author Vaadin Ltd
     * @version @VERSION@
     * @since 7.0.0
     */
    public interface StateChangeHandler extends Serializable, EventHandler {
        /**
         * Notifies the event handler that the state has changed.
         * 
         * @param stateChangeEvent
         *            the state change event with details about the change
         */
        public void onStateChanged(StateChangeEvent stateChangeEvent);
    }

    /**
     * Gets the properties that have changed.
     * 
     * @return a set of names of the changed properties
     */
    public Set<String> getChangedProperties() {
        return Collections.unmodifiableSet(changedProperties);
    }
}
