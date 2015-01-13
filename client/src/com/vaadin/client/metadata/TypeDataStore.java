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
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.JSONSerializer;
import com.vaadin.shared.annotations.NoLayout;

public class TypeDataStore {
    public static enum MethodAttribute {
        DELAYED, LAST_ONLY, NO_LAYOUT, NO_LOADING_INDICATOR;
    }

    private static final String CONSTRUCTOR_NAME = "!new";

    private final FastStringMap<Class<?>> identifiers = FastStringMap.create();

    private final FastStringMap<Invoker> serializerFactories = FastStringMap
            .create();
    private final FastStringMap<ProxyHandler> proxyHandlers = FastStringMap
            .create();
    private final FastStringMap<JsArrayString> delegateToWidgetProperties = FastStringMap
            .create();
    private final FastStringMap<Type> presentationTypes = FastStringMap
            .create();

    /**
     * Maps connector class -> state property name -> hander method data
     */
    private final FastStringMap<FastStringMap<JsArrayObject<OnStateChangeMethod>>> onStateChangeMethods = FastStringMap
            .create();

    private final FastStringMap<FastStringSet> methodAttributes = FastStringMap
            .create();

    private final FastStringMap<Type> returnTypes = FastStringMap.create();
    private final FastStringMap<Invoker> invokers = FastStringMap.create();
    private final FastStringMap<Type[]> paramTypes = FastStringMap.create();

    private final FastStringMap<String> delegateToWidget = FastStringMap
            .create();

    private final JavaScriptObject jsTypeData = JavaScriptObject.createObject();

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

    // this is a very inefficient implementation for getting all the identifiers
    // for a class
    public FastStringSet findIdentifiersFor(Class<?> type) {
        FastStringSet result = FastStringSet.create();

        JsArrayString keys = identifiers.getKeys();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.get(i);
            if (identifiers.get(key) == type) {
                result.add(key);
            }
        }

        return result;
    }

    public static Type getType(Class<?> clazz) {
        return new Type(clazz);
    }

    public static Type getReturnType(Method method) throws NoDataException {
        Type type = get().returnTypes.get(method.getLookupKey());
        if (type == null) {
            throw new NoDataException("There is no return type for "
                    + method.getSignature());
        }
        return type;
    }

    public static Invoker getInvoker(Method method) throws NoDataException {
        Invoker invoker = get().invokers.get(method.getLookupKey());
        if (invoker == null) {
            throw new NoDataException("There is no invoker for "
                    + method.getSignature());
        }
        return invoker;
    }

    public static Invoker getConstructor(Type type) throws NoDataException {
        Invoker invoker = get().invokers.get(new Method(type, CONSTRUCTOR_NAME)
                .getLookupKey());
        if (invoker == null) {
            throw new NoDataException("There is no constructor for "
                    + type.getSignature());
        }
        return invoker;
    }

    public static Object getValue(Property property, Object target)
            throws NoDataException {
        return getJsPropertyValue(get().jsTypeData, property.getBeanType()
                .getBaseTypeName(), property.getName(), target);
    }

    public static String getDelegateToWidget(Property property) {
        return get().delegateToWidget.get(property.getLookupKey());
    }

    public static JsArrayString getDelegateToWidgetProperites(Type type) {
        return get().delegateToWidgetProperties.get(type.getBaseTypeName());
    }

    public static Type getPresentationType(Class<?> type) {
        return get().presentationTypes.get(getType(type).getBaseTypeName());
    }

    public void setDelegateToWidget(Class<?> clazz, String propertyName,
            String delegateValue) {
        Type type = getType(clazz);
        delegateToWidget.put(new Property(type, propertyName).getLookupKey(),
                delegateValue);
        JsArrayString typeProperties = delegateToWidgetProperties.get(type
                .getBaseTypeName());
        if (typeProperties == null) {
            typeProperties = JavaScriptObject.createArray().cast();
            delegateToWidgetProperties.put(type.getBaseTypeName(),
                    typeProperties);
        }
        typeProperties.push(propertyName);
    }

    public void setPresentationType(Class<?> type, Class<?> presentationType) {
        presentationTypes.put(getType(type).getBaseTypeName(),
                getType(presentationType));
    }

    public void setReturnType(Class<?> type, String methodName, Type returnType) {
        returnTypes.put(new Method(getType(type), methodName).getLookupKey(),
                returnType);
    }

    public void setConstructor(Class<?> type, Invoker constructor) {
        setInvoker(type, CONSTRUCTOR_NAME, constructor);
    }

    public void setInvoker(Class<?> type, String methodName, Invoker invoker) {
        invokers.put(new Method(getType(type), methodName).getLookupKey(),
                invoker);
    }

    public static Type[] getParamTypes(Method method) throws NoDataException {
        Type[] types = get().paramTypes.get(method.getLookupKey());
        if (types == null) {
            throw new NoDataException("There are no parameter type data for "
                    + method.getSignature());
        }
        return types;
    }

    public void setParamTypes(Class<?> type, String methodName,
            Type[] paramTypes) {
        this.paramTypes.put(
                new Method(getType(type), methodName).getLookupKey(),
                paramTypes);
    }

    public static boolean hasIdentifier(String identifier) {
        return get().identifiers.containsKey(identifier);
    }

    public static ProxyHandler getProxyHandler(Type type)
            throws NoDataException {
        ProxyHandler proxyHandler = get().proxyHandlers.get(type
                .getBaseTypeName());
        if (proxyHandler == null) {
            throw new NoDataException("No proxy handler for "
                    + type.getSignature());
        }
        return proxyHandler;
    }

    public void setProxyHandler(Class<?> type, ProxyHandler proxyHandler) {
        proxyHandlers.put(getType(type).getBaseTypeName(), proxyHandler);
    }

    public static boolean isDelayed(Method method) {
        return hasMethodAttribute(method, MethodAttribute.DELAYED);
    }

    public static boolean isNoLoadingIndicator(Method method) {
        return hasMethodAttribute(method, MethodAttribute.NO_LOADING_INDICATOR);
    }

    private static boolean hasMethodAttribute(Method method,
            MethodAttribute attribute) {
        FastStringSet attributes = get().methodAttributes.get(method
                .getLookupKey());
        return attributes != null && attributes.contains(attribute.name());
    }

    public void setMethodAttribute(Class<?> type, String methodName,
            MethodAttribute attribute) {
        String key = getType(type).getMethod(methodName).getLookupKey();
        FastStringSet attributes = methodAttributes.get(key);
        if (attributes == null) {
            attributes = FastStringSet.create();
            methodAttributes.put(key, attributes);
        }
        attributes.add(attribute.name());
    }

    public static boolean isLastOnly(Method method) {
        return hasMethodAttribute(method, MethodAttribute.LAST_ONLY);
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
        JsArrayString names = getJsPropertyNames(get().jsTypeData,
                type.getBaseTypeName());

        // Create Property instances for each property name
        JsArrayObject<Property> properties = JavaScriptObject.createArray()
                .cast();
        for (int i = 0; i < names.length(); i++) {
            properties.add(new Property(type, names.get(i)));
        }

        return properties;
    }

    public static Type getType(Property property) throws NoDataException {
        return getJsPropertyType(get().jsTypeData, property.getBeanType()
                .getBaseTypeName(), property.getName());
    }

    public void setPropertyType(Class<?> clazz, String propertyName, Type type) {
        setJsPropertyType(jsTypeData, clazz.getName(), propertyName, type);
    }

    public static void setValue(Property property, Object target, Object value) {
        setJsPropertyValue(get().jsTypeData, property.getBeanType()
                .getBaseTypeName(), property.getName(), target, value);
    }

    public void setSerializerFactory(Class<?> clazz, Invoker factory) {
        serializerFactories.put(getType(clazz).getBaseTypeName(), factory);
    }

    public static JSONSerializer<?> findSerializer(Type type) {
        Invoker factoryCreator = get().serializerFactories.get(type
                .getBaseTypeName());
        if (factoryCreator == null) {
            return null;
        }
        return (JSONSerializer<?>) factoryCreator.invoke(null);
    }

    public static boolean hasProperties(Type type) {
        return hasJsProperties(get().jsTypeData, type.getBaseTypeName());
    }

    public void setSuperClass(Class<?> baseClass, Class<?> superClass) {
        String superClassName = superClass == null ? null : superClass
                .getName();
        setSuperClass(jsTypeData, baseClass.getName(), superClassName);
    }

    public void setPropertyData(Class<?> type, String propertyName,
            JavaScriptObject propertyData) {
        setPropertyData(jsTypeData, type.getName(), propertyName, propertyData);
    }

    private static native void setPropertyData(JavaScriptObject typeData,
            String className, String propertyName, JavaScriptObject propertyData)
    /*-{
        typeData[className][propertyName] = propertyData;
    }-*/;

    /*
     * This method sets up prototypes chain for <code>baseClassName</code>.
     * Precondition is : <code>superClassName</code> had to be handled before
     * its child <code>baseClassName</code>.
     * 
     * It makes all properties defined in the <code>superClassName</code>
     * available for <code>baseClassName</code> as well.
     */
    private static native void setSuperClass(JavaScriptObject typeData,
            String baseClassName, String superClassName)
    /*-{
        var parentType = typeData[superClassName];
        if (parentType !== undefined ){
            var ctor = function () {};
            ctor.prototype = parentType;
            typeData[baseClassName] = new ctor;
        }
        else {
            typeData[baseClassName] = {};
        } 
    }-*/;

    private static native boolean hasGetter(JavaScriptObject typeData,
            String beanName, String propertyName)
    /*-{
        return typeData[beanName][propertyName].getter !== undefined;
    }-*/;

    private static native boolean hasSetter(JavaScriptObject typeData,
            String beanName, String propertyName)
    /*-{
        return typeData[beanName][propertyName].setter !== undefined;
    }-*/;

    private static native boolean hasNoLayout(JavaScriptObject typeData,
            String beanName, String propertyName)
    /*-{
        return typeData[beanName][propertyName].noLayout !== undefined;
    }-*/;

    private static native Object getJsPropertyValue(JavaScriptObject typeData,
            String beanName, String propertyName, Object beanInstance)
    /*-{
        return typeData[beanName][propertyName].getter(beanInstance);
    }-*/;

    private static native void setJsPropertyValue(JavaScriptObject typeData,
            String beanName, String propertyName, Object beanInstance,
            Object value)
    /*-{
        typeData[beanName][propertyName].setter(beanInstance, value);
    }-*/;

    private static native Type getJsPropertyType(JavaScriptObject typeData,
            String beanName, String propertyName)
    /*-{
        return typeData[beanName][propertyName].type;
    }-*/;

    private static native void setJsPropertyType(JavaScriptObject typeData,
            String beanName, String propertyName, Type type)
    /*-{
        typeData[beanName][propertyName].type = type;
    }-*/;

    private static native JsArrayString getJsPropertyNames(
            JavaScriptObject typeData, String beanName)
    /*-{
        var names = [];
        for(var name in typeData[beanName]) {
            names.push(name);
        }
        return names;
    }-*/;

    private static native boolean hasJsProperties(JavaScriptObject typeData,
            String beanName)
    /*-{
        return typeData[beanName] !== undefined ;
    }-*/;

    /**
     * Gets data for all methods annotated with {@link OnStateChange} in the
     * given connector type.
     * 
     * @since 7.2
     * @param type
     *            the connector type
     * @return a map of state property names to handler method data
     */
    public static FastStringMap<JsArrayObject<OnStateChangeMethod>> getOnStateChangeMethods(
            Class<?> type) {
        return get().onStateChangeMethods.get(getType(type).getSignature());
    }

    /**
     * Adds data about a method annotated with {@link OnStateChange} for the
     * given connector type.
     * 
     * @since 7.2
     * @param clazz
     *            the connector type
     * @param method
     *            the state change method data
     */
    public void addOnStateChangeMethod(Class<?> clazz,
            OnStateChangeMethod method) {
        FastStringMap<JsArrayObject<OnStateChangeMethod>> handlers = getOnStateChangeMethods(clazz);
        if (handlers == null) {
            handlers = FastStringMap.create();
            onStateChangeMethods.put(getType(clazz).getSignature(), handlers);
        }

        for (String property : method.getProperties()) {
            JsArrayObject<OnStateChangeMethod> propertyHandlers = handlers
                    .get(property);
            if (propertyHandlers == null) {
                propertyHandlers = JsArrayObject.createArray().cast();
                handlers.put(property, propertyHandlers);
            }

            propertyHandlers.add(method);
        }
    }

    /**
     * Checks whether the provided method is annotated with {@link NoLayout}.
     * 
     * @param method
     *            the rpc method to check
     * 
     * @since 7.4
     * 
     * @return <code>true</code> if the method has a NoLayout annotation;
     *         otherwise <code>false</code>
     */
    public static boolean isNoLayoutRpcMethod(Method method) {
        return hasMethodAttribute(method, MethodAttribute.NO_LAYOUT);
    }

    /**
     * Checks whether the provided property is annotated with {@link NoLayout}.
     * 
     * @param property
     *            the property to check
     * 
     * @since 7.4
     * 
     * @return <code>true</code> if the property has a NoLayout annotation;
     *         otherwise <code>false</code>
     */
    public static boolean isNoLayoutProperty(Property property) {
        return hasNoLayout(get().jsTypeData, property.getBeanType()
                .getSignature(), property.getName());
    }
}
