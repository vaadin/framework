/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.data;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.util.BeanUtil;
import com.vaadin.server.Setter;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.util.ReflectTools;

/**
 * A {@link PropertySet} that uses reflection to find bean properties.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 * @param <T>
 *            the type of the bean
 */
public class BeanPropertySet<T> implements PropertySet<T> {

    /**
     * Serialized form of a property set. When deserialized, the property set
     * for the corresponding bean type is requested, which either returns the
     * existing cached instance or creates a new one.
     *
     * @see #readResolve()
     * @see BeanPropertyDefinition#writeReplace()
     */
    private static class SerializedPropertySet implements Serializable {
        private final Class<?> beanType;

        private SerializedPropertySet(Class<?> beanType) {
            this.beanType = beanType;
        }

        private Object readResolve() {
            /*
             * When this instance is deserialized, it will be replaced with a
             * property set for the corresponding bean type and property name.
             */
            return get(beanType);
        }
    }

    /**
     * Serialized form of a property definition. When deserialized, the property
     * set for the corresponding bean type is requested, which either returns
     * the existing cached instance or creates a new one. The right property
     * definition is then fetched from the property set.
     *
     * @see #readResolve()
     * @see BeanPropertySet#writeReplace()
     */
    private static class SerializedPropertyDefinition implements Serializable {
        private final Class<?> beanType;
        private final String propertyName;

        private SerializedPropertyDefinition(Class<?> beanType,
                String propertyName) {
            this.beanType = beanType;
            this.propertyName = propertyName;
        }

        private Object readResolve() throws IOException {
            /*
             * When this instance is deserialized, it will be replaced with a
             * property definition for the corresponding bean type and property
             * name.
             */
            return get(beanType).getProperty(propertyName)
                    .orElseThrow(() -> new IOException(
                            beanType + " no longer has a property named "
                                    + propertyName));
        }
    }

    private static class BeanPropertyDefinition<T, V>
            implements PropertyDefinition<T, V> {

        private final PropertyDescriptor descriptor;
        private final BeanPropertySet<T> propertySet;

        public BeanPropertyDefinition(BeanPropertySet<T> propertySet,
                PropertyDescriptor descriptor) {
            this.propertySet = propertySet;
            this.descriptor = descriptor;

            if (descriptor.getReadMethod() == null) {
                throw new IllegalArgumentException(
                        "Bean property has no accessible getter: "
                                + propertySet.beanType + "."
                                + descriptor.getName());
            }

        }

        @Override
        public ValueProvider<T, V> getGetter() {
            return bean -> {
                Method readMethod = descriptor.getReadMethod();
                Object value = invokeWrapExceptions(readMethod, bean);
                return getType().cast(value);
            };
        }

        @Override
        public Optional<Setter<T, V>> getSetter() {
            if (descriptor.getWriteMethod() == null) {
                return Optional.empty();
            }

            Setter<T, V> setter = (bean, value) -> {
                // Do not "optimize" this getter call,
                // if its done outside the code block, that will produce
                // NotSerializableException because of some lambda compilation magic
                Method innerSetter = descriptor.getWriteMethod();
                invokeWrapExceptions(innerSetter, bean, value);
            };
            return Optional.of(setter);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<V> getType() {
            return (Class<V>) ReflectTools
                    .convertPrimitiveType(descriptor.getPropertyType());
        }

        @Override
        public String getName() {
            return descriptor.getName();
        }

        @Override
        public String getCaption() {
            return SharedUtil.propertyIdToHumanFriendly(getName());
        }

        @Override
        public BeanPropertySet<T> getPropertySet() {
            return propertySet;
        }

        private Object writeReplace() {
            /*
             * Instead of serializing this actual property definition, only
             * serialize a DTO that when deserialized will get the corresponding
             * property definition from the cache.
             */
            return new SerializedPropertyDefinition(getPropertySet().beanType,
                    getName());
        }
    }

    private static final ConcurrentMap<Class<?>, BeanPropertySet<?>> instances = new ConcurrentHashMap<>();

    private final Class<T> beanType;

    private final Map<String, PropertyDefinition<T, ?>> definitions;

    private BeanPropertySet(Class<T> beanType) {
        this.beanType = beanType;

        try {
            definitions = BeanUtil.getBeanPropertyDescriptors(beanType).stream()
                    .filter(BeanPropertySet::hasNonObjectReadMethod)
                    .map(descriptor -> new BeanPropertyDefinition<>(this,
                            descriptor))
                    .collect(Collectors.toMap(PropertyDefinition::getName,
                            Function.identity()));
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(
                    "Cannot find property descriptors for "
                            + beanType.getName(),
                    e);
        }
    }

    /**
     * Gets a {@link BeanPropertySet} for the given bean type.
     *
     * @param beanType
     *            the bean type to get a property set for, not <code>null</code>
     * @return the bean property set, not <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> PropertySet<T> get(Class<? extends T> beanType) {
        Objects.requireNonNull(beanType, "Bean type cannot be null");

        // Cache the reflection results
        return (PropertySet<T>) instances.computeIfAbsent(beanType,
                BeanPropertySet::new);
    }

    @Override
    public Stream<PropertyDefinition<T, ?>> getProperties() {
        return definitions.values().stream();
    }

    @Override
    public Optional<PropertyDefinition<T, ?>> getProperty(String name) {
        return Optional.ofNullable(definitions.get(name));
    }

    private static boolean hasNonObjectReadMethod(
            PropertyDescriptor descriptor) {
        Method readMethod = descriptor.getReadMethod();
        return readMethod != null
                && readMethod.getDeclaringClass() != Object.class;
    }

    private static Object invokeWrapExceptions(Method method, Object target,
            Object... parameters) {
        try {
            return method.invoke(target, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Property set for bean " + beanType.getName();
    }

    private Object writeReplace() {
        /*
         * Instead of serializing this actual property set, only serialize a DTO
         * that when deserialized will get the corresponding property set from
         * the cache.
         */
        return new SerializedPropertySet(beanType);
    }
}
