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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.Application;
import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.external.json.JSONObject;
import com.vaadin.terminal.gwt.client.Connector;
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
        registerType(Connector.class, JsonEncoder.VTYPE_CONNECTOR);
        registerType(Boolean.class, JsonEncoder.VTYPE_BOOLEAN);
        registerType(boolean.class, JsonEncoder.VTYPE_BOOLEAN);
        registerType(Integer.class, JsonEncoder.VTYPE_INTEGER);
        registerType(int.class, JsonEncoder.VTYPE_INTEGER);
        registerType(Float.class, JsonEncoder.VTYPE_FLOAT);
        registerType(float.class, JsonEncoder.VTYPE_FLOAT);
        registerType(Double.class, JsonEncoder.VTYPE_DOUBLE);
        registerType(double.class, JsonEncoder.VTYPE_FLOAT);
        registerType(Long.class, JsonEncoder.VTYPE_LONG);
        registerType(long.class, JsonEncoder.VTYPE_LONG);
        // transported as string representation
        registerType(Enum.class, JsonEncoder.VTYPE_STRING);
        registerType(String[].class, JsonEncoder.VTYPE_STRINGARRAY);
        registerType(Object[].class, JsonEncoder.VTYPE_ARRAY);
        registerType(Map.class, JsonEncoder.VTYPE_MAP);
        registerType(List.class, JsonEncoder.VTYPE_LIST);
        registerType(Set.class, JsonEncoder.VTYPE_SET);
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
     * @param application
     *            mapper between connector ID and {@link Connector} objects
     * @return converted value (does not contain JSON types)
     * @throws JSONException
     *             if the conversion fails
     */
    public static Object decode(JSONArray value, Application application)
            throws JSONException {
        return decodeVariableValue(value.getString(0), value.get(1),
                application);
    }

    private static Object decodeVariableValue(String variableType,
            Object value, Application application) throws JSONException {
        Object val = null;
        // TODO type checks etc.
        if (JsonEncoder.VTYPE_ARRAY.equals(variableType)) {
            val = decodeArray((JSONArray) value, application);
        } else if (JsonEncoder.VTYPE_LIST.equals(variableType)) {
            val = decodeList((JSONArray) value, application);
        } else if (JsonEncoder.VTYPE_SET.equals(variableType)) {
            val = decodeSet((JSONArray) value, application);
        } else if (JsonEncoder.VTYPE_MAP.equals(variableType)) {
            val = decodeMap((JSONObject) value, application);
        } else if (JsonEncoder.VTYPE_STRINGARRAY.equals(variableType)) {
            val = decodeStringArray((JSONArray) value);
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
        } else if (JsonEncoder.VTYPE_CONNECTOR.equals(variableType)) {
            val = application.getConnector(String.valueOf(value));
        } else if (JsonEncoder.VTYPE_NULL.equals(variableType)) {
            val = null;
        } else {
            // Try to decode object using fields
            return decodeObject(variableType, (JSONObject) value, application);

        }

        return val;
    }

    private static Object decodeMap(JSONObject jsonMap, Application application)
            throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<String> it = jsonMap.keys();
        while (it.hasNext()) {
            String key = it.next();
            map.put(key, decode(jsonMap.getJSONArray(key), application));
        }
        return map;
    }

    private static String[] decodeStringArray(JSONArray jsonArray)
            throws JSONException {
        int length = jsonArray.length();
        List<String> tokens = new ArrayList<String>(length);
        for (int i = 0; i < length; ++i) {
            tokens.add(jsonArray.getString(i));
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private static Object decodeArray(JSONArray jsonArray,
            Application application) throws JSONException {
        List list = decodeList(jsonArray, application);
        return list.toArray(new Object[list.size()]);
    }

    private static List<Object> decodeList(JSONArray jsonArray,
            Application application) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            // each entry always has two elements: type and value
            JSONArray entryArray = jsonArray.getJSONArray(i);
            list.add(decode(entryArray, application));
        }
        return list;
    }

    private static Set<Object> decodeSet(JSONArray jsonArray,
            Application application) throws JSONException {
        HashSet<Object> set = new HashSet<Object>();
        set.addAll(decodeList(jsonArray, application));
        return set;
    }

    /**
     * Encode a value to a JSON representation for transport from the server to
     * the client.
     * 
     * @param value
     *            value to convert
     * @param application
     *            mapper between connector ID and {@link Connector} objects
     * @return JSON representation of the value
     * @throws JSONException
     *             if encoding a value fails (e.g. NaN or infinite number)
     */
    public static JSONArray encode(Object value, Application application)
            throws JSONException {
        return encode(value, null, application);
    }

    public static JSONArray encode(Object value, Class<?> valueType,
            Application application) throws JSONException {

        if (null == value) {
            return combineTypeAndValue(JsonEncoder.VTYPE_NULL, JSONObject.NULL);
        }

        if (valueType == null) {
            valueType = value.getClass();
        }

        String transportType = getTransportType(valueType);
        if (value instanceof String[]) {
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
            return combineTypeAndValue(transportType, value);
        } else if (value instanceof Collection) {
            if (transportType == null) {
                throw new RuntimeException(
                        "Unable to serialize unsupported type: " + valueType);
            }
            Collection<?> collection = (Collection<?>) value;
            JSONArray jsonArray = encodeCollection(collection, application);

            return combineTypeAndValue(transportType, jsonArray);
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            JSONArray jsonArray = encodeArrayContents(array, application);
            return combineTypeAndValue(JsonEncoder.VTYPE_ARRAY, jsonArray);
        } else if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            JSONObject jsonMap = encodeMapContents(map, application);
            return combineTypeAndValue(JsonEncoder.VTYPE_MAP, jsonMap);
        } else if (value instanceof Connector) {
            Connector connector = (Connector) value;
            return combineTypeAndValue(JsonEncoder.VTYPE_CONNECTOR,
                    connector.getConnectorId());
        } else if (transportType != null) {
            return combineTypeAndValue(transportType, String.valueOf(value));
        } else {
            // Any object that we do not know how to encode we encode by looping
            // through fields
            return combineTypeAndValue(valueType.getCanonicalName(),
                    encodeObject(value, application));
        }
    }

    private static Object encodeObject(Object value, Application application)
            throws JSONException {
        JSONObject jsonMap = new JSONObject();

        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(
                    value.getClass()).getPropertyDescriptors()) {
                String fieldName = pd.getName();
                Class<?> fieldType = pd.getPropertyType();
                if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
                    continue;
                }
                Method getterMethod = pd.getReadMethod();
                Object fieldValue = getterMethod.invoke(value, (Object[]) null);
                jsonMap.put(fieldName,
                        encode(fieldValue, fieldType, application));
            }
        } catch (Exception e) {
            // TODO: Should exceptions be handled in a different way?
            throw new JSONException(e);
        }
        return jsonMap;
    }

    private static Object decodeObject(String type,
            JSONObject serializedObject, Application application)
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
                        decode(encodedObject, application));
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
            Application application) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object o : array) {
            // TODO handle object graph loops?
            jsonArray.put(encode(o, application));
        }
        return jsonArray;
    }

    private static JSONArray encodeCollection(Collection collection,
            Application application) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object o : collection) {
            // TODO handle object graph loops?
            jsonArray.put(encode(o, application));
        }
        return jsonArray;
    }

    private static JSONObject encodeMapContents(Map<String, Object> map,
            Application application) throws JSONException {
        JSONObject jsonMap = new JSONObject();
        for (String mapKey : map.keySet()) {
            // TODO handle object graph loops?
            Object mapValue = map.get(mapKey);
            jsonMap.put(mapKey, encode(mapValue, application));
        }
        return jsonMap;
    }

    private static JSONArray combineTypeAndValue(String type, Object value) {
        if (type == null) {
            throw new RuntimeException("Type for value " + value
                    + " cannot be null!");
        }
        JSONArray outerArray = new JSONArray();
        outerArray.put(type);
        outerArray.put(value);
        return outerArray;
    }

    /**
     * Gets the transport type for the value. Returns null if no transport type
     * can be found.
     * 
     * @param value
     * @return
     * @throws JSONException
     */
    private static String getTransportType(Object value) {
        if (null == value) {
            return JsonEncoder.VTYPE_NULL;
        }
        return getTransportType(value.getClass());
    }

    /**
     * Gets the transport type for the given class. Returns null if no transport
     * type can be found.
     * 
     * @param valueType
     *            The type that should be transported
     * @return
     * @throws JSONException
     */
    private static String getTransportType(Class<?> valueType) {
        return typeToTransportType.get(valueType);

    }

}
