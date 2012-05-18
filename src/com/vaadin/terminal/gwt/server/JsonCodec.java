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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import com.vaadin.ui.Component;

/**
 * Decoder for converting RPC parameters and other values from JSON in transfer
 * between the client and the server and vice versa.
 * 
 * @since 7.0
 */
public class JsonCodec implements Serializable {

    private static Map<Class<?>, String> typeToTransportType = new HashMap<Class<?>, String>();

    /**
     * Note! This does not contain primitives.
     * <p>
     */
    private static Map<String, Class<?>> transportTypeToType = new HashMap<String, Class<?>>();

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
        registerType(double.class, JsonEncoder.VTYPE_DOUBLE);
        registerType(Long.class, JsonEncoder.VTYPE_LONG);
        registerType(long.class, JsonEncoder.VTYPE_LONG);
        registerType(String[].class, JsonEncoder.VTYPE_STRINGARRAY);
        registerType(Object[].class, JsonEncoder.VTYPE_ARRAY);
        registerType(Map.class, JsonEncoder.VTYPE_MAP);
        registerType(HashMap.class, JsonEncoder.VTYPE_MAP);
        registerType(List.class, JsonEncoder.VTYPE_LIST);
        registerType(Set.class, JsonEncoder.VTYPE_SET);
    }

    private static void registerType(Class<?> type, String transportType) {
        typeToTransportType.put(type, transportType);
        if (!type.isPrimitive()) {
            transportTypeToType.put(transportType, type);
        }
    }

    public static boolean isInternalTransportType(String transportType) {
        return transportTypeToType.containsKey(transportType);
    }

    public static boolean isInternalType(Type type) {
        if (type instanceof Class && ((Class<?>) type).isPrimitive()) {
            // All primitive types are handled internally
            return true;
        }
        return typeToTransportType.containsKey(getClassForType(type));
    }

    private static Class<?> getClassForType(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) (((ParameterizedType) type).getRawType());
        } else {
            return (Class<?>) type;
        }
    }

    public static String getTransportType(JSONArray encodedValue)
            throws JSONException {
        return encodedValue.getString(0);
    }

    private static Class<?> getType(String transportType) {
        return transportTypeToType.get(transportType);
    }

    /**
     * Decodes the given value and type, restricted to using only internal
     * types.
     * 
     * @param valueAndType
     * @param application
     * @throws JSONException
     */
    @Deprecated
    public static Object decodeInternalType(JSONArray valueAndType,
            Application application) throws JSONException {
        String transportType = getTransportType(valueAndType);
        return decodeInternalType(getType(transportType), true, valueAndType,
                application);
    }

    public static Object decodeInternalOrCustomType(Type targetType,
            JSONArray valueAndType, Application application)
            throws JSONException {
        if (isInternalType(targetType)) {
            return decodeInternalType(targetType, false, valueAndType,
                    application);
        } else {
            return decodeCustomType(targetType, valueAndType, application);
        }
    }

    public static Object decodeCustomType(Type targetType,
            JSONArray valueAndType, Application application)
            throws JSONException {
        if (isInternalType(targetType)) {
            throw new JSONException("decodeCustomType cannot be used for "
                    + targetType + ", which is an internal type");
        }
        String transportType = getCustomTransportType(getClassForType(targetType));
        String encodedTransportType = valueAndType.getString(0);
        if (!transportTypesCompatible(encodedTransportType, transportType)) {
            throw new JSONException("Expected a value of type " + transportType
                    + ", received " + encodedTransportType);
        }

        // Try to decode object using fields
        Object value = valueAndType.get(1);
        if (value == JSONObject.NULL) {
            return null;
        } else {
            return decodeObject(targetType, (JSONObject) value, application);
        }
    }

    /**
     * Decodes a value that is of an internal type.
     * <p>
     * Ensures the encoded value is of the same type as target type.
     * </p>
     * <p>
     * Allows restricting collections so that they must be declared using
     * generics. If this is used then all objects in the collection are encoded
     * using the declared type. Otherwise only internal types are allowed in
     * collections.
     * </p>
     * 
     * @param targetType
     *            The type that should be returned by this method
     * @param valueAndType
     *            The encoded value and type array
     * @param application
     *            A reference to the application
     * @param enforceGenericsInCollections
     *            true if generics should be enforce, false to only allow
     *            internal types in collections
     * @return
     * @throws JSONException
     */
    public static Object decodeInternalType(Type targetType,
            boolean restrictToInternalTypes, JSONArray valueAndType,
            Application application) throws JSONException {
        String encodedTransportType = valueAndType.getString(0);
        if (!isInternalType(targetType)) {
            throw new JSONException("Type " + targetType
                    + " is not a supported internal type.");
        }
        String transportType = getInternalTransportType(targetType);
        if (!transportTypesCompatible(encodedTransportType, transportType)) {
            throw new JSONException("Expected a value of type " + targetType
                    + ", received " + getType(encodedTransportType));
        }

        Object encodedJsonValue = valueAndType.get(1);

        if (JsonEncoder.VTYPE_NULL.equals(encodedTransportType)) {
            return null;
        }
        // Collections
        if (JsonEncoder.VTYPE_LIST.equals(transportType)) {
            return decodeList(targetType, restrictToInternalTypes,
                    (JSONArray) encodedJsonValue, application);
        } else if (JsonEncoder.VTYPE_SET.equals(transportType)) {
            return decodeSet(targetType, restrictToInternalTypes,
                    (JSONArray) encodedJsonValue, application);
        } else if (JsonEncoder.VTYPE_MAP.equals(transportType)) {
            return decodeMap(targetType, restrictToInternalTypes,
                    (JSONObject) encodedJsonValue, application);
        }

        // Arrays
        if (JsonEncoder.VTYPE_ARRAY.equals(transportType)) {

            return decodeObjectArray(targetType, (JSONArray) encodedJsonValue,
                    application);

        } else if (JsonEncoder.VTYPE_STRINGARRAY.equals(transportType)) {
            return decodeStringArray((JSONArray) encodedJsonValue);
        }

        // Special Vaadin types

        String stringValue = String.valueOf(encodedJsonValue);

        if (JsonEncoder.VTYPE_CONNECTOR.equals(transportType)) {
            return application.getConnector(stringValue);
        }

        // Standard Java types

        if (JsonEncoder.VTYPE_STRING.equals(transportType)) {
            return stringValue;
        } else if (JsonEncoder.VTYPE_INTEGER.equals(transportType)) {
            return Integer.valueOf(stringValue);
        } else if (JsonEncoder.VTYPE_LONG.equals(transportType)) {
            return Long.valueOf(stringValue);
        } else if (JsonEncoder.VTYPE_FLOAT.equals(transportType)) {
            return Float.valueOf(stringValue);
        } else if (JsonEncoder.VTYPE_DOUBLE.equals(transportType)) {
            return Double.valueOf(stringValue);
        } else if (JsonEncoder.VTYPE_BOOLEAN.equals(transportType)) {
            return Boolean.valueOf(stringValue);
        }

        throw new JSONException("Unknown type " + transportType);
    }

    private static boolean transportTypesCompatible(
            String encodedTransportType, String transportType) {
        if (encodedTransportType == null) {
            return false;
        }
        if (encodedTransportType.equals(transportType)) {
            return true;
        }
        if (encodedTransportType.equals(JsonEncoder.VTYPE_NULL)) {
            return true;
        }

        return false;
    }

    private static Map<Object, Object> decodeMap(Type targetType,
            boolean restrictToInternalTypes, JSONObject jsonMap,
            Application application) throws JSONException {
        HashMap<Object, Object> map = new HashMap<Object, Object>();

        Iterator<String> it = jsonMap.keys();
        while (it.hasNext()) {
            String key = it.next();
            JSONArray encodedKey = new JSONArray(key);
            JSONArray encodedValue = jsonMap.getJSONArray(key);

            Object decodedKey = decodeParametrizedType(targetType,
                    restrictToInternalTypes, 0, encodedKey, application);
            Object decodedValue = decodeParametrizedType(targetType,
                    restrictToInternalTypes, 1, encodedValue, application);
            map.put(decodedKey, decodedValue);
        }
        return map;
    }

    /**
     * @param targetType
     * @param restrictToInternalTypes
     * @param typeIndex
     *            The index of a generic type to use to define the child type
     *            that should be decoded
     * @param encodedValueAndType
     * @param application
     * @return
     * @throws JSONException
     */
    private static Object decodeParametrizedType(Type targetType,
            boolean restrictToInternalTypes, int typeIndex,
            JSONArray encodedValueAndType, Application application)
            throws JSONException {
        if (!restrictToInternalTypes && targetType instanceof ParameterizedType) {
            Type childType = ((ParameterizedType) targetType)
                    .getActualTypeArguments()[typeIndex];
            // Only decode the given type
            return decodeInternalOrCustomType(childType, encodedValueAndType,
                    application);
        } else {
            // Only internal types when not enforcing a given type to avoid
            // security issues
            return decodeInternalType(encodedValueAndType, application);
        }
    }

    private static Object decodeEnum(Class<? extends Enum> cls, JSONObject value) {
        String enumIdentifier = String.valueOf(value);
        return Enum.valueOf(cls, enumIdentifier);
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

    private static Object[] decodeObjectArray(Type targetType,
            JSONArray jsonArray, Application application) throws JSONException {
        List list = decodeList(List.class, true, jsonArray, application);
        return list.toArray(new Object[list.size()]);
    }

    private static List<Object> decodeList(Type targetType,
            boolean restrictToInternalTypes, JSONArray jsonArray,
            Application application) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            // each entry always has two elements: type and value
            JSONArray encodedValueAndType = jsonArray.getJSONArray(i);
            Object decodedChild = decodeParametrizedType(targetType,
                    restrictToInternalTypes, 0, encodedValueAndType,
                    application);
            list.add(decodedChild);
        }
        return list;
    }

    private static Set<Object> decodeSet(Type targetType,
            boolean restrictToInternalTypes, JSONArray jsonArray,
            Application application) throws JSONException {
        HashSet<Object> set = new HashSet<Object>();
        set.addAll(decodeList(List.class, restrictToInternalTypes, jsonArray,
                application));
        return set;
    }

    /**
     * Returns the name that should be used as field name in the JSON. We strip
     * "set" from the setter, keeping the result - this is easy to do on both
     * server and client, avoiding some issues with cASE. E.g setZIndex()
     * becomes "ZIndex". Also ensures that both getter and setter are present,
     * returning null otherwise.
     * 
     * @param pd
     * @return the name to be used or null if both getter and setter are not
     *         found.
     */
    static String getTransportFieldName(PropertyDescriptor pd) {
        if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
            return null;
        }
        return pd.getWriteMethod().getName().substring(3);
    }

    private static Object decodeObject(Type targetType,
            JSONObject serializedObject, Application application)
            throws JSONException {

        Class<?> targetClass = getClassForType(targetType);
        if (Enum.class.isAssignableFrom(targetClass)) {
            return decodeEnum(targetClass.asSubclass(Enum.class),
                    serializedObject);
        }

        try {
            Object decodedObject = targetClass.newInstance();
            for (PropertyDescriptor pd : Introspector.getBeanInfo(targetClass)
                    .getPropertyDescriptors()) {

                String fieldName = getTransportFieldName(pd);
                if (fieldName == null) {
                    continue;
                }
                JSONArray encodedFieldValue = serializedObject
                        .getJSONArray(fieldName);
                Type fieldType = pd.getReadMethod().getGenericReturnType();
                Object decodedFieldValue = decodeInternalOrCustomType(
                        fieldType, encodedFieldValue, application);

                pd.getWriteMethod().invoke(decodedObject, decodedFieldValue);
            }

            return decodedObject;
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

    @Deprecated
    private static JSONArray encode(Object value, Application application)
            throws JSONException {
        return encode(value, null, null, application);
    }

    public static JSONArray encode(Object value, Object referenceValue,
            Type valueType, Application application) throws JSONException {

        if (null == value) {
            return encodeNull();
        }

        if (valueType == null) {
            valueType = value.getClass();
        }

        String internalTransportType = getInternalTransportType(valueType);
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
            return combineTypeAndValue(internalTransportType, value);
        } else if (value instanceof Collection) {
            if (internalTransportType == null) {
                throw new RuntimeException(
                        "Unable to serialize unsupported type: " + valueType);
            }
            Collection<?> collection = (Collection<?>) value;
            JSONArray jsonArray = encodeCollection(valueType, collection,
                    application);

            return combineTypeAndValue(internalTransportType, jsonArray);
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            JSONArray jsonArray = encodeArrayContents(array, application);
            return combineTypeAndValue(JsonEncoder.VTYPE_ARRAY, jsonArray);
        } else if (value instanceof Map) {
            JSONObject jsonMap = encodeMap(valueType, (Map<?, ?>) value,
                    application);
            return combineTypeAndValue(JsonEncoder.VTYPE_MAP, jsonMap);
        } else if (value instanceof Connector) {
            Connector connector = (Connector) value;
            if (value instanceof Component
                    && !(AbstractCommunicationManager
                            .isVisible((Component) value))) {
                return encodeNull();
            }
            return combineTypeAndValue(JsonEncoder.VTYPE_CONNECTOR,
                    connector.getConnectorId());
        } else if (internalTransportType != null) {
            return combineTypeAndValue(internalTransportType,
                    String.valueOf(value));
        } else if (value instanceof Enum) {
            return encodeEnum((Enum) value, application);
        } else {
            // Any object that we do not know how to encode we encode by looping
            // through fields
            return combineTypeAndValue(
                    getCustomTransportType((Class<?>) valueType),
                    encodeObject(value, referenceValue, application));
        }
    }

    private static JSONArray encodeNull() {
        return combineTypeAndValue(JsonEncoder.VTYPE_NULL, JSONObject.NULL);
    }

    private static Object encodeObject(Object value, Object referenceValue,
            Application application) throws JSONException {
        JSONObject jsonMap = new JSONObject();

        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(
                    value.getClass()).getPropertyDescriptors()) {
                String fieldName = getTransportFieldName(pd);
                if (fieldName == null) {
                    continue;
                }
                Method getterMethod = pd.getReadMethod();
                // We can't use PropertyDescriptor.getPropertyType() as it does
                // not support generics
                Type fieldType = getterMethod.getGenericReturnType();
                Object fieldValue = getterMethod.invoke(value, (Object[]) null);
                boolean equals = false;
                Object referenceFieldValue = null;
                if (referenceValue != null) {
                    referenceFieldValue = getterMethod.invoke(referenceValue,
                            (Object[]) null);
                    equals = equals(fieldValue, referenceFieldValue);
                }
                if (!equals) {
                    jsonMap.put(
                            fieldName,
                            encode(fieldValue, referenceFieldValue, fieldType,
                                    application));
                    // } else {
                    // System.out.println("Skipping field " + fieldName
                    // + " of type " + fieldType.getName()
                    // + " for object " + value.getClass().getName()
                    // + " as " + fieldValue + "==" + referenceFieldValue);
                }
            }
        } catch (Exception e) {
            // TODO: Should exceptions be handled in a different way?
            throw new JSONException(e);
        }
        return jsonMap;
    }

    /**
     * Compares the value with the reference. If they match, returns true.
     * 
     * @param fieldValue
     * @param referenceValue
     * @return
     */
    private static boolean equals(Object fieldValue, Object referenceValue) {
        if (fieldValue == null) {
            return referenceValue == null;
        }

        if (fieldValue.equals(referenceValue)) {
            return true;
        }

        return false;
    }

    private static JSONArray encodeEnum(Enum e, Application application)
            throws JSONException {
        String enumIdentifier = e.name();
        return combineTypeAndValue(e.getClass().getName(), enumIdentifier);
    }

    private static JSONArray encodeArrayContents(Object[] array,
            Application application) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object o : array) {
            jsonArray.put(encode(o, null, null, application));
        }
        return jsonArray;
    }

    private static JSONArray encodeCollection(Type targetType,
            Collection collection, Application application)
            throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object o : collection) {
            jsonArray.put(encodeChild(targetType, 0, o, application));
        }
        return jsonArray;
    }

    private static JSONArray encodeChild(Type targetType, int typeIndex,
            Object o, Application application) throws JSONException {
        if (targetType instanceof ParameterizedType) {
            Type childType = ((ParameterizedType) targetType)
                    .getActualTypeArguments()[typeIndex];
            // Encode using the given type
            return encode(o, null, childType, application);
        } else {
            return encode(o, application);
        }
    }

    private static JSONObject encodeMap(Type mapType, Map<?, ?> map,
            Application application) throws JSONException {
        Type keyType, valueType;

        if (mapType instanceof ParameterizedType) {
            keyType = ((ParameterizedType) mapType).getActualTypeArguments()[0];
            valueType = ((ParameterizedType) mapType).getActualTypeArguments()[1];
        } else {
            throw new JSONException("Map is missing generics");
        }

        JSONObject jsonMap = new JSONObject();
        for (Object mapKey : map.keySet()) {
            Object mapValue = map.get(mapKey);
            JSONArray encodedKey = encode(mapKey, null, keyType, application);
            JSONArray encodedValue = encode(mapValue, null, valueType,
                    application);
            jsonMap.put(encodedKey.toString(), encodedValue);
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
     * Gets the transport type for the given class. Returns null if no transport
     * type can be found.
     * 
     * @param valueType
     *            The type that should be transported
     * @return
     * @throws JSONException
     */
    private static String getInternalTransportType(Type valueType) {
        return typeToTransportType.get(getClassForType(valueType));
    }

    private static String getCustomTransportType(Class<?> targetType) {
        return targetType.getName();
    }

}
