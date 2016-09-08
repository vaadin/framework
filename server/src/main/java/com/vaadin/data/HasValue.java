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

import com.vaadin.event.ConnectorEvent;
import com.vaadin.event.EventListener;
import com.vaadin.server.ClientConnector;
import com.vaadin.shared.Registration;

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
    public class ValueChange<V> extends ConnectorEvent {

        private final V value;
        private final boolean userOriginated;

        /**
         * Creates a new {@code ValueChange} event containing the current value
         * of the given value-bearing source connector.
         *
         * @param <CONNECTOR>
         *            the type of the source connector
         * @param source
         *            the source connector bearing the value, not null
         * @param userOriginated
         *            {@code true} if this event originates from the client,
         *            {@code false} otherwise.
         */
        public <CONNECTOR extends ClientConnector & HasValue<V>> ValueChange(
                CONNECTOR source, boolean userOriginated) {
            this(source, source.getValue(), userOriginated);
        }

        /**
         * Creates a new {@code ValueChange} event containing the given value,
         * originating from the given source connector.
         *
         * @param source
         *            the source connector, not null
         * @param value
         *            the new value, may be null
         * @param userOriginated
         *            {@code true} if this event originates from the client,
         *            {@code false} otherwise.
         */
        public ValueChange(ClientConnector source, V value,
                boolean userOriginated) {
            super(source);
            this.value = value;
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
    public interface ValueChangeListener<V> extends
            EventListener<ValueChange<V>> {

        /**
         * Invoked when this listener receives a value change event from an
         * event source to which it has been added.
         *
         * @param event
         *            the received event, not null
         */
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
     * Adds a value change listener. The listener is called when the value of
     * this {@code hasValue} is changed either by the user or programmatically.
     *
     * @param listener
     *            the value change listener, not null
     * @return a registration for the listener
     */
    public Registration addValueChangeListener(
            ValueChangeListener<? super V> listener);
}
