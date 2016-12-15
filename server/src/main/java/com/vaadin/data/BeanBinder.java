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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.annotations.PropertyId;
import com.vaadin.data.util.BeanUtil;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.Setter;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.Label;
import com.vaadin.util.ReflectTools;

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
     * @param <TARGET>
     *            the target property type
     */
    public interface BeanBindingBuilder<BEAN, TARGET>
            extends BindingBuilder<BEAN, TARGET> {

        @Override
        public BeanBindingBuilder<BEAN, TARGET> withValidator(
                Validator<? super TARGET> validator);

        @Override
        public default BeanBindingBuilder<BEAN, TARGET> withValidator(
                SerializablePredicate<? super TARGET> predicate,
                String message) {
            return (BeanBindingBuilder<BEAN, TARGET>) BindingBuilder.super.withValidator(
                    predicate, message);
        }

        @Override
        default BeanBindingBuilder<BEAN, TARGET> withValidator(
                SerializablePredicate<? super TARGET> predicate,
                ErrorMessageProvider errorMessageProvider) {
            return (BeanBindingBuilder<BEAN, TARGET>) BindingBuilder.super.withValidator(
                    predicate, errorMessageProvider);
        }

        @Override
        default BeanBindingBuilder<BEAN, TARGET> withNullRepresentation(
                TARGET nullRepresentation) {
            return (BeanBindingBuilder<BEAN, TARGET>) BindingBuilder.super.withNullRepresentation(
                    nullRepresentation);
        }

        @Override
        public BeanBindingBuilder<BEAN, TARGET> asRequired(
                ErrorMessageProvider errorMessageProvider);

        @Override
        public default BeanBindingBuilder<BEAN, TARGET> asRequired(
                String errorMessage) {
            return (BeanBindingBuilder<BEAN, TARGET>) BindingBuilder.super.asRequired(
                    errorMessage);
        }

        @Override
        public <NEWTARGET> BeanBindingBuilder<BEAN, NEWTARGET> withConverter(
                Converter<TARGET, NEWTARGET> converter);

        @Override
        public default <NEWTARGET> BeanBindingBuilder<BEAN, NEWTARGET> withConverter(
                SerializableFunction<TARGET, NEWTARGET> toModel,
                SerializableFunction<NEWTARGET, TARGET> toPresentation) {
            return (BeanBindingBuilder<BEAN, NEWTARGET>) BindingBuilder.super.withConverter(
                    toModel, toPresentation);
        }

        @Override
        public default <NEWTARGET> BeanBindingBuilder<BEAN, NEWTARGET> withConverter(
                SerializableFunction<TARGET, NEWTARGET> toModel,
                SerializableFunction<NEWTARGET, TARGET> toPresentation,
                String errorMessage) {
            return (BeanBindingBuilder<BEAN, NEWTARGET>) BindingBuilder.super.withConverter(
                    toModel, toPresentation, errorMessage);
        }

        @Override
        public BeanBindingBuilder<BEAN, TARGET> withValidationStatusHandler(
                BindingValidationStatusHandler handler);

        @Override
        public default BeanBindingBuilder<BEAN, TARGET> withStatusLabel(
                Label label) {
            return (BeanBindingBuilder<BEAN, TARGET>) BindingBuilder.super.withStatusLabel(
                    label);
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
         * @return the newly created binding
         *
         * @throws IllegalArgumentException
         *             if the property name is invalid
         * @throws IllegalArgumentException
         *             if the property has no accessible getter
         *
         * @see BindingBuilder#bind(ValueProvider,
         *      Setter)
         */
        public Binding<BEAN, TARGET> bind(String propertyName);
    }

    /**
     * An internal implementation of {@link BeanBindingBuilder}.
     *
     * @param <BEAN>
     *            the bean type
     * @param <FIELDVALUE>
     *            the field value type
     * @param <TARGET>
     *            the target property type
     */
    protected static class BeanBindingImpl<BEAN, FIELDVALUE, TARGET>
            extends BindingBuilderImpl<BEAN, FIELDVALUE, TARGET>
            implements BeanBindingBuilder<BEAN, TARGET> {

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
                BindingValidationStatusHandler statusHandler) {
            super(binder, field, converter, statusHandler);
        }

        @Override
        public BeanBindingBuilder<BEAN, TARGET> withValidator(
                Validator<? super TARGET> validator) {
            return (BeanBindingBuilder<BEAN, TARGET>) super.withValidator(
                    validator);
        }

        @Override
        public <NEWTARGET> BeanBindingBuilder<BEAN, NEWTARGET> withConverter(
                Converter<TARGET, NEWTARGET> converter) {
            return (BeanBindingBuilder<BEAN, NEWTARGET>) super.withConverter(
                    converter);
        }

        @Override
        public BeanBindingBuilder<BEAN, TARGET> withValidationStatusHandler(
                BindingValidationStatusHandler handler) {
            return (BeanBindingBuilder<BEAN, TARGET>) super.withValidationStatusHandler(
                    handler);
        }

        @Override
        public BeanBindingBuilder<BEAN, TARGET> asRequired(
                ErrorMessageProvider errorMessageProvider) {
            return (BeanBindingBuilder<BEAN, TARGET>) super.asRequired(
                    errorMessageProvider);
        }

        @Override
        public Binding<BEAN, TARGET> bind(String propertyName) {
            checkUnbound();

            BindingBuilder<BEAN, Object> finalBinding;

            PropertyDescriptor descriptor = getDescriptor(propertyName);

            Method getter = descriptor.getReadMethod();
            Method setter = descriptor.getWriteMethod();

            finalBinding = withConverter(
                    createConverter(getter.getReturnType()), false);

            if (BeanUtil.checkBeanValidationAvailable()) {
                finalBinding = finalBinding.withValidator(
                        new BeanValidator(getBinder().beanType, propertyName));
            }

            try {
                return (Binding<BEAN, TARGET>) finalBinding.bind(
                        bean -> invokeWrapExceptions(getter, bean),
                        (bean, value) -> invokeWrapExceptions(setter, bean,
                                value));
            } finally {
                getBinder().boundProperties.add(propertyName);
            }
        }

        @Override
        protected BeanBinder<BEAN> getBinder() {
            return (BeanBinder<BEAN>) super.getBinder();
        }

        private static Object invokeWrapExceptions(Method method, Object target,
                Object... parameters) {
            if (method == null) {
                return null;
            }
            try {
                return method.invoke(target, parameters);
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
        private Converter<TARGET, Object> createConverter(Class<?> getterType) {
            return Converter.from(fieldValue -> cast(fieldValue, getterType),
                    propertyValue -> (TARGET) propertyValue, exception -> {
                        throw new RuntimeException(exception);
                    });
        }

        private <T> T cast(TARGET value, Class<T> clazz) {
            if (clazz.isPrimitive()) {
                return (T) ReflectTools.convertPrimitiveType(clazz).cast(value);
            } else {
                return clazz.cast(value);
            }
        }
    }

    private final Class<? extends BEAN> beanType;
    private final Set<String> boundProperties;

    /**
     * Creates a new {@code BeanBinder} supporting beans of the given type.
     *
     * @param beanType
     *            the bean {@code Class} instance, not null
     */
    public BeanBinder(Class<? extends BEAN> beanType) {
        BeanUtil.checkBeanValidationAvailable();
        this.beanType = beanType;
        boundProperties = new HashSet<>();
    }

    @Override
    public <FIELDVALUE> BeanBindingBuilder<BEAN, FIELDVALUE> forField(
            HasValue<FIELDVALUE> field) {
        return (BeanBindingBuilder<BEAN, FIELDVALUE>) super.forField(field);
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
     * @return the newly created binding
     *
     * @throws IllegalArgumentException
     *             if the property name is invalid
     * @throws IllegalArgumentException
     *             if the property has no accessible getter
     *
     * @see #bind(HasValue, ValueProvider, Setter)
     */
    public <FIELDVALUE> Binding<BEAN, FIELDVALUE> bind(
            HasValue<FIELDVALUE> field, String propertyName) {
        return forField(field).bind(propertyName);
    }

    @Override
    public BeanBinder<BEAN> withValidator(Validator<? super BEAN> validator) {
        return (BeanBinder<BEAN>) super.withValidator(validator);
    }

    @Override
    protected <FIELDVALUE, TARGET> BeanBindingImpl<BEAN, FIELDVALUE, TARGET> createBinding(
            HasValue<FIELDVALUE> field, Converter<FIELDVALUE, TARGET> converter,
            BindingValidationStatusHandler handler) {
        Objects.requireNonNull(field, "field cannot be null");
        Objects.requireNonNull(converter, "converter cannot be null");
        return new BeanBindingImpl<>(this, field, converter, handler);
    }

    /**
     * Binds member fields found in the given object.
     * <p>
     * This method processes all (Java) member fields whose type extends
     * {@link HasValue} and that can be mapped to a property id. Property id
     * mapping is done based on the field name or on a @{@link PropertyId}
     * annotation on the field. All non-null unbound fields for which a property
     * id can be determined are bound to the property id.
     * </p>
     * <p>
     * For example:
     *
     * <pre>
     * public class MyForm extends VerticalLayout {
     * private TextField firstName = new TextField("First name");
     * &#64;PropertyId("last")
     * private TextField lastName = new TextField("Last name");
     *
     * MyForm myForm = new MyForm();
     * ...
     * binder.bindMemberFields(myForm);
     * </pre>
     *
     * </p>
     * This binds the firstName TextField to a "firstName" property in the item,
     * lastName TextField to a "last" property.
     * <p>
     * It's not always possible to bind a field to a property because their
     * types are incompatible. E.g. custom converter is required to bind
     * {@code HasValue<String>} and {@code Integer} property (that would be a
     * case of "age" property). In such case {@link IllegalStateException} will
     * be thrown unless the field has been configured manually before calling
     * the {@link #bindInstanceFields(Object)} method.
     * <p>
     * It's always possible to do custom binding for any field: the
     * {@link #bindInstanceFields(Object)} method doesn't override existing
     * bindings.
     *
     * @param objectWithMemberFields
     *            The object that contains (Java) member fields to bind
     * @throws IllegalStateException
     *             if there are incompatible HasValue<T> and property types
     */
    public void bindInstanceFields(Object objectWithMemberFields) {
        Class<?> objectClass = objectWithMemberFields.getClass();

        getFieldsInDeclareOrder(objectClass).stream()
                .filter(memberField -> HasValue.class
                        .isAssignableFrom(memberField.getType()))
                .forEach(memberField -> handleProperty(memberField,
                        (property, type) -> bindProperty(objectWithMemberFields,
                                memberField, property, type)));
    }

    /**
     * Binds {@code property} with {@code propertyType} to the field in the
     * {@code objectWithMemberFields} instance using {@code memberField} as a
     * reference to a member.
     *
     * @param objectWithMemberFields
     *            the object that contains (Java) member fields to build and
     *            bind
     * @param memberField
     *            reference to a member field to bind
     * @param property
     *            property name to bind
     * @param propertyType
     *            type of the property
     */
    protected void bindProperty(Object objectWithMemberFields,
            Field memberField, String property, Class<?> propertyType) {
        Type valueType = GenericTypeReflector.getTypeParameter(
                memberField.getGenericType(),
                HasValue.class.getTypeParameters()[0]);
        if (valueType == null) {
            throw new IllegalStateException(String.format(
                    "Unable to detect value type for the member '%s' in the "
                            + "class '%s'.",
                    memberField.getName(),
                    objectWithMemberFields.getClass().getName()));
        }
        if (propertyType.equals(GenericTypeReflector.erase(valueType))) {
            HasValue<?> field;
            // Get the field from the object
            try {
                field = (HasValue<?>) ReflectTools.getJavaFieldValue(
                        objectWithMemberFields, memberField, HasValue.class);
            } catch (IllegalArgumentException | IllegalAccessException
                    | InvocationTargetException e) {
                // If we cannot determine the value, just skip the field
                return;
            }
            if (field == null) {
                field = makeFieldInstance(
                        (Class<? extends HasValue<?>>) memberField.getType());
                initializeField(objectWithMemberFields, memberField, field);
            }
            forField(field).bind(property);
        } else {
            throw new IllegalStateException(String.format(
                    "Property type '%s' doesn't "
                            + "match the field type '%s'. "
                            + "Binding should be configured manually using converter.",
                    propertyType.getName(), valueType.getTypeName()));
        }
    }

    /**
     * Makes an instance of the field type {@code fieldClass}.
     * <p>
     * The resulting field instance is used to bind a property to it using the
     * {@link #bindInstanceFields(Object)} method.
     * <p>
     * The default implementation relies on the default constructor of the
     * class. If there is no suitable default constructor or you want to
     * configure the instantiated class then override this method and provide
     * your own implementation.
     *
     * @see #bindInstanceFields(Object)
     * @param fieldClass
     *            type of the field
     * @return a {@code fieldClass} instance object
     */
    protected HasValue<?> makeFieldInstance(
            Class<? extends HasValue<?>> fieldClass) {
        try {
            return fieldClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(
                    String.format("Couldn't create an '%s' type instance",
                            fieldClass.getName()),
                    e);
        }
    }

    /**
     * Returns an array containing {@link Field} objects reflecting all the
     * fields of the class or interface represented by this Class object. The
     * elements in the array returned are sorted in declare order from sub class
     * to super class.
     *
     * @param searchClass
     *            class to introspect
     * @return list of all fields in the class considering hierarchy
     */
    protected List<Field> getFieldsInDeclareOrder(Class<?> searchClass) {
        ArrayList<Field> memberFieldInOrder = new ArrayList<>();

        while (searchClass != null) {
            memberFieldInOrder
                    .addAll(Arrays.asList(searchClass.getDeclaredFields()));
            searchClass = searchClass.getSuperclass();
        }
        return memberFieldInOrder;
    }

    private void initializeField(Object objectWithMemberFields,
            Field memberField, HasValue<?> value) {
        try {
            ReflectTools.setJavaFieldValue(objectWithMemberFields, memberField,
                    value);
        } catch (IllegalArgumentException | IllegalAccessException
                | InvocationTargetException e) {
            throw new IllegalStateException(
                    String.format("Could not assign value to field '%s'",
                            memberField.getName()),
                    e);
        }
    }

    private void handleProperty(Field field,
            BiConsumer<String, Class<?>> propertyHandler) {
        Optional<PropertyDescriptor> descriptor = getPropertyDescriptor(field);

        if (!descriptor.isPresent()) {
            return;
        }

        String propertyName = descriptor.get().getName();
        if (boundProperties.contains(propertyName)) {
            return;
        }

        propertyHandler.accept(propertyName,
                descriptor.get().getPropertyType());
        boundProperties.add(propertyName);
    }

    private Optional<PropertyDescriptor> getPropertyDescriptor(Field field) {
        PropertyId propertyIdAnnotation = field.getAnnotation(PropertyId.class);

        String propertyId;
        if (propertyIdAnnotation != null) {
            // @PropertyId(propertyId) always overrides property id
            propertyId = propertyIdAnnotation.value();
        } else {
            propertyId = field.getName();
        }

        List<PropertyDescriptor> descriptors;
        try {
            descriptors = BeanUtil.getBeanPropertyDescriptors(beanType);
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(String.format(
                    "Could not resolve bean '%s' properties (see the cause):",
                    beanType.getName()), e);
        }
        Optional<PropertyDescriptor> propertyDescitpor = descriptors.stream()
                .filter(descriptor -> minifyFieldName(descriptor.getName())
                        .equals(minifyFieldName(propertyId)))
                .findFirst();
        return propertyDescitpor;
    }

    private String minifyFieldName(String fieldName) {
        return fieldName.toLowerCase(Locale.ENGLISH).replace("_", "");
    }

}
