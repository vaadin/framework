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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.vaadin.tokka.data.Validator.Result;
import com.vaadin.tokka.server.communication.data.SelectionModel;
import com.vaadin.tokka.ui.components.HasValue;
import com.vaadin.tokka.ui.components.Listing;

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
 * 
 * @author Vaadin Ltd
 *
 * @param <T>
 *            the bean type
 * 
 * @see Binding
 * @see HasValue
 * @see Validator
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
         * 
         * @param validator
         *            the validator to add, not null
         * @return this binding, for chaining
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public Binding<T, V> addValidator(Validator<? super V> validator);

        /**
         * Completes this binding using the given getter and setter functions
         * representing a backing bean property. The functions are used to
         * update the field value from the property and to store the field value
         * to the property, respectively.
         * 
         * @param getter
         *            the function to get the value of the property to the field
         * @param setter
         *            the function to save the field value to the property
         * @throws IllegalStateException
         *             if {@code bind} has already been called
         */
        public void bind(Function<T, V> getter, BiConsumer<T, V> setter);
    }

    /**
     * An internal implementation of {@code Binding}.
     */
    private class BindingImpl<V> implements Binding<T, V> {

        private HasValue<V> field;
        private Function<T, V> getter;
        private BiConsumer<T, V> setter;
        private List<Validator<? super V>> validators = new ArrayList<>();

        private BindingImpl(HasValue<V> field) {
            Objects.requireNonNull(field, "field cannot be null");
            this.field = field;
        }

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
        }

        @Override
        public Binding<T, V> addValidator(Validator<? super V> validator) {
            checkUnbound();
            Objects.requireNonNull(validator, "validator cannot be null");
            validators.add(validator);
            return this;
        }

        /**
         * Sets the field value by invoking the getter function on the given
         * bean.
         * 
         * @param bean
         *            the bean to fetch the property value from
         */
        private void setFieldValue(T bean) {
            field.setValue(getter.apply(bean));
        }

        /**
         * Saves the field value by invoking the setter function on the given
         * bean, if the value passes all registered validators.
         * 
         * @param bean
         *            the bean to set the property value to
         */
        private void storeFieldValue(T bean) {
            if (setter == null) {
                return;
            }
            V value = field.getValue();
            if (validators.stream().map(v -> v.apply(value))
                    .allMatch(Result::isOk)) {
                setter.accept(bean, value);
                // TODO Validation error handling
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

    private Set<BindingImpl<?>> bindings = new LinkedHashSet<>();

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
     *            the field to be bound
     * @return the new binding
     */
    public <V> Binding<T, V> addField(HasValue<V> field) {
        return new BindingImpl<>(field);
    }

    /**
     * Binds a field with a property represented by the given getter and setter
     * pair. If the Binder is already bound to some item, the new field will be
     * also bound to it.
     * <p>
     * Not providing a setter implicitly sets the field to be read only.
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
     *            editor field, not null
     * @param getter
     *            getter method to fetch data from the bean, not null
     * @param setter
     *            setter method to store data back to the bean or null if the
     *            field should be read-only
     */
    public <V> void addField(HasValue<V> field, Function<T, V> getter,
            BiConsumer<T, V> setter) {
        addField(field).bind(getter, setter);
    }

    /**
     * Binds the given bean to all the fields added to this Binder. If the bean
     * is null, removes any existing binding.
     * <p>
     * When a bean is bound, the field values are updated by invoking their
     * corresponding getter functions. Field values are saved into the bean
     * properties using the corresponding setter function when the
     * {@link #save()} method is called.
     * 
     * @param bean
     *            edited bean or null to bind nothing
     */
    public void bind(T bean) {
        this.bean = bean;

        if (bean == null) {
            // TODO: clean up?
            return;
        }

        for (BindingImpl<?> binding : bindings) {
            binding.setFieldValue(bean);
        }
    }

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

    // TODO: Is this correct return value? How should we manage the error
    // messages from custom validation? Documentation?
    // public abstract void addValidator(ValueProvider<T, String> validator);
    // TODO: Needs remove method as well?

    /**
     * Saves any changes from the bound fields to the edited bean. Values that
     * do not pass validation are not saved. If there is no currently bound
     * bean, does nothing.
     */
    public void save() {
        if (bean == null) {
            return;
        }

        for (BindingImpl<?> binding : bindings) {
            binding.storeFieldValue(bean);
        }

        // TODO: Postvalidation
    }

    /**
     * Resets any changes in the fields to match values from the edited bean.
     */
    // TODO: Do we need this?
    public void reset() {
        // Re-bind to refresh all fields
        bind(bean);
    }

    // Exists for the sake of making something before / after field update is
    // processed
    /**
     * This method does prevalidation for every changed field. By overriding
     * this method you can react to changes that need to happen when certain
     * fields are edited. e.g. re-apply conversions.
     * 
     * @param field
     *            changed field
     */
    protected <V> void handleChangeEvent(HasValue<V> field) {
        // TODO: pre-validation ?
    }

    /*
     * FIXME: Validation needs an idea. // TODO: Document protected abstract
     * void validate();
     * 
     * // TODO: Document protected abstract void doJSRValidation();
     */
}
