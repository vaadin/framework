/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

/**
 * Decoder for converting RPC parameters and other values from JSON in transfer
 * between the client and the server and vice versa.
 * 
 * @since 7.0
 */
public class JsonCodec implements Serializable {

    private static Map<Class<?>, String> typeToTransportType = new HashMap<Class<?>, String>();

    static {
        registerType(String.class, JsonEncoder.VTYPE_STRING);
        registerType(Paintable.class, JsonEncoder.VTYPE_PAINTABLE);
        registerType(Boolean.class, JsonEncoder.VTYPE_BOOLEAN);
        registerType(Integer.class, JsonEncoder.VTYPE_INTEGER);
        registerType(Float.class, JsonEncoder.VTYPE_FLOAT);
        registerType(Double.class, JsonEncoder.VTYPE_DOUBLE);
        registerType(Long.class, JsonEncoder.VTYPE_LONG);
        // transported as string representation
        registerType(Enum.class, JsonEncoder.VTYPE_STRING);
        registerType(String[].class, JsonEncoder.VTYPE_STRINGARRAY);
        registerType(Object[].class, JsonEncoder.VTYPE_ARRAY);
        registerType(Map.class, JsonEncoder.VTYPE_MAP);
    }

    private static void registerType(Class<?> type, String transportType) {
        typeToTransportType.put(type, transportType);
    }

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
    public static Object decode(JSONArray value, PaintableIdMapper idMapper)
            throws JSONException {
        return convertVariableValue(value.getString(0), value.get(1), idMapper);
    }

    private static Object convertVariableValue(String variableType,
            Object value, PaintableIdMapper idMapper) throws JSONException {
        Object val = null;
        // TODO type checks etc.
        if (JsonEncoder.VTYPE_ARRAY.equals(variableType)) {
            val = convertArray((JSONArray) value, idMapper);
        } else if (JsonEncoder.VTYPE_MAP.equals(variableType)) {
            val = convertMap((JSONObject) value, idMapper);
        } else if (JsonEncoder.VTYPE_STRINGARRAY.equals(variableType)) {
            val = convertStringArray((JSONArray) value);
        } else if (JsonEncoder.VTYPE_STRING.equals(variableType)) {
            val = value;
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
            // Try to decode object using fields
            return decodeObject(variableType, (JSONObject) value, idMapper);

        }

        return val;
    }

    private static Object convertMap(JSONObject jsonMap,
            PaintableIdMapper idMapper) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<String> it = jsonMap.keys();
        while (it.hasNext()) {
            String key = it.next();
            map.put(key, decode(jsonMap.getJSONArray(key), idMapper));
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
            tokens.add(decode(entryArray, idMapper));
        }
        return tokens.toArray(new Object[tokens.size()]);
    }

    /**
     * Encode a value to a JSON representation for transport from the server to
     * the client.
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
        } else if (value instanceof Number) {
            return combineTypeAndValue(getTransportType(value), value);
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
        } else if (getTransportType(value) != JsonEncoder.VTYPE_UNDEFINED) {
            return combineTypeAndValue(getTransportType(value),
                    String.valueOf(value));
        } else {
            // Any object that we do not know how to encode we encode by looping
            // through fields
            return combineTypeAndValue(value.getClass().getCanonicalName(),
                    encodeObject(value, idMapper));
        }
    }

    private static Object encodeObject(Object value, PaintableIdMapper idMapper)
            throws JSONException {
        JSONObject jsonMap = new JSONObject();

        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(
                    value.getClass()).getPropertyDescriptors()) {
                String fieldName = pd.getName();
                if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
                    continue;
                }
                Method getterMethod = pd.getReadMethod();
                Object fieldValue = getterMethod.invoke(value, null);
                jsonMap.put(fieldName, encode(fieldValue, idMapper));
            }
        } catch (Exception e) {
            // TODO: Should exceptions be handled in a different way?
            throw new JSONException(e);
        }
        return jsonMap;
    }

    private static Object decodeObject(String type,
            JSONObject serializedObject, PaintableIdMapper idMapper)
            throws JSONException {

        Class<?> cls;
        try {
            cls = Class.forName(type);

            Object decodedObject = cls.newInstance();
            for (PropertyDescriptor pd : Introspector.getBeanInfo(cls)
                    .getPropertyDescriptors()) {
                if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
                    continue;
                }

                String fieldName = pd.getName();
                JSONArray encodedObject = serializedObject
                        .getJSONArray(fieldName);
                pd.getWriteMethod().invoke(decodedObject,
                        decode(encodedObject, idMapper));
            }

            return decodedObject;
        } catch (ClassNotFoundException e) {
            throw new JSONException(e);
        } catch (IllegalArgumentException e) {
            throw new JSONException(e);
        } catch (IllegalAccessException e) {
            throw new JSONException(e);
        } catch (InvocationTargetException e) {
            throw new JSONException(e);
        } catch (InstantiationException e) {
            throw new JSONException(e);
        } catch (IntrospectionException e) {
            throw new JSONException(e);
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

    private static JSONArray combineTypeAndValue(String type, Object value) {
        JSONArray outerArray = new JSONArray();
        outerArray.put(type);
        outerArray.put(value);
        return outerArray;
    }

    private static String getTransportType(Object value) {
        if (null == value) {
            return JsonEncoder.VTYPE_UNDEFINED;
        }
        String transportType = typeToTransportType.get(value.getClass());
        if (null != transportType) {
            return transportType;
        }
        // TODO throw exception?
        return JsonEncoder.VTYPE_UNDEFINED;
    }

}
