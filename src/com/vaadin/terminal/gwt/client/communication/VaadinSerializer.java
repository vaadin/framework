/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import com.google.gwt.json.client.JSONObject;
import com.vaadin.terminal.gwt.client.ConnectorMap;

/**
 * Serializer that can deserialize custom objects received from the server.
 * 
 * Each serializer can handle objects of a single type - see
 * {@link SerializerMap}.
 * 
 * @since 7.0
 */
public interface VaadinSerializer {

    /**
     * Creates and deserializes an object received from the server.
     * 
     * @param jsonValue
     *            JSON map from property name to property value
     * @param idMapper
     *            mapper from paintable id to paintable, used to decode
     *            references to paintables
     * @return deserialized object
     */
    // TODO Object -> something
    Object deserialize(JSONObject jsonValue, ConnectorMap idMapper);

}
