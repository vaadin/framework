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

import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.util.BeanUtil;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.Setter;
import com.vaadin.util.ReflectTools;

/**
 * A {@link BinderPropertySet} that uses reflection to find bean properties.
 *
 * @author Vaadin Ltd
 *
 * @since
 *
 * @param <T>
 *            the type of the bean
 */
public class BeanBinderPropertySet<T> implements BinderPropertySet<T> {

    private static class BeanBinderPropertyDefinition<T, V>
            implements BinderPropertyDefinition<T, V> {

        private final PropertyDescriptor descriptor;
        private final BeanBinderPropertySet<T> propertySet;

        public BeanBinderPropertyDefinition(
                BeanBinderPropertySet<T> propertySet,
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
            Method setter = descriptor.getWriteMethod();
            if (setter == null) {
                return Optional.empty();
            }

            return Optional.of(
                    (bean, value) -> invokeWrapExceptions(setter, bean, value));
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<V> getType() {
            return (Class<V>) ReflectTools
                    .convertPrimitiveType(descriptor.getPropertyType());
        }

        @Override
        public BindingBuilder<T, V> beforeBind(
                BindingBuilder<T, V> originalBuilder) {
            if (BeanUtil.checkBeanValidationAvailable()) {
                return originalBuilder.withValidator(new BeanValidator(
                        getPropertySet().beanType, descriptor.getName()));
            } else {
                return originalBuilder;
            }
        }

        @Override
        public String getName() {
            return descriptor.getName();
        }

        @Override
        public BeanBinderPropertySet<T> getPropertySet() {
            return propertySet;
        }

    }

    private static final ConcurrentMap<Class<?>, BeanBinderPropertySet<?>> instances = new ConcurrentHashMap<>();

    private final Class<T> beanType;

    private final Map<String, BinderPropertyDefinition<T, ?>> definitions;

    /**
     * Gets a {@link BeanBinderPropertySet} for the given bean type.
     *
     * @param beanType
     *            the bean type to get a property set for, not <code>null</code>
     * @return the bean binder property set, not <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> BinderPropertySet<T> get(Class<? extends T> beanType) {
        Objects.requireNonNull(beanType, "Bean type cannot be null");

        // Cache the reflection results
        return (BinderPropertySet<T>) instances.computeIfAbsent(beanType,
                BeanBinderPropertySet::new);
    }

    private BeanBinderPropertySet(Class<T> beanType) {
        this.beanType = beanType;

        try {
            definitions = BeanUtil.getBeanPropertyDescriptors(beanType).stream()
                    .filter(BeanBinderPropertySet::hasReadMethod)
                    .map(descriptor -> new BeanBinderPropertyDefinition<>(this,
                            descriptor))
                    .collect(Collectors.toMap(BinderPropertyDefinition::getName,
                            Function.identity()));
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(
                    "Cannot find property descriptors for "
                            + beanType.getName(),
                    e);
        }
    }

    @Override
    public Stream<BinderPropertyDefinition<T, ?>> getProperties() {
        return definitions.values().stream();
    }

    @Override
    public Optional<BinderPropertyDefinition<T, ?>> getProperty(String name) {
        return Optional.ofNullable(definitions.get(name));
    }

    private static boolean hasReadMethod(PropertyDescriptor descriptor) {
        return descriptor.getReadMethod() != null;
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
}
