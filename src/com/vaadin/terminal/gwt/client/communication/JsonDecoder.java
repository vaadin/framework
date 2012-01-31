/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
    /**
     * Convert a JSON array with two elements (type and value) into a
     * client-side type, recursively if necessary.
     * 
     * @param value
     *            JSON array with two elements
     * @param idMapper
     *            mapper between paintable ID and {@link VPaintable} objects
     * @return converted value (does not contain JSON types)
     */
    public static Object convertValue(JSONArray value, VPaintableMap idMapper) {
        return convertValue(
                ((JSONString) value.get(0)).stringValue().charAt(0),
                value.get(1), idMapper);
    }

    private static Object convertValue(char variableType, Object value,
            VPaintableMap idMapper) {
        Object val = null;
        // TODO type checks etc.
        switch (variableType) {
        case JsonEncoder.VTYPE_ARRAY:
            val = convertArray((JSONArray) value, idMapper);
            break;
        case JsonEncoder.VTYPE_MAP:
            val = convertMap((JSONObject) value, idMapper);
            break;
        case JsonEncoder.VTYPE_STRINGARRAY:
            val = convertStringArray((JSONArray) value);
            break;
        case JsonEncoder.VTYPE_STRING:
            val = value;
            break;
        case JsonEncoder.VTYPE_INTEGER:
            // TODO handle properly
            val = Integer.valueOf(String.valueOf(value));
            break;
        case JsonEncoder.VTYPE_LONG:
            // TODO handle properly
            val = Long.valueOf(String.valueOf(value));
            break;
        case JsonEncoder.VTYPE_FLOAT:
            // TODO handle properly
            val = Float.valueOf(String.valueOf(value));
            break;
        case JsonEncoder.VTYPE_DOUBLE:
            // TODO handle properly
            val = Double.valueOf(String.valueOf(value));
            break;
        case JsonEncoder.VTYPE_BOOLEAN:
            // TODO handle properly
            val = Boolean.valueOf(String.valueOf(value));
            break;
        case JsonEncoder.VTYPE_PAINTABLE:
            // TODO handle properly
            val = idMapper.getPaintable(String.valueOf(value));
            break;
        case JsonEncoder.VTYPE_SHAREDSTATE:
            val = convertMap((JSONObject) value, idMapper);
            // TODO convert to a SharedState instance
            break;
        }

        return val;
    }

    private static Object convertMap(JSONObject jsonMap, VPaintableMap idMapper) {
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

    private static Object convertArray(JSONArray jsonArray,
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
