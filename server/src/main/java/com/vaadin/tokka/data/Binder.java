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
package com.vaadin.tokka.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.vaadin.server.UserError;
import com.vaadin.tokka.data.selection.SelectionModel;
import com.vaadin.tokka.data.util.Result;
import com.vaadin.tokka.ui.components.HasValue;
import com.vaadin.tokka.ui.components.Listing;
import com.vaadin.ui.AbstractComponent;

/**
 * Connects one or more {@code Field} or {@code Select} components to properties
 * of a backing data type such as a bean type. With a binder, input components
 * can be grouped together into forms to easily create and update business
 * objects with little explicit logic needed to move data between the UI and the
 * data layers of the application.
 * <p>
 * A binder is a collection of <i>bindings</i>, each representing the
 * association of a single field and a backing property. Each binding may have a
 * set of <i>validators</i>, used to ensure the user input is in a valid format
 * before storing it to the property.
 * <p>
 * A binder instance can be bound to a single bean instance at a time, but can
 * be rebound as needed. This allows usage patterns like a <i>master-details</i>
 * view, where a select component is used to pick the bean to edit.
 * <p>
 * Unless otherwise specified, {@code Binder} method arguments cannot be null.
 * 
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the bean type
 * 
 * @see Binding
 * @see HasValue
 * @see Validator
 * @see Converter
 */
public class Binder<T> implements Serializable {

    /**
     * Represents the binding between a single field and a property.
     *
     * @param <T>
     *            the item type
     * @param <V>
     *            the field value type
     * 
     * @see Binder#addField(HasValue)
     */
    public interface Binding<T, V> extends Serializable {

        /**
         * Adds a validator to this binding. Validators are applied, in
         * registration order, when the field value is saved to the backing
         * property. If any validator returns a failure, the property value is
         * not updated.
         * <p>
         * Unless otherwise specified, {@code Binding} method arguments cannot
         * be null.
         * 
         * @param validator
         *            the validator to add
         * @return this binding, for chaining
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public Binding<T, V> addValidator(Validator<V> validator);

        /**
         * Allows the binding to map to another model type via the given
         * converter. For instance, with an appropriate converter, a
         * {@code TextField} can be bound to an integer-typed property.
         * 
         * @param <U>
         *            the model type of the converter
         * @param converter
         *            the converter to use, not null
         * @return a new binding with the appropriate type
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public <U> Binding<T, U> setConverter(Converter<V, U> converter);

        /**
         * Completes this binding using the given getter and setter functions
         * representing a backing bean property. The functions are used to
         * update the field value from the property and to store the field value
         * to the property, respectively. The setter may be null; in that case
         * the bound field will be read-only.
         * 
         * @param getter
         *            the function to get the value of the property to the
         *            field, not null
         * @param setter
         *            the function to save the field value to the property or
         *            null if read-only
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public void bind(Function<T, V> getter, BiConsumer<T, V> setter);
    }

    /**
     * An internal implementation of {@code Binding}.
     * 
     * @param <U>
     *            the field value type
     * @param <V>
     *            the property value type
     */
    // TODO make protected, allow customization
    private class BindingImpl<U, V> implements Binding<T, V> {

        private HasValue<U> field;

        private Function<T, V> getter;
        private BiConsumer<T, V> setter;

        private Converter<U, V> converter;

        @Override
        public void bind(Function<T, V> getter, BiConsumer<T, V> setter) {
            checkUnbound();
            Objects.requireNonNull(getter, "getter cannot be null");

            this.getter = getter;
            this.setter = setter;
            bindings.add(this);
            if (bean != null) {
                setFieldValue(bean);
            }
            field.addValueChangeListener(e -> validate());
        }

        @Override
        public Binding<T, V> addValidator(Validator<V> validator) {
            checkUnbound();
            Objects.requireNonNull(validator, "validator cannot be null");

            converter = converter.chain(Converter.from(validator, v -> v));
            return this;
        }

        @Override
        public <W> Binding<T, W> setConverter(Converter<V, W> converter) {
            checkUnbound();
            Objects.requireNonNull(converter, "converter cannot be null");

            BindingImpl<U, W> b = new BindingImpl<>();
            b.field = field;
            b.converter = this.converter.chain(converter);
            return b;
        }

        /**
         * Sets the field value by invoking the getter function on the given
         * bean.
         * 
         * @param bean
         *            the bean to fetch the property value from
         */
        private void setFieldValue(T bean) {
            assert bean != null;
            field.setValue(converter.toPresentation(getter.apply(bean)));
        }

        private Result<V> validate() {
            return converter.toModel(field.getValue());
        }

        /**
         * Saves the field value by invoking the setter function on the given
         * bean, if the value passes all registered validators.
         * 
         * @param bean
         *            the bean to set the property value to
         */
        private void storeFieldValue(T bean) {
            assert bean != null;
            if (setter != null) {
                validate().ifOk(value -> setter.accept(bean, value));
            }
        }

        private void checkUnbound() {
            if (this.getter != null) {
                throw new IllegalStateException(
                        "cannot modify binding: already bound to a property");
            }
        }
    }

    private T bean;

    private Set<BindingImpl<?, ?>> bindings = new LinkedHashSet<>();

    /**
     * Returns an {@code Optional} of the bean that has been bound with
     * {@link #bind}, or an empty optional if a bean is not currently bound.
     * 
     * @return the currently bound bean if any
     */
    public Optional<T> getBean() {
        return Optional.ofNullable(bean);
    }

    /**
     * Creates a new binding for the given field. The returned binding may be
     * further configured before invoking
     * {@link Binding#bind(Function, BiConsumer) Binding.bind} which completes
     * the binding. Until {@code Binding.bind} is called, the binding has no
     * effect.
     * 
     * @param <V>
     *            the value type of the field
     * @param field
     *            the field to be bound, not null
     * @return the new binding
     */
    public <V> Binding<T, V> addField(HasValue<V> field) {
        return createBinding(field);
    }

    /**
     * Binds a field with a property represented by the given getter and setter
     * pair. If the Binder is already bound to some item, the new field will be
     * also bound to it.
     * <p>
     * The setter may be null; in that case the field will be read-only.
     * <p>
     * Getters and setters can be used to make conversions happen. They are also
     * a good place to update the state of the field. You should avoid making
     * any unnecessary changes after binding a field to avoid any unexpected
     * problems.
     * <p>
     * Use the {@link #addField(HasValue)} overload instead if you want to
     * further configure the new binding.
     * 
     * @param <V>
     *            the value type of the field
     * @param field
     *            the editor field to bind, not null
     * @param getter
     *            a function to fetch data from the bean, not null
     * @param setter
     *            a function to store data back to the bean, or null if the
     *            field should be read-only
     */
    public <V> void addField(HasValue<V> field, Function<T, V> getter,
            BiConsumer<T, V> setter) {
        addField(field).bind(getter, setter);
    }

    /**
     * Binds the given bean to all the fields added to this Binder. To remove
     * the binding, call {@link #unbind()}.
     * <p>
     * When a bean is bound, the field values are updated by invoking their
     * corresponding getter functions. Field values are saved into the bean
     * properties using the corresponding setter function when the
     * {@link #save()} method is called.
     * 
     * @param bean
     *            the bean to edit, not null
     */
    public void bind(T bean) {
        Objects.requireNonNull(bean, "bean cannot be null");
        this.bean = bean;
        for (BindingImpl<?, ?> binding : bindings) {
            binding.setFieldValue(bean);
        }
    }

    /**
     * Unbinds the currently bound bean if any. If there is no bound bean, does
     * nothing.
     */
    public void unbind() {
        this.bean = null;
    }

    // FIXME Javadoc
    public <V> void addSelect(Listing<V> listing, Function<T, V> getter,
            BiConsumer<T, V> setter) {
        if (listing.getSelectionModel() instanceof SelectionModel.Single) {
            addField((SelectionModel.Single<V>) listing.getSelectionModel(),
                    getter, setter);
        } else {
            throw new IllegalArgumentException(
                    "Listing did not have a single selection model.");
        }
    }

    // FIXME Javadoc
    public <V> void addMultiSelect(Listing<V> listing,
            Function<T, Collection<V>> getter,
            BiConsumer<T, Collection<V>> setter) {
        if (listing.getSelectionModel() instanceof SelectionModel.Multi) {
            addField((SelectionModel.Multi<V>) listing.getSelectionModel(),
                    getter, setter);
        } else {
            throw new IllegalArgumentException(
                    "Listing did not have a multi selection model.");
        }
    }

    /**
     * Validates the values of all bound fields and returns the result of the
     * validation.
     * 
     * @return the validation result.
     */
    public Result<?> validate() {
        Result<?> result = Result.ok(null);
        for (BindingImpl<?, ?> b : bindings) {
            clearError(b.field);
            Result<?> r = b.validate();
            r.ifError(msg -> handleError(b.field, msg));
            result = result.flatMap(v -> r);
        }
        return result;
    }

    /**
     * Saves changes from the bound fields to the edited bean. If any value
     * fails validation, no values are saved.
     *
     * @return a result that contains the bean if the save succeeded, an error
     *         otherwise
     * @throws IllegalStateException
     *             if there is no bound bean
     */
    public Result<T> save() {
        if (bean == null) {
            throw new IllegalStateException("Cannot save: no bean bound");
        }
        Result<?> result = validate();
        result.ifOk(v -> doSave());
        return result.map(v -> bean);
    }

    /**
     * Handles a validation error emitted when trying to save the value of the
     * given field. The default implementation sets the
     * {@link AbstractComponent#setComponentError(ErrorMessage) component error}
     * of the field if it is a Component, otherwise does nothing.
     * 
     * @param field
     *            the field with the invalid value
     * @param error
     *            the error message returned
     */
    protected void handleError(HasValue<?> field, String error) {
        if (field instanceof AbstractComponent) {
            ((AbstractComponent) field).setComponentError(new UserError(error));
        }
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

    private void doSave() {
        assert bean != null;
        bindings.forEach(b -> b.storeFieldValue(bean));
    }

    private <V> BindingImpl<V, V> createBinding(HasValue<V> field) {
        Objects.requireNonNull(field, "field cannot be null");

        BindingImpl<V, V> b = new BindingImpl<>();
        b.field = field;
        b.converter = Converter.identity();
        return b;
    }
}
