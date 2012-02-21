/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.vaadin.terminal.gwt.client.VPaintable;
import com.vaadin.terminal.gwt.client.VPaintableMap;

/**
 * Client side decoder for converting shared state and other values from JSON
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
    private static SerializerMap serializerMap = GWT
            .create(SerializerMap.class);

    /**
     * Convert a JSON array with two elements (type and value) into a
     * client-side type, recursively if necessary.
     * 
     * @param jsonArray
     *            JSON array with two elements
     * @param idMapper
     *            mapper between paintable ID and {@link VPaintable} objects
     * @return converted value (does not contain JSON types)
     */
    public static Object convertValue(JSONArray jsonArray,
            VPaintableMap idMapper) {
        String type = ((JSONString) jsonArray.get(0)).stringValue();
        return convertValue(type, jsonArray.get(1), idMapper);
    }

    private static Object convertValue(String variableType, Object value,
            VPaintableMap idMapper) {
        Object val = null;
        // TODO type checks etc.
        if (JsonEncoder.VTYPE_ARRAY.equals(variableType)) {
            val = convertArray((JSONArray) value, idMapper);
        } else if (JsonEncoder.VTYPE_MAP.equals(variableType)) {
            val = convertMap((JSONObject) value, idMapper);
        } else if (JsonEncoder.VTYPE_STRINGARRAY.equals(variableType)) {
            val = convertStringArray((JSONArray) value);
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
        } else if (JsonEncoder.VTYPE_PAINTABLE.equals(variableType)) {
            // TODO handle properly
            val = idMapper.getPaintable(String.valueOf(value));
        } else {
            // object, class name as type
            VaadinSerializer serializer = serializerMap
                    .getSerializer(variableType);
            Object object = serializer
                    .deserialize((JSONObject) value, idMapper);
            return object;
        }

        return val;
    }

    private static Map<String, Object> convertMap(JSONObject jsonMap,
            VPaintableMap idMapper) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<String> it = jsonMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            map.put(key, convertValue((JSONArray) jsonMap.get(key), idMapper));
        }
        return map;
    }

    private static String[] convertStringArray(JSONArray jsonArray) {
        int size = jsonArray.size();
        List<String> tokens = new ArrayList<String>(size);
        for (int i = 0; i < size; ++i) {
            tokens.add(String.valueOf(jsonArray.get(i)));
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private static Object[] convertArray(JSONArray jsonArray,
            VPaintableMap idMapper) {
        List<Object> tokens = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.size(); ++i) {
            // each entry always has two elements: type and value
            JSONArray entryArray = (JSONArray) jsonArray.get(i);
            tokens.add(convertValue(entryArray, idMapper));
        }
        return tokens.toArray(new Object[tokens.size()]);
    }

}
