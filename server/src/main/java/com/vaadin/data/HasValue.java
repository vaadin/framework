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
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * A generic interface for field components and other user interface objects
 * that have a user-editable value. Emits change events whenever the value is
 * changed, either by the user or programmatically.
 *
 * @author Vaadin Ltd.
 *
 * @param <V>
 *            the value type
 *
 * @since 8.0
 */
public interface HasValue<V> extends Serializable {

    /**
     * An event fired when the value of a {@code HasValue} changes.
     *
     * @param <V>
     *            the value type
     */
    public class ValueChangeEvent<V> extends EventObject {

        private final boolean userOriginated;
        private final Component component;

        /**
         * Creates a new {@code ValueChange} event containing the current value
         * of the given value-bearing source component.
         *
         * @param <COMPONENT>
         *            the type of the source component
         * @param component
         *            the source component bearing the value, not null
         * @param userOriginated
         *            {@code true} if this event originates from the client,
         *            {@code false} otherwise.
         */
        public <COMPONENT extends Component & HasValue<V>> ValueChangeEvent(
                COMPONENT component, boolean userOriginated) {
            this(component, component, userOriginated);
        }

        /**
         * Creates a new {@code ValueChange} event containing the given value,
         * originating from the given source component.
         *
         * @param component
         *            the component, not null
         * @param hasValue
         *            the HasValue instance bearing the value, not null
         * @param userOriginated
         *            {@code true} if this event originates from the client,
         *            {@code false} otherwise.
         */

        public ValueChangeEvent(Component component, HasValue<V> hasValue,
                boolean userOriginated) {
            super(hasValue);
            this.userOriginated = userOriginated;
            this.component = component;
        }

        /**
         * Returns the new value of the event source.
         * <p>
         * This a shorthand method for {@link HasValue#getValue()} for the event
         * source {@link #getSource()}. Thus the value is always the most recent
         * one, even if has been changed after the firing of this event.
         *
         * @see HasValue#getValue()
         *
         * @return the new value
         */
        public V getValue() {
            return getSource().getValue();
        }

        /**
         * Returns whether this event was triggered by user interaction, on the
         * client side, or programmatically, on the server side.
         *
         * @return {@code true} if this event originates from the client,
         *         {@code false} otherwise.
         */
        public boolean isUserOriginated() {
            return userOriginated;
        }

        /**
         * Returns the component.
         *
         * @return the component, not null
         */
        public Component getComponent() {
            return component;
        }

        @SuppressWarnings("unchecked")
        @Override
        public HasValue<V> getSource() {
            return (HasValue<V>) super.getSource();
        }
    }

    /**
     * A listener for value change events.
     *
     * @param <V>
     *            the value type
     *
     * @see ValueChangeEvent
     * @see Registration
     */
    @FunctionalInterface
    public interface ValueChangeListener<V>
            extends Consumer<ValueChangeEvent<V>>, Serializable {
        @Deprecated
        public static final Method VALUE_CHANGE_METHOD = ReflectTools
                .findMethod(ValueChangeListener.class, "accept",
                        ValueChangeEvent.class);

        /**
         * Invoked when this listener receives a value change event from an
         * event source to which it has been added.
         *
         * @param event
         *            the received event, not null
         */
        @Override
        public void accept(ValueChangeEvent<V> event);
    }

    /**
     * Sets the value of this object. If the new value is not equal to
     * {@code getValue()}, fires a value change event. May throw
     * {@code IllegalArgumentException} if the value is not acceptable.
     * <p>
     * <i>Implementation note:</i> the implementing class should document
     * whether null values are accepted or not.
     *
     * @param value
     *            the new value
     * @throws IllegalArgumentException
     *             if the value is invalid
     */
    public void setValue(V value);

    /**
     * Returns the current value of this object.
     * <p>
     * <i>Implementation note:</i> the implementing class should document
     * whether null values may be returned or not.
     *
     * @return the current value
     */
    public V getValue();

    /**
     * Adds a value change listener. The listener is called when the value of
     * this {@code HasValue} is changed either by the user or programmatically.
     *
     * @param listener
     *            the value change listener, not null
     * @return a registration for the listener
     */
    public Registration addValueChangeListener(ValueChangeListener<V> listener);

    /**
     * Returns the value that represents an empty value.
     * <p>
     * By default {@link HasValue} is expected to support {@code null} as empty
     * values. Specific implementations might not support this.
     *
     * @return empty value
     * @see Binder#bind(HasValue, java.util.function.Function, BiConsumer)
     */
    public default V getEmptyValue() {
        return null;
    }

    /**
     * Returns whether this {@code HasValue} is considered to be empty.
     * <p>
     * By default this is an equality check between current value and empty
     * value.
     *
     * @return {@code true} if considered empty; {@code false} if not
     */
    public default boolean isEmpty() {
        return Objects.equals(getValue(), getEmptyValue());
    }

    /**
     * Sets the required indicator visible or not.
     * <p>
     * If set visible, it is visually indicated in the user interface.
     *
     * @param requiredIndicatorVisible
     *            <code>true</code> to make the required indicator visible,
     *            <code>false</code> if not
     */
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible);

    /**
     * Checks whether the required indicator is visible.
     *
     * @return <code>true</code> if visible, <code>false</code> if not
     */
    public boolean isRequiredIndicatorVisible();
}
