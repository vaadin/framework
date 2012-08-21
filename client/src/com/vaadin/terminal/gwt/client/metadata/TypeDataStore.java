/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.metadata;

import java.util.HashMap;
import java.util.Map;

public class TypeDataStore {
    private static final String CONSTRUCTOR_NAME = "!new";

    private final Map<String, Class<?>> identifiers = new HashMap<String, Class<?>>();

    private final Map<Method, Type> returnTypes = new HashMap<Method, Type>();
    private final Map<Method, Invoker> invokers = new HashMap<Method, Invoker>();
    private final Map<Method, Type[]> paramTypes = new HashMap<Method, Type[]>();

    private final Map<Property, Invoker> getters = new HashMap<Property, Invoker>();
    private final Map<Property, String> delegateToWidget = new HashMap<Property, String>();

    public static TypeDataStore get() {
        return ConnectorBundleLoader.get().getTypeDataStore();
    }

    public void setClass(String identifier, Class<?> type) {
        identifiers.put(identifier, type);
    }

    public static Class<?> getClass(String identifier) throws NoDataException {
        Class<?> class1 = get().identifiers.get(identifier);
        if (class1 == null) {
            throw new NoDataException("There is not class for identifier "
                    + identifier);
        }
        return class1;
    }

    public static Type getType(Class<?> clazz) {
        return new Type(clazz);
    }

    public static Type getReturnType(Method method) throws NoDataException {
        Type type = get().returnTypes.get(method);
        if (type == null) {
            throw new NoDataException("There is return type for "
                    + method.getSignature());
        }
        return type;
    }

    public static Invoker getInvoker(Method method) throws NoDataException {
        Invoker invoker = get().invokers.get(method);
        if (invoker == null) {
            throw new NoDataException("There is invoker for "
                    + method.getSignature());
        }
        return invoker;
    }

    public static Invoker getConstructor(Type type) throws NoDataException {
        Invoker invoker = get().invokers
                .get(new Method(type, CONSTRUCTOR_NAME));
        if (invoker == null) {
            throw new NoDataException("There is constructor for "
                    + type.getSignature());
        }
        return invoker;
    }

    public static Invoker getGetter(Property property) throws NoDataException {
        Invoker getter = get().getters.get(property);
        if (getter == null) {
            throw new NoDataException("There is getter for "
                    + property.getSignature());
        }

        return getter;
    }

    public static String getDelegateToWidget(Property property) {
        return get().delegateToWidget.get(property);
    }

    public void setReturnType(Class<?> type, String methodName, Type returnType) {
        returnTypes.put(new Method(getType(type), methodName), returnType);
    }

    public void setConstructor(Class<?> type, Invoker constructor) {
        setInvoker(type, CONSTRUCTOR_NAME, constructor);
    }

    public void setInvoker(Class<?> type, String methodName, Invoker invoker) {
        invokers.put(new Method(getType(type), methodName), invoker);
    }

    public static Type[] getParamTypes(Method method) {
        return get().paramTypes.get(method);
    }

    public void setParamTypes(Class<?> type, String methodName,
            Type[] paramTypes) {
        this.paramTypes.put(new Method(getType(type), methodName), paramTypes);
    }

    public static boolean hasIdentifier(String identifier) {
        return get().identifiers.containsKey(identifier);
    }
}
