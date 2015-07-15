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
package com.vaadin.server.widgetsetutils.metadata;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JArrayType;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JEnumType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.JSONSerializer;
import com.vaadin.client.connectors.AbstractRendererConnector;
import com.vaadin.client.metadata.TypeDataStore.MethodAttribute;
import com.vaadin.client.ui.UnknownComponentConnector;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonValue;

public class ConnectorBundle {
    private static final String FAIL_IF_NOT_SERIALIZABLE = "vFailIfNotSerializable";

    public static final Comparator<JClassType> jClassComparator = new Comparator<JClassType>() {
        @Override
        public int compare(JClassType o1, JClassType o2) {
            return o1.getQualifiedSourceName().compareTo(
                    o2.getQualifiedSourceName());
        }
    };

    public static final Comparator<JMethod> jMethodComparator = new Comparator<JMethod>() {
        @Override
        public int compare(JMethod o1, JMethod o2) {
            return o1.getReadableDeclaration().compareTo(
                    o2.getReadableDeclaration());
        }
    };

    private final String name;
    private final ConnectorBundle previousBundle;
    private final Collection<TypeVisitor> visitors;
    private final Map<JType, JClassType> customSerializers;

    private final Set<JType> hasSerializeSupport = new HashSet<JType>();
    private final Set<JType> needsSerializeSupport = new HashSet<JType>();

    private final Map<JType, GeneratedSerializer> serializers = new TreeMap<JType, GeneratedSerializer>(
            new Comparator<JType>() {
                @Override
                public int compare(JType o1, JType o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });

    private final Map<JClassType, Map<JMethod, Set<MethodAttribute>>> methodAttributes = new TreeMap<JClassType, Map<JMethod, Set<MethodAttribute>>>(
            jClassComparator);
    private final Set<JClassType> needsSuperClass = new TreeSet<JClassType>(
            jClassComparator);
    private final Set<JClassType> needsGwtConstructor = new TreeSet<JClassType>(
            jClassComparator);
    private final Set<JClassType> visitedTypes = new HashSet<JClassType>();

    private final Set<JClassType> needsProxySupport = new TreeSet<JClassType>(
            jClassComparator);

    private final Map<JClassType, JType> presentationTypes = new TreeMap<JClassType, JType>(
            jClassComparator);
    private final Map<JClassType, Set<String>> identifiers = new TreeMap<JClassType, Set<String>>(
            jClassComparator);
    private final Map<JClassType, Set<JMethod>> needsReturnType = new TreeMap<JClassType, Set<JMethod>>(
            jClassComparator);
    private final Map<JClassType, Set<JMethod>> needsInvoker = new TreeMap<JClassType, Set<JMethod>>(
            jClassComparator);
    private final Map<JClassType, Set<JMethod>> needsParamTypes = new TreeMap<JClassType, Set<JMethod>>(
            jClassComparator);
    private final Map<JClassType, Set<JMethod>> needsOnStateChange = new TreeMap<JClassType, Set<JMethod>>(
            jClassComparator);

    private final Set<Property> needsProperty = new TreeSet<Property>();
    private final Map<JClassType, Set<Property>> needsDelegateToWidget = new TreeMap<JClassType, Set<Property>>(
            jClassComparator);

    private ConnectorBundle(String name, ConnectorBundle previousBundle,
            Collection<TypeVisitor> visitors,
            Map<JType, JClassType> customSerializers) {
        this.name = name;
        this.previousBundle = previousBundle;
        this.visitors = visitors;
        this.customSerializers = customSerializers;
    }

    public ConnectorBundle(String name, ConnectorBundle previousBundle) {
        this(name, previousBundle, previousBundle.visitors,
                previousBundle.customSerializers);
    }

    public ConnectorBundle(String name, Collection<TypeVisitor> visitors,
            TypeOracle oracle) throws NotFoundException {
        this(name, null, visitors, findCustomSerializers(oracle));
    }

    private static Map<JType, JClassType> findCustomSerializers(
            TypeOracle oracle) throws NotFoundException {
        Map<JType, JClassType> serializers = new HashMap<JType, JClassType>();

        JClassType serializerInterface = oracle.findType(JSONSerializer.class
                .getName());
        JType[] deserializeParamTypes = new JType[] {
                oracle.findType(com.vaadin.client.metadata.Type.class.getName()),
                oracle.findType(JsonValue.class.getName()),
                oracle.findType(ApplicationConnection.class.getName()) };
        String deserializeMethodName = "deserialize";
        // Just test that the method exists
        serializerInterface.getMethod(deserializeMethodName,
                deserializeParamTypes);

        for (JClassType serializer : serializerInterface.getSubtypes()) {
            JMethod deserializeMethod = serializer.findMethod(
                    deserializeMethodName, deserializeParamTypes);
            if (deserializeMethod == null) {
                continue;
            }
            JType returnType = deserializeMethod.getReturnType();

            serializers.put(returnType, serializer);
        }
        return serializers;
    }

    public void setNeedsGwtConstructor(JClassType type) {
        if (!needsGwtConstructor(type)) {
            needsGwtConstructor.add(type);
        }
    }

    private boolean needsGwtConstructor(JClassType type) {
        if (needsGwtConstructor.contains(type)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.needsGwtConstructor(type);
        }
    }

    public void setIdentifier(JClassType type, String identifier) {
        if (!hasIdentifier(type, identifier)) {
            addMapping(identifiers, type, identifier);
        }
    }

    private boolean hasIdentifier(JClassType type, String identifier) {
        if (hasMapping(identifiers, type, identifier)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.hasIdentifier(type, identifier);
        }
    }

    public ConnectorBundle getPreviousBundle() {
        return previousBundle;
    }

    public String getName() {
        return name;
    }

    public Map<JClassType, Set<String>> getIdentifiers() {
        return Collections.unmodifiableMap(identifiers);
    }

    public Set<JClassType> getGwtConstructors() {
        return Collections.unmodifiableSet(needsGwtConstructor);
    }

    public void processTypes(TreeLogger logger, Collection<JClassType> types)
            throws UnableToCompleteException {
        for (JClassType type : types) {
            processType(logger, type);
        }
    }

    public void processType(TreeLogger logger, JClassType type)
            throws UnableToCompleteException {
        if (!isTypeVisited(type)) {
            for (TypeVisitor typeVisitor : visitors) {
                invokeVisitor(logger, type, typeVisitor);
            }
            visitedTypes.add(type);
            purgeSerializeSupportQueue(logger);
        }
    }

    private boolean isTypeVisited(JClassType type) {
        if (visitedTypes.contains(type)) {
            return true;
        } else {
            return previousBundle != null && previousBundle.isTypeVisited(type);
        }
    }

    private void purgeSerializeSupportQueue(TreeLogger logger)
            throws UnableToCompleteException {
        while (!needsSerializeSupport.isEmpty()) {
            Iterator<JType> iterator = needsSerializeSupport.iterator();
            JType type = iterator.next();
            iterator.remove();

            if (hasSerializeSupport(type)) {
                continue;
            }

            addSerializeSupport(logger, type);
        }
    }

    private void addSerializeSupport(TreeLogger logger, JType type)
            throws UnableToCompleteException {
        hasSerializeSupport.add(type);

        JParameterizedType parametrized = type.isParameterized();
        if (parametrized != null) {
            for (JClassType parameterType : parametrized.getTypeArgs()) {
                setNeedsSerialize(parameterType);
            }
        }

        if (serializationHandledByFramework(type)) {
            return;
        }

        JClassType customSerializer = customSerializers.get(type);
        JClassType typeAsClass = type.isClass();
        JEnumType enumType = type.isEnum();
        JArrayType arrayType = type.isArray();

        if (customSerializer != null) {
            logger.log(Type.INFO, "Will serialize " + type + " using "
                    + customSerializer.getName());
            setSerializer(type, new CustomSerializer(customSerializer));
        } else if (arrayType != null) {
            logger.log(Type.INFO, "Will serialize " + type + " as an array");
            setSerializer(type, new ArraySerializer(arrayType));
            setNeedsSerialize(arrayType.getComponentType());
        } else if (enumType != null) {
            logger.log(Type.INFO, "Will serialize " + type + " as an enum");
            setSerializer(type, new EnumSerializer(enumType));
        } else if (typeAsClass != null) {
            // Bean
            checkSerializable(logger, typeAsClass);

            logger.log(Type.INFO, "Will serialize " + type + " as a bean");

            JClassType needsSuperClass = typeAsClass;
            while (needsSuperClass != null) {
                if (needsSuperClass.isPublic()) {
                    setNeedsSuperclass(needsSuperClass);
                }
                needsSuperClass = needsSuperClass.getSuperclass();
            }

            setNeedsGwtConstructor(typeAsClass);

            for (Property property : getProperties(typeAsClass)) {
                setNeedsProperty(property);

                JType propertyType = property.getPropertyType();
                setNeedsSerialize(propertyType);
            }
        }
    }

    private void checkSerializable(TreeLogger logger, JClassType type)
            throws UnableToCompleteException {
        JClassType javaSerializable = type.getOracle().findType(
                Serializable.class.getName());
        boolean serializable = type.isAssignableTo(javaSerializable);
        if (!serializable) {
            boolean abortCompile = "true".equals(System
                    .getProperty(FAIL_IF_NOT_SERIALIZABLE));
            logger.log(
                    abortCompile ? Type.ERROR : Type.WARN,
                    type
                            + " is used in RPC or shared state but does not implement "
                            + Serializable.class.getName()
                            + ". Communication will work but the Application on server side cannot be serialized if it refers to objects of this type. "
                            + "If the system property "
                            + FAIL_IF_NOT_SERIALIZABLE
                            + " is set to \"true\", this causes the compilation to fail instead of just emitting a warning.");
            if (abortCompile) {
                throw new UnableToCompleteException();
            }
        }
    }

    private void setSerializer(JType type, GeneratedSerializer serializer) {
        if (!hasSerializer(type)) {
            serializers.put(type, serializer);
        }
    }

    private boolean hasSerializer(JType type) {
        if (serializers.containsKey(type)) {
            return true;
        } else {
            return previousBundle != null && previousBundle.hasSerializer(type);
        }
    }

    public Map<JType, GeneratedSerializer> getSerializers() {
        return Collections.unmodifiableMap(serializers);
    }

    public void setPresentationType(JClassType type, JType presentationType) {
        if (!hasPresentationType(type)) {
            presentationTypes.put(type, presentationType);
        }
    }

    private boolean hasPresentationType(JClassType type) {
        if (presentationTypes.containsKey(type)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.hasPresentationType(type);
        }
    }

    public Map<JClassType, JType> getPresentationTypes() {
        return Collections.unmodifiableMap(presentationTypes);
    }

    private void setNeedsSuperclass(JClassType typeAsClass) {
        if (!isNeedsSuperClass(typeAsClass)) {
            needsSuperClass.add(typeAsClass);
        }
    }

    private boolean isNeedsSuperClass(JClassType typeAsClass) {
        if (needsSuperClass.contains(typeAsClass)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.isNeedsSuperClass(typeAsClass);
        }
    }

    public Set<JClassType> getNeedsSuperclass() {
        return Collections.unmodifiableSet(needsSuperClass);
    }

    private void setNeedsProperty(Property property) {
        if (!isNeedsProperty(property)) {
            needsProperty.add(property);
        }
    }

    private boolean isNeedsProperty(Property property) {
        if (needsProperty.contains(property)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.isNeedsProperty(property);
        }
    }

    public Set<Property> getNeedsProperty() {
        return Collections.unmodifiableSet(needsProperty);
    }

    public Collection<Property> getProperties(JClassType type) {
        Set<Property> properties = new TreeSet<Property>();

        properties.addAll(MethodProperty.findProperties(type));
        properties.addAll(FieldProperty.findProperties(type));

        return properties;
    }

    private void invokeVisitor(TreeLogger logger, JClassType type,
            TypeVisitor typeVisitor) throws UnableToCompleteException {
        TreeLogger subLogger = logger.branch(Type.TRACE,
                "Visiting " + type.getName() + " with "
                        + typeVisitor.getClass().getSimpleName());
        if (isConnectedConnector(type)) {
            typeVisitor.visitConnector(subLogger, type, this);
        }
        if (isClientRpc(type)) {
            typeVisitor.visitClientRpc(subLogger, type, this);
        }
        if (isServerRpc(type)) {
            typeVisitor.visitServerRpc(subLogger, type, this);
        }
    }

    public void processSubTypes(TreeLogger logger, JClassType type)
            throws UnableToCompleteException {
        processTypes(logger, Arrays.asList(type.getSubtypes()));
    }

    public void setNeedsReturnType(JClassType type, JMethod method) {
        if (!isNeedsReturnType(type, method)) {
            addMapping(needsReturnType, type, method);
        }
    }

    private boolean isNeedsReturnType(JClassType type, JMethod method) {
        if (hasMapping(needsReturnType, type, method)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.isNeedsReturnType(type, method);
        }
    }

    public Map<JClassType, Set<JMethod>> getMethodReturnTypes() {
        return Collections.unmodifiableMap(needsReturnType);
    }

    private static boolean isClientRpc(JClassType type) {
        return isInterfaceType(type, ClientRpc.class);
    }

    private static boolean isServerRpc(JClassType type) {
        return isInterfaceType(type, ServerRpc.class);
    }

    public static boolean isConnectedConnector(JClassType type) {
        return isConnected(type) && isType(type, ServerConnector.class);
    }

    private static boolean isConnected(JClassType type) {
        return type.isAnnotationPresent(Connect.class)
                || type.getQualifiedSourceName().equals(
                        UnknownComponentConnector.class.getCanonicalName());
    }

    public static boolean isConnectedComponentConnector(JClassType type) {
        return isConnected(type) && isType(type, ComponentConnector.class);
    }

    public static boolean isConnectedRendererConnector(JClassType type) {
        return isConnected(type)
                && isType(type, AbstractRendererConnector.class);
    }

    private static boolean isInterfaceType(JClassType type, Class<?> class1) {
        return type.isInterface() != null && isType(type, class1);
    }

    private static boolean isType(JClassType type, Class<?> class1) {
        try {
            return type.getOracle().getType(class1.getName())
                    .isAssignableFrom(type);
        } catch (NotFoundException e) {
            throw new RuntimeException("Could not find " + class1.getName(), e);
        }
    }

    public void setNeedsInvoker(JClassType type, JMethod method) {
        if (!isNeedsInvoker(type, method)) {
            addMapping(needsInvoker, type, method);
        }
    }

    private <K> void addMapping(Map<K, Set<String>> map, K key, String value) {
        Set<String> set = map.get(key);
        if (set == null) {
            set = new TreeSet<String>();
            map.put(key, set);
        }
        set.add(value);
    }

    private <K> void addMapping(Map<K, Set<JMethod>> map, K key, JMethod value) {
        Set<JMethod> set = map.get(key);
        if (set == null) {
            set = new TreeSet<JMethod>(jMethodComparator);
            map.put(key, set);
        }
        set.add(value);
    }

    private <K, V> boolean hasMapping(Map<K, Set<V>> map, K key, V value) {
        return map.containsKey(key) && map.get(key).contains(value);
    }

    private boolean isNeedsInvoker(JClassType type, JMethod method) {
        if (hasMapping(needsInvoker, type, method)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.isNeedsInvoker(type, method);
        }
    }

    public Map<JClassType, Set<JMethod>> getNeedsInvoker() {
        return Collections.unmodifiableMap(needsInvoker);
    }

    public void setNeedsParamTypes(JClassType type, JMethod method) {
        if (!isNeedsParamTypes(type, method)) {
            addMapping(needsParamTypes, type, method);
        }
    }

    private boolean isNeedsParamTypes(JClassType type, JMethod method) {
        if (hasMapping(needsParamTypes, type, method)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.isNeedsParamTypes(type, method);
        }
    }

    public Map<JClassType, Set<JMethod>> getNeedsParamTypes() {
        return Collections.unmodifiableMap(needsParamTypes);
    }

    public void setNeedsProxySupport(JClassType type) {
        if (!isNeedsProxySupport(type)) {
            needsProxySupport.add(type);
        }
    }

    private boolean isNeedsProxySupport(JClassType type) {
        if (needsProxySupport.contains(type)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.isNeedsProxySupport(type);
        }
    }

    public Set<JClassType> getNeedsProxySupport() {
        return Collections.unmodifiableSet(needsProxySupport);
    }

    public void setMethodAttribute(JClassType type, JMethod method,
            MethodAttribute methodAttribute) {
        if (!hasMethodAttribute(type, method, methodAttribute)) {
            Map<JMethod, Set<MethodAttribute>> typeData = methodAttributes
                    .get(type);
            if (typeData == null) {
                typeData = new TreeMap<JMethod, Set<MethodAttribute>>(
                        jMethodComparator);
                methodAttributes.put(type, typeData);
            }

            Map<JMethod, Set<MethodAttribute>> methods = methodAttributes
                    .get(type);
            if (methods == null) {
                methods = new TreeMap<JMethod, Set<MethodAttribute>>(
                        jMethodComparator);
                methodAttributes.put(type, methods);
            }

            Set<MethodAttribute> attributes = methods.get(method);
            if (attributes == null) {
                attributes = new TreeSet<MethodAttribute>();
                methods.put(method, attributes);
            }

            attributes.add(methodAttribute);
        }
    }

    private boolean hasMethodAttribute(JClassType type, JMethod method,
            MethodAttribute methodAttribute) {
        Map<JMethod, Set<MethodAttribute>> typeData = methodAttributes
                .get(type);
        if (typeData != null && hasMapping(typeData, method, methodAttribute)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.hasMethodAttribute(type, method,
                            methodAttribute);
        }
    }

    public Map<JClassType, Map<JMethod, Set<MethodAttribute>>> getMethodAttributes() {
        return Collections.unmodifiableMap(methodAttributes);
    }

    public void setNeedsSerialize(JType type) {
        if (!hasSerializeSupport(type)) {
            needsSerializeSupport.add(type);
        }
    }

    private static Set<Class<?>> frameworkHandledTypes = new LinkedHashSet<Class<?>>();
    {
        frameworkHandledTypes.add(String.class);
        frameworkHandledTypes.add(Boolean.class);
        frameworkHandledTypes.add(Integer.class);
        frameworkHandledTypes.add(Float.class);
        frameworkHandledTypes.add(Double.class);
        frameworkHandledTypes.add(Long.class);
        frameworkHandledTypes.add(Enum.class);
        frameworkHandledTypes.add(String[].class);
        frameworkHandledTypes.add(Object[].class);
        frameworkHandledTypes.add(Map.class);
        frameworkHandledTypes.add(List.class);
        frameworkHandledTypes.add(Set.class);
        frameworkHandledTypes.add(Byte.class);
        frameworkHandledTypes.add(Character.class);
        frameworkHandledTypes.add(Void.class);
    }

    private boolean serializationHandledByFramework(JType setterType) {
        // Some types are handled by the framework at the moment. See #8449
        // This method should be removed at some point.
        if (setterType.isPrimitive() != null) {
            return true;
        }

        String qualifiedName = setterType.getQualifiedSourceName();
        for (Class<?> cls : frameworkHandledTypes) {
            if (qualifiedName.equals(cls.getName())) {
                return true;
            }
        }

        return false;
    }

    private boolean hasSerializeSupport(JType type) {
        if (hasSerializeSupport.contains(type)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.hasSerializeSupport(type);
        }
    }

    public void setNeedsDelegateToWidget(Property property, JClassType type) {
        if (!isNeedsDelegateToWidget(type)) {
            TreeSet<Property> set = new TreeSet<Property>();
            set.add(property);
            needsDelegateToWidget.put(type, set);
        } else if (!needsDelegateToWidget.get(type).contains(property)) {
            needsDelegateToWidget.get(type).add(property);
        }
    }

    private boolean isNeedsDelegateToWidget(JClassType type) {
        if (needsDelegateToWidget.containsKey(type)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.isNeedsDelegateToWidget(type);
        }
    }

    public Map<JClassType, Set<Property>> getNeedsDelegateToWidget() {
        return Collections.unmodifiableMap(needsDelegateToWidget);
    }

    public void setNeedsOnStateChangeHandler(JClassType type, JMethod method) {
        if (!isNeedsOnStateChangeHandler(type, method)) {
            addMapping(needsOnStateChange, type, method);
        }
    }

    private boolean isNeedsOnStateChangeHandler(JClassType type, JMethod method) {
        if (hasMapping(needsOnStateChange, type, method)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.isNeedsOnStateChangeHandler(type, method);
        }
    }

    public Map<JClassType, Set<JMethod>> getNeedsOnStateChangeHandler() {
        return Collections.unmodifiableMap(needsOnStateChange);
    }

    public static JMethod findInheritedMethod(JClassType type,
            String methodName, JType... params) {

        JClassType currentType = type;
        while (currentType != null) {
            JMethod method = currentType.findMethod(methodName, params);
            if (method != null) {
                return method;
            }
            currentType = currentType.getSuperclass();
        }

        JClassType[] interfaces = type.getImplementedInterfaces();
        for (JClassType iface : interfaces) {
            JMethod method = iface.findMethod(methodName, params);
            if (method != null) {
                return method;
            }
        }

        return null;
    }
}
