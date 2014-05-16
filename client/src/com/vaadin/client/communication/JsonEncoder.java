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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.JsArrayObject;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Property;
import com.vaadin.client.metadata.Type;
import com.vaadin.shared.Connector;
import com.vaadin.shared.JsonConstants;
import com.vaadin.shared.communication.UidlValue;

/**
 * Encoder for converting RPC parameters and other values to JSON for transfer
 * between the client and the server.
 * 
 * Currently, basic data types as well as Map, String[] and Object[] are
 * supported, where maps and Object[] can contain other supported data types.
 * 
 * TODO extensible type support
 * 
 * @since 7.0
 */
public class JsonEncoder {

    /**
     * Encode a value to a JSON representation for transport from the client to
     * the server.
     * 
     * @param value
     *            value to convert
     * @param connection
     * @return JSON representation of the value
     */
    public static JSONValue encode(Object value, Type type,
            ApplicationConnection connection) {
        if (null == value) {
            return JSONNull.getInstance();
        } else if (value instanceof JSONValue) {
            return (JSONValue) value;
        } else if (value instanceof String[]) {
            String[] array = (String[]) value;
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                jsonArray.set(i, new JSONString(array[i]));
            }
            return jsonArray;
        } else if (value instanceof String) {
            return new JSONString((String) value);
        } else if (value instanceof Boolean) {
            return JSONBoolean.getInstance((Boolean) value);
        } else if (value instanceof Byte) {
            return new JSONNumber((Byte) value);
        } else if (value instanceof Character) {
            return new JSONString(String.valueOf(value));
        } else if (value instanceof Object[] && type == null) {
            // Non-legacy arrays handed by generated serializer
            return encodeLegacyObjectArray((Object[]) value, connection);
        } else if (value instanceof Enum) {
            return encodeEnum((Enum<?>) value, connection);
        } else if (value instanceof Map) {
            return encodeMap((Map) value, type, connection);
        } else if (value instanceof Connector) {
            Connector connector = (Connector) value;
            return new JSONString(connector.getConnectorId());
        } else if (value instanceof Collection) {
            return encodeCollection((Collection) value, type, connection);
        } else if (value instanceof UidlValue) {
            return encodeVariableChange((UidlValue) value, connection);
        } else {
            // First see if there's a custom serializer
            JSONSerializer<Object> serializer = null;
            if (type != null) {
                serializer = (JSONSerializer<Object>) type.findSerializer();
                if (serializer != null) {
                    return serializer.serialize(value, connection);
                }
            }

            String transportType = getTransportType(value);
            if (transportType != null) {
                // Send the string value for remaining legacy types
                return new JSONString(String.valueOf(value));
            } else if (type != null) {
                // And finally try using bean serialization logic
                try {
                    JsArrayObject<Property> properties = type
                            .getPropertiesAsArray();

                    JSONObject jsonObject = new JSONObject();

                    int size = properties.size();
                    for (int i = 0; i < size; i++) {
                        Property property = properties.get(i);
                        Object propertyValue = property.getValue(value);
                        Type propertyType = property.getType();
                        JSONValue encodedPropertyValue = encode(propertyValue,
                                propertyType, connection);
                        jsonObject
                                .put(property.getName(), encodedPropertyValue);
                    }
                    return jsonObject;

                } catch (NoDataException e) {
                    throw new RuntimeException("Can not encode "
                            + type.getSignature(), e);
                }

            } else {
                throw new RuntimeException("Can't encode " + value.getClass()
                        + " without type information");
            }
        }
    }

    private static JSONValue encodeVariableChange(UidlValue uidlValue,
            ApplicationConnection connection) {
        Object value = uidlValue.getValue();

        JSONArray jsonArray = new JSONArray();
        String transportType = getTransportType(value);
        if (transportType == null) {
            /*
             * This should not happen unless you try to send an unsupported type
             * in a legacy variable change from the client to the server.
             */
            String valueType = null;
            if (value != null) {
                valueType = value.getClass().getName();
            }
            throw new IllegalArgumentException("Cannot encode object of type "
                    + valueType);
        }
        jsonArray.set(0, new JSONString(transportType));
        jsonArray.set(1, encode(value, null, connection));

        return jsonArray;
    }

    private static JSONValue encodeMap(Map<Object, Object> map, Type type,
            ApplicationConnection connection) {
        /*
         * As we have no info about declared types, we instead select encoding
         * scheme based on actual type of first key. We can't do this if there's
         * no first key, so instead we send some special value that the
         * server-side decoding must check for. (see #8906)
         */
        if (map.isEmpty()) {
            return new JSONArray();
        }

        Object firstKey = map.keySet().iterator().next();
        if (firstKey instanceof String) {
            return encodeStringMap(map, type, connection);
        } else if (type == null) {
            throw new IllegalStateException(
                    "Only string keys supported for legacy maps");
        } else if (firstKey instanceof Connector) {
            return encodeConnectorMap(map, type, connection);
        } else {
            return encodeObjectMap(map, type, connection);
        }
    }

    private static JSONValue encodeChildValue(Object value,
            Type collectionType, int typeIndex, ApplicationConnection connection) {
        if (collectionType == null) {
            return encode(new UidlValue(value), null, connection);
        } else {
            assert collectionType.getParameterTypes() != null
                    && collectionType.getParameterTypes().length > typeIndex
                    && collectionType.getParameterTypes()[typeIndex] != null : "Proper generics required for encoding child value, assertion failed for "
                    + collectionType;
            Type childType = collectionType.getParameterTypes()[typeIndex];
            return encode(value, childType, connection);
        }
    }

    private static JSONValue encodeObjectMap(Map<Object, Object> map,
            Type type, ApplicationConnection connection) {
        JSONArray keys = new JSONArray();
        JSONArray values = new JSONArray();

        assert type != null : "Should only be used for non-legacy types";

        for (Entry<?, ?> entry : map.entrySet()) {
            keys.set(keys.size(),
                    encodeChildValue(entry.getKey(), type, 0, connection));
            values.set(values.size(),
                    encodeChildValue(entry.getValue(), type, 1, connection));
        }

        JSONArray keysAndValues = new JSONArray();
        keysAndValues.set(0, keys);
        keysAndValues.set(1, values);

        return keysAndValues;
    }

    private static JSONValue encodeConnectorMap(Map<Object, Object> map,
            Type type, ApplicationConnection connection) {
        JSONObject jsonMap = new JSONObject();

        for (Entry<?, ?> entry : map.entrySet()) {
            Connector connector = (Connector) entry.getKey();

            JSONValue encodedValue = encodeChildValue(entry.getValue(), type,
                    1, connection);

            jsonMap.put(connector.getConnectorId(), encodedValue);
        }

        return jsonMap;
    }

    private static JSONValue encodeStringMap(Map<Object, Object> map,
            Type type, ApplicationConnection connection) {
        JSONObject jsonMap = new JSONObject();

        for (Entry<?, ?> entry : map.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();

            jsonMap.put(key, encodeChildValue(value, type, 1, connection));
        }

        return jsonMap;
    }

    private static JSONValue encodeEnum(Enum<?> e,
            ApplicationConnection connection) {
        return new JSONString(e.toString());
    }

    private static JSONValue encodeLegacyObjectArray(Object[] array,
            ApplicationConnection connection) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < array.length; ++i) {
            // TODO handle object graph loops?
            Object value = array[i];
            jsonArray.set(i, encode(value, null, connection));
        }
        return jsonArray;
    }

    private static JSONValue encodeCollection(Collection collection, Type type,
            ApplicationConnection connection) {
        JSONArray jsonArray = new JSONArray();
        int idx = 0;
        for (Object o : collection) {
            JSONValue encodedObject = encodeChildValue(o, type, 0, connection);
            jsonArray.set(idx++, encodedObject);
        }
        if (collection instanceof Set) {
            return jsonArray;
        } else if (collection instanceof List) {
            return jsonArray;
        } else {
            throw new RuntimeException("Unsupport collection type: "
                    + collection.getClass().getName());
        }

    }

    /**
     * Returns the transport type for the given value. Only returns a transport
     * type for internally handled values.
     * 
     * @param value
     *            The value that should be transported
     * @return One of the JsonEncode.VTYPE_ constants or null if the value
     *         cannot be transported using an internally handled type.
     */
    private static String getTransportType(Object value) {
        if (value == null) {
            return JsonConstants.VTYPE_NULL;
        } else if (value instanceof String) {
            return JsonConstants.VTYPE_STRING;
        } else if (value instanceof Connector) {
            return JsonConstants.VTYPE_CONNECTOR;
        } else if (value instanceof Boolean) {
            return JsonConstants.VTYPE_BOOLEAN;
        } else if (value instanceof Integer) {
            return JsonConstants.VTYPE_INTEGER;
        } else if (value instanceof Float) {
            return JsonConstants.VTYPE_FLOAT;
        } else if (value instanceof Double) {
            return JsonConstants.VTYPE_DOUBLE;
        } else if (value instanceof Long) {
            return JsonConstants.VTYPE_LONG;
        } else if (value instanceof List) {
            return JsonConstants.VTYPE_LIST;
        } else if (value instanceof Set) {
            return JsonConstants.VTYPE_SET;
        } else if (value instanceof String[]) {
            return JsonConstants.VTYPE_STRINGARRAY;
        } else if (value instanceof Object[]) {
            return JsonConstants.VTYPE_ARRAY;
        } else if (value instanceof Map) {
            return JsonConstants.VTYPE_MAP;
        } else if (value instanceof Enum<?>) {
            // Enum value is processed as a string
            return JsonConstants.VTYPE_STRING;
        }
        return null;
    }
}
