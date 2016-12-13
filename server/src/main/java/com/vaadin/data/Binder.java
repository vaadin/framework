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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.stream.Collectors;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.util.converter.ValueContext;
import com.vaadin.event.EventRouter;
import com.vaadin.event.Listener;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.SerializableBiConsumer;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.UserError;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

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
         * instance representing the outcome of the validation.
         *
         * @see Binder#validate()
         * @see Validator#apply(Object)
         *
         * @return the validation result.
         */
        public ValidationStatus<TARGET> validate();

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
        public Binding<BEAN, TARGET> bind(
                SerializableFunction<BEAN, TARGET> getter,
                com.vaadin.server.SerializableBiConsumer<BEAN, TARGET> setter);

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
         * {@link Validator#from(SerializablePredicate, ErrorMessageProvider)}
         * factory method.
         * <p>
         * Validators are applied, in registration order, when the field value
         * is written to the backing property. If any validator returns a
         * failure, the property value is not updated.
         *
         * @see #withValidator(Validator)
         * @see #withValidator(SerializablePredicate, String)
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
         * Maps the binding to another data type using the given
         * {@link Converter}.
         * <p>
         * A converter is capable of converting between a presentation type,
         * which must match the current target data type of the binding, and a
         * model type, which can be any data type and becomes the new target
         * type of the binding. When invoking
         * {@link #bind(SerializableFunction, SerializableBiConsumer)}, the
         * target type of the binding must match the getter/setter types.
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
         * {@link #bind(SerializableFunction, SerializableBiConsumer)}, the
         * target type of the binding must match the getter/setter types.
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
         * {@link #bind(SerializableFunction, SerializableBiConsumer)}, the
         * target type of the binding must match the getter/setter types.
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
                            ? null : fieldValue,
                    modelValue -> Objects.isNull(modelValue)
                            ? nullRepresentation : modelValue);
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
         * {@link #withValidationStatusHandler(ValidationStatusHandler)} method
         * where the handler instance hides the {@code label} if there is no
         * error and shows it with validation error message if validation fails.
         * It means that it cannot be called after
         * {@link #withValidationStatusHandler(ValidationStatusHandler)} method
         * call or {@link #withValidationStatusHandler(ValidationStatusHandler)}
         * after this method call.
         *
         * @see #withValidationStatusHandler(ValidationStatusHandler)
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
         * Sets a {@link ValidationStatusHandler} to track validation status
         * changes.
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
                ValidationStatusHandler handler);

        /**
         * Sets the field to be required. This means two things:
         * <ol>
         * <li>the required indicator is visible</li>
         * <li>the field value is validated for not being empty*</li>
         * </ol>
         * For localizing the error message, use
         * {@link #setRequired(SerializableFunction)}.
         * <p>
         * *Value not being the equal to what {@link HasValue#getEmptyValue()}
         * returns.
         *
         * @see #setRequired(SerializableFunction)
         * @see HasValue#setRequiredIndicatorVisible(boolean)
         * @see HasValue#isEmpty()
         * @param errorMessage
         *            the error message to show for the invalid value
         * @return this binding, for chaining
         */
        public default BindingBuilder<BEAN, TARGET> setRequired(
                String errorMessage) {
            return setRequired(context -> errorMessage);
        }

        /**
         * Sets the field to be required. This means two things:
         * <ol>
         * <li>the required indicator is visible</li>
         * <li>the field value is validated for not being empty*</li>
         * </ol>
         * *Value not being the equal to what {@link HasValue#getEmptyValue()}
         * returns.
         *
         * @see HasValue#setRequiredIndicatorVisible(boolean)
         * @see HasValue#isEmpty()
         * @param errorMessageProvider
         *            the provider for localized validation error message
         * @return this binding, for chaining
         */
        public BindingBuilder<BEAN, TARGET> setRequired(
                ErrorMessageProvider errorMessageProvider);
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

        private final Binder<BEAN> binder;

        private final HasValue<FIELDVALUE> field;
        private ValidationStatusHandler statusHandler;
        private boolean isStatusHandlerChanged;

        private boolean bound;

        /**
         * Contains all converters and validators chained together in the
         * correct order.
         */
        private Converter<FIELDVALUE, TARGET> converterValidatorChain;

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
                ValidationStatusHandler statusHandler) {
            this.field = field;
            this.binder = binder;
            this.converterValidatorChain = converterValidatorChain;
            this.statusHandler = statusHandler;
        }

        @Override
        public Binding<BEAN, TARGET> bind(
                SerializableFunction<BEAN, TARGET> getter,
                SerializableBiConsumer<BEAN, TARGET> setter) {
            checkUnbound();
            Objects.requireNonNull(getter, "getter cannot be null");

            BindingImpl<BEAN, FIELDVALUE, TARGET> binding = new BindingImpl<>(
                    this, getter, setter);

            getBinder().bindings.add(binding);
            if (getBinder().getBean() != null) {
                binding.initFieldValue(getBinder().getBean());
            }
            getBinder().fireStatusChangeEvent(false);

            bound = true;

            return binding;
        }

        @Override
        public BindingBuilder<BEAN, TARGET> withValidator(
                Validator<? super TARGET> validator) {
            checkUnbound();
            Objects.requireNonNull(validator, "validator cannot be null");

            converterValidatorChain = converterValidatorChain
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
                ValidationStatusHandler handler) {
            checkUnbound();
            Objects.requireNonNull(handler, "handler cannot be null");
            if (isStatusHandlerChanged) {
                throw new IllegalStateException(
                        "A " + ValidationStatusHandler.class.getSimpleName()
                                + " has already been set");
            }
            isStatusHandlerChanged = true;
            statusHandler = handler;
            return this;
        }

        @Override
        public BindingBuilder<BEAN, TARGET> setRequired(
                ErrorMessageProvider errorMessageProvider) {
            checkUnbound();
            field.setRequiredIndicatorVisible(true);
            return withValidator(
                    value -> !Objects.equals(value, field.getEmptyValue()),
                    errorMessageProvider);
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

            return getBinder().createBinding(field,
                    converterValidatorChain.chain(converter), statusHandler);
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

        private final Binder<BEAN> binder;

        private final HasValue<FIELDVALUE> field;
        private final ValidationStatusHandler statusHandler;

        private final SerializableFunction<BEAN, TARGET> getter;
        private final SerializableBiConsumer<BEAN, TARGET> setter;

        // Not final since we temporarily remove listener while changing values
        private Registration onValueChange;

        /**
         * Contains all converters and validators chained together in the
         * correct order.
         */
        private final Converter<FIELDVALUE, TARGET> converterValidatorChain;

        public BindingImpl(BindingBuilderImpl<BEAN, FIELDVALUE, TARGET> builder,
                SerializableFunction<BEAN, TARGET> getter,
                SerializableBiConsumer<BEAN, TARGET> setter) {
            this.binder = builder.getBinder();
            this.field = builder.field;
            this.statusHandler = builder.statusHandler;
            converterValidatorChain = builder.converterValidatorChain;

            onValueChange = getField()
                    .addValueChangeListener(this::handleFieldValueChange);

            this.getter = getter;
            this.setter = setter;
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
        public ValidationStatus<TARGET> validate() {
            ValidationStatus<TARGET> status = doValidation();
            getBinder().getValidationStatusHandler()
                    .onEvent(new BinderValidationStatus<>(getBinder(),
                            Arrays.asList(status), Collections.emptyList()));
            getBinder().fireStatusChangeEvent(status.isError());
            return status;
        }

        /**
         * Returns the field value run through all converters and validators,
         * but doesn't pass the {@link ValidationStatus} to any status handler.
         *
         * @return the result of the conversion
         */
        private Result<TARGET> doConversion() {
            FIELDVALUE fieldValue = field.getValue();
            return converterValidatorChain.convertToModel(fieldValue,
                    createValueContext());
        }

        private ValidationStatus<TARGET> toValidationStatus(
                Result<TARGET> result) {
            return new ValidationStatus<>(this,
                    result.isError()
                            ? ValidationResult.error(result.getMessage().get())
                            : ValidationResult.ok());
        }

        /**
         * Returns the field value run through all converters and validators,
         * but doesn't pass the {@link ValidationStatus} to any status handler.
         *
         * @return the validation status
         */
        private ValidationStatus<TARGET> doValidation() {
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
                return new ValueContext((Component) field);
            }
            return new ValueContext(findLocale());
        }

        /**
         * Sets the field value by invoking the getter function on the given
         * bean. The default listener attached to the field will be removed for
         * the duration of this update.
         *
         * @param bean
         *            the bean to fetch the property value from
         */
        private void initFieldValue(BEAN bean) {
            assert bean != null;
            assert onValueChange != null;
            onValueChange.remove();
            try {
                getField().setValue(convertDataToFieldType(bean));
            } finally {
                onValueChange = getField()
                        .addValueChangeListener(this::handleFieldValueChange);
            }
        }

        private FIELDVALUE convertDataToFieldType(BEAN bean) {
            return converterValidatorChain.convertToPresentation(
                    getter.apply(bean), createValueContext());
        }

        /**
         * Handles the value change triggered by the bound field.
         *
         * @param bean
         *            the new value
         */
        private void handleFieldValueChange(
                ValueChangeEvent<FIELDVALUE> event) {
            getBinder().setHasChanges(true);
            List<ValidationResult> binderValidationResults = Collections
                    .emptyList();
            ValidationStatus<TARGET> fieldValidationStatus;
            if (getBinder().getBean() != null) {
                BEAN bean = getBinder().getBean();
                fieldValidationStatus = writeFieldValue(bean);
                if (!getBinder().bindings.stream()
                        .map(BindingImpl::doValidation)
                        .anyMatch(ValidationStatus::isError)) {
                    binderValidationResults = getBinder().validateBean(bean);
                    if (!binderValidationResults.stream()
                            .anyMatch(ValidationResult::isError)) {
                        getBinder().setHasChanges(false);
                    }
                }
            } else {
                fieldValidationStatus = doValidation();
            }
            BinderValidationStatus<BEAN> status = new BinderValidationStatus<>(
                    getBinder(), Arrays.asList(fieldValidationStatus),
                    binderValidationResults);
            getBinder().getValidationStatusHandler().onEvent(status);
            getBinder().fireStatusChangeEvent(status.hasErrors());
        }

        /**
         * Write the field value by invoking the setter function on the given
         * bean, if the value passes all registered validators.
         *
         * @param bean
         *            the bean to set the property value to
         */
        private ValidationStatus<TARGET> writeFieldValue(BEAN bean) {
            assert bean != null;

            Result<TARGET> result = doConversion();
            if (setter != null) {
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

        private void notifyStatusHandler(ValidationStatus<?> status) {
            statusHandler.accept(status);
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
            if (validationResult.isError()) {
                return Result.error(validationResult.getErrorMessage());
            } else {
                return Result.ok(value);
            }
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

    private BEAN bean;

    private final Set<BindingImpl<BEAN, ?, ?>> bindings = new LinkedHashSet<>();

    private final List<Validator<? super BEAN>> validators = new ArrayList<>();

    private final Map<HasValue<?>, ConverterDelegate<?>> initialConverters = new IdentityHashMap<>();

    private EventRouter eventRouter;

    private Label statusLabel;

    private Listener<BinderValidationStatus<BEAN>> statusHandler;

    private boolean hasChanges = false;

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
     * {@link BindingBuilder#bind(SerializableFunction, SerializableBiConsumer)}
     * which completes the binding. Until {@code Binding.bind} is called, the
     * binding has no effect.
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
     * @see #bind(HasValue, SerializableFunction, SerializableBiConsumer)
     */
    public <FIELDVALUE> BindingBuilder<BEAN, FIELDVALUE> forField(
            HasValue<FIELDVALUE> field) {
        Objects.requireNonNull(field, "field cannot be null");
        // clear previous errors for this field and any bean level validation
        clearError(field);
        getStatusLabel().ifPresent(label -> label.setValue(""));

        return createBinding(field, createNullRepresentationAdapter(field),
                this::handleValidationStatus);
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
     * {@link Binding#withNullRepresentation(Object))}.
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
            HasValue<FIELDVALUE> field,
            SerializableFunction<BEAN, FIELDVALUE> getter,
            SerializableBiConsumer<BEAN, FIELDVALUE> setter) {
        return forField(field).bind(getter, setter);
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
     *
     * @see #readBean(Object)
     * @see #writeBean(Object)
     * @see #writeBeanIfValid(Object)
     *
     * @param bean
     *            the bean to edit, or {@code null} to remove a currently bound
     *            bean
     */
    public void setBean(BEAN bean) {
        if (bean == null) {
            if (this.bean != null) {
                doRemoveBean(true);
            }
        } else {
            doRemoveBean(false);
            this.bean = bean;
            bindings.forEach(b -> b.initFieldValue(bean));
            // if there has been field value change listeners that trigger
            // validation, need to make sure the validation errors are cleared
            getValidationStatusHandler().onEvent(
                    BinderValidationStatus.createUnresolvedStatus(this));
            fireStatusChangeEvent(false);
        }
    }

    /**
     * Removes the currently set bean, if any. If there is no bound bean, does
     * nothing.
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
     *            the bean whose property values to read, not null
     */
    public void readBean(BEAN bean) {
        Objects.requireNonNull(bean, "bean cannot be null");
        setHasChanges(false);
        bindings.forEach(binding -> binding.initFieldValue(bean));

        getValidationStatusHandler()
                .onEvent(BinderValidationStatus.createUnresolvedStatus(this));
        fireStatusChangeEvent(false);
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
        BinderValidationStatus<BEAN> status = doWriteIfValid(bean);
        if (status.hasErrors()) {
            throw new ValidationException(status.getFieldValidationErrors(),
                    status.getBeanValidationErrors());
        }
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
        return doWriteIfValid(bean).isOk();
    }

    /**
     * Writes the field values into the given bean if all field level validators
     * pass. Runs bean level validators on the bean after writing.
     *
     * @param bean
     *            the bean to write field values into
     * @return a list of field validation errors if such occur, otherwise a list
     *         of bean validation errors.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private BinderValidationStatus<BEAN> doWriteIfValid(BEAN bean) {
        Objects.requireNonNull(bean, "bean cannot be null");
        // First run fields level validation
        List<ValidationStatus<?>> bindingStatuses = validateBindings();
        // If no validation errors then update bean
        if (bindingStatuses.stream().filter(ValidationStatus::isError).findAny()
                .isPresent()) {
            fireStatusChangeEvent(true);
            return new BinderValidationStatus<>(this, bindingStatuses,
                    Collections.emptyList());
        }

        // Store old bean values so we can restore them if validators fail
        Map<Binding<BEAN, ?>, Object> oldValues = new HashMap<>();
        bindings.forEach(
                binding -> oldValues.put(binding, binding.getter.apply(bean)));

        bindings.forEach(binding -> binding.writeFieldValue(bean));
        // Now run bean level validation against the updated bean
        List<ValidationResult> binderResults = validateBean(bean);
        boolean hasErrors = binderResults.stream()
                .filter(ValidationResult::isError).findAny().isPresent();
        if (hasErrors) {
            // Bean validator failed, revert values
            bindings.forEach((BindingImpl binding) -> binding.setter
                    .accept(bean, oldValues.get(binding)));
        } else {
            // Write successful, reset hasChanges to false
            setHasChanges(false);
        }
        fireStatusChangeEvent(hasErrors);
        return new BinderValidationStatus<>(this, bindingStatuses,
                binderResults);
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
     * Validates the values of all bound fields and returns the validation
     * status.
     * <p>
     * If all field level validators pass, and {@link #setBean(Object)} has been
     * used to bind to a bean, bean level validators are run for that bean. Bean
     * level validators are ignored if there is no bound bean or if any field
     * level validator fails.
     * <p>
     *
     * @return validation status for the binder
     */
    public BinderValidationStatus<BEAN> validate() {
        List<ValidationStatus<?>> bindingStatuses = validateBindings();

        BinderValidationStatus<BEAN> validationStatus;
        if (bindingStatuses.stream().filter(ValidationStatus::isError).findAny()
                .isPresent() || bean == null) {
            validationStatus = new BinderValidationStatus<>(this,
                    bindingStatuses, Collections.emptyList());
        } else {
            validationStatus = new BinderValidationStatus<>(this,
                    bindingStatuses, validateBean(bean));
        }
        getValidationStatusHandler().onEvent(validationStatus);
        fireStatusChangeEvent(validationStatus.hasErrors());
        return validationStatus;
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
    private List<ValidationStatus<?>> validateBindings() {
        List<ValidationStatus<?>> results = new ArrayList<>();
        for (BindingImpl<?, ?, ?> binding : bindings) {
            results.add(binding.doValidation());
        }
        return results;
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
        List<ValidationResult> results = Collections
                .unmodifiableList(validators.stream()
                        .map(validator -> validator.apply(bean,
                                new ValueContext()))
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
     * {@link #setValidationStatusHandler(BinderStatusHandler)}, which means
     * that this method cannot be used after the handler has been set. Also the
     * handler cannot be set after this label has been set.
     *
     * @param statusLabel
     *            the status label to set
     * @see #setValidationStatusHandler(BinderStatusHandler)
     * @see BindingBuilder#withStatusLabel(Label)
     */
    public void setStatusLabel(Label statusLabel) {
        if (statusHandler != null) {
            throw new IllegalStateException("Cannot set status label if a "
                    + Listener.class.getSimpleName()
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
     * @see BindingBuilder#withValidationStatusHandler(ValidationStatusHandler)
     */
    public void setValidationStatusHandler(
            com.vaadin.event.Listener<BinderValidationStatus<BEAN>> statusHandler) {
        Objects.requireNonNull(statusHandler, "Cannot set a null "
                + Listener.class.getSimpleName());
        if (statusLabel != null) {
            throw new IllegalStateException("Cannot set "
                    + Listener.class.getSimpleName()
                    + " if a status label has already been set.");
        }
        this.statusHandler = statusHandler;
    }

    /**
     * Gets the status handler of this form.
     * <p>
     * If none has been set with
     * {@link #setValidationStatusHandler(BinderStatusHandler)}, the default
     * implementation is returned.
     *
     * @return the status handler used, never <code>null</code>
     * @see #setValidationStatusHandler(BinderStatusHandler)
     */
    public Listener<BinderValidationStatus<BEAN>> getValidationStatusHandler() {
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
     * <li>
     * {@link BindingBuilder#bind(SerializableFunction, SerializableBiConsumer)}
     * is called
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
    public Registration addStatusChangeListener(Listener<StatusChangeEvent> listener) {
        return getEventRouter().addListener(StatusChangeEvent.class, listener,
                Listener.class.getDeclaredMethods()[0]);
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
            ValidationStatusHandler handler) {
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
     * @param error
     *            the error message to set
     */
    protected void handleError(HasValue<?> field, String error) {
        if (field instanceof AbstractComponent) {
            ((AbstractComponent) field).setComponentError(new UserError(error));
        }

    }

    /**
     * Default {@link ValidationStatusHandler} functional method implementation.
     *
     * @param status
     *            the validation status
     */
    protected void handleValidationStatus(ValidationStatus<?> status) {
        HasValue<?> source = status.getField();
        clearError(source);
        if (status.isError()) {
            handleError(source, status.getMessage().get());
        }
    }

    /**
     * Returns the bindings for this binder.
     *
     * @return a set of the bindings
     */
    protected Set<BindingImpl<BEAN, ?, ?>> getBindings() {
        return bindings;
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
        binderStatus.getFieldValidationStatuses()
                .forEach(status -> ((BindingImpl<?, ?, ?>) status.getBinding())
                        .notifyStatusHandler(status));

        // show first possible error or OK status in the label if set
        if (getStatusLabel().isPresent()) {
            String statusMessage = binderStatus.getBeanValidationErrors()
                    .stream().findFirst().map(ValidationResult::getErrorMessage)
                    .orElse("");
            getStatusLabel().get().setValue(statusMessage);
        }
    }

    /**
     * Sets whether the values of the fields this binder is bound to have
     * changed since the last explicit call to either bind, write or read.
     *
     * @param hasChanges
     *            whether this binder should be marked to have changes
     */
    private void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
    }

    /**
     * Check whether any of the bound fields' values have changed since last
     * explicit call to {@link #setBean(Object)}, {@link #readBean(Object)},
     * {@link #removeBean()}, {@link #writeBean(Object)} or
     * {@link #writeBeanIfValid(Object)}. Unsuccessful write operations will not
     * affect this value. Return values for each case are compiled into the
     * following table:
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
        return hasChanges;
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

    private void doRemoveBean(boolean fireStatusEvent) {
        setHasChanges(false);
        if (bean != null) {
            bean = null;
        }
        getValidationStatusHandler()
                .onEvent(BinderValidationStatus.createUnresolvedStatus(this));
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
                                ? field.getEmptyValue() : modelValue,
                        exception -> exception.getMessage());
        ConverterDelegate<FIELDVALUE> converter = new ConverterDelegate<>(
                nullRepresentationConverter);
        initialConverters.put(field, converter);
        return converter;
    }

}
