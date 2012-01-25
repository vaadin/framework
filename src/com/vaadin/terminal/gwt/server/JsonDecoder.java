/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.external.json.JSONObject;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.gwt.client.communication.JsonEncoder;

/**
 * Decoder for converting RPC parameters and other values from JSON in transfer
 * between the client and the server.
 * 
 * TODO support bi-directional codec functionality
 * 
 * @since 7.0
 */
public class JsonDecoder implements Serializable {

    /**
     * Convert a JSON array with two elements (type and value) into a
     * server-side type, recursively if necessary.
     * 
     * @param value
     *            JSON array with two elements
     * @param idMapper
     *            mapper from paintable ID to {@link Paintable} objects
     * @return converted value (does not contain JSON types)
     * @throws JSONException
     *             if the conversion fails
     */
    public static Object convertVariableValue(JSONArray value,
            PaintableIdMapper idMapper) throws JSONException {
        return convertVariableValue(value.getString(0).charAt(0), value.get(1),
                idMapper);
    }

    private static Object convertVariableValue(char variableType, Object value,
            PaintableIdMapper idMapper) throws JSONException {
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
        }

        return val;
    }

    private static Object convertMap(JSONObject jsonMap,
            PaintableIdMapper idMapper) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<String> it = jsonMap.keys();
        while (it.hasNext()) {
            String key = it.next();
            map.put(key,
                    convertVariableValue(jsonMap.getJSONArray(key), idMapper));
        }
        return map;
    }

    private static String[] convertStringArray(JSONArray jsonArray)
            throws JSONException {
        List<String> tokens = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            tokens.add(jsonArray.getString(i));
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private static Object convertArray(JSONArray jsonArray,
            PaintableIdMapper idMapper) throws JSONException {
        List<Object> tokens = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            // each entry always has two elements: type and value
            JSONArray entryArray = jsonArray.getJSONArray(i);
            tokens.add(convertVariableValue(entryArray, idMapper));
        }
        return tokens.toArray(new Object[tokens.size()]);
    }

}
