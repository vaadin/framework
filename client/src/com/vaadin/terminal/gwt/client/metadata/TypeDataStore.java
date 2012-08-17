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

    private final Map<Property, Invoker> getters = new HashMap<Property, Invoker>();
    private final Map<Property, String> delegateToWidget = new HashMap<Property, String>();

    public static TypeDataStore get() {
        return ConnectorBundleLoader.get().getTypeDataStore();
    }

    public void setClass(String identifier, Class<?> type) {
        identifiers.put(identifier, type);
    }

    public static Class<?> getClass(String identifier) {
        return get().identifiers.get(identifier);
    }

    public static Type getType(Class<?> clazz) {
        return new Type(clazz);
    }

    public static Type getReturnType(Method method) {
        return get().returnTypes.get(method);
    }

    public static Invoker getInvoker(Method method) {
        return get().invokers.get(method);
    }

    public static Invoker getConstructor(Type type) {
        return get().invokers.get(new Method(type, CONSTRUCTOR_NAME));
    }

    public static Invoker getGetter(Property property) {
        return get().getters.get(property);
    }

    public static String getDelegateToWidget(Property property) {
        return get().delegateToWidget.get(property);
    }

    public void setReturnType(Class<?> type, String methodName, Type returnType) {
        returnTypes.put(new Method(getType(type), methodName), returnType);
    }

    public void setConstructor(Class<?> type, Invoker constructor) {
        invokers.put(new Method(getType(type), CONSTRUCTOR_NAME), constructor);
    }
}
