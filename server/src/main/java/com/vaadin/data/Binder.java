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
package com.vaadin.data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.vaadin.event.Registration;

/**
 * Connects one or more {@code Field} components to properties of a backing data
 * type such as a bean type. With a binder, input components can be grouped
 * together into forms to easily create and update business objects with little
 * explicit logic needed to move data between the UI and the data layers of the
 * application.
 * <p>
 * A binder is a collection of <i>bindings</i>, each representing the
 * association of a single field and a backing property.
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
 * 
 * @since
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
     * @see Binder#forField(HasValue)
     */
    public interface Binding<T, V> extends Serializable {

        /**
         * Completes this binding using the given getter and setter functions
         * representing a backing bean property. The functions are used to
         * update the field value from the property and to store the field value
         * to the property, respectively.
         * <p>
         * When a bean is bound with {@link Binder#bind(T)}, the field value is
         * set to the return value of the given getter. The property value is
         * then updated via the given setter whenever the field value changes.
         * The setter may be null; in that case the property value is never
         * updated and the binding is said to be <i>read-only</i>.
         * <p>
         * If the Binder is already bound to some item, the newly bound field is
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
         *            the function to save the field value to the property or
         *            null if read-only
         * @throws IllegalStateException
         *             if {@code bind} has already been called on this binding
         */
        public void bind(Function<T, V> getter, BiConsumer<T, V> setter);
    }

    /**
     * An internal implementation of {@code Binding}.
     * 
     * @param <V>
     *            the value type
     */
    protected class BindingImpl<V> implements Binding<T, V> {

        private HasValue<V> field;
        private Registration onValueChange;

        private Function<T, V> getter;
        private BiConsumer<T, V> setter;

        /**
         * Creates a new binding associated with the given field.
         * 
         * @param field
         *            the field to bind
         */
        protected BindingImpl(HasValue<V> field) {
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
                bind(bean);
            }
        }

        private void bind(T bean) {
            setFieldValue(bean);
            onValueChange = field.addValueChangeListener(e -> storeFieldValue(
                    bean));
        }

        private void unbind() {
            onValueChange.remove();
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
            assert bean != null;
            if (setter != null) {
                setter.accept(bean, field.getValue());
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
     *            the field to be bound, not null
     * @return the new binding
     */
    public <V> Binding<T, V> forField(HasValue<V> field) {
        return createBinding(field);
    }

    /**
     * Binds a field to a bean property represented by the given getter and
     * setter pair. The functions are used to update the field value from the
     * property and to store the field value to the property, respectively.
     * <p>
     * Use the {@link #forField(HasValue)} overload instead if you want to
     * further configure the new binding.
     * <p>
     * When a bean is bound with {@link Binder#bind(T)}, the field value is set
     * to the return value of the given getter. The property value is then
     * updated via the given setter whenever the field value changes. The setter
     * may be null; in that case the property value is never updated and the
     * binding is said to be <i>read-only</i>.
     * <p>
     * If the Binder is already bound to some item, the newly bound field is
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
     * @param <V>
     *            the value type of the field
     * @param field
     *            the field to bind, not null
     * @param getter
     *            the function to get the value of the property to the field,
     *            not null
     * @param setter
     *            the function to save the field value to the property or null
     *            if read-only
     */
    public <V> void bind(HasValue<V> field, Function<T, V> getter,
            BiConsumer<T, V> setter) {
        forField(field).bind(getter, setter);
    }

    /**
     * Binds the given bean to all the fields added to this Binder. To remove
     * the binding, call {@link #unbind()}.
     * <p>
     * When a bean is bound, the field values are updated by invoking their
     * corresponding getter functions. Any changes to field values are reflected
     * back to their corresponding property values of the bean as long as the
     * bean is bound.
     * 
     * @param bean
     *            the bean to edit, not null
     */
    public void bind(T bean) {
        Objects.requireNonNull(bean, "bean cannot be null");
        unbind();
        this.bean = bean;
        bindings.forEach(b -> b.bind(bean));
    }

    /**
     * Unbinds the currently bound bean if any. If there is no bound bean, does
     * nothing.
     */
    public void unbind() {
        if (bean != null) {
            bean = null;
            bindings.forEach(b -> b.unbind());
        }
    }

    /**
     * Creates a new binding with the given field.
     * 
     * @param <V>
     *            the field value type
     * @param field
     *            the field to bind
     * @return the new incomplete binding
     */
    protected <V> BindingImpl<V> createBinding(HasValue<V> field) {
        Objects.requireNonNull(field, "field cannot be null");
        BindingImpl<V> b = new BindingImpl<>(field);
        return b;
    }
}
