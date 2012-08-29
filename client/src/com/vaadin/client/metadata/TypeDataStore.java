/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.client.metadata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.client.communication.JSONSerializer;

public class TypeDataStore {
    private static final String CONSTRUCTOR_NAME = "!new";

    private final Map<String, Class<?>> identifiers = new HashMap<String, Class<?>>();

    private final Map<Type, Invoker> serializerFactories = new HashMap<Type, Invoker>();
    private final Map<Type, ProxyHandler> proxyHandlers = new HashMap<Type, ProxyHandler>();
    private final Map<Type, Collection<Property>> properties = new HashMap<Type, Collection<Property>>();

    private final Set<Method> delayedMethods = new HashSet<Method>();
    private final Set<Method> lastonlyMethods = new HashSet<Method>();

    private final Map<Method, Type> returnTypes = new HashMap<Method, Type>();
    private final Map<Method, Invoker> invokers = new HashMap<Method, Invoker>();
    private final Map<Method, Type[]> paramTypes = new HashMap<Method, Type[]>();

    private final Map<Property, Type> propertyTypes = new HashMap<Property, Type>();
    private final Map<Property, Invoker> setters = new HashMap<Property, Invoker>();
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

    public void setGetter(Class<?> clazz, String propertyName, Invoker invoker) {
        getters.put(new Property(getType(clazz), propertyName), invoker);
    }

    public static String getDelegateToWidget(Property property) {
        return get().delegateToWidget.get(property);
    }

    public void setDelegateToWidget(Class<?> clazz, String propertyName,
            String delegateValue) {
        delegateToWidget.put(new Property(getType(clazz), propertyName),
                delegateValue);
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

    public static Type[] getParamTypes(Method method) throws NoDataException {
        Type[] types = get().paramTypes.get(method);
        if (types == null) {
            throw new NoDataException("There are no parameter type data for "
                    + method.getSignature());
        }
        return types;
    }

    public void setParamTypes(Class<?> type, String methodName,
            Type[] paramTypes) {
        this.paramTypes.put(new Method(getType(type), methodName), paramTypes);
    }

    public static boolean hasIdentifier(String identifier) {
        return get().identifiers.containsKey(identifier);
    }

    public static ProxyHandler getProxyHandler(Type type)
            throws NoDataException {
        ProxyHandler proxyHandler = get().proxyHandlers.get(type);
        if (proxyHandler == null) {
            throw new NoDataException("No proxy handler for "
                    + type.getSignature());
        }
        return proxyHandler;
    }

    public void setProxyHandler(Class<?> type, ProxyHandler proxyHandler) {
        proxyHandlers.put(getType(type), proxyHandler);
    }

    public static boolean isDelayed(Method method) {
        return get().delayedMethods.contains(method);
    }

    public void setDelayed(Class<?> type, String methodName) {
        delayedMethods.add(getType(type).getMethod(methodName));
    }

    public static boolean isLastonly(Method method) {
        return get().lastonlyMethods.contains(method);
    }

    public void setLastonly(Class<?> clazz, String methodName) {
        lastonlyMethods.add(getType(clazz).getMethod(methodName));
    }

    public static Collection<Property> getProperties(Type type)
            throws NoDataException {
        Collection<Property> properties = get().properties.get(type);
        if (properties == null) {
            throw new NoDataException("No property list for "
                    + type.getSignature());
        }
        return properties;
    }

    public void setProperties(Class<?> clazz, String[] propertyNames) {
        Set<Property> properties = new HashSet<Property>();
        Type type = getType(clazz);
        for (String name : propertyNames) {
            properties.add(new Property(type, name));
        }
        this.properties.put(type, Collections.unmodifiableSet(properties));
    }

    public static Type getType(Property property) throws NoDataException {
        Type type = get().propertyTypes.get(property);
        if (type == null) {
            throw new NoDataException("No return type for "
                    + property.getSignature());
        }
        return type;
    }

    public void setPropertyType(Class<?> clazz, String propertName, Type type) {
        propertyTypes.put(new Property(getType(clazz), propertName), type);
    }

    public static Invoker getSetter(Property property) throws NoDataException {
        Invoker setter = get().setters.get(property);
        if (setter == null) {
            throw new NoDataException("No setter for "
                    + property.getSignature());
        }
        return setter;
    }

    public void setSetter(Class<?> clazz, String propertyName, Invoker setter) {
        setters.put(new Property(getType(clazz), propertyName), setter);
    }

    public void setSerializerFactory(Class<?> clazz, Invoker factory) {
        serializerFactories.put(getType(clazz), factory);
    }

    public static JSONSerializer<?> findSerializer(Type type) {
        Invoker factoryCreator = get().serializerFactories.get(type);
        if (factoryCreator == null) {
            return null;
        }
        return (JSONSerializer<?>) factoryCreator.invoke(null);
    }

    public static boolean hasProperties(Type type) {
        return get().properties.containsKey(type);
    }
}
