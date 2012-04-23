/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Connector;
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
            ConnectorMap idMapper, ApplicationConnection connection) {
        String type = ((JSONString) jsonArray.get(0)).stringValue();
        return decodeValue(type, jsonArray.get(1), target, idMapper, connection);
    }

    private static Object decodeValue(String variableType, Object value,
            Object target, ConnectorMap idMapper,
            ApplicationConnection connection) {
        Object val = null;
        // TODO type checks etc.
        if (JsonEncoder.VTYPE_NULL.equals(variableType)) {
            val = null;
        } else if (JsonEncoder.VTYPE_ARRAY.equals(variableType)) {
            val = decodeArray((JSONArray) value, idMapper, connection);
        } else if (JsonEncoder.VTYPE_MAP.equals(variableType)) {
            val = decodeMap((JSONObject) value, idMapper, connection);
        } else if (JsonEncoder.VTYPE_MAP_CONNECTOR.equals(variableType)) {
            val = decodeConnectorMap((JSONObject) value, idMapper, connection);
        } else if (JsonEncoder.VTYPE_LIST.equals(variableType)) {
            val = decodeList((JSONArray) value, idMapper, connection);
        } else if (JsonEncoder.VTYPE_SET.equals(variableType)) {
            val = decodeSet((JSONArray) value, idMapper, connection);
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
            val = idMapper.getConnector(((JSONString) value).stringValue());
        } else {
            return decodeObject(variableType, (JSONObject) value, target,
                    idMapper, connection);
        }

        return val;
    }

    private static Object decodeObject(String variableType,
            JSONObject encodedValue, Object target, ConnectorMap idMapper,
            ApplicationConnection connection) {
        // object, class name as type
        JSONSerializer<Object> serializer = connection.getSerializerMap()
                .getSerializer(variableType);
        // TODO handle case with no serializer found
        Object object = serializer.deserialize(encodedValue, target, idMapper,
                connection);
        return object;
    }

    private static Map<String, Object> decodeMap(JSONObject jsonMap,
            ConnectorMap idMapper, ApplicationConnection connection) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<String> it = jsonMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            map.put(key,
                    decodeValue((JSONArray) jsonMap.get(key), null, idMapper,
                            connection));
        }
        return map;
    }

    private static Map<Connector, Object> decodeConnectorMap(
            JSONObject jsonMap, ConnectorMap idMapper,
            ApplicationConnection connection) {
        HashMap<Connector, Object> map = new HashMap<Connector, Object>();
        Iterator<String> it = jsonMap.keySet().iterator();
        while (it.hasNext()) {
            String connectorId = it.next();
            Connector connector = idMapper.getConnector(connectorId);
            map.put(connector,
                    decodeValue((JSONArray) jsonMap.get(connectorId), null,
                            idMapper, connection));
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
            ConnectorMap idMapper, ApplicationConnection connection) {
        List<Object> list = decodeList(jsonArray, idMapper, connection);
        return list.toArray(new Object[list.size()]);
    }

    private static List<Object> decodeList(JSONArray jsonArray,
            ConnectorMap idMapper, ApplicationConnection connection) {
        List<Object> tokens = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.size(); ++i) {
            // each entry always has two elements: type and value
            JSONArray entryArray = (JSONArray) jsonArray.get(i);
            tokens.add(decodeValue(entryArray, null, idMapper, connection));
        }
        return tokens;
    }

    private static Set<Object> decodeSet(JSONArray jsonArray,
            ConnectorMap idMapper, ApplicationConnection connection) {
        Set<Object> tokens = new HashSet<Object>();
        for (int i = 0; i < jsonArray.size(); ++i) {
            // each entry always has two elements: type and value
            JSONArray entryArray = (JSONArray) jsonArray.get(i);
            tokens.add(decodeValue(entryArray, null, idMapper, connection));
        }
        return tokens;
    }
}
