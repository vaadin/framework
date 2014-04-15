/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.metadata;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.vaadin.client.FastStringMap;
import com.vaadin.client.FastStringSet;
import com.vaadin.client.JsArrayObject;
import com.vaadin.client.communication.JSONSerializer;

public class TypeDataStore {
    private static final String CONSTRUCTOR_NAME = "!new";

    private final FastStringMap<Class<?>> identifiers = FastStringMap.create();

    private final FastStringMap<Invoker> serializerFactories = FastStringMap
            .create();
    private final FastStringMap<ProxyHandler> proxyHandlers = FastStringMap
            .create();
    private final FastStringMap<JsArrayObject<Property>> properties = FastStringMap
            .create();
    private final FastStringMap<JsArrayString> delegateToWidgetProperties = FastStringMap
            .create();

    private final FastStringSet delayedMethods = FastStringSet.create();
    private final FastStringSet lastOnlyMethods = FastStringSet.create();

    private final FastStringMap<Type> returnTypes = FastStringMap.create();
    private final FastStringMap<Invoker> invokers = FastStringMap.create();
    private final FastStringMap<Type[]> paramTypes = FastStringMap.create();

    private final FastStringMap<Type> propertyTypes = FastStringMap.create();
    private final FastStringMap<Invoker> setters = FastStringMap.create();
    private final FastStringMap<Invoker> getters = FastStringMap.create();
    private final FastStringMap<String> delegateToWidget = FastStringMap
            .create();

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
        Type type = get().returnTypes.get(method.getSignature());
        if (type == null) {
            throw new NoDataException("There is no return type for "
                    + method.getSignature());
        }
        return type;
    }

    public static Invoker getInvoker(Method method) throws NoDataException {
        Invoker invoker = get().invokers.get(method.getSignature());
        if (invoker == null) {
            throw new NoDataException("There is no invoker for "
                    + method.getSignature());
        }
        return invoker;
    }

    public static Invoker getConstructor(Type type) throws NoDataException {
        Invoker invoker = get().invokers.get(new Method(type, CONSTRUCTOR_NAME)
                .getSignature());
        if (invoker == null) {
            throw new NoDataException("There is no constructor for "
                    + type.getSignature());
        }
        return invoker;
    }

    public static Invoker getGetter(Property property) throws NoDataException {
        Invoker getter = get().getters.get(property.getSignature());
        if (getter == null) {
            throw new NoDataException("There is no getter for "
                    + property.getSignature());
        }

        return getter;
    }

    public void setGetter(Class<?> clazz, String propertyName, Invoker invoker) {
        getters.put(new Property(getType(clazz), propertyName).getSignature(),
                invoker);
    }

    public static String getDelegateToWidget(Property property) {
        return get().delegateToWidget.get(property.getSignature());
    }

    public static JsArrayString getDelegateToWidgetProperites(Type type) {
        return get().delegateToWidgetProperties.get(type.getSignature());
    }

    public void setDelegateToWidget(Class<?> clazz, String propertyName,
            String delegateValue) {
        Type type = getType(clazz);
        delegateToWidget.put(new Property(type, propertyName).getSignature(),
                delegateValue);
        JsArrayString typeProperties = delegateToWidgetProperties.get(type
                .getSignature());
        if (typeProperties == null) {
            typeProperties = JavaScriptObject.createArray().cast();
            delegateToWidgetProperties.put(type.getSignature(), typeProperties);
        }
        typeProperties.push(propertyName);
    }

    public void setReturnType(Class<?> type, String methodName, Type returnType) {
        returnTypes.put(new Method(getType(type), methodName).getSignature(),
                returnType);
    }

    public void setConstructor(Class<?> type, Invoker constructor) {
        setInvoker(type, CONSTRUCTOR_NAME, constructor);
    }

    public void setInvoker(Class<?> type, String methodName, Invoker invoker) {
        invokers.put(new Method(getType(type), methodName).getSignature(),
                invoker);
    }

    public static Type[] getParamTypes(Method method) throws NoDataException {
        Type[] types = get().paramTypes.get(method.getSignature());
        if (types == null) {
            throw new NoDataException("There are no parameter type data for "
                    + method.getSignature());
        }
        return types;
    }

    public void setParamTypes(Class<?> type, String methodName,
            Type[] paramTypes) {
        this.paramTypes.put(
                new Method(getType(type), methodName).getSignature(),
                paramTypes);
    }

    public static boolean hasIdentifier(String identifier) {
        return get().identifiers.containsKey(identifier);
    }

    public static ProxyHandler getProxyHandler(Type type)
            throws NoDataException {
        ProxyHandler proxyHandler = get().proxyHandlers
                .get(type.getSignature());
        if (proxyHandler == null) {
            throw new NoDataException("No proxy handler for "
                    + type.getSignature());
        }
        return proxyHandler;
    }

    public void setProxyHandler(Class<?> type, ProxyHandler proxyHandler) {
        proxyHandlers.put(getType(type).getSignature(), proxyHandler);
    }

    public static boolean isDelayed(Method method) {
        return get().delayedMethods.contains(method.getSignature());
    }

    public void setDelayed(Class<?> type, String methodName) {
        delayedMethods.add(getType(type).getMethod(methodName).getSignature());
    }

    public static boolean isLastOnly(Method method) {
        return get().lastOnlyMethods.contains(method.getSignature());
    }

    public void setLastOnly(Class<?> clazz, String methodName) {
        lastOnlyMethods
                .add(getType(clazz).getMethod(methodName).getSignature());
    }

    /**
     * @param type
     * @return
     * @throws NoDataException
     * 
     * @deprecated As of 7.0.1, use {@link #getPropertiesAsArray(Type)} instead
     *             for improved performance
     */
    @Deprecated
    public static Collection<Property> getProperties(Type type)
            throws NoDataException {
        JsArrayObject<Property> propertiesArray = getPropertiesAsArray(type);
        int size = propertiesArray.size();
        ArrayList<Property> properties = new ArrayList<Property>(size);
        for (int i = 0; i < size; i++) {
            properties.add(propertiesArray.get(i));
        }

        return properties;
    }

    public static JsArrayObject<Property> getPropertiesAsArray(Type type)
            throws NoDataException {
        JsArrayObject<Property> properties = get().properties.get(type
                .getSignature());
        if (properties == null) {
            throw new NoDataException("No property list for "
                    + type.getSignature());
        }
        return properties;
    }

    public void setProperties(Class<?> clazz, String[] propertyNames) {
        JsArrayObject<Property> properties = JavaScriptObject.createArray()
                .cast();
        Type type = getType(clazz);
        for (String name : propertyNames) {
            properties.add(new Property(type, name));
        }
        this.properties.put(type.getSignature(), properties);
    }

    public static Type getType(Property property) throws NoDataException {
        Type type = get().propertyTypes.get(property.getSignature());
        if (type == null) {
            throw new NoDataException("No return type for "
                    + property.getSignature());
        }
        return type;
    }

    public void setPropertyType(Class<?> clazz, String propertName, Type type) {
        propertyTypes.put(
                new Property(getType(clazz), propertName).getSignature(), type);
    }

    public static Invoker getSetter(Property property) throws NoDataException {
        Invoker setter = get().setters.get(property.getSignature());
        if (setter == null) {
            throw new NoDataException("No setter for "
                    + property.getSignature());
        }
        return setter;
    }

    public void setSetter(Class<?> clazz, String propertyName, Invoker setter) {
        setters.put(new Property(getType(clazz), propertyName).getSignature(),
                setter);
    }

    public void setSerializerFactory(Class<?> clazz, Invoker factory) {
        serializerFactories.put(getType(clazz).getSignature(), factory);
    }

    public static JSONSerializer<?> findSerializer(Type type) {
        Invoker factoryCreator = get().serializerFactories.get(type
                .getSignature());
        if (factoryCreator == null) {
            return null;
        }
        return (JSONSerializer<?>) factoryCreator.invoke(null);
    }

    public static boolean hasProperties(Type type) {
        return get().properties.containsKey(type.getSignature());
    }
}
