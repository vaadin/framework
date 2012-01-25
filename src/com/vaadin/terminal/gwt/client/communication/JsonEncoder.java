package com.vaadin.terminal.gwt.client.communication;

import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.terminal.gwt.client.VPaintable;
import com.vaadin.terminal.gwt.client.VPaintableMap;

/**
 * Encoder for converting RPC parameters and other values to JSON for transfer
 * between the client and the server.
 * 
 * Currently, basic data types as well as Map, String[] and Object[] are
 * supported, where maps and Object[] can contain other supported data types.
 * 
 * TODO support bi-directional codec functionality
 * 
 * TODO extensible type support
 * 
 * @since 7.0
 */
public class JsonEncoder {

    public static final char VTYPE_PAINTABLE = 'p';
    public static final char VTYPE_BOOLEAN = 'b';
    public static final char VTYPE_DOUBLE = 'd';
    public static final char VTYPE_FLOAT = 'f';
    public static final char VTYPE_LONG = 'l';
    public static final char VTYPE_INTEGER = 'i';
    public static final char VTYPE_STRING = 's';
    public static final char VTYPE_ARRAY = 'a';
    public static final char VTYPE_STRINGARRAY = 'c';
    public static final char VTYPE_MAP = 'm';

    // TODO is this needed?
    public static final char VTYPE_UNDEFINED = 'u';

    /**
     * Encode a value to a JSON representation for transport from the client to
     * the server.
     * 
     * @param value
     *            value to convert
     * @param paintableMap
     *            mapper from paintables to paintable IDs
     * @return JSON representation of the value
     */
    public static JSONValue encode(Object value, VPaintableMap paintableMap) {
        if (null == value) {
            // TODO as undefined type?
            return combineTypeAndValue(VTYPE_UNDEFINED, JSONNull.getInstance());
        } else if (value instanceof String[]) {
            String[] array = (String[]) value;
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                jsonArray.set(i, new JSONString(array[i]));
            }
            return combineTypeAndValue(VTYPE_STRINGARRAY, jsonArray);
        } else if (value instanceof String) {
            return combineTypeAndValue(VTYPE_STRING, new JSONString(
                    (String) value));
        } else if (value instanceof Boolean) {
            return combineTypeAndValue(VTYPE_BOOLEAN,
                    JSONBoolean.getInstance((Boolean) value));
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                // TODO handle object graph loops?
                jsonArray.set(i, encode(array[i], paintableMap));
            }
            return combineTypeAndValue(VTYPE_ARRAY, jsonArray);
        } else if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            // TODO implement; types for each element
            JSONObject jsonMap = new JSONObject();
            for (String mapKey : map.keySet()) {
                // TODO handle object graph loops?
                Object mapValue = map.get(mapKey);
                jsonMap.put(mapKey, encode(mapValue, paintableMap));
            }
            return combineTypeAndValue(VTYPE_MAP, jsonMap);
        } else if (value instanceof VPaintable) {
            VPaintable paintable = (VPaintable) value;
            return combineTypeAndValue(VTYPE_PAINTABLE, new JSONString(
                    paintableMap.getPid(paintable)));
        } else {
            return combineTypeAndValue(getTransportType(value), new JSONString(
                    String.valueOf(value)));
        }
    }

    private static JSONValue combineTypeAndValue(char type, JSONValue value) {
        JSONArray outerArray = new JSONArray();
        outerArray.set(0, new JSONString(String.valueOf(type)));
        outerArray.set(1, value);
        return outerArray;
    }

    private static char getTransportType(Object value) {
        if (value instanceof String) {
            return VTYPE_STRING;
        } else if (value instanceof VPaintable) {
            return VTYPE_PAINTABLE;
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
        } else if (value instanceof Enum) {
            return VTYPE_STRING; // transported as string representation
        } else if (value instanceof String[]) {
            return VTYPE_STRINGARRAY;
        } else if (value instanceof Object[]) {
            return VTYPE_ARRAY;
        } else if (value instanceof Map) {
            return VTYPE_MAP;
        }
        // TODO throw exception?
        return VTYPE_UNDEFINED;
    }

}
