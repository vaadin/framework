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
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import com.vaadin.data.util.BeanUtil;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.validator.BeanValidator;

/**
 * A {@code Binder} subclass specialized for binding <em>beans</em>: classes
 * that conform to the JavaBeans specification. Bean properties are bound by
 * their names. If a JSR-303 bean validation implementation is present on the
 * classpath, {@code BeanBinder} adds a {@link BeanValidator} to each binding.
 *
 * @author Vaadin Ltd.
 *
 * @param <BEAN>
 *            the bean type
 *
 * @since 8.0
 */
public class BeanBinder<BEAN> extends Binder<BEAN> {

    /**
     * Represents the binding between a single field and a bean property.
     *
     * @param <BEAN>
     *            the bean type
     * @param <FIELDVALUE>
     *            the field value type
     * @param <TARGET>
     *            the target property type
     */
    public interface BeanBinding<BEAN, FIELDVALUE, TARGET>
            extends Binding<BEAN, FIELDVALUE, TARGET> {

        @Override
        public BeanBinding<BEAN, FIELDVALUE, TARGET> withValidator(
                Validator<? super TARGET> validator);

        @Override
        public default BeanBinding<BEAN, FIELDVALUE, TARGET> withValidator(
                Predicate<? super TARGET> predicate, String message) {
            return (BeanBinding<BEAN, FIELDVALUE, TARGET>) Binding.super.withValidator(
                    predicate, message);
        }

        @Override
        public <NEWTARGET> BeanBinding<BEAN, FIELDVALUE, NEWTARGET> withConverter(
                Converter<TARGET, NEWTARGET> converter);

        @Override
        public default <NEWTARGET> BeanBinding<BEAN, FIELDVALUE, NEWTARGET> withConverter(
                Function<TARGET, NEWTARGET> toModel,
                Function<NEWTARGET, TARGET> toPresentation) {
            return (BeanBinding<BEAN, FIELDVALUE, NEWTARGET>) Binding.super.withConverter(
                    toModel, toPresentation);
        }

        @Override
        public default <NEWTARGET> BeanBinding<BEAN, FIELDVALUE, NEWTARGET> withConverter(
                Function<TARGET, NEWTARGET> toModel,
                Function<NEWTARGET, TARGET> toPresentation,
                String errorMessage) {
            return (BeanBinding<BEAN, FIELDVALUE, NEWTARGET>) Binding.super.withConverter(
                    toModel, toPresentation, errorMessage);
        }

        /**
         * Completes this binding by connecting the field to the property with
         * the given name. The getter and setter methods of the property are
         * looked up with bean introspection and used to read and write the
         * property value.
         * <p>
         * If a JSR-303 bean validation implementation is present on the
         * classpath, adds a {@link BeanValidator} to this binding.
         * <p>
         * The property must have an accessible getter method. It need not have
         * an accessible setter; in that case the property value is never
         * updated and the binding is said to be <i>read-only</i>.
         *
         * @param propertyName
         *            the name of the property to bind, not null
         *
         * @throws IllegalArgumentException
         *             if the property name is invalid
         * @throws IllegalArgumentException
         *             if the property has no accessible getter
         *
         * @see Binding#bind(Function, java.util.function.BiConsumer)
         */
        public void bind(String propertyName);
    }

    /**
     * An internal implementation of {@link BeanBinding}.
     *
     * @param <BEAN>
     *            the bean type
     * @param <FIELDVALUE>
     *            the field value type
     * @param <TARGET>
     *            the target property type
     */
    protected static class BeanBindingImpl<BEAN, FIELDVALUE, TARGET>
            extends BindingImpl<BEAN, FIELDVALUE, TARGET>
            implements BeanBinding<BEAN, FIELDVALUE, TARGET> {

        private Method getter;
        private Method setter;

        /**
         * Creates a new bean binding.
         *
         * @param binder
         *            the binder this instance is connected to, not null
         * @param field
         *            the field to use, not null
         * @param converter
         *            the initial converter to use, not null
         * @param statusHandler
         *            the handler to notify of status changes, not null
         */
        protected BeanBindingImpl(BeanBinder<BEAN> binder,
                HasValue<FIELDVALUE> field,
                Converter<FIELDVALUE, TARGET> converter,
                ValidationStatusHandler statusHandler) {
            super(binder, field, converter, statusHandler);
        }

        @Override
        public BeanBinding<BEAN, FIELDVALUE, TARGET> withValidator(
                Validator<? super TARGET> validator) {
            return (BeanBinding<BEAN, FIELDVALUE, TARGET>) super.withValidator(
                    validator);
        }

        @Override
        public <NEWTARGET> BeanBinding<BEAN, FIELDVALUE, NEWTARGET> withConverter(
                Converter<TARGET, NEWTARGET> converter) {
            return (BeanBinding<BEAN, FIELDVALUE, NEWTARGET>) super.withConverter(
                    converter);
        }

        @Override
        public void bind(String propertyName) {
            checkUnbound();

            Binding<BEAN, FIELDVALUE, Object> finalBinding;

            finalBinding = withConverter(createConverter());

            if (BeanValidator.checkBeanValidationAvailable()) {
                finalBinding = finalBinding.withValidator(new BeanValidator(
                        getBinder().beanType, propertyName, findLocale()));
            }

            PropertyDescriptor descriptor = getDescriptor(propertyName);
            getter = descriptor.getReadMethod();
            setter = descriptor.getWriteMethod();
            finalBinding.bind(this::getValue, this::setValue);
        }

        @Override
        protected BeanBinder<BEAN> getBinder() {
            return (BeanBinder<BEAN>) super.getBinder();
        }

        private void setValue(BEAN bean, Object value) {
            try {
                if (setter != null) {
                    setter.invoke(bean, value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private Object getValue(BEAN bean) {
            try {
                return getter.invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private PropertyDescriptor getDescriptor(String propertyName) {
            final Class<?> beanType = getBinder().beanType;
            PropertyDescriptor descriptor = null;
            try {
                descriptor = BeanUtil.getPropertyDescriptor(beanType,
                        propertyName);
            } catch (IntrospectionException ie) {
                throw new IllegalArgumentException(
                        "Could not resolve bean property name (see the cause): "
                                + beanType.getName() + "." + propertyName,
                        ie);
            }
            if (descriptor == null) {
                throw new IllegalArgumentException(
                        "Could not resolve bean property name (please check spelling and getter visibility): "
                                + beanType.getName() + "." + propertyName);
            }
            if (descriptor.getReadMethod() == null) {
                throw new IllegalArgumentException(
                        "Bean property has no accessible getter: "
                                + beanType.getName() + "." + propertyName);
            }
            return descriptor;
        }

        @SuppressWarnings("unchecked")
        private Converter<TARGET, Object> createConverter() {
            return Converter.from(
                    fieldValue -> getter.getReturnType().cast(fieldValue),
                    propertyValue -> (TARGET) propertyValue, exception -> {
                        throw new RuntimeException(exception);
                    });
        }
    }

    private final Class<? extends BEAN> beanType;

    /**
     * Creates a new {@code BeanBinder} supporting beans of the given type.
     *
     * @param beanType
     *            the bean {@code Class} instance, not null
     */
    public BeanBinder(Class<? extends BEAN> beanType) {
        BeanValidator.checkBeanValidationAvailable();
        this.beanType = beanType;
    }

    @Override
    public <FIELDVALUE> BeanBinding<BEAN, FIELDVALUE, FIELDVALUE> forField(
            HasValue<FIELDVALUE> field) {
        return createBinding(field, Converter.identity(),
                this::handleValidationStatus);
    }

    /**
     * Binds the given field to the property with the given name. The getter and
     * setter methods of the property are looked up with bean introspection and
     * used to read and write the property value.
     * <p>
     * Use the {@link #forField(HasValue)} overload instead if you want to
     * further configure the new binding.
     * <p>
     * The property must have an accessible getter method. It need not have an
     * accessible setter; in that case the property value is never updated and
     * the binding is said to be <i>read-only</i>.
     *
     * @param <FIELDVALUE>
     *            the value type of the field to bind
     * @param field
     *            the field to bind, not null
     * @param propertyName
     *            the name of the property to bind, not null
     *
     * @throws IllegalArgumentException
     *             if the property name is invalid
     * @throws IllegalArgumentException
     *             if the property has no accessible getter
     *
     * @see #bind(HasValue, java.util.function.Function,
     *      java.util.function.BiConsumer)
     */
    public <FIELDVALUE> void bind(HasValue<FIELDVALUE> field,
            String propertyName) {
        forField(field).bind(propertyName);
    }

    @Override
    public BeanBinder<BEAN> withValidator(Validator<? super BEAN> validator) {
        return (BeanBinder<BEAN>) super.withValidator(validator);
    }

    @Override
    protected <FIELDVALUE, TARGET> BeanBindingImpl<BEAN, FIELDVALUE, TARGET> createBinding(
            HasValue<FIELDVALUE> field, Converter<FIELDVALUE, TARGET> converter,
            ValidationStatusHandler handler) {
        Objects.requireNonNull(field, "field cannot be null");
        Objects.requireNonNull(converter, "converter cannot be null");
        return new BeanBindingImpl<>(this, field, converter, handler);
    }

}
