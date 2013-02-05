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
package com.vaadin.client.communication;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.vaadin.client.FastStringSet;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;

public class StateChangeEvent extends
        AbstractServerConnectorEvent<StateChangeHandler> {
    /**
     * Type of this event, used by the event bus.
     */
    public static final Type<StateChangeHandler> TYPE = new Type<StateChangeHandler>();

    private final FastStringSet changedProperties;

    /**
     * Used to cache a Set representation of the changedProperties if one is
     * needed.
     */
    private Set<String> changedPropertiesSet;

    @Override
    public Type<StateChangeHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Creates a new state change event.
     * 
     * @param connector
     *            the event whose state has changed
     * @param changedPropertiesSet
     *            a set of names of the changed properties
     * @deprecated As of 7.0.1, use
     *             {@link #StateChangeEvent(ServerConnector, FastStringSet)}
     *             instead for improved performance.
     */
    @Deprecated
    public StateChangeEvent(ServerConnector connector,
            Set<String> changedPropertiesSet) {
        setConnector(connector);
        this.changedPropertiesSet = changedPropertiesSet;
        changedProperties = FastStringSet.create();
        for (String property : changedPropertiesSet) {
            changedProperties.add(property);
        }
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
            FastStringSet changedProperties) {
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
     * 
     * @deprecated As of 7.0.1, use {@link #getChangedPropertiesFastSet()} or
     *             {@link #hasPropertyChanged(String)} instead for improved
     *             performance.
     */
    @Deprecated
    public Set<String> getChangedProperties() {
        if (changedPropertiesSet == null) {
            changedPropertiesSet = new HashSet<String>();
            changedProperties.addAllTo(changedPropertiesSet);
        }
        return changedPropertiesSet;
    }

    /**
     * Gets the properties that have changed.
     * 
     * @return a set of names of the changed properties
     * 
     */
    public FastStringSet getChangedPropertiesFastSet() {
        return changedProperties;
    }

    /**
     * Checks whether the give property has changed.
     * 
     * @param property
     *            the name of the property to check
     * @return <code>true</code> if the property has changed, else
     *         <code>false></code>
     */
    public boolean hasPropertyChanged(String property) {
        return changedProperties.contains(property);
    }
}
