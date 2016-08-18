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
import java.util.function.Consumer;

import com.vaadin.event.ConnectorEvent;
import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.Registration;
import com.vaadin.server.ClientConnector;

/**
 * A generic interface for field components and other user interface objects
 * that have a user-editable value. Emits change events whenever the value is
 * changed, either by the user or programmatically.
 *
 * @since
 * @param <V>
 *            the value type
 */
public interface HasValue<V> extends Serializable {

    /**
     * An event fired when the value of a {@code HasValue} changes.
     *
     * @param <V>
     *            the value type
     */
    public class ValueChange<V> extends ConnectorEvent {

        private final V value;
        private final boolean userOriginated;

        /**
         * Creates a new {@code ValueChange} event containing the current value
         * of the given value-bearing source connector.
         *
         * @param <C>
         *            the type of the source connector
         * @param source
         *            the source connector bearing the value, not null
         * @param userOriginated
         *            {@code true} if this event originates from the client,
         *            {@code false} otherwise.
         */
        public <C extends ClientConnector & HasValue<V>> ValueChange(C source,
                boolean userOriginated) {
            super(source);
            this.value = source.getValue();
            this.userOriginated = userOriginated;
        }

        /**
         * Returns the new value of the source connector.
         *
         * @return the new value
         */
        public V getValue() {
            return value;
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
    }

    /**
     * A listener for value change events.
     *
     * @param <V>
     *            the value type
     *
     * @see ValueChange
     * @see Registration
     */
    @FunctionalInterface
    public interface ValueChangeListener<V>
            extends Consumer<ValueChange<V>>, ConnectorEventListener {

        /**
         * Invoked when this listener receives a value change event from an
         * event source to which it has been added.
         *
         * @param event
         *            the received event, not null
         */
        // In addition to customizing the Javadoc, this override is needed
        // to make ReflectTools.findMethod work as expected. It uses
        // Class.getDeclaredMethod, but even if it used getMethod instead, the
        // superinterface argument type is Object, not Event, after type
        // erasure.
        @Override
        public void accept(ValueChange<V> event);
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
     * Adds an {@link ValueChangeListener}. The listener is called when the
     * value of this {@code hasValue} is changed either by the user or
     * programmatically.
     *
     * @param listener
     *            the value change listener, not null
     * @return a registration for the listener
     */
    public Registration addValueChangeListener(
            ValueChangeListener<? super V> listener);
}
