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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.metadata.Type;

/**
 * Implementors of this interface knows how to serialize an Object of a given
 * type to JSON and how to deserialize the JSON back into an object.
 * 
 * The {@link #serialize(Object, ConnectorMap)} and
 * {@link #deserialize(JSONObject, ConnectorMap)} methods must be symmetric so
 * they can be chained and produce the original result (or an equal result).
 * 
 * Each {@link JSONSerializer} implementation can handle an object of a single
 * type - see {@link SerializerMap}.
 * 
 * @since 7.0
 */
public interface JSONSerializer<T> {

    /**
     * Creates and deserializes an object received from the server. Must be
     * compatible with {@link #serialize(Object, ConnectorMap)} and also with
     * the server side JsonCodec.encode(Object,
     * com.vaadin.terminal.gwt.server.PaintableIdMapper) .
     * 
     * @param jsonValue
     *            JSON map from property name to property value
     * @return A deserialized object
     */
    T deserialize(Type type, JSONValue jsonValue,
            ApplicationConnection connection);

    /**
     * Serialize the given object into JSON. Must be compatible with
     * {@link #deserialize(JSONObject, ConnectorMap)} and also with the server
     * side JsonCodec.decode(com.vaadin.external.json.JSONArray,
     * com.vaadin.terminal.gwt.server.PaintableIdMapper)
     * 
     * @param value
     *            The object to serialize
     * @return A JSON serialized version of the object
     */
    JSONValue serialize(T value, ApplicationConnection connection);

}
