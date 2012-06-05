/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ConnectorMap;

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

    public static final String VTYPE_CONNECTOR = "c";
    public static final String VTYPE_BOOLEAN = "b";
    public static final String VTYPE_DOUBLE = "d";
    public static final String VTYPE_FLOAT = "f";
    public static final String VTYPE_LONG = "l";
    public static final String VTYPE_INTEGER = "i";
    public static final String VTYPE_STRING = "s";
    public static final String VTYPE_ARRAY = "a";
    public static final String VTYPE_STRINGARRAY = "S";
    public static final String VTYPE_MAP = "m";
    public static final String VTYPE_LIST = "L";
    public static final String VTYPE_SET = "q";
    public static final String VTYPE_NULL = "n";

    /**
     * Encode a value to a JSON representation for transport from the client to
     * the server.
     * 
     * @param value
     *            value to convert
     * @param connectorMap
     *            mapper from connectors to connector IDs
     * @param connection
     * @return JSON representation of the value
     */
    public static JSONValue encode(Object value,
            boolean restrictToInternalTypes, ConnectorMap connectorMap,
            ApplicationConnection connection) {
        if (null == value) {
            return JSONNull.getInstance();
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
        } else if (value instanceof Object[]) {
            return encodeObjectArray((Object[]) value, restrictToInternalTypes,
                    connectorMap, connection);
        } else if (value instanceof Enum) {
            if (restrictToInternalTypes) {
                // Enums are encoded as strings in Vaadin 6 so we still do that
                // for backwards copmatibility.
                return encode(new UidlValue(value.toString()),
                        restrictToInternalTypes, connectorMap, connection);
            } else {
                Enum e = (Enum) value;
                return encodeEnum(e, connectorMap, connection);
            }
        } else if (value instanceof Map) {
            return encodeMap((Map) value, restrictToInternalTypes,
                    connectorMap, connection);
        } else if (value instanceof Connector) {
            Connector connector = (Connector) value;
            return new JSONString(connector.getConnectorId());
        } else if (value instanceof Collection) {
            return encodeCollection((Collection) value,
                    restrictToInternalTypes, connectorMap, connection);
        } else if (value instanceof UidlValue) {
            return encodeVariableChange((UidlValue) value, connectorMap,
                    connection);
        } else {
            String transportType = getTransportType(value);
            if (transportType != null) {
                return new JSONString(String.valueOf(value));
            } else {
                // Try to find a generated serializer object, class name is the
                // type
                transportType = value.getClass().getName();
                JSONSerializer serializer = connection.getSerializerMap()
                        .getSerializer(transportType);

                // TODO handle case with no serializer found
                return serializer.serialize(value, connectorMap, connection);
            }
        }
    }

    private static JSONValue encodeVariableChange(UidlValue uidlValue,
            ConnectorMap connectorMap, ApplicationConnection connection) {
        Object value = uidlValue.getValue();

        JSONArray jsonArray = new JSONArray();
        jsonArray.set(0, new JSONString(getTransportType(value)));
        jsonArray.set(1, encode(value, true, connectorMap, connection));

        return jsonArray;
    }

    private static JSONValue encodeMap(Map<Object, Object> map,
            boolean restrictToInternalTypes, ConnectorMap connectorMap,
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
            return encodeStringMap(map, restrictToInternalTypes, connectorMap,
                    connection);
        } else if (restrictToInternalTypes) {
            throw new IllegalStateException(
                    "Only string keys supported for legacy maps");
        } else if (firstKey instanceof Connector) {
            return encodeConenctorMap(map, connectorMap, connection);
        } else {
            return encodeObjectMap(map, connectorMap, connection);
        }
    }

    private static JSONValue encodeObjectMap(Map<Object, Object> map,
            ConnectorMap connectorMap, ApplicationConnection connection) {
        JSONArray keys = new JSONArray();
        JSONArray values = new JSONArray();
        for (Entry<?, ?> entry : map.entrySet()) {
            // restrictToInternalTypes always false if we end up here
            keys.set(keys.size(),
                    encode(entry.getKey(), false, connectorMap, connection));
            values.set(values.size(),
                    encode(entry.getValue(), false, connectorMap, connection));
        }

        JSONArray keysAndValues = new JSONArray();
        keysAndValues.set(0, keys);
        keysAndValues.set(1, values);

        return keysAndValues;
    }

    private static JSONValue encodeConenctorMap(Map<Object, Object> map,
            ConnectorMap connectorMap, ApplicationConnection connection) {
        JSONObject jsonMap = new JSONObject();

        for (Entry<?, ?> entry : map.entrySet()) {
            Connector connector = (Connector) entry.getKey();

            // restrictToInternalTypes always false if we end up here
            JSONValue encodedValue = encode(entry.getValue(), false,
                    connectorMap, connection);

            jsonMap.put(connector.getConnectorId(), encodedValue);
        }

        return jsonMap;
    }

    private static JSONValue encodeStringMap(Map<Object, Object> map,
            boolean restrictToInternalTypes, ConnectorMap connectorMap,
            ApplicationConnection connection) {
        JSONObject jsonMap = new JSONObject();

        for (Entry<?, ?> entry : map.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();

            if (restrictToInternalTypes) {
                value = new UidlValue(value);
            }

            JSONValue encodedValue = encode(value, restrictToInternalTypes,
                    connectorMap, connection);

            jsonMap.put(key, encodedValue);
        }

        return jsonMap;
    }

    private static JSONValue encodeEnum(Enum e, ConnectorMap connectorMap,
            ApplicationConnection connection) {
        return new JSONString(e.toString());
    }

    private static JSONValue encodeObjectArray(Object[] array,
            boolean restrictToInternalTypes, ConnectorMap connectorMap,
            ApplicationConnection connection) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < array.length; ++i) {
            // TODO handle object graph loops?
            Object value = array[i];
            if (restrictToInternalTypes) {
                value = new UidlValue(value);
            }
            jsonArray.set(
                    i,
                    encode(value, restrictToInternalTypes, connectorMap,
                            connection));
        }
        return jsonArray;
    }

    private static JSONValue encodeCollection(Collection collection,
            boolean restrictToInternalTypes, ConnectorMap connectorMap,
            ApplicationConnection connection) {
        JSONArray jsonArray = new JSONArray();
        int idx = 0;
        for (Object o : collection) {
            JSONValue encodedObject = encode(o, restrictToInternalTypes,
                    connectorMap, connection);
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
            return VTYPE_NULL;
        } else if (value instanceof String) {
            return VTYPE_STRING;
        } else if (value instanceof Connector) {
            return VTYPE_CONNECTOR;
        } else if (value instanceof Boolean) {
            return VTYPE_BOOLEAN;
        } else if (value instanceof Integer) {
            return VTYPE_INTEGER;
        } else if (value instanceof Float) {
            return VTYPE_FLOAT;
        } else if (value instanceof Double) {
            return VTYPE_DOUBLE;
        } else if (value instanceof Long) {
            return VTYPE_LONG;
        } else if (value instanceof List) {
            return VTYPE_LIST;
        } else if (value instanceof Set) {
            return VTYPE_SET;
        } else if (value instanceof String[]) {
            return VTYPE_STRINGARRAY;
        } else if (value instanceof Object[]) {
            return VTYPE_ARRAY;
        } else if (value instanceof Map) {
            return VTYPE_MAP;
        }
        return null;
    }
}
