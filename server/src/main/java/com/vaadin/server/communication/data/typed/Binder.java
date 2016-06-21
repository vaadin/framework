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
package com.vaadin.server.communication.data.typed;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.vaadin.ui.components.HasValue;
import com.vaadin.ui.components.Listing;

// TODO: Should this class listen to changes in DataSource?
public class Binder<T> implements Serializable {

    private Set<FieldBinding<T, ?>> bindings = new LinkedHashSet<>();
    private T bean;

    /**
     * Internal class for tracking field bindings with getters and setters.
     *
     * @param <T>
     *            bean type
     * @param <V>
     *            value type
     */
    private static class FieldBinding<T, V> implements Serializable {
        private HasValue<V> field;
        private Function<T, V> getter;
        private BiConsumer<T, V> setter;

        public FieldBinding(HasValue<V> field, Function<T, V> getter,
                BiConsumer<T, V> setter) {
            this.field = field;
            this.getter = getter;
            this.setter = setter;
        }

        void setFieldValue(T bean) {
            field.setValue(getter.apply(bean));
        }

        void storeFieldValue(T bean) {
            if (setter != null) {
                setter.accept(bean, field.getValue());
            }
        }
    }
    
    /**
     * Gets the bean that has been bound with {@link #bind}.
     * 
     * @return the currently bound bean.
     */
    public T getBean() {
        return bean;
    }

    /**
     * Registers a binding for given field by using the getter and setter. If
     * the Binder is already bound to some bean, the new field will be also
     * bound to it.
     * <p>
     * Not providing a setter implicitly sets the field to be read only.
     * <p>
     * Getters and setters can be used to make conversions happen. They are also
     * a good place to update the state of the field. You should avoid making
     * any unnecessary changes after binding a field to avoid any unexpected
     * problems.
     * 
     * @param field
     *            editor field
     * @param getter
     *            getter method to fetch data from the bean
     * @param setter
     *            setter method to store data back to the bean
     */
    public <V> void addField(HasValue<V> field, Function<T, V> getter,
            BiConsumer<T, V> setter) {
        if (getter == null || field == null) {
            throw new IllegalArgumentException();
        }

        FieldBinding<T, V> binding = new FieldBinding<>(field, getter, setter);
        bindings.add(binding);

        if (bean != null) {
            binding.setFieldValue(bean);
        }

        field.onChange(value -> handleChangeEvent(field));
    }

    /**
     * Binds the given bean to all bound fields.
     * 
     * @param bean
     *            edited bean
     */
    public void bind(T bean) {
        this.bean = bean;

        if (bean == null) {
            // TODO: clean up?
            return;
        }

        for (FieldBinding<T, ?> binding : bindings) {
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
     * Saves any changes from the bound fields to the edited bean.
     */
    public void save() {
        if (bean == null) {
            return;
        }

        for (FieldBinding<T, ?> binding : bindings) {
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

    /* PROTECTED SCOPE */

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
