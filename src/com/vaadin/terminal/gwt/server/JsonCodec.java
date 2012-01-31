/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.external.json.JSONObject;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.gwt.client.communication.JsonEncoder;
import com.vaadin.terminal.gwt.client.communication.SharedState;

/**
 * Decoder for converting RPC parameters and other values from JSON in transfer
 * between the client and the server and vice versa.
 * 
 * @since 7.0
 */
public class JsonCodec implements Serializable {

    /**
     * Convert a JSON array with two elements (type and value) into a
     * server-side type, recursively if necessary.
     * 
     * @param value
     *            JSON array with two elements
     * @param idMapper
     *            mapper between paintable ID and {@link Paintable} objects
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
        int length = jsonArray.length();
        List<String> tokens = new ArrayList<String>(length);
        for (int i = 0; i < length; ++i) {
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

    /**
     * Encode a value to a JSON representation for transport from the client to
     * the server.
     * 
     * @param value
     *            value to convert
     * @param idMapper
     *            mapper between paintable ID and {@link Paintable} objects
     * @return JSON representation of the value
     * @throws JSONException
     *             if encoding a value fails (e.g. NaN or infinite number)
     */
    public static JSONArray encode(Object value, PaintableIdMapper idMapper)
            throws JSONException {
        if (null == value) {
            // TODO as undefined type?
            return combineTypeAndValue(JsonEncoder.VTYPE_UNDEFINED,
                    JSONObject.NULL);
        } else if (value instanceof SharedState) {
            // TODO implement by encoding the bean
            Map<String, Object> map = ((SharedState) value).getState();
            return combineTypeAndValue(JsonEncoder.VTYPE_SHAREDSTATE,
                    encodeMapContents(map, idMapper));
        } else if (value instanceof String[]) {
            String[] array = (String[]) value;
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                jsonArray.put(array[i]);
            }
            return combineTypeAndValue(JsonEncoder.VTYPE_STRINGARRAY, jsonArray);
        } else if (value instanceof String) {
            return combineTypeAndValue(JsonEncoder.VTYPE_STRING, value);
        } else if (value instanceof Boolean) {
            return combineTypeAndValue(JsonEncoder.VTYPE_BOOLEAN, value);
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            JSONArray jsonArray = encodeArrayContents(array, idMapper);
            return combineTypeAndValue(JsonEncoder.VTYPE_ARRAY, jsonArray);
        } else if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            JSONObject jsonMap = encodeMapContents(map, idMapper);
            return combineTypeAndValue(JsonEncoder.VTYPE_MAP, jsonMap);
        } else if (value instanceof Paintable) {
            Paintable paintable = (Paintable) value;
            return combineTypeAndValue(JsonEncoder.VTYPE_PAINTABLE,
                    idMapper.getPaintableId(paintable));
        } else {
            return combineTypeAndValue(getTransportType(value),
                    String.valueOf(value));
        }
    }

    private static JSONArray encodeArrayContents(Object[] array,
            PaintableIdMapper idMapper) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < array.length; ++i) {
            // TODO handle object graph loops?
            jsonArray.put(encode(array[i], idMapper));
        }
        return jsonArray;
    }

    private static JSONObject encodeMapContents(Map<String, Object> map,
            PaintableIdMapper idMapper) throws JSONException {
        JSONObject jsonMap = new JSONObject();
        for (String mapKey : map.keySet()) {
            // TODO handle object graph loops?
            Object mapValue = map.get(mapKey);
            jsonMap.put(mapKey, encode(mapValue, idMapper));
        }
        return jsonMap;
    }

    private static JSONArray combineTypeAndValue(char type, Object value) {
        JSONArray outerArray = new JSONArray();
        outerArray.put(String.valueOf(type));
        outerArray.put(value);
        return outerArray;
    }

    private static char getTransportType(Object value) {
        if (value instanceof String) {
            return JsonEncoder.VTYPE_STRING;
        } else if (value instanceof Paintable) {
            return JsonEncoder.VTYPE_PAINTABLE;
        } else if (value instanceof Boolean) {
            return JsonEncoder.VTYPE_BOOLEAN;
        } else if (value instanceof Integer) {
            return JsonEncoder.VTYPE_INTEGER;
        } else if (value instanceof Float) {
            return JsonEncoder.VTYPE_FLOAT;
        } else if (value instanceof Double) {
            return JsonEncoder.VTYPE_DOUBLE;
        } else if (value instanceof Long) {
            return JsonEncoder.VTYPE_LONG;
        } else if (value instanceof Enum) {
            // transported as string representation
            return JsonEncoder.VTYPE_STRING;
        } else if (value instanceof String[]) {
            return JsonEncoder.VTYPE_STRINGARRAY;
        } else if (value instanceof Object[]) {
            return JsonEncoder.VTYPE_ARRAY;
        } else if (value instanceof Map) {
            return JsonEncoder.VTYPE_MAP;
        }
        // TODO throw exception?
        return JsonEncoder.VTYPE_UNDEFINED;
    }

}
