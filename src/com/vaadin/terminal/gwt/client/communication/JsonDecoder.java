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
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ConnectorMap;

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
     * @param jsonValue
     *            JSON value with encoded data
     * @param connection
     *            reference to the current ApplicationConnection
     * @return decoded value (does not contain JSON types)
     */
    public static Object decodeValue(Type type, JSONValue jsonValue,
            Object target, ApplicationConnection connection) {

        // Null is null, regardless of type
        if (jsonValue.isNull() != null) {
            return null;
        }

        String baseTypeName = type.getBaseTypeName();
        if (baseTypeName.endsWith("[]")) {
            return decodeArray(type, (JSONArray) jsonValue, connection);
        } else if (Map.class.getName().equals(baseTypeName)
                || HashMap.class.getName().equals(baseTypeName)) {
            return decodeMap(type, (JSONObject) jsonValue, connection);
        } else if (List.class.getName().equals(baseTypeName)
                || ArrayList.class.getName().equals(baseTypeName)) {
            return decodeList(type, (JSONArray) jsonValue, connection);
        } else if (Set.class.getName().equals(baseTypeName)) {
            return decodeSet(type, (JSONArray) jsonValue, connection);
        } else if (String.class.getName().equals(baseTypeName)) {
            return ((JSONString) jsonValue).stringValue();
        } else if (Integer.class.getName().equals(baseTypeName)) {
            return Integer.valueOf(String.valueOf(jsonValue));
        } else if (Long.class.getName().equals(baseTypeName)) {
            // TODO handle properly
            return Long.valueOf(String.valueOf(jsonValue));
        } else if (Float.class.getName().equals(baseTypeName)) {
            // TODO handle properly
            return Float.valueOf(String.valueOf(jsonValue));
        } else if (Double.class.getName().equals(baseTypeName)) {
            // TODO handle properly
            return Double.valueOf(String.valueOf(jsonValue));
        } else if (Boolean.class.getName().equals(baseTypeName)) {
            // TODO handle properly
            return Boolean.valueOf(String.valueOf(jsonValue));
        } else if (Connector.class.getName().equals(baseTypeName)) {
            return ConnectorMap.get(connection).getConnector(
                    ((JSONString) jsonValue).stringValue());
        } else {
            return decodeObject(type, jsonValue, target, connection);
        }
    }

    private static Object decodeObject(Type type, JSONValue jsonValue,
            Object target, ApplicationConnection connection) {
        JSONSerializer<Object> serializer = connection.getSerializerMap()
                .getSerializer(type.getBaseTypeName());
        // TODO handle case with no serializer found
        // Currently getSerializer throws exception if not found

        if (target != null && serializer instanceof DiffJSONSerializer<?>) {
            DiffJSONSerializer<Object> diffSerializer = (DiffJSONSerializer<Object>) serializer;
            diffSerializer.update(target, type, jsonValue, connection);
            return target;
        } else {
            Object object = serializer.deserialize(type, jsonValue, connection);
            return object;
        }
    }

    private static Map<Object, Object> decodeMap(Type type, JSONObject jsonMap,
            ApplicationConnection connection) {
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        Iterator<String> it = jsonMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            JSONValue encodedKey = JSONParser.parseStrict(key);
            JSONValue encodedValue = jsonMap.get(key);
            Object decodedKey = decodeValue(type.getParameterTypes()[0],
                    encodedKey, null, connection);
            Object decodedValue = decodeValue(type.getParameterTypes()[1],
                    encodedValue, null, connection);
            map.put(decodedKey, decodedValue);
        }
        return map;
    }

    private static Object[] decodeArray(Type type, JSONArray jsonArray,
            ApplicationConnection connection) {
        String arrayTypeName = type.getBaseTypeName();
        String chldTypeName = arrayTypeName.substring(0,
                arrayTypeName.length() - 2);
        List<Object> list = decodeList(new Type(chldTypeName, null), jsonArray,
                connection);
        return list.toArray(new Object[list.size()]);
    }

    private static List<Object> decodeList(Type type, JSONArray jsonArray,
            ApplicationConnection connection) {
        List<Object> tokens = new ArrayList<Object>();
        decodeIntoCollection(type.getParameterTypes()[0], jsonArray,
                connection, tokens);
        return tokens;
    }

    private static Set<Object> decodeSet(Type type, JSONArray jsonArray,
            ApplicationConnection connection) {
        Set<Object> tokens = new HashSet<Object>();
        decodeIntoCollection(type.getParameterTypes()[0], jsonArray,
                connection, tokens);
        return tokens;
    }

    private static void decodeIntoCollection(Type childType,
            JSONArray jsonArray, ApplicationConnection connection,
            Collection<Object> tokens) {
        for (int i = 0; i < jsonArray.size(); ++i) {
            // each entry always has two elements: type and value
            JSONValue entryValue = jsonArray.get(i);
            tokens.add(decodeValue(childType, entryValue, null, connection));
        }
    }
}
