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
package com.vaadin.client.communication;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventHandler;
import com.vaadin.client.FastStringSet;
import com.vaadin.client.JsArrayObject;
import com.vaadin.client.Profiler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Property;
import com.vaadin.client.ui.AbstractConnector;

import elemental.json.JsonObject;

public class StateChangeEvent extends
        AbstractServerConnectorEvent<StateChangeHandler> {
    /**
     * Type of this event, used by the event bus.
     */
    public static final Type<StateChangeHandler> TYPE = new Type<StateChangeHandler>();

    /**
     * Used to cache a FastStringSet representation of the properties that have
     * changed if one is needed.
     */
    @Deprecated
    private FastStringSet changedProperties;

    /**
     * Used to cache a Set representation of the changedProperties if one is
     * needed.
     */
    @Deprecated
    private Set<String> changedPropertiesSet;

    private boolean initialStateChange = false;

    private JsonObject stateJson;

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
     *             {@link #StateChangeEvent(ServerConnector, JsonObject, boolean)}
     *             instead for improved performance.
     */
    @Deprecated
    public StateChangeEvent(ServerConnector connector,
            Set<String> changedPropertiesSet) {
        setConnector(connector);
        // Keep instance around for caching
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
     * @deprecated As of 7.0.2, use
     *             {@link #StateChangeEvent(ServerConnector, JsonObject, boolean)}
     *             instead for improved performance.
     */
    @Deprecated
    public StateChangeEvent(ServerConnector connector,
            FastStringSet changedProperties) {
        setConnector(connector);
        this.changedProperties = changedProperties;
    }

    /**
     * /** Creates a new state change event.
     * 
     * @param connector
     *            the event whose state has changed
     * @param stateJson
     *            the JSON representation of the state change
     * @param initialStateChange
     *            <code>true</code> if the state change is for a new connector,
     *            otherwise <code>false</code>
     */
    public StateChangeEvent(ServerConnector connector, JsonObject stateJson,
            boolean initialStateChange) {
        setConnector(connector);
        this.stateJson = stateJson;
        this.initialStateChange = initialStateChange;
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
     * @deprecated As of 7.0.1, use {@link #hasPropertyChanged(String)} instead
     *             for improved performance.
     */
    @Deprecated
    public Set<String> getChangedProperties() {
        if (changedPropertiesSet == null) {
            Profiler.enter("StateChangeEvent.getChangedProperties populate");
            changedPropertiesSet = new HashSet<String>();
            getChangedPropertiesFastSet().addAllTo(changedPropertiesSet);
            Profiler.leave("StateChangeEvent.getChangedProperties populate");
        }
        return changedPropertiesSet;
    }

    /**
     * Gets the properties that have changed.
     * 
     * @return a set of names of the changed properties
     * 
     * @deprecated As of 7.0.1, use {@link #hasPropertyChanged(String)} instead
     *             for improved performance.
     */
    @Deprecated
    public FastStringSet getChangedPropertiesFastSet() {
        if (changedProperties == null) {
            Profiler.enter("StateChangeEvent.getChangedPropertiesFastSet populate");
            changedProperties = FastStringSet.create();

            addJsonFields(stateJson, changedProperties, "");
            if (isInitialStateChange()) {
                addAllStateFields(
                        AbstractConnector.getStateType(getConnector()),
                        changedProperties, "");
            }

            Profiler.leave("StateChangeEvent.getChangedPropertiesFastSet populate");
        }
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
        if (isInitialStateChange()) {
            // Everything has changed for a new connector
            return true;
        } else if (stateJson != null) {
            // Check whether it's in the json object
            return isInJson(property, Util.json2jso(stateJson));
        } else {
            // Legacy cases
            if (changedProperties != null) {
                // Check legacy stuff
                return changedProperties.contains(property);
            } else if (changedPropertiesSet != null) {
                // Check legacy stuff
                return changedPropertiesSet.contains(property);
            } else {
                throw new IllegalStateException(
                        "StateChangeEvent should have either stateJson, changedProperties or changePropertiesSet");
            }
        }
    }

    /**
     * Checks whether the given property name (which might contains dots) is
     * defined in some JavaScript object.
     * 
     * @param property
     *            the name of the property, might include dots to reference
     *            inner objects
     * @param target
     *            the JavaScript object to check
     * @return true if the property is defined
     */
    private static native final boolean isInJson(String property,
            JavaScriptObject target)
    /*-{
        var segments = property.split('.');
        while (typeof target == 'object') {
            var nextSegment = segments.shift();
            if (!(nextSegment in target)) {
                // Abort if segment is not found
                return false;
            } else if (segments.length == 0) {
                // Done if there are no more segments
                return true;
            } else {
                // Else just go deeper
                target = target[nextSegment];
            }
        }
        // Not defined if we reach something that isn't an object 
        return false;
    }-*/;

    /**
     * Recursively adds the names of all properties in the provided state type.
     * 
     * @param type
     *            the type to process
     * @param changedProperties
     *            a set of all currently added properties
     * @param context
     *            the base name of the current object
     */
    @Deprecated
    private static void addAllStateFields(com.vaadin.client.metadata.Type type,
            FastStringSet changedProperties, String context) {
        try {
            JsArrayObject<Property> properties = type.getPropertiesAsArray();
            int size = properties.size();
            for (int i = 0; i < size; i++) {
                Property property = properties.get(i);
                String propertyName = context + property.getName();
                changedProperties.add(propertyName);

                com.vaadin.client.metadata.Type propertyType = property
                        .getType();
                if (propertyType.hasProperties()) {
                    addAllStateFields(propertyType, changedProperties,
                            propertyName + ".");
                }
            }
        } catch (NoDataException e) {
            throw new IllegalStateException("No property info for " + type
                    + ". Did you remember to compile the right widgetset?", e);
        }
    }

    /**
     * Recursively adds the names of all fields in all objects in the provided
     * json object.
     * 
     * @param json
     *            the json object to process
     * @param changedProperties
     *            a set of all currently added fields
     * @param context
     *            the base name of the current object
     */
    @Deprecated
    private static void addJsonFields(JsonObject json,
            FastStringSet changedProperties, String context) {
        for (String key : json.keys()) {
            String fieldName = context + key;
            changedProperties.add(fieldName);

            JsonObject object = json.get(key);
            if (object != null) {
                addJsonFields(object, changedProperties, fieldName + ".");
            }
        }
    }

    /**
     * Checks if the state change event is the first one for the given
     * connector.
     * 
     * @since 7.1
     * @return true if this is the first state change event for the connector,
     *         false otherwise
     */
    public boolean isInitialStateChange() {
        return initialStateChange;
    }

}
