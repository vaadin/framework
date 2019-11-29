/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.annotations.PropertyId;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.event.EventRouter;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.Setter;
import com.vaadin.server.UserError;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.util.ReflectTools;

/**
 * Connects one or more {@code Field} components to properties of a backing data
 * type such as a bean type. With a binder, input components can be grouped
 * together into forms to easily create and update business objects with little
 * explicit logic needed to move data between the UI and the data layers of the
 * application.
 * <p>
 * A binder is a collection of <i>bindings</i>, each representing the mapping of
 * a single field, through converters and validators, to a backing property.
 * <p>
 * A binder instance can be bound to a single bean instance at a time, but can
 * be rebound as needed. This allows usage patterns like a <i>master-details</i>
 * view, where a select component is used to pick the bean to edit.
 * <p>
 * Bean level validators can be added using the
 * {@link #withValidator(Validator)} method and will be run on the bound bean
 * once it has been updated from the values of the bound fields. Bean level
 * validators are also run as part of {@link #writeBean(Object)} and
 * {@link #writeBeanIfValid(Object)} if all field level validators pass.
 * <p>
 * Note: For bean level validators, the bean must be updated before the
 * validators are run. If a bean level validator fails in
 * {@link #writeBean(Object)} or {@link #writeBeanIfValid(Object)}, the bean
 * will be reverted to the previous state before returning from the method. You
 * should ensure that the getters/setters in the bean do not have side effects.
 * <p>
 * Unless otherwise specified, {@code Binder} method arguments cannot be null.
 *
 * @author Vaadin Ltd.
 *
 * @param <BEAN>
 *            the bean type
 *
 * @see BindingBuilder
 * @see Binding
 * @see HasValue
 *
 * @since 8.0
 */
public class Binder<BEAN> implements Serializable {

    /**
     * Represents the binding between a field and a data property.
     *
     * @param <BEAN>
     *            the bean type
     * @param <TARGET>
     *            the target data type of the binding, matches the field type
     *            unless a converter has been set
     *
     * @see Binder#forField(HasValue)
     */
    public interface Binding<BEAN, TARGET> extends Serializable {

        /**
         * Gets the field the binding uses.
         *
         * @return the field for the binding
         */
        public HasValue<?> getField();

        /**
         * Validates the field value and returns a {@code ValidationStatus}
         * instance representing the outcome of the validation. This method is a
         * short-hand for calling {@link #validate(boolean)} with
         * {@code fireEvent} {@code true}.
         *
         * @see #validate(boolean)
         * @see Binder#validate()
         * @see Validator#apply(Object, ValueContext)
         *
         * @return the validation result.
         */
        public default BindingValidationStatus<TARGET> validate() {
            return validate(true);
        }

        /**
         * Validates the field value and returns a {@code ValidationStatus}
         * instance representing the outcome of the validation.
         *
         * <strong>Note:</strong> Calling this method will not trigger the value
         * update in the bean automatically. This method will attempt to
         * temporarily apply all current changes to the bean and run full bean
         * validation for it. The changes are reverted after bean validation.
         *
         * @see #validate()
         * @see Binder#validate()
         *
         * @param fireEvent
         *            {@code true} to fire status event; {@code false} to not
         * @return the validation result.
         *
         * @since 8.2
         */
        public BindingValidationStatus<TARGET> validate(boolean fireEvent);

        /**
         * Gets the validation status handler for this Binding.
         *
         * @return the validation status handler for this binding
         *
         * @since 8.2
         */
        public BindingValidationStatusHandler getValidationStatusHandler();

        /**
         * Unbinds the binding from its respective {@code Binder} Removes any
         * {@code ValueChangeListener} {@code Registration} from associated
         * {@code HasValue}.
         *
         * @since 8.2
         */
        public void unbind();

        /**
         * Reads the value from given item and stores it to the bound field.
         *
         * @param bean
         *            the bean to read from
         *
         * @since 8.2
         */
        public void read(BEAN bean);

        /**
         * Sets the read-only status on for this Binding. Setting a Binding
         * read-only will mark the field read-only and not write any values from
         * the fields to the bean.
         * <p>
         * This helper method is the preferred way to control the read-only
         * state of the bound field.
         *
         * @param readOnly
         *            {@code true} to set binding read-only; {@code false} to
         *            enable writes
         * @since 8.4
         * @throws IllegalStateException
         *             if trying to make binding read-write and the setter is
         *             {@code null}
         */
        public void setReadOnly(boolean readOnly);

        /**
         * Gets the current read-only status for this Binding.
         *
         * @see #setReadOnly(boolean)
         *
         * @return {@code true} if read-only; {@code false} if not
         * @since 8.4
         */
        public boolean isReadOnly();

        /**
         * Gets the getter associated with this Binding.
         *
         * @return the getter
         * @since 8.4
         */
        public ValueProvider<BEAN, TARGET> getGetter();

        /**
         * Gets the setter associated with this Binding.
         *
         * @return the setter
         * @since 8.4
         */
        public Setter<BEAN, TARGET> getSetter();

        /**
         * Enable or disable asRequired validator.
         * The validator is enabled by default.
         *
         * @see #asRequired(String)
         * @see #asRequired(ErrorMessageProvider)
         *
         * @param asRequiredEnabled
         *            {@code false} if asRequired validator should
         *            be disabled, {@code true} otherwise (default)
         */
        public void setAsRequiredEnabled(boolean asRequiredEnabled);

        /**
         * Returns if asRequired validator is currently enabled or not
         *
         * @see #asRequired(String)
         * @see #asRequired(ErrorMessageProvider)
         *
         * @return {@code false} if asRequired validator is disabled
         *         {@code true} otherwise (default)
         */
        public boolean isAsRequiredEnabled();
    }

    /**
     * Creates a binding between a field and a data property.
     *
     * @param <BEAN>
     *            the bean type
     * @param <TARGET>
     *            the target data type of the binding, matches the field type
     *            until a converter has been set
     *
     * @see Binder#forField(HasValue)
     */
    public interface BindingBuilder<BEAN, TARGET> extends Serializable {

        /**
         * Gets the field the binding is being built for.
         *
         * @return the field this binding is being built for
         */
        public HasValue<?> getField();

        /**
         * Completes this binding using the given getter and setter functions
         * representing a backing bean property. The functions are used to
         * update the field value from the property and to store the field value
         * to the property, respectively.
         * <p>
         * When a bean is bound with {@link Binder#setBean(BEAN)}, the field
         * value is set to the return value of the given getter. The property
         * value is then updated via the given setter whenever the field value
         * changes. The setter may be null; in that case the property value is
         * never updated and the binding is said to be <i>read-only</i>.
         * <p>
         * If the Binder is already bound to some bean, the newly bound field is
         * associated with the corresponding bean property as described above.
         * <p>
         * The getter and setter can be arbitrary functions, for instance
         * implementing user-defined conversion or validation. However, in the
         * most basic use case you can simply pass a pair of method references
         * to this method as follows:
         *
         * <pre>
         * class Person {
         *     public String getName() { ... }
         *     public void setName(String name) { ... }
         * }
         *
         * TextField nameField = new TextField();
         * binder.forField(nameField).bind(Person::getName, Person::setName);
         * </pre>
         *
         * <p>
         * <strong>Note:</strong> when a {@code null} setter is given the field
         * will be marked as read-only by invoking
         * {@link HasValue#setReadOnly(boolean)}.
         *
         * @param getter
         *            the function to get the value of the property to the
         *            field, not null
         * @param setter
         *            the function to write the field value to the property or
         *            null if read-only
         * @return the newly created binding
         * @throws IllegalStateException
         *             if {@code bind} has already been called on this binding
         */
        public Binding<BEAN, TARGET> bind(ValueProvider<BEAN, TARGET> getter,
                Setter<BEAN, TARGET> setter);

        /**
         * Completes this binding by connecting the field to the property with
         * the given name. The getter and setter of the property are looked up
         * using a {@link PropertySet}.
         * <p>
         * For a <code>Binder</code> created using the
         * {@link Binder#Binder(Class)} constructor, introspection will be used
         * to find a Java Bean property. If a JSR-303 bean validation
         * implementation is present on the classpath, a {@link BeanValidator}
         * is also added to the binding.
         * <p>
         * The property must have an accessible getter method. It need not have
         * an accessible setter; in that case the property value is never
         * updated and the binding is said to be <i>read-only</i>.
         *
         * <p>
         * <strong>Note:</strong> when the binding is <i>read-only</i> the field
         * will be marked as read-only by invoking
         * {@link HasValue#setReadOnly(boolean)}.
         *
         * @param propertyName
         *            the name of the property to bind, not null
         * @return the newly created binding
         *
         * @throws IllegalArgumentException
         *             if the property name is invalid
         * @throws IllegalArgumentException
         *             if the property has no accessible getter
         * @throws IllegalStateException
         *             if the binder is not configured with an appropriate
         *             {@link PropertySet}
         *
         * @see Binder.BindingBuilder#bind(ValueProvider, Setter)
         */
        public Binding<BEAN, TARGET> bind(String propertyName);

        /**
         * Adds a validator to this binding. Validators are applied, in
         * registration order, when the field value is written to the backing
         * property. If any validator returns a failure, the property value is
         * not updated.
         *
         * @see #withValidator(SerializablePredicate, String)
         * @see #withValidator(SerializablePredicate, ErrorMessageProvider)
         *
         * @param validator
         *            the validator to add, not null
         * @return this binding, for chaining
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public BindingBuilder<BEAN, TARGET> withValidator(
                Validator<? super TARGET> validator);

        /**
         * A convenience method to add a validator to this binding using the
         * {@link Validator#from(SerializablePredicate, String)} factory method.
         * <p>
         * Validators are applied, in registration order, when the field value
         * is written to the backing property. If any validator returns a
         * failure, the property value is not updated.
         *
         * @see #withValidator(Validator)
         * @see #withValidator(SerializablePredicate, String, ErrorLevel)
         * @see #withValidator(SerializablePredicate, ErrorMessageProvider)
         * @see Validator#from(SerializablePredicate, String)
         *
         * @param predicate
         *            the predicate performing validation, not null
         * @param message
         *            the error message to report in case validation failure
         * @return this binding, for chaining
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public default BindingBuilder<BEAN, TARGET> withValidator(
                SerializablePredicate<? super TARGET> predicate,
                String message) {
            return withValidator(Validator.from(predicate, message));
        }

        /**
         * A convenience method to add a validator to this binding using the
         * {@link Validator#from(SerializablePredicate, String, ErrorLevel)}
         * factory method.
         * <p>
         * Validators are applied, in registration order, when the field value
         * is written to the backing property. If any validator returns a
         * failure, the property value is not updated.
         *
         * @see #withValidator(Validator)
         * @see #withValidator(SerializablePredicate, String)
         * @see #withValidator(SerializablePredicate, ErrorMessageProvider,
         *      ErrorLevel)
         * @see Validator#from(SerializablePredicate, String)
         *
         * @param predicate
         *            the predicate performing validation, not null
         * @param message
         *            the error message to report in case validation failure
         * @param errorLevel
         *            the error level for failures from this validator, not null
         * @return this binding, for chaining
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         *
         * @since 8.2
         */
        public default BindingBuilder<BEAN, TARGET> withValidator(
                SerializablePredicate<? super TARGET> predicate, String message,
                ErrorLevel errorLevel) {
            return withValidator(
                    Validator.from(predicate, message, errorLevel));
        }

        /**
         * A convenience method to add a validator to this binding using the
         * {@link Validator#from(SerializablePredicate, ErrorMessageProvider)}
         * factory method.
         * <p>
         * Validators are applied, in registration order, when the field value
         * is written to the backing property. If any validator returns a
         * failure, the property value is not updated.
         *
         * @see #withValidator(Validator)
         * @see #withValidator(SerializablePredicate, String)
         * @see #withValidator(SerializablePredicate, ErrorMessageProvider,
         *      ErrorLevel)
         * @see Validator#from(SerializablePredicate, ErrorMessageProvider)
         *
         * @param predicate
         *            the predicate performing validation, not null
         * @param errorMessageProvider
         *            the provider to generate error messages, not null
         * @return this binding, for chaining
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public default BindingBuilder<BEAN, TARGET> withValidator(
                SerializablePredicate<? super TARGET> predicate,
                ErrorMessageProvider errorMessageProvider) {
            return withValidator(
                    Validator.from(predicate, errorMessageProvider));
        }

        /**
         * A convenience method to add a validator to this binding using the
         * {@link Validator#from(SerializablePredicate, ErrorMessageProvider, ErrorLevel)}
         * factory method.
         * <p>
         * Validators are applied, in registration order, when the field value
         * is written to the backing property. If any validator returns a
         * failure, the property value is not updated.
         *
         * @see #withValidator(Validator)
         * @see #withValidator(SerializablePredicate, String, ErrorLevel)
         * @see #withValidator(SerializablePredicate, ErrorMessageProvider)
         * @see Validator#from(SerializablePredicate, ErrorMessageProvider,
         *      ErrorLevel)
         *
         * @param predicate
         *            the predicate performing validation, not null
         * @param errorMessageProvider
         *            the provider to generate error messages, not null
         * @param errorLevel
         *            the error level for failures from this validator, not null
         * @return this binding, for chaining
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         *
         * @since 8.2
         */
        public default BindingBuilder<BEAN, TARGET> withValidator(
                SerializablePredicate<? super TARGET> predicate,
                ErrorMessageProvider errorMessageProvider,
                ErrorLevel errorLevel) {
            return withValidator(Validator.from(predicate, errorMessageProvider,
                    errorLevel));
        }

        /**
         * Maps the binding to another data type using the given
         * {@link Converter}.
         * <p>
         * A converter is capable of converting between a presentation type,
         * which must match the current target data type of the binding, and a
         * model type, which can be any data type and becomes the new target
         * type of the binding. When invoking
         * {@link #bind(ValueProvider, Setter)}, the target type of the binding
         * must match the getter/setter types.
         * <p>
         * For instance, a {@code TextField} can be bound to an integer-typed
         * property using an appropriate converter such as a
         * {@link StringToIntegerConverter}.
         *
         * @param <NEWTARGET>
         *            the type to convert to
         * @param converter
         *            the converter to use, not null
         * @return a new binding with the appropriate type
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public <NEWTARGET> BindingBuilder<BEAN, NEWTARGET> withConverter(
                Converter<TARGET, NEWTARGET> converter);

        /**
         * Maps the binding to another data type using the mapping functions and
         * a possible exception as the error message.
         * <p>
         * The mapping functions are used to convert between a presentation
         * type, which must match the current target data type of the binding,
         * and a model type, which can be any data type and becomes the new
         * target type of the binding. When invoking
         * {@link #bind(ValueProvider, Setter)}, the target type of the binding
         * must match the getter/setter types.
         * <p>
         * For instance, a {@code TextField} can be bound to an integer-typed
         * property using appropriate functions such as:
         * <code>withConverter(Integer::valueOf, String::valueOf);</code>
         *
         * @param <NEWTARGET>
         *            the type to convert to
         * @param toModel
         *            the function which can convert from the old target type to
         *            the new target type
         * @param toPresentation
         *            the function which can convert from the new target type to
         *            the old target type
         * @return a new binding with the appropriate type
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public default <NEWTARGET> BindingBuilder<BEAN, NEWTARGET> withConverter(
                SerializableFunction<TARGET, NEWTARGET> toModel,
                SerializableFunction<NEWTARGET, TARGET> toPresentation) {
            return withConverter(Converter.from(toModel, toPresentation,
                    exception -> exception.getMessage()));
        }

        /**
         * Maps the binding to another data type using the mapping functions and
         * the given error error message if a value cannot be converted to the
         * new target type.
         * <p>
         * The mapping functions are used to convert between a presentation
         * type, which must match the current target data type of the binding,
         * and a model type, which can be any data type and becomes the new
         * target type of the binding. When invoking
         * {@link #bind(ValueProvider, Setter)}, the target type of the binding
         * must match the getter/setter types.
         * <p>
         * For instance, a {@code TextField} can be bound to an integer-typed
         * property using appropriate functions such as:
         * <code>withConverter(Integer::valueOf, String::valueOf);</code>
         *
         * @param <NEWTARGET>
         *            the type to convert to
         * @param toModel
         *            the function which can convert from the old target type to
         *            the new target type
         * @param toPresentation
         *            the function which can convert from the new target type to
         *            the old target type
         * @param errorMessage
         *            the error message to use if conversion using
         *            <code>toModel</code> fails
         * @return a new binding with the appropriate type
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public default <NEWTARGET> BindingBuilder<BEAN, NEWTARGET> withConverter(
                SerializableFunction<TARGET, NEWTARGET> toModel,
                SerializableFunction<NEWTARGET, TARGET> toPresentation,
                String errorMessage) {
            return withConverter(Converter.from(toModel, toPresentation,
                    exception -> errorMessage));
        }

        /**
         * Maps binding value {@code null} to given null representation and back
         * to {@code null} when converting back to model value.
         *
         * @param nullRepresentation
         *            the value to use instead of {@code null}
         * @return a new binding with null representation handling.
         */
        public default BindingBuilder<BEAN, TARGET> withNullRepresentation(
                TARGET nullRepresentation) {
            return withConverter(
                    fieldValue -> Objects.equals(fieldValue, nullRepresentation)
                            ? null
                            : fieldValue,
                    modelValue -> Objects.isNull(modelValue)
                            ? nullRepresentation
                            : modelValue);
        }

        /**
         * Sets the given {@code label} to show an error message if validation
         * fails.
         * <p>
         * The validation state of each field is updated whenever the user
         * modifies the value of that field. The validation state is by default
         * shown using {@link AbstractComponent#setComponentError} which is used
         * by the layout that the field is shown in. Most built-in layouts will
         * show this as a red exclamation mark icon next to the component, so
         * that hovering or tapping the icon shows a tooltip with the message
         * text.
         * <p>
         * This method allows to customize the way a binder displays error
         * messages to get more flexibility than what
         * {@link AbstractComponent#setComponentError} provides (it replaces the
         * default behavior).
         * <p>
         * This is just a shorthand for
         * {@link #withValidationStatusHandler(BindingValidationStatusHandler)}
         * method where the handler instance hides the {@code label} if there is
         * no error and shows it with validation error message if validation
         * fails. It means that it cannot be called after
         * {@link #withValidationStatusHandler(BindingValidationStatusHandler)}
         * method call or
         * {@link #withValidationStatusHandler(BindingValidationStatusHandler)}
         * after this method call.
         *
         * @see #withValidationStatusHandler(BindingValidationStatusHandler)
         * @see AbstractComponent#setComponentError(ErrorMessage)
         * @param label
         *            label to show validation status for the field
         * @return this binding, for chaining
         */
        public default BindingBuilder<BEAN, TARGET> withStatusLabel(
                Label label) {
            return withValidationStatusHandler(status -> {
                label.setValue(status.getMessage().orElse(""));
                // Only show the label when validation has failed
                label.setVisible(status.isError());
            });
        }

        /**
         * Sets a {@link BindingValidationStatusHandler} to track validation
         * status changes.
         * <p>
         * The validation state of each field is updated whenever the user
         * modifies the value of that field. The validation state is by default
         * shown using {@link AbstractComponent#setComponentError} which is used
         * by the layout that the field is shown in. Most built-in layouts will
         * show this as a red exclamation mark icon next to the component, so
         * that hovering or tapping the icon shows a tooltip with the message
         * text.
         * <p>
         * This method allows to customize the way a binder displays error
         * messages to get more flexibility than what
         * {@link AbstractComponent#setComponentError} provides (it replaces the
         * default behavior).
         * <p>
         * The method may be called only once. It means there is no chain unlike
         * {@link #withValidator(Validator)} or
         * {@link #withConverter(Converter)}. Also it means that the shorthand
         * method {@link #withStatusLabel(Label)} also may not be called after
         * this method.
         *
         * @see #withStatusLabel(Label)
         * @see AbstractComponent#setComponentError(ErrorMessage)
         * @param handler
         *            status change handler
         * @return this binding, for chaining
         */
        public BindingBuilder<BEAN, TARGET> withValidationStatusHandler(
                BindingValidationStatusHandler handler);

        /**
         * Sets the field to be required. This means two things:
         * <ol>
         * <li>the required indicator will be displayed for this field</li>
         * <li>the field value is validated for not being empty, i.e. that the
         * field's value is not equal to what {@link HasValue#getEmptyValue()}
         * returns</li>
         * </ol>
         * <p>
         * For localizing the error message, use
         * {@link #asRequired(ErrorMessageProvider)}.
         *
         * @see #asRequired(ErrorMessageProvider)
         * @see HasValue#setRequiredIndicatorVisible(boolean)
         * @see HasValue#isEmpty()
         * @param errorMessage
         *            the error message to show for the invalid value
         * @return this binding, for chaining
         */
        public default BindingBuilder<BEAN, TARGET> asRequired(
                String errorMessage) {
            return asRequired(context -> errorMessage);
        }

        /**
         * Sets the field to be required. This means two things:
         * <ol>
         * <li>the required indicator will be displayed for this field</li>
         * <li>the field value is validated for not being empty, i.e. that the
         * field's value is not equal to what {@link HasValue#getEmptyValue()}
         * returns</li>
         * </ol>
         * <p>
         * For setting an error message, use {@link #asRequired(String)}.
         * <p>
         * For localizing the error message, use
         * {@link #asRequired(ErrorMessageProvider)}.
         *
         * @see #asRequired(String)
         * @see #asRequired(ErrorMessageProvider)
         * @see HasValue#setRequiredIndicatorVisible(boolean)
         * @see HasValue#isEmpty()
         * @return this binding, for chaining
         * @since 8.2
         */
        public default BindingBuilder<BEAN, TARGET> asRequired() {
            return asRequired(context -> "");
        }

        /**
         * Sets the field to be required. This means two things:
         * <ol>
         * <li>the required indicator will be displayed for this field</li>
         * <li>the field value is validated for not being empty, i.e. that the
         * field's value is not equal to what {@link HasValue#getEmptyValue()}
         * returns</li>
         * </ol>
         *
         * @see HasValue#setRequiredIndicatorVisible(boolean)
         * @see HasValue#isEmpty()
         * @param errorMessageProvider
         *            the provider for localized validation error message
         * @return this binding, for chaining
         */
        public BindingBuilder<BEAN, TARGET> asRequired(
                ErrorMessageProvider errorMessageProvider);

        /**
         * Sets the field to be required and delegates the required check to a
         * custom validator. This means two things:
         * <ol>
         * <li>the required indicator will be displayed for this field</li>
         * <li>the field value is validated by {@code requiredValidator}</li>
         * </ol>
         *
         * @see HasValue#setRequiredIndicatorVisible(boolean)
         * @param requiredValidator
         *            validator responsible for the required check
         * @return this binding, for chaining
         * @since 8.4
         */
        public BindingBuilder<BEAN, TARGET> asRequired(
                Validator<TARGET> requiredValidator);
    }

    /**
     * An internal implementation of {@code BindingBuilder}.
     *
     * @param <BEAN>
     *            the bean type, must match the Binder bean type
     * @param <FIELDVALUE>
     *            the value type of the field
     * @param <TARGET>
     *            the target data type of the binding, matches the field type
     *            until a converter has been set
     */
    protected static class BindingBuilderImpl<BEAN, FIELDVALUE, TARGET>
            implements BindingBuilder<BEAN, TARGET> {

        private Binder<BEAN> binder;

        private final HasValue<FIELDVALUE> field;
        private BindingValidationStatusHandler statusHandler;
        private boolean isStatusHandlerChanged;

        private boolean bound;

        /**
         * Contains all converters and validators chained together in the
         * correct order.
         */
        private Converter<FIELDVALUE, ?> converterValidatorChain;

        private boolean asRequiredSet;

        /**
         * Creates a new binding builder associated with the given field.
         * Initializes the builder with the given converter chain and status
         * change handler.
         *
         * @param binder
         *            the binder this instance is connected to, not null
         * @param field
         *            the field to bind, not null
         * @param converterValidatorChain
         *            the converter/validator chain to use, not null
         * @param statusHandler
         *            the handler to track validation status, not null
         */
        protected BindingBuilderImpl(Binder<BEAN> binder,
                HasValue<FIELDVALUE> field,
                Converter<FIELDVALUE, TARGET> converterValidatorChain,
                BindingValidationStatusHandler statusHandler) {
            this.field = field;
            this.binder = binder;
            this.converterValidatorChain = converterValidatorChain;
            this.statusHandler = statusHandler;
        }

        @Override
        public Binding<BEAN, TARGET> bind(ValueProvider<BEAN, TARGET> getter,
                Setter<BEAN, TARGET> setter) {
            checkUnbound();
            Objects.requireNonNull(getter, "getter cannot be null");

            BindingImpl<BEAN, FIELDVALUE, TARGET> binding = new BindingImpl<>(
                    this, getter, setter);

            getBinder().bindings.add(binding);
            if (getBinder().getBean() != null) {
                binding.initFieldValue(getBinder().getBean(), true);
            }
            if (setter == null) {
                binding.getField().setReadOnly(true);
            }
            getBinder().fireStatusChangeEvent(false);

            bound = true;
            getBinder().incompleteBindings.remove(getField());

            return binding;
        }

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Binding<BEAN, TARGET> bind(String propertyName) {
            Objects.requireNonNull(propertyName,
                    "Property name cannot be null");
            checkUnbound();

            PropertyDefinition<BEAN, ?> definition = getBinder().propertySet
                    .getProperty(propertyName)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Could not resolve property name " + propertyName
                                    + " from " + getBinder().propertySet));

            ValueProvider<BEAN, ?> getter = definition.getGetter();
            Setter<BEAN, ?> setter = definition.getSetter().orElse(null);
            if (setter == null) {
                getLogger().fine(() -> propertyName
                        + " does not have an accessible setter");
            }

            BindingBuilder<BEAN, ?> finalBinding = withConverter(
                    createConverter(definition.getType()), false);

            finalBinding = getBinder().configureBinding(finalBinding,
                    definition);

            try {
                Binding binding = ((BindingBuilder) finalBinding).bind(getter,
                        setter);
                getBinder().boundProperties.put(propertyName, binding);
                return binding;
            } finally {
                getBinder().incompleteMemberFieldBindings.remove(getField());
            }
        }

        @SuppressWarnings("unchecked")
        private Converter<TARGET, Object> createConverter(Class<?> getterType) {
            return Converter.from(fieldValue -> getterType.cast(fieldValue),
                    propertyValue -> (TARGET) propertyValue, exception -> {
                        throw new RuntimeException(exception);
                    });
        }

        @Override
        public BindingBuilder<BEAN, TARGET> withValidator(
                Validator<? super TARGET> validator) {
            checkUnbound();
            Objects.requireNonNull(validator, "validator cannot be null");

            converterValidatorChain = ((Converter<FIELDVALUE, TARGET>) converterValidatorChain)
                    .chain(new ValidatorAsConverter<>(validator));
            return this;
        }

        @Override
        public <NEWTARGET> BindingBuilder<BEAN, NEWTARGET> withConverter(
                Converter<TARGET, NEWTARGET> converter) {
            return withConverter(converter, true);
        }

        @Override
        public BindingBuilder<BEAN, TARGET> withValidationStatusHandler(
                BindingValidationStatusHandler handler) {
            checkUnbound();
            Objects.requireNonNull(handler, "handler cannot be null");
            if (isStatusHandlerChanged) {
                throw new IllegalStateException("A "
                        + BindingValidationStatusHandler.class.getSimpleName()
                        + " has already been set");
            }
            isStatusHandlerChanged = true;
            statusHandler = handler;
            return this;
        }

        @Override
        public BindingBuilder<BEAN, TARGET> asRequired(
                ErrorMessageProvider errorMessageProvider) {
            return asRequired(Validator.from(
                    value -> !Objects.equals(value, field.getEmptyValue()),
                    errorMessageProvider));
        }

        @Override
        public BindingBuilder<BEAN, TARGET> asRequired(
                Validator<TARGET> customRequiredValidator) {
            checkUnbound();
            this.asRequiredSet = true;
            field.setRequiredIndicatorVisible(true);
            return withValidator((value, context) -> {
                if (!field.isRequiredIndicatorVisible())
                    return ValidationResult.ok();
                else
                    return customRequiredValidator.apply(value, context);
            });
        }

        /**
         * Implements {@link #withConverter(Converter)} method with additional
         * possibility to disable (reset) default null representation converter.
         * <p>
         * The method {@link #withConverter(Converter)} calls this method with
         * {@code true} provided as the second argument value.
         *
         * @see #withConverter(Converter)
         *
         * @param converter
         *            the converter to use, not null
         * @param resetNullRepresentation
         *            if {@code true} then default null representation will be
         *            deactivated (if not yet), otherwise it won't be removed
         * @return a new binding with the appropriate type
         * @param <NEWTARGET>
         *            the type to convert to
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        protected <NEWTARGET> BindingBuilder<BEAN, NEWTARGET> withConverter(
                Converter<TARGET, NEWTARGET> converter,
                boolean resetNullRepresentation) {
            checkUnbound();
            Objects.requireNonNull(converter, "converter cannot be null");

            if (resetNullRepresentation) {
                getBinder().initialConverters.get(field).setIdentity();
            }

            converterValidatorChain = ((Converter<FIELDVALUE, TARGET>) converterValidatorChain)
                    .chain(converter);

            return (BindingBuilder<BEAN, NEWTARGET>) this;
        }

        /**
         * Returns the {@code Binder} connected to this {@code Binding}
         * instance.
         *
         * @return the binder
         */
        protected Binder<BEAN> getBinder() {
            return binder;
        }

        /**
         * Throws if this binding is already completed and cannot be modified
         * anymore.
         *
         * @throws IllegalStateException
         *             if this binding is already bound
         */
        protected void checkUnbound() {
            if (bound) {
                throw new IllegalStateException(
                        "cannot modify binding: already bound to a property");
            }
        }

        @Override
        public HasValue<FIELDVALUE> getField() {
            return field;
        }
    }

    /**
     * An internal implementation of {@code Binding}.
     *
     * @param <BEAN>
     *            the bean type, must match the Binder bean type
     * @param <FIELDVALUE>
     *            the value type of the field
     * @param <TARGET>
     *            the target data type of the binding, matches the field type
     *            unless a converter has been set
     */
    protected static class BindingImpl<BEAN, FIELDVALUE, TARGET>
            implements Binding<BEAN, TARGET> {

        private Binder<BEAN> binder;

        private HasValue<FIELDVALUE> field;
        private final BindingValidationStatusHandler statusHandler;

        private final ValueProvider<BEAN, TARGET> getter;
        private final Setter<BEAN, TARGET> setter;

        private boolean readOnly;

        private Registration onValueChange;
        private boolean valueInit = false;

        /**
         * Contains all converters and validators chained together in the
         * correct order.
         */
        private final Converter<FIELDVALUE, TARGET> converterValidatorChain;

        private boolean asRequiredSet;

        public BindingImpl(BindingBuilderImpl<BEAN, FIELDVALUE, TARGET> builder,
                ValueProvider<BEAN, TARGET> getter,
                Setter<BEAN, TARGET> setter) {
            this.binder = builder.getBinder();
            this.field = builder.field;
            this.statusHandler = builder.statusHandler;
            this.asRequiredSet = builder.asRequiredSet;
            converterValidatorChain = ((Converter<FIELDVALUE, TARGET>) builder.converterValidatorChain);

            onValueChange = getField()
                    .addValueChangeListener(this::handleFieldValueChange);

            this.getter = getter;
            this.setter = setter;
            readOnly = setter == null;
        }

        @Override
        public HasValue<FIELDVALUE> getField() {
            return field;
        }

        /**
         * Finds an appropriate locale to be used in conversion and validation.
         *
         * @return the found locale, not null
         */
        protected Locale findLocale() {
            Locale l = null;
            if (getField() instanceof Component) {
                l = ((Component) getField()).getLocale();
            }
            if (l == null && UI.getCurrent() != null) {
                l = UI.getCurrent().getLocale();
            }
            if (l == null) {
                l = Locale.getDefault();
            }
            return l;
        }

        @Override
        public BindingValidationStatus<TARGET> validate(boolean fireEvent) {
            Objects.requireNonNull(binder,
                    "This Binding is no longer attached to a Binder");
            BindingValidationStatus<TARGET> status = doValidation();
            if (fireEvent) {
                getBinder().getValidationStatusHandler()
                        .statusChange(new BinderValidationStatus<>(getBinder(),
                                Arrays.asList(status),
                                Collections.emptyList()));
                getBinder().fireStatusChangeEvent(status.isError());
            }
            return status;
        }

        /**
         * Removes this binding from its binder and unregisters the
         * {@code ValueChangeListener} from any bound {@code HasValue}. It does
         * nothing if it is called for an already unbound binding.
         *
         * @since 8.2
         */
        @Override
        public void unbind() {
            if (onValueChange != null) {
                onValueChange.remove();
                onValueChange = null;
            }

            if (binder != null) {
                binder.removeBindingInternal(this);
                binder = null;
            }

            field = null;
        }

        /**
         * Returns the field value run through all converters and validators,
         * but doesn't pass the {@link BindingValidationStatus} to any status
         * handler.
         *
         * @return the result of the conversion
         */
        private Result<TARGET> doConversion() {
            FIELDVALUE fieldValue = field.getValue();
            return converterValidatorChain.convertToModel(fieldValue,
                    createValueContext());
        }

        private BindingValidationStatus<TARGET> toValidationStatus(
                Result<TARGET> result) {
            return new BindingValidationStatus<>(result, this);
        }

        /**
         * Returns the field value run through all converters and validators,
         * but doesn't pass the {@link BindingValidationStatus} to any status
         * handler.
         *
         * @return the validation status
         */
        private BindingValidationStatus<TARGET> doValidation() {
            return toValidationStatus(doConversion());
        }

        /**
         * Creates a value context from the current state of the binding and its
         * field.
         *
         * @return the value context
         */
        protected ValueContext createValueContext() {
            if (field instanceof Component) {
                return new ValueContext((Component) field, field);
            }
            return new ValueContext(null, field, findLocale());
        }

        /**
         * Sets the field value by invoking the getter function on the given
         * bean. The default listener attached to the field will be removed for
         * the duration of this update.
         *
         * @param bean
         *            the bean to fetch the property value from
         * @param writeBackChangedValues
         *            <code>true</code> if the bean value should be updated if
         *            the value is different after converting to and from the
         *            presentation value; <code>false</code> to avoid updating
         *            the bean value
         */
        private void initFieldValue(BEAN bean, boolean writeBackChangedValues) {
            assert bean != null;
            assert onValueChange != null;
            valueInit = true;
            try {
                TARGET originalValue = getter.apply(bean);
                convertAndSetFieldValue(originalValue);

                if (writeBackChangedValues && setter != null) {
                    doConversion().ifOk(convertedValue -> {
                        if (!Objects.equals(originalValue, convertedValue)) {
                            setter.accept(bean, convertedValue);
                        }
                    });
                }
            } finally {
                valueInit = false;
            }
        }

        private FIELDVALUE convertToFieldType(TARGET target) {
            ValueContext valueContext = createValueContext();
            return converterValidatorChain.convertToPresentation(target,
                    valueContext);
        }

        /**
         * Handles the value change triggered by the bound field.
         *
         * @param event
         */
        private void handleFieldValueChange(
                ValueChangeEvent<FIELDVALUE> event) {
            // Don't handle change events when setting initial value
            if (valueInit) {
                return;
            }

            if (binder != null) {
                // Inform binder of changes; if setBean: writeIfValid
                getBinder().handleFieldValueChange(this, event);
                getBinder().fireValueChangeEvent(event);
            }
        }

        /**
         * Write the field value by invoking the setter function on the given
         * bean, if the value passes all registered validators.
         *
         * @param bean
         *            the bean to set the property value to
         */
        private BindingValidationStatus<TARGET> writeFieldValue(BEAN bean) {
            assert bean != null;

            Result<TARGET> result = doConversion();
            if (!isReadOnly()) {
                result.ifOk(value -> setter.accept(bean, value));
            }
            return toValidationStatus(result);
        }

        /**
         * Returns the {@code Binder} connected to this {@code Binding}
         * instance.
         *
         * @return the binder
         */
        protected Binder<BEAN> getBinder() {
            return binder;
        }

        @Override
        public BindingValidationStatusHandler getValidationStatusHandler() {
            return statusHandler;
        }

        @Override
        public void read(BEAN bean) {
            convertAndSetFieldValue(getter.apply(bean));
        }

        private void convertAndSetFieldValue(TARGET modelValue) {
            FIELDVALUE convertedValue = convertToFieldType(modelValue);
            try {
                getField().setValue(convertedValue);
            } catch (RuntimeException e) {
                /*
                 * Add an additional hint to the exception for the typical case
                 * with a field that doesn't accept null values. The non-null
                 * empty value is used as a heuristic to determine that the
                 * field doesn't accept null rather than throwing for some other
                 * reason.
                 */
                if (convertedValue == null && getField().getEmptyValue() != null) {
                    throw new IllegalStateException(String.format(
                            "A field of type %s didn't accept a null value."
                                    + " If null values are expected, then configure a null representation for the binding.",
                            field.getClass().getName()), e);
                } else {
                    // Otherwise, let the original exception speak for itself
                    throw e;
                }
            }
        }

        @Override
        public void setReadOnly(boolean readOnly) {
            if (setter == null && !readOnly) {
                throw new IllegalStateException(
                        "Binding with a null setter has to be read-only");
            }
            this.readOnly = readOnly;
            getField().setReadOnly(readOnly);
        }

        @Override
        public boolean isReadOnly() {
            return readOnly;
        }

        @Override
        public ValueProvider<BEAN, TARGET> getGetter() {
            return getter;
        }

        @Override
        public Setter<BEAN, TARGET> getSetter() {
            return setter;
        }

        @Override
        public void setAsRequiredEnabled(boolean asRequiredEnabled) {
            if (!asRequiredSet) {
                throw new IllegalStateException(
                 "Unable to toggle asRequired validation since " 
                         + "asRequired has not been set.");
            }
            if (asRequiredEnabled != isAsRequiredEnabled()) {
                field.setRequiredIndicatorVisible(asRequiredEnabled);
                        validate();
            }
        }

        @Override
        public boolean isAsRequiredEnabled() {
            return field.isRequiredIndicatorVisible();
        }
    }

    /**
     * Wraps a validator as a converter.
     * <p>
     * The type of the validator must be of the same type as this converter or a
     * super type of it.
     *
     * @param <T>
     *            the type of the converter
     */
    private static class ValidatorAsConverter<T> implements Converter<T, T> {

        private final Validator<? super T> validator;

        /**
         * Creates a new converter wrapping the given validator.
         *
         * @param validator
         *            the validator to wrap
         */
        public ValidatorAsConverter(Validator<? super T> validator) {
            this.validator = validator;
        }

        @Override
        public Result<T> convertToModel(T value, ValueContext context) {
            ValidationResult validationResult = validator.apply(value, context);
            return new ValidationResultWrap<>(value, validationResult);
        }

        @Override
        public T convertToPresentation(T value, ValueContext context) {
            return value;
        }

    }

    /**
     * Converter decorator-strategy pattern to use initially provided "delegate"
     * converter to execute its logic until the {@code setIdentity()} method is
     * called. Once the method is called the class changes its behavior to the
     * same as {@link Converter#identity()} behavior.
     */
    private static class ConverterDelegate<FIELDVALUE>
            implements Converter<FIELDVALUE, FIELDVALUE> {

        private Converter<FIELDVALUE, FIELDVALUE> delegate;

        private ConverterDelegate(Converter<FIELDVALUE, FIELDVALUE> converter) {
            delegate = converter;
        }

        @Override
        public Result<FIELDVALUE> convertToModel(FIELDVALUE value,
                ValueContext context) {
            if (delegate == null) {
                return Result.ok(value);
            } else {
                return delegate.convertToModel(value, context);
            }
        }

        @Override
        public FIELDVALUE convertToPresentation(FIELDVALUE value,
                ValueContext context) {
            if (delegate == null) {
                return value;
            } else {
                return delegate.convertToPresentation(value, context);
            }
        }

        void setIdentity() {
            delegate = null;
        }
    }

    private final PropertySet<BEAN> propertySet;

    /**
     * Property names that have been used for creating a binding.
     */
    private final Map<String, Binding<BEAN, ?>> boundProperties = new HashMap<>();

    private final Map<HasValue<?>, BindingBuilder<BEAN, ?>> incompleteMemberFieldBindings = new IdentityHashMap<>();

    private BEAN bean;

    private final Collection<Binding<BEAN, ?>> bindings = new ArrayList<>();

    private final Map<HasValue<?>, BindingBuilder<BEAN, ?>> incompleteBindings = new IdentityHashMap<>();

    private final List<Validator<? super BEAN>> validators = new ArrayList<>();

    private final Map<HasValue<?>, ConverterDelegate<?>> initialConverters = new IdentityHashMap<>();

    private EventRouter eventRouter;

    private Label statusLabel;

    private BinderValidationStatusHandler<BEAN> statusHandler;

    private Set<Binding<BEAN, ?>> changedBindings = new LinkedHashSet<>();

    /**
     * Creates a binder using a custom {@link PropertySet} implementation for
     * finding and resolving property names for
     * {@link #bindInstanceFields(Object)}, {@link #bind(HasValue, String)} and
     * {@link BindingBuilder#bind(String)}.
     *
     * @param propertySet
     *            the property set implementation to use, not <code>null</code>.
     */
    protected Binder(PropertySet<BEAN> propertySet) {
        Objects.requireNonNull(propertySet, "propertySet cannot be null");
        this.propertySet = propertySet;
    }

    /**
     * Informs the Binder that a value in Binding was changed. This method will
     * trigger validating and writing of the whole bean if using
     * {@link #setBean(Object)}. If using {@link #readBean(Object)} only the
     * field validation is run.
     *
     * @param binding
     *            the binding whose value has been changed
     * @param event
     *            the value change event
     * @since 8.2
     */
    protected void handleFieldValueChange(Binding<BEAN, ?> binding,
            ValueChangeEvent<?> event) {
        changedBindings.add(binding);
        if (getBean() != null) {
            doWriteIfValid(getBean(), changedBindings);
        } else {
            binding.validate();
        }
    }

    /**
     * Creates a new binder that uses reflection based on the provided bean type
     * to resolve bean properties.
     *
     * @param beanType
     *            the bean type to use, not <code>null</code>
     */
    public Binder(Class<BEAN> beanType) {
        this(BeanPropertySet.get(beanType));
    }

    /**
     * Creates a new binder that uses reflection based on the provided bean type
     * to resolve bean properties.
     *
     * @param beanType
     *            the bean type to use, not {@code null}
     * @param scanNestedDefinitions
     *            if {@code true}, scan for nested property definitions as well
     * @since 8.2
     */
    public Binder(Class<BEAN> beanType, boolean scanNestedDefinitions) {
        this(BeanPropertySet.get(beanType, scanNestedDefinitions,
                PropertyFilterDefinition.getDefaultFilter()));
    }

    /**
     * Creates a new binder without support for creating bindings based on
     * property names. Use an alternative constructor, such as
     * {@link Binder#Binder(Class)}, to create a binder that support creating
     * bindings based on instance fields through
     * {@link #bindInstanceFields(Object)}, or based on a property name through
     * {@link #bind(HasValue, String)} or {@link BindingBuilder#bind(String)}.
     */
    public Binder() {
        this(new PropertySet<BEAN>() {
            @Override
            public Stream<PropertyDefinition<BEAN, ?>> getProperties() {
                throw new IllegalStateException(
                        "This Binder instance was created using the default constructor. "
                                + "To be able to use property names and bind to instance fields, create the binder using the Binder(Class<BEAN> beanType) constructor instead.");
            }

            @Override
            public Optional<PropertyDefinition<BEAN, ?>> getProperty(
                    String name) {
                throw new IllegalStateException(
                        "This Binder instance was created using the default constructor. "
                                + "To be able to use property names and bind to instance fields, create the binder using the Binder(Class<BEAN> beanType) constructor instead.");
            }
        });
    }

    /**
     * Creates a binder using a custom {@link PropertySet} implementation for
     * finding and resolving property names for
     * {@link #bindInstanceFields(Object)}, {@link #bind(HasValue, String)} and
     * {@link BindingBuilder#bind(String)}.
     * <p>
     * This functionality is provided as static method instead of as a public
     * constructor in order to make it possible to use a custom property set
     * without creating a subclass while still leaving the public constructors
     * focused on the common use cases.
     *
     * @see Binder#Binder()
     * @see Binder#Binder(Class)
     *
     * @param propertySet
     *            the property set implementation to use, not <code>null</code>.
     * @return a new binder using the provided property set, not
     *         <code>null</code>
     */
    public static <BEAN> Binder<BEAN> withPropertySet(
            PropertySet<BEAN> propertySet) {
        return new Binder<>(propertySet);
    }

    /**
     * Returns the bean that has been bound with {@link #bind}, or null if a
     * bean is not currently bound.
     *
     * @return the currently bound bean if any
     */
    public BEAN getBean() {
        return bean;
    }

    /**
     * Creates a new binding for the given field. The returned builder may be
     * further configured before invoking
     * {@link BindingBuilder#bind(ValueProvider, Setter)} which completes the
     * binding. Until {@code Binding.bind} is called, the binding has no effect.
     * <p>
     * <strong>Note:</strong> Not all {@link HasValue} implementations support
     * passing {@code null} as the value. For these the Binder will
     * automatically change {@code null} to a null representation provided by
     * {@link HasValue#getEmptyValue()}. This conversion is one-way only, if you
     * want to have a two-way mapping back to {@code null}, use
     * {@link BindingBuilder#withNullRepresentation(Object)}.
     *
     * @param <FIELDVALUE>
     *            the value type of the field
     * @param field
     *            the field to be bound, not null
     * @return the new binding
     *
     * @see #bind(HasValue, ValueProvider, Setter)
     */
    public <FIELDVALUE> BindingBuilder<BEAN, FIELDVALUE> forField(
            HasValue<FIELDVALUE> field) {
        Objects.requireNonNull(field, "field cannot be null");
        // clear previous errors for this field and any bean level validation
        clearError(field);
        getStatusLabel().ifPresent(label -> label.setValue(""));

        return createBinding(field, createNullRepresentationAdapter(field),
                this::handleValidationStatus)
                        .withValidator(field.getDefaultValidator());
    }

    /**
     * Creates a new binding for the given field. The returned builder may be
     * further configured before invoking {@link #bindInstanceFields(Object)}.
     * Unlike with the {@link #forField(HasValue)} method, no explicit call to
     * {@link BindingBuilder#bind(String)} is needed to complete this binding in
     * the case that the name of the field matches a field name found in the
     * bean.
     *
     * @param <FIELDVALUE>
     *            the value type of the field
     * @param field
     *            the field to be bound, not null
     * @return the new binding builder
     *
     * @see #forField(HasValue)
     * @see #bindInstanceFields(Object)
     */
    public <FIELDVALUE> BindingBuilder<BEAN, FIELDVALUE> forMemberField(
            HasValue<FIELDVALUE> field) {
        incompleteMemberFieldBindings.put(field, null);
        return forField(field);
    }

    /**
     * Binds a field to a bean property represented by the given getter and
     * setter pair. The functions are used to update the field value from the
     * property and to store the field value to the property, respectively.
     * <p>
     * Use the {@link #forField(HasValue)} overload instead if you want to
     * further configure the new binding.
     * <p>
     * <strong>Note:</strong> Not all {@link HasValue} implementations support
     * passing {@code null} as the value. For these the Binder will
     * automatically change {@code null} to a null representation provided by
     * {@link HasValue#getEmptyValue()}. This conversion is one-way only, if you
     * want to have a two-way mapping back to {@code null}, use
     * {@link #forField(HasValue)} and
     * {@link BindingBuilder#withNullRepresentation(Object)}.
     * <p>
     * When a bean is bound with {@link Binder#setBean(BEAN)}, the field value
     * is set to the return value of the given getter. The property value is
     * then updated via the given setter whenever the field value changes. The
     * setter may be null; in that case the property value is never updated and
     * the binding is said to be <i>read-only</i>.
     * <p>
     * If the Binder is already bound to some bean, the newly bound field is
     * associated with the corresponding bean property as described above.
     * <p>
     * The getter and setter can be arbitrary functions, for instance
     * implementing user-defined conversion or validation. However, in the most
     * basic use case you can simply pass a pair of method references to this
     * method as follows:
     *
     * <pre>
     * class Person {
     *     public String getName() { ... }
     *     public void setName(String name) { ... }
     * }
     *
     * TextField nameField = new TextField();
     * binder.bind(nameField, Person::getName, Person::setName);
     * </pre>
     *
     * <p>
     * <strong>Note:</strong> when a {@code null} setter is given the field will
     * be marked as read-only by invoking {@link HasValue#setReadOnly(boolean)}.
     *
     * @param <FIELDVALUE>
     *            the value type of the field
     * @param field
     *            the field to bind, not null
     * @param getter
     *            the function to get the value of the property to the field,
     *            not null
     * @param setter
     *            the function to write the field value to the property or null
     *            if read-only
     * @return the newly created binding
     */
    public <FIELDVALUE> Binding<BEAN, FIELDVALUE> bind(
            HasValue<FIELDVALUE> field, ValueProvider<BEAN, FIELDVALUE> getter,
            Setter<BEAN, FIELDVALUE> setter) {
        return forField(field).bind(getter, setter);
    }

    /**
     * Binds the given field to the property with the given name. The getter and
     * setter of the property are looked up using a {@link PropertySet}.
     * <p>
     * For a <code>Binder</code> created using the {@link Binder#Binder(Class)}
     * constructor, introspection will be used to find a Java Bean property. If
     * a JSR-303 bean validation implementation is present on the classpath, a
     * {@link BeanValidator} is also added to the binding.
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
     * @throws IllegalStateException
     *             if the binder is not configured with an appropriate
     *             {@link PropertySet}
     *
     * @see #bind(HasValue, ValueProvider, Setter)
     */
    public <FIELDVALUE> Binding<BEAN, FIELDVALUE> bind(
            HasValue<FIELDVALUE> field, String propertyName) {
        return forField(field).bind(propertyName);
    }

    /**
     * Binds the given bean to all the fields added to this Binder. A
     * {@code null} value removes a currently bound bean.
     * <p>
     * When a bean is bound, the field values are updated by invoking their
     * corresponding getter functions. Any changes to field values are reflected
     * back to their corresponding property values of the bean as long as the
     * bean is bound.
     * <p>
     * Any change made in the fields also runs validation for the field
     * {@link Binding} and bean level validation for this binder (bean level
     * validators are added using {@link Binder#withValidator(Validator)}.
     * <p>
     * After updating each field, the value is read back from the field and the
     * bean's property value is updated if it has been changed from the original
     * value by the field or a converter.     
     *
     * @see #readBean(Object)
     * @see #writeBean(Object)
     * @see #writeBeanIfValid(Object)
     *
     * @param bean
     *            the bean to edit, or {@code null} to remove a currently bound
     *            bean and clear bound fields
     */
    public void setBean(BEAN bean) {
        checkBindingsCompleted("setBean");
        if (bean == null) {
            if (this.bean != null) {
                doRemoveBean(true);
                clearFields();
            }
        } else {
            doRemoveBean(false);
            this.bean = bean;
            getBindings().forEach(b -> b.initFieldValue(bean, true));
            // if there has been field value change listeners that trigger
            // validation, need to make sure the validation errors are cleared
            getValidationStatusHandler().statusChange(
                    BinderValidationStatus.createUnresolvedStatus(this));
            fireStatusChangeEvent(false);
        }
    }

    /**
     * Removes the currently set bean and clears bound fields. If there is no
     * bound bean, does nothing.
     * <p>
     * This is a shorthand for {@link #setBean(Object)} with {@code null} bean.
     */
    public void removeBean() {
        setBean(null);
    }

    /**
     * Reads the bound property values from the given bean to the corresponding
     * fields.
     * <p>
     * The bean is not otherwise associated with this binder; in particular its
     * property values are not bound to the field value changes. To achieve
     * that, use {@link #setBean(BEAN)}.
     *
     * @see #setBean(Object)
     * @see #writeBeanIfValid(Object)
     * @see #writeBean(Object)
     *
     * @param bean
     *            the bean whose property values to read or {@code null} to
     *            clear bound fields
     */
    public void readBean(BEAN bean) {
        checkBindingsCompleted("readBean");
        if (bean == null) {
            clearFields();
        } else {
            changedBindings.clear();
            getBindings().forEach(binding -> {
                // Some bindings may have been removed from binder
                // during readBean. We should skip those bindings to
                // avoid NPE inside initFieldValue. It happens e.g. when
                // we unbind a binding in valueChangeListener of another
                // field.
                if (binding.getField() != null)
                    binding.initFieldValue(bean, false);
            });
            getValidationStatusHandler().statusChange(
                    BinderValidationStatus.createUnresolvedStatus(this));
            fireStatusChangeEvent(false);
        }
    }

    /**
     * Writes changes from the bound fields to the given bean if all validators
     * (binding and bean level) pass.
     * <p>
     * If any field binding validator fails, no values are written and a
     * {@code ValidationException} is thrown.
     * <p>
     * If all field level validators pass, the given bean is updated and bean
     * level validators are run on the updated bean. If any bean level validator
     * fails, the bean updates are reverted and a {@code ValidationException} is
     * thrown.
     *
     * @see #writeBeanIfValid(Object)
     * @see #readBean(Object)
     * @see #setBean(Object)
     *
     * @param bean
     *            the object to which to write the field values, not
     *            {@code null}
     * @throws ValidationException
     *             if some of the bound field values fail to validate
     */
    public void writeBean(BEAN bean) throws ValidationException {
        BinderValidationStatus<BEAN> status = doWriteIfValid(bean,
                new ArrayList<>(bindings));
        if (status.hasErrors()) {
            throw new ValidationException(status.getFieldValidationErrors(),
                    status.getBeanValidationErrors());
        }
    }

    /**
     * Writes successfully converted and validated changes from the bound fields
     * to the bean even if there are other fields with non-validated changes.
     *
     * @see #writeBean(Object)
     * @see #writeBeanIfValid(Object)
     * @see #readBean(Object)
     * @see #setBean(Object)
     *
     * @param bean
     *            the object to which to write the field values, not
     *            {@code null}
     */
    public void writeBeanAsDraft(BEAN bean) {
        doWriteDraft(bean, new ArrayList<>(bindings));
    }

    /**
     * Writes changes from the bound fields to the given bean if all validators
     * (binding and bean level) pass.
     * <p>
     * If any field binding validator fails, no values are written and
     * <code>false</code> is returned.
     * <p>
     * If all field level validators pass, the given bean is updated and bean
     * level validators are run on the updated bean. If any bean level validator
     * fails, the bean updates are reverted and <code>false</code> is returned.
     *
     * @see #writeBean(Object)
     * @see #readBean(Object)
     * @see #setBean(Object)
     *
     * @param bean
     *            the object to which to write the field values, not
     *            {@code null}
     * @return {@code true} if there was no validation errors and the bean was
     *         updated, {@code false} otherwise
     */
    public boolean writeBeanIfValid(BEAN bean) {
        return doWriteIfValid(bean, new ArrayList<>(bindings)).isOk();
    }

    /**
     * Writes the field values into the given bean if all field level validators
     * pass. Runs bean level validators on the bean after writing.
     * <p>
     * <strong>Note:</strong> The collection of bindings is cleared on
     * successful save.
     *
     * @param bean
     *            the bean to write field values into
     * @param bindings
     *            the set of bindings to write to the bean
     * @return a list of field validation errors if such occur, otherwise a list
     *         of bean validation errors.
     */
    @SuppressWarnings({ "unchecked" })
    private BinderValidationStatus<BEAN> doWriteIfValid(BEAN bean,
            Collection<Binding<BEAN, ?>> bindings) {
        Objects.requireNonNull(bean, "bean cannot be null");
        List<ValidationResult> binderResults = Collections.emptyList();

        // First run fields level validation, if no validation errors then
        // update bean
        List<BindingValidationStatus<?>> bindingResults = bindings.stream()
                .map(b -> b.validate(false)).collect(Collectors.toList());

        if (bindingResults.stream()
                .noneMatch(BindingValidationStatus::isError)) {
            // Store old bean values so we can restore them if validators fail
            Map<Binding<BEAN, ?>, Object> oldValues = getBeanState(bean,
                    bindings);

            bindings.forEach(binding -> ((BindingImpl<BEAN, ?, ?>) binding)
                    .writeFieldValue(bean));
            // Now run bean level validation against the updated bean
            binderResults = validateBean(bean);
            if (binderResults.stream().anyMatch(ValidationResult::isError)) {
                // Bean validator failed, revert values
                restoreBeanState(bean, oldValues);
            } else if (bean.equals(getBean())) {
                /*
                 * Changes have been successfully saved. The set is only cleared
                 * when the changes are stored in the currently set bean.
                 */
                bindings.clear();
            } else if (getBean() == null) {
                /*
                 * When using readBean and writeBean there is no knowledge of
                 * which bean the changes come from or are stored in. Binder is
                 * no longer "changed" when saved succesfully to any bean.
                 */
                changedBindings.clear();
            }
        }

        // Generate status object and fire events.
        BinderValidationStatus<BEAN> status = new BinderValidationStatus<>(this,
                bindingResults, binderResults);
        getValidationStatusHandler().statusChange(status);
        fireStatusChangeEvent(!status.isOk());
        return status;
    }

    /**
     * Writes the successfully converted and validated field values into the
     * given bean.
     *
     * @param bean
     *            the bean to write field values into
     * @param bindings
     *            the set of bindings to write to the bean
     */
    @SuppressWarnings({ "unchecked" })
    private void doWriteDraft(BEAN bean, Collection<Binding<BEAN, ?>> bindings) {
        Objects.requireNonNull(bean, "bean cannot be null");

        bindings.forEach(binding -> ((BindingImpl<BEAN, ?, ?>) binding)
                    .writeFieldValue(bean));
    }

    /**
     * Restores the state of the bean from the given values. This method is used
     * together with {@link #getBeanState(Object, Collection)} to provide a way
     * to revert changes in case the bean validation fails after save.
     *
     * @param bean
     *            the bean
     * @param oldValues
     *            the old values
     *
     * @since 8.2
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void restoreBeanState(BEAN bean,
            Map<Binding<BEAN, ?>, Object> oldValues) {
        getBindings().stream().filter(oldValues::containsKey)
                .forEach(binding -> {
                    Setter setter = binding.setter;
                    if (setter != null) {
                        setter.accept(bean, oldValues.get(binding));
                    }
                });
    }

    /**
     * Stores the state of the given bean. This method is used together with
     * {@link #restoreBeanState(Object, Map)} to provide a way to revert changes
     * in case the bean validation fails after save.
     *
     * @param bean
     *            the bean to store the state of
     * @param bindings
     *            the bindings to store
     *
     * @return map from binding to value
     *
     * @since 8.2
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Map<Binding<BEAN, ?>, Object> getBeanState(BEAN bean,
            Collection<Binding<BEAN, ?>> bindings) {
        Map<Binding<BEAN, ?>, Object> oldValues = new HashMap<>();
        bindings.stream().map(binding -> (BindingImpl) binding)
                .filter(binding -> binding.setter != null)
                .forEach(binding -> oldValues.put(binding,
                        binding.getter.apply(bean)));
        return oldValues;
    }

    /**
     * Adds an bean level validator.
     * <p>
     * Bean level validators are applied on the bean instance after the bean is
     * updated. If the validators fail, the bean instance is reverted to its
     * previous state.
     *
     * @see #writeBean(Object)
     * @see #writeBeanIfValid(Object)
     * @see #withValidator(SerializablePredicate, String)
     * @see #withValidator(SerializablePredicate, ErrorMessageProvider)
     *
     * @param validator
     *            the validator to add, not null
     * @return this binder, for chaining
     */
    public Binder<BEAN> withValidator(Validator<? super BEAN> validator) {
        Objects.requireNonNull(validator, "validator cannot be null");
        validators.add(validator);
        return this;
    }

    /**
     * A convenience method to add a validator to this binder using the
     * {@link Validator#from(SerializablePredicate, String)} factory method.
     * <p>
     * Bean level validators are applied on the bean instance after the bean is
     * updated. If the validators fail, the bean instance is reverted to its
     * previous state.
     *
     * @see #writeBean(Object)
     * @see #writeBeanIfValid(Object)
     * @see #withValidator(Validator)
     * @see #withValidator(SerializablePredicate, ErrorMessageProvider)
     *
     * @param predicate
     *            the predicate performing validation, not null
     * @param message
     *            the error message to report in case validation failure
     * @return this binder, for chaining
     */
    public Binder<BEAN> withValidator(SerializablePredicate<BEAN> predicate,
            String message) {
        return withValidator(Validator.from(predicate, message));
    }

    /**
     * A convenience method to add a validator to this binder using the
     * {@link Validator#from(SerializablePredicate, ErrorMessageProvider)}
     * factory method.
     * <p>
     * Bean level validators are applied on the bean instance after the bean is
     * updated. If the validators fail, the bean instance is reverted to its
     * previous state.
     *
     * @see #writeBean(Object)
     * @see #writeBeanIfValid(Object)
     * @see #withValidator(Validator)
     * @see #withValidator(SerializablePredicate, String)
     *
     * @param predicate
     *            the predicate performing validation, not null
     * @param errorMessageProvider
     *            the provider to generate error messages, not null
     * @return this binder, for chaining
     */
    public Binder<BEAN> withValidator(SerializablePredicate<BEAN> predicate,
            ErrorMessageProvider errorMessageProvider) {
        return withValidator(Validator.from(predicate, errorMessageProvider));
    }

    /**
     * Clear all the bound fields for this binder.
     */
    private void clearFields() {
        bindings.forEach(binding -> {
            binding.getField().clear();
            clearError(binding.getField());
        });
        if (hasChanges()) {
            fireStatusChangeEvent(false);
        }
        changedBindings.clear();
    }

    /**
     * Validates the values of all bound fields and returns the validation
     * status.
     * <p>
     * If all field level validators pass, and {@link #setBean(Object)} has been
     * used to bind to a bean, bean level validators are run for that bean. Bean
     * level validators are ignored if there is no bound bean or if any field
     * level validator fails.
     * <p>
     * <strong>Note:</strong> This method will attempt to temporarily apply all
     * current changes to the bean and run full bean validation for it. The
     * changes are reverted after bean validation.
     *
     * @return validation status for the binder
     */
    public BinderValidationStatus<BEAN> validate() {
        return validate(true);
    }

    /**
     * Validates the values of all bound fields and returns the validation
     * status. This method can fire validation status events. Firing the events
     * depends on the given {@code boolean}.
     *
     * @param fireEvent
     *            {@code true} to fire validation status events; {@code false}
     *            to not
     * @return validation status for the binder
     *
     * @since 8.2
     */
    protected BinderValidationStatus<BEAN> validate(boolean fireEvent) {
        if (getBean() == null && !validators.isEmpty()) {
            throw new IllegalStateException("Cannot validate binder: "
                    + "bean level validators have been configured "
                    + "but no bean is currently set");
        }
        List<BindingValidationStatus<?>> bindingStatuses = validateBindings();

        BinderValidationStatus<BEAN> validationStatus;
        if (validators.isEmpty() || bindingStatuses.stream()
                .anyMatch(BindingValidationStatus::isError)) {
            validationStatus = new BinderValidationStatus<>(this,
                    bindingStatuses, Collections.emptyList());
        } else {
            Map<Binding<BEAN, ?>, Object> beanState = getBeanState(getBean(),
                    changedBindings);
            changedBindings
                    .forEach(binding -> ((BindingImpl<BEAN, ?, ?>) binding)
                            .writeFieldValue(getBean()));
            validationStatus = new BinderValidationStatus<>(this,
                    bindingStatuses, validateBean(getBean()));
            restoreBeanState(getBean(), beanState);
        }
        if (fireEvent) {
            getValidationStatusHandler().statusChange(validationStatus);
            fireStatusChangeEvent(validationStatus.hasErrors());
        }
        return validationStatus;
    }

    /**
     * Runs all currently configured field level validators, as well as all bean
     * level validators if a bean is currently set with
     * {@link #setBean(Object)}, and returns whether any of the validators
     * failed.
     * <p>
     * <b>Note:</b> Calling this method will not trigger status change events,
     * unlike {@link #validate()} and will not modify the UI. To also update
     * error indicators on fields, use {@code validate().isOk()}.
     * <p>
     * <strong>Note:</strong> This method will attempt to temporarily apply all
     * current changes to the bean and run full bean validation for it. The
     * changes are reverted after bean validation.
     *
     * @see #validate()
     *
     * @return whether this binder is in a valid state
     * @throws IllegalStateException
     *             if bean level validators have been configured and no bean is
     *             currently set
     */
    public boolean isValid() {
        return validate(false).isOk();
    }

    /**
     * Validates the bindings and returns the result of the validation as a list
     * of validation statuses.
     * <p>
     * Does not run bean validators.
     *
     * @see #validateBean(Object)
     *
     * @return an immutable list of validation results for bindings
     */
    private List<BindingValidationStatus<?>> validateBindings() {
        return getBindings().stream().map(BindingImpl::doValidation)
                .collect(Collectors.toList());
    }

    /**
     * Validates the {@code bean} using validators added using
     * {@link #withValidator(Validator)} and returns the result of the
     * validation as a list of validation results.
     * <p>
     *
     * @see #withValidator(Validator)
     *
     * @param bean
     *            the bean to validate
     * @return a list of validation errors or an empty list if validation
     *         succeeded
     */
    private List<ValidationResult> validateBean(BEAN bean) {
        Objects.requireNonNull(bean, "bean cannot be null");
        List<ValidationResult> results = Collections.unmodifiableList(validators
                .stream()
                .map(validator -> validator.apply(bean, new ValueContext()))
                .collect(Collectors.toList()));
        return results;
    }

    /**
     * Sets the label to show the binder level validation errors not related to
     * any specific field.
     * <p>
     * Only the one validation error message is shown in this label at a time.
     * <p>
     * This is a convenience method for
     * {@link #setValidationStatusHandler(BinderValidationStatusHandler)}, which
     * means that this method cannot be used after the handler has been set.
     * Also the handler cannot be set after this label has been set.
     *
     * @param statusLabel
     *            the status label to set
     * @see #setValidationStatusHandler(BinderValidationStatusHandler)
     * @see BindingBuilder#withStatusLabel(Label)
     */
    public void setStatusLabel(Label statusLabel) {
        if (statusHandler != null) {
            throw new IllegalStateException("Cannot set status label if a "
                    + BinderValidationStatusHandler.class.getSimpleName()
                    + " has already been set.");
        }
        this.statusLabel = statusLabel;
    }

    /**
     * Gets the status label or an empty optional if none has been set.
     *
     * @return the optional status label
     * @see #setStatusLabel(Label)
     */
    public Optional<Label> getStatusLabel() {
        return Optional.ofNullable(statusLabel);
    }

    /**
     * Sets the status handler to track form status changes.
     * <p>
     * Setting this handler will override the default behavior, which is to let
     * fields show their validation status messages and show binder level
     * validation errors or OK status in the label set with
     * {@link #setStatusLabel(Label)}.
     * <p>
     * This handler cannot be set after the status label has been set with
     * {@link #setStatusLabel(Label)}, or {@link #setStatusLabel(Label)} cannot
     * be used after this handler has been set.
     *
     * @param statusHandler
     *            the status handler to set, not <code>null</code>
     * @throws NullPointerException
     *             for <code>null</code> status handler
     * @see #setStatusLabel(Label)
     * @see BindingBuilder#withValidationStatusHandler(BindingValidationStatusHandler)
     */
    public void setValidationStatusHandler(
            BinderValidationStatusHandler<BEAN> statusHandler) {
        Objects.requireNonNull(statusHandler, "Cannot set a null "
                + BinderValidationStatusHandler.class.getSimpleName());
        if (statusLabel != null) {
            throw new IllegalStateException("Cannot set "
                    + BinderValidationStatusHandler.class.getSimpleName()
                    + " if a status label has already been set.");
        }
        this.statusHandler = statusHandler;
    }

    /**
     * Gets the status handler of this form.
     * <p>
     * If none has been set with
     * {@link #setValidationStatusHandler(BinderValidationStatusHandler)}, the
     * default implementation is returned.
     *
     * @return the status handler used, never <code>null</code>
     * @see #setValidationStatusHandler(BinderValidationStatusHandler)
     */
    public BinderValidationStatusHandler<BEAN> getValidationStatusHandler() {
        return Optional.ofNullable(statusHandler)
                .orElse(this::handleBinderValidationStatus);
    }

    /**
     * Adds status change listener to the binder.
     * <p>
     * The {@link Binder} status is changed whenever any of the following
     * happens:
     * <ul>
     * <li>if it's bound and any of its bound field or select has been changed
     * <li>{@link #writeBean(Object)} or {@link #writeBeanIfValid(Object)} is
     * called
     * <li>{@link #readBean(Object)} is called
     * <li>{@link #setBean(Object)} is called
     * <li>{@link #removeBean()} is called
     * <li>{@link BindingBuilder#bind(ValueProvider, Setter)} is called
     * <li>{@link Binder#validate()} or {@link Binding#validate()} is called
     * </ul>
     *
     * @see #readBean(Object)
     * @see #writeBean(Object)
     * @see #writeBeanIfValid(Object)
     * @see #setBean(Object)
     * @see #removeBean()
     * @see #forField(HasValue)
     * @see #validate()
     * @see Binding#validate()
     *
     * @param listener
     *            status change listener to add, not null
     * @return a registration for the listener
     */
    public Registration addStatusChangeListener(StatusChangeListener listener) {
        return getEventRouter().addListener(StatusChangeEvent.class, listener,
                ReflectTools.getMethod(StatusChangeListener.class));
    }

    /**
     * Adds field value change listener to all the fields in the binder.
     * <p>
     * Added listener is notified every time whenever any bound field value is
     * changed, i.e. the UI component value was changed, passed all the
     * conversions and validations then propagated to the bound bean field. The
     * same functionality can be achieved by adding a
     * {@link ValueChangeListener} to all fields in the {@link Binder}.
     * <p>
     * The listener is added to all fields regardless of whether the method is
     * invoked before or after field is bound.
     *
     * @see ValueChangeEvent
     * @see ValueChangeListener
     *
     * @param listener
     *            a field value change listener
     * @return a registration for the listener
     */
    public Registration addValueChangeListener(
            ValueChangeListener<?> listener) {
        return getEventRouter().addListener(ValueChangeEvent.class, listener,
                ReflectTools.getMethod(ValueChangeListener.class));
    }

    /**
     * Creates a new binding with the given field.
     *
     * @param <FIELDVALUE>
     *            the value type of the field
     * @param <TARGET>
     *            the target data type
     * @param field
     *            the field to bind, not null
     * @param converter
     *            the converter for converting between FIELDVALUE and TARGET
     *            types, not null
     * @param handler
     *            the handler to notify of status changes, not null
     * @return the new incomplete binding
     */
    protected <FIELDVALUE, TARGET> BindingBuilder<BEAN, TARGET> createBinding(
            HasValue<FIELDVALUE> field, Converter<FIELDVALUE, TARGET> converter,
            BindingValidationStatusHandler handler) {
        BindingBuilder<BEAN, TARGET> newBinding = doCreateBinding(field,
                converter, handler);
        if (incompleteMemberFieldBindings.containsKey(field)) {
            incompleteMemberFieldBindings.put(field, newBinding);
        }
        incompleteBindings.put(field, newBinding);
        return newBinding;
    }

    protected <FIELDVALUE, TARGET> BindingBuilder<BEAN, TARGET> doCreateBinding(
            HasValue<FIELDVALUE> field, Converter<FIELDVALUE, TARGET> converter,
            BindingValidationStatusHandler handler) {
        return new BindingBuilderImpl<>(this, field, converter, handler);
    }

    /**
     * Clears the error condition of the given field, if any. The default
     * implementation clears the
     * {@link AbstractComponent#setComponentError(ErrorMessage) component error}
     * of the field if it is a Component, otherwise does nothing.
     *
     * @param field
     *            the field with an invalid value
     */
    protected void clearError(HasValue<?> field) {
        if (field instanceof AbstractComponent) {
            ((AbstractComponent) field).setComponentError(null);
        }
    }

    /**
     * Handles a validation error emitted when trying to write the value of the
     * given field. The default implementation sets the
     * {@link AbstractComponent#setComponentError(ErrorMessage) component error}
     * of the field if it is a Component, otherwise does nothing.
     *
     * @param field
     *            the field with the invalid value
     * @param result
     *            the validation error result
     *
     * @since 8.2
     */
    protected void handleError(HasValue<?> field, ValidationResult result) {
        result.getErrorLevel().ifPresent(level -> {
            if (field instanceof AbstractComponent) {
                ((AbstractComponent) field).setComponentError(new UserError(
                        result.getErrorMessage(), ContentMode.TEXT, level));
            }
        });
    }

    /**
     * Default {@link BindingValidationStatusHandler} functional method
     * implementation.
     *
     * @param status
     *            the validation status
     */
    protected void handleValidationStatus(BindingValidationStatus<?> status) {
        HasValue<?> source = status.getField();
        clearError(source);
        if (status.isError()) {
            Optional<ValidationResult> firstError = status
                    .getValidationResults().stream()
                    .filter(ValidationResult::isError).findFirst();
            if (firstError.isPresent()) {
                // Failed with a Validation error
                handleError(source, firstError.get());
            } else {
                // Conversion error
                status.getResult()
                        .ifPresent(result -> handleError(source, result));
            }
        } else {
            // Show first non-error ValidationResult message.
            status.getValidationResults().stream()
                    .filter(result -> result.getErrorLevel().isPresent())
                    .findFirst()
                    .ifPresent(result -> handleError(source, result));
        }
    }

    /**
     * Returns the bindings for this binder.
     *
     * @return a list of the bindings
     */
    protected Collection<BindingImpl<BEAN, ?, ?>> getBindings() {
        return bindings.stream().map(b -> ((BindingImpl<BEAN, ?, ?>) b))
                .collect(Collectors.toList());
    }

    /**
     * The default binder level status handler.
     * <p>
     * Passes all field related results to the Binding status handlers. All
     * other status changes are displayed in the status label, if one has been
     * set with {@link #setStatusLabel(Label)}.
     *
     * @param binderStatus
     *            status of validation results from binding and/or bean level
     *            validators
     */
    protected void handleBinderValidationStatus(
            BinderValidationStatus<BEAN> binderStatus) {
        // let field events go to binding status handlers
        binderStatus.notifyBindingValidationStatusHandlers();

        // show first possible error or OK status in the label if set
        if (getStatusLabel().isPresent()) {
            String statusMessage = binderStatus.getBeanValidationErrors()
                    .stream().findFirst().map(ValidationResult::getErrorMessage)
                    .orElse("");
            getStatusLabel().get().setValue(statusMessage);
        }
    }

    /**
     * Check whether any of the bound fields' have uncommitted changes since
     * last explicit call to {@link #readBean(Object)}, {@link #removeBean()},
     * {@link #writeBean(Object)} or {@link #writeBeanIfValid(Object)}.
     * Unsuccessful write operations will not affect this value.
     * <p>
     * Note that if you use {@link #setBean(Object)} method, Binder tries to
     * commit changes as soon as all validators have passed. Thus, when using
     * this method with it seldom makes sense and almost always returns false.
     *
     * Return values for each case are compiled into the following table:
     *
     * <p>
     *
     * <table>
     * <tr>
     * <td></td>
     * <td>After readBean, setBean or removeBean</td>
     * <td>After valid user changes</td>
     * <td>After invalid user changes</td>
     * <td>After successful writeBean or writeBeanIfValid</td>
     * <td>After unsuccessful writeBean or writeBeanIfValid</td>
     * </tr>
     * <tr>
     * <td>A bean is currently bound</td>
     * <td>{@code false}</td>
     * <td>{@code false}</td>
     * <td>{@code true}</td>
     * <td>{@code false}</td>
     * <td>no change</td>
     * </tr>
     * <tr>
     * <td>No bean is currently bound</td>
     * <td>{@code false}</td>
     * <td>{@code true}</td>
     * <td>{@code true}</td>
     * <td>{@code false}</td>
     * <td>no change</td>
     * </tr>
     * </table>
     *
     * @return whether any bound field's value has changed since last call to
     *         setBean, readBean, writeBean or writeBeanIfValid
     */
    public boolean hasChanges() {
        return !changedBindings.isEmpty();
    }

    /**
     * Sets the read only state to the given value for all current bindings.
     * <p>
     * This is just a shorthand for calling {@link Binding#setReadOnly(boolean)}
     * for all current bindings. It means that bindings added after this method
     * call won't be set read-only.
     *
     * @param readOnly
     *            {@code true} to set the bindings to read-only, {@code false}
     *            to set them to read-write
     */
    public void setReadOnly(boolean readOnly) {
        getBindings().stream().filter(binding -> binding.getSetter() != null)
                .forEach(binding -> binding.setReadOnly(readOnly));
    }

    /**
     * Returns the event router for this binder.
     *
     * @return the event router, not null
     */
    protected EventRouter getEventRouter() {
        if (eventRouter == null) {
            eventRouter = new EventRouter();
        }
        return eventRouter;
    }

    /**
     * Configures the {@code binding} with the property definition
     * {@code definition} before it's being bound.
     *
     * @param binding
     *            a binding to configure
     * @param definition
     *            a property definition information
     * @return the new configured binding
     */
    protected BindingBuilder<BEAN, ?> configureBinding(
            BindingBuilder<BEAN, ?> binding,
            PropertyDefinition<BEAN, ?> definition) {
        return binding;
    }

    private void doRemoveBean(boolean fireStatusEvent) {
        changedBindings.clear();
        if (bean != null) {
            bean = null;
        }
        getValidationStatusHandler().statusChange(
                BinderValidationStatus.createUnresolvedStatus(this));
        if (fireStatusEvent) {
            fireStatusChangeEvent(false);
        }
    }

    private void fireStatusChangeEvent(boolean hasValidationErrors) {
        getEventRouter()
                .fireEvent(new StatusChangeEvent(this, hasValidationErrors));
    }

    private <FIELDVALUE> Converter<FIELDVALUE, FIELDVALUE> createNullRepresentationAdapter(
            HasValue<FIELDVALUE> field) {
        Converter<FIELDVALUE, FIELDVALUE> nullRepresentationConverter = Converter
                .from(fieldValue -> fieldValue,
                        modelValue -> Objects.isNull(modelValue)
                                ? field.getEmptyValue()
                                : modelValue,
                        exception -> exception.getMessage());
        ConverterDelegate<FIELDVALUE> converter = new ConverterDelegate<>(
                nullRepresentationConverter);
        initialConverters.put(field, converter);
        return converter;
    }

    /**
     * Throws if this binder has incomplete bindings.
     *
     * @param methodName
     *            name of the method where this call is originated from
     * @throws IllegalStateException
     *             if this binder has incomplete bindings
     */
    private void checkBindingsCompleted(String methodName) {
        if (!incompleteMemberFieldBindings.isEmpty()) {
            throw new IllegalStateException(
                    "All bindings created with forMemberField must "
                            + "be completed with bindInstanceFields before calling "
                            + methodName);
        }

        if (!incompleteBindings.isEmpty()) {
            throw new IllegalStateException(
                    "All bindings created with forField must be completed before calling "
                            + methodName);
        }
    }

    /**
     * Binds member fields found in the given object.
     * <p>
     * This method processes all (Java) member fields whose type extends
     * {@link HasValue} and that can be mapped to a property id. Property name
     * mapping is done based on the field name or on a @{@link PropertyId}
     * annotation on the field. All non-null unbound fields for which a property
     * name can be determined are bound to the property name using
     * {@link BindingBuilder#bind(String)}.
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
     * binder.bindInstanceFields(myForm);
     * </pre>
     *
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
     *             if there are incompatible HasValue&lt;T&gt; and property
     *             types
     */
    public void bindInstanceFields(Object objectWithMemberFields) {
        Class<?> objectClass = objectWithMemberFields.getClass();

        Integer numberOfBoundFields = getFieldsInDeclareOrder(objectClass)
                .stream()
                .filter(memberField -> HasValue.class
                        .isAssignableFrom(memberField.getType()))
                .filter(memberField -> !isFieldBound(memberField,
                        objectWithMemberFields))
                .map(memberField -> handleProperty(memberField,
                        objectWithMemberFields,
                        (property, type) -> bindProperty(objectWithMemberFields,
                                memberField, property, type)))
                .reduce(0, this::accumulate, Integer::sum);
        if (numberOfBoundFields == 0 && bindings.isEmpty()
                && incompleteBindings.isEmpty()) {
            // Throwing here for incomplete bindings would be wrong as they
            // may be completed after this call. If they are not, setBean and
            // other methods will throw for those cases
            throw new IllegalStateException("There are no instance fields "
                    + "found for automatic binding");
        }

    }

    private boolean isFieldBound(Field memberField,
            Object objectWithMemberFields) {
        try {
            HasValue field = (HasValue) getMemberFieldValue(memberField,
                    objectWithMemberFields);
            return bindings.stream()
                    .anyMatch(binding -> binding.getField() == field);
        } catch (Exception e) {
            return false;
        }
    }

    private int accumulate(int count, boolean value) {
        return value ? count + 1 : count;
    }

    private BindingBuilder<BEAN, ?> getIncompleteMemberFieldBinding(
            Field memberField, Object objectWithMemberFields) {
        return incompleteMemberFieldBindings
                .get(getMemberFieldValue(memberField, objectWithMemberFields));
    }

    private Object getMemberFieldValue(Field memberField,
            Object objectWithMemberFields) {
        memberField.setAccessible(true);
        try {
            return memberField.get(objectWithMemberFields);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            memberField.setAccessible(false);
        }
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
     * @return {@code true} if property is successfully bound
     */
    private boolean bindProperty(Object objectWithMemberFields,
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
                return false;
            }
            if (field == null) {
                field = makeFieldInstance(
                        (Class<? extends HasValue<?>>) memberField.getType());
                initializeField(objectWithMemberFields, memberField, field);
            }
            forField(field).bind(property);
            return true;
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
    private HasValue<?> makeFieldInstance(
            Class<? extends HasValue<?>> fieldClass) {
        try {
            return ReflectTools.createInstance(fieldClass);
        } catch (IllegalArgumentException e) {
            // Rethrow as the exception type declared for bindInstanceFields
            throw new IllegalStateException(e);
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
    private List<Field> getFieldsInDeclareOrder(Class<?> searchClass) {
        List<Field> memberFieldInOrder = new ArrayList<>();

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

    private boolean handleProperty(Field field, Object objectWithMemberFields,
            BiFunction<String, Class<?>, Boolean> propertyHandler) {
        Optional<PropertyDefinition<BEAN, ?>> descriptor = getPropertyDescriptor(
                field);

        if (!descriptor.isPresent()) {
            return false;
        }

        String propertyName = descriptor.get().getName();
        if (boundProperties.containsKey(propertyName)) {
            return false;
        }

        BindingBuilder<BEAN, ?> tentativeBinding = getIncompleteMemberFieldBinding(
                field, objectWithMemberFields);
        if (tentativeBinding != null) {
            tentativeBinding.bind(propertyName);
            return false;
        }

        Boolean isPropertyBound = propertyHandler.apply(propertyName,
                descriptor.get().getType());
        assert boundProperties.containsKey(propertyName);
        return isPropertyBound;
    }

    /**
     * Gets the binding for a property name. Bindings are available by property
     * name if bound using {@link #bind(HasValue, String)},
     * {@link BindingBuilder#bind(String)} or indirectly using
     * {@link #bindInstanceFields(Object)}.
     *
     * @param propertyName
     *            the property name of the binding to get
     * @return the binding corresponding to the property name, or an empty
     *         optional if there is no binding with that property name
     */
    public Optional<Binding<BEAN, ?>> getBinding(String propertyName) {
        return Optional.ofNullable(boundProperties.get(propertyName));
    }

    private Optional<PropertyDefinition<BEAN, ?>> getPropertyDescriptor(
            Field field) {
        PropertyId propertyIdAnnotation = field.getAnnotation(PropertyId.class);
        String propertyId;
        if (propertyIdAnnotation != null) {
            // @PropertyId(propertyId) always overrides property id
            propertyId = propertyIdAnnotation.value();
        } else {
            propertyId = field.getName();
        }
        String minifiedFieldName = minifyFieldName(propertyId);
        return propertySet.getProperties().map(PropertyDefinition::getName)
                .filter(name -> minifyFieldName(name).equals(minifiedFieldName))
                .findFirst().flatMap(propertySet::getProperty);
    }

    private String minifyFieldName(String fieldName) {
        return fieldName.toLowerCase(Locale.ROOT).replace("_", "");
    }

    private <V> void fireValueChangeEvent(ValueChangeEvent<V> event) {
        getEventRouter().fireEvent(event);
    }

    /**
     * Returns the fields this binder has been bound to.
     *
     * @return the fields with bindings
     * @since 8.1
     */
    public Stream<HasValue<?>> getFields() {
        return bindings.stream().map(Binding::getField);
    }

    /**
     * Finds and removes all Bindings for the given field. Note that this method
     * and other overloads of removeBinding method do not reset component errors
     * that might have been added to the field and do not remove required
     * indicator of the field no matter if it was set by Binder or not. To reset
     * component errors, {@code field.setComponentError(null)} should be called
     * and to remove required indicator,
     * {@code field.setRequiredIndicatorVisible(false)} should be called.
     *
     * @see com.vaadin.ui.AbstractComponent#setComponentError
     * @see com.vaadin.ui.AbstractComponent#setRequiredIndicatorVisible
     *
     * @param field
     *            the field to remove from bindings
     *
     * @since 8.2
     */
    public void removeBinding(HasValue<?> field) {
        Objects.requireNonNull(field, "Field can not be null");
        Set<BindingImpl<BEAN, ?, ?>> toRemove = getBindings().stream()
                .filter(binding -> field.equals(binding.getField()))
                .collect(Collectors.toSet());
        toRemove.forEach(Binding::unbind);
    }

    /**
     * Removes the given Binding from this Binder.
     *
     * @see Binder#removeBinding(HasValue)
     * @see com.vaadin.ui.AbstractComponent#setComponentError
     * @see com.vaadin.ui.AbstractComponent#setRequiredIndicatorVisible
     *
     * @param binding
     *            the binding to remove
     *
     * @since 8.2
     *
     * @throws IllegalArgumentException
     *             if the given Binding is not in this Binder
     */
    public void removeBinding(Binding<BEAN, ?> binding)
            throws IllegalArgumentException {
        Objects.requireNonNull(binding, "Binding can not be null");
        if (!bindings.contains(binding)) {
            throw new IllegalArgumentException(
                    "Provided Binding is not in this Binder");
        }
        binding.unbind();
    }

    /**
     * Removes (internally) the {@code Binding} from the bound properties map
     * (if present) and from the list of {@code Binding}s. Note that this DOES
     * NOT remove the {@code ValueChangeListener} that the {@code Binding} might
     * have registered with any {@code HasValue}s or decouple the {@code Binder}
     * from within the {@code Binding}. To do that, use
     *
     * {@link Binding#unbind()}
     *
     * This method should just be used for internal cleanup.
     *
     * @param binding
     *            The {@code Binding} to remove from the binding map
     *
     * @since 8.2
     */
    protected void removeBindingInternal(Binding<BEAN, ?> binding) {
        if (bindings.remove(binding)) {
            boundProperties.entrySet()
                    .removeIf(entry -> entry.getValue().equals(binding));
        }
    }

    /**
     * Finds and removes the Binding for the given property name.
     *
     * @see Binder#removeBinding(HasValue)
     * @see com.vaadin.ui.AbstractComponent#setComponentError
     * @see com.vaadin.ui.AbstractComponent#setRequiredIndicatorVisible
     *
     * @param propertyName
     *            the propertyName to remove from bindings
     *
     * @since 8.2
     */
    public void removeBinding(String propertyName) {
        Objects.requireNonNull(propertyName, "Property name can not be null");
        Optional.ofNullable(boundProperties.get(propertyName))
                .ifPresent(Binding::unbind);
    }

    private static final Logger getLogger() {
        return Logger.getLogger(Binder.class.getName());
    }

}
