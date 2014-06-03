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
package com.vaadin.server.communication;

import java.lang.reflect.Type;

import com.vaadin.ui.ConnectorTracker;

/**
 * Implementors of this interface knows how to serialize an Object of a given
 * type to JSON and how to deserialize the JSON back into an object.
 * <p>
 * The {@link #serialize(Object, ConnectorTracker)} and
 * {@link #deserialize(Type, Object, ConnectorTracker)} methods must be
 * symmetric so they can be chained and produce the original result (or an equal
 * result).
 * <p>
 * Each {@link JSONSerializer} implementation can handle an object of a single
 * type.
 * <p>
 * This is the server side interface, see
 * com.vaadin.client.communication.JSONSerializer for the client side interface.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface JSONSerializer<T> {
    /**
     * Creates and deserializes an object received from the client. Must be
     * compatible with {@link #serialize(Object, ConnectorTracker)} and also
     * with the client side com.vaadin.client.communication.JSONSerializer.
     * <p>
     * The json parameter is of type Object as org.json JSON classes have no
     * other common super class
     * 
     * @param type
     *            The expected return type
     * @param jsonValue
     *            the value from the JSON
     * @param connectorTracker
     *            the connector tracker instance for the UI
     * @return A deserialized object
     */
    T deserialize(Type type, Object jsonValue, ConnectorTracker connectorTracker);

    /**
     * Serialize the given object into JSON. Must be compatible with
     * {@link #deserialize(Object, connectorTracker)} and the client side
     * com.vaadin.client.communication.JSONSerializer
     * 
     * @param value
     *            The object to serialize
     * @param connectorTracker
     *            The connector tracker instance for the UI
     * @return A JSON serialized version of the object
     */
    Object serialize(T value, ConnectorTracker connectorTracker);

}
