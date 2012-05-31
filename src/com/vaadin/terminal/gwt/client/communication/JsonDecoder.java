/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.ServerConnector;

/**
 * Client side decoder for decodeing shared state and other values from JSON
 * received from the server.
 * 
 * Currently, basic data types as well as Map, String[] and Object[] are
 * supported, where maps and Object[] can contain other supported data types.
 * 
 * TODO extensible type support
 * 
 * @since 7.0
 */
public class JsonDecoder {

    /**
     * Decode a JSON array with two elements (type and value) into a client-side
     * type, recursively if necessary.
     * 
     * @param jsonArray
     *            JSON array with two elements
     * @param idMapper
     *            mapper between connector ID and {@link ServerConnector}
     *            objects
     * @param connection
     *            reference to the current ApplicationConnection
     * @return decoded value (does not contain JSON types)
     */
    public static Object decodeValue(JSONArray jsonArray, Object target,
            ApplicationConnection connection) {
        String type = ((JSONString) jsonArray.get(0)).stringValue();
        return decodeValue(type, jsonArray.get(1), target, connection);
    }

    private static Object decodeValue(String variableType, JSONValue value,
            Object target, ApplicationConnection connection) {
        Object val = null;
        // TODO type checks etc.
        if (JsonEncoder.VTYPE_NULL.equals(variableType)) {
            val = null;
        } else if (JsonEncoder.VTYPE_ARRAY.equals(variableType)) {
            val = decodeArray((JSONArray) value, connection);
        } else if (JsonEncoder.VTYPE_MAP.equals(variableType)) {
            val = decodeMap((JSONObject) value, connection);
        } else if (JsonEncoder.VTYPE_LIST.equals(variableType)) {
            val = decodeList((JSONArray) value, connection);
        } else if (JsonEncoder.VTYPE_SET.equals(variableType)) {
            val = decodeSet((JSONArray) value, connection);
        } else if (JsonEncoder.VTYPE_STRINGARRAY.equals(variableType)) {
            val = decodeStringArray((JSONArray) value);
        } else if (JsonEncoder.VTYPE_STRING.equals(variableType)) {
            val = ((JSONString) value).stringValue();
        } else if (JsonEncoder.VTYPE_INTEGER.equals(variableType)) {
            // TODO handle properly
            val = Integer.valueOf(String.valueOf(value));
        } else if (JsonEncoder.VTYPE_LONG.equals(variableType)) {
            // TODO handle properly
            val = Long.valueOf(String.valueOf(value));
        } else if (JsonEncoder.VTYPE_FLOAT.equals(variableType)) {
            // TODO handle properly
            val = Float.valueOf(String.valueOf(value));
        } else if (JsonEncoder.VTYPE_DOUBLE.equals(variableType)) {
            // TODO handle properly
            val = Double.valueOf(String.valueOf(value));
        } else if (JsonEncoder.VTYPE_BOOLEAN.equals(variableType)) {
            // TODO handle properly
            val = Boolean.valueOf(String.valueOf(value));
        } else if (JsonEncoder.VTYPE_CONNECTOR.equals(variableType)) {
            val = ConnectorMap.get(connection).getConnector(
                    ((JSONString) value).stringValue());
        } else {
            return decodeObject(new Type(variableType, null), value, target,
                    connection);
        }

        return val;
    }

    private static Object decodeObject(Type type, JSONValue encodedValue,
            Object target, ApplicationConnection connection) {
        // object, class name as type
        JSONSerializer<Object> serializer = connection.getSerializerMap()
                .getSerializer(type.getBaseTypeName());
        // TODO handle case with no serializer found

        if (target != null && serializer instanceof DiffJSONSerializer<?>) {
            DiffJSONSerializer<Object> diffSerializer = (DiffJSONSerializer<Object>) serializer;
            diffSerializer.update(target, type, encodedValue, connection);
            return target;
        } else {
            Object object = serializer.deserialize(type, encodedValue,
                    connection);
            return object;
        }
    }

    private static Map<Object, Object> decodeMap(JSONObject jsonMap,
            ApplicationConnection connection) {
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        Iterator<String> it = jsonMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            JSONArray encodedKey = (JSONArray) JSONParser.parseStrict(key);
            JSONArray encodedValue = (JSONArray) jsonMap.get(key);
            Object decodedKey = decodeValue(encodedKey, null, connection);
            Object decodedValue = decodeValue(encodedValue, null, connection);
            map.put(decodedKey, decodedValue);
        }
        return map;
    }

    private static String[] decodeStringArray(JSONArray jsonArray) {
        int size = jsonArray.size();
        List<String> tokens = new ArrayList<String>(size);
        for (int i = 0; i < size; ++i) {
            tokens.add(String.valueOf(jsonArray.get(i)));
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private static Object[] decodeArray(JSONArray jsonArray,
            ApplicationConnection connection) {
        List<Object> list = decodeList(jsonArray, connection);
        return list.toArray(new Object[list.size()]);
    }

    private static List<Object> decodeList(JSONArray jsonArray,
            ApplicationConnection connection) {
        List<Object> tokens = new ArrayList<Object>();
        decodeIntoCollection(jsonArray, connection, tokens);
        return tokens;
    }

    private static Set<Object> decodeSet(JSONArray jsonArray,
            ApplicationConnection connection) {
        Set<Object> tokens = new HashSet<Object>();
        decodeIntoCollection(jsonArray, connection, tokens);
        return tokens;
    }

    private static void decodeIntoCollection(JSONArray jsonArray,
            ApplicationConnection connection, Collection<Object> tokens) {
        for (int i = 0; i < jsonArray.size(); ++i) {
            // each entry always has two elements: type and value
            JSONArray entryArray = (JSONArray) jsonArray.get(i);
            tokens.add(decodeValue(entryArray, null, connection));
        }
    }
}
