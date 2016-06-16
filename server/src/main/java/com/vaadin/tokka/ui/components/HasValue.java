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
package com.vaadin.tokka.ui.components;

import java.io.Serializable;

import com.vaadin.server.ClientConnector;
import com.vaadin.tokka.event.Event;
import com.vaadin.tokka.event.Handler;
import com.vaadin.tokka.event.Registration;

/**
 * Generic interface for Components that have a value.
 *
 * @since
 * @param <V>
 *            value type
 */
public interface HasValue<V> extends Serializable {

    public abstract class ValueChange<V> extends Event {

        private final V value;

        protected <C extends ClientConnector & HasValue<V>> ValueChange(
                C source, boolean userOriginated) {
            this(source, source.getValue(), userOriginated);
        }

        protected ValueChange(ClientConnector source, V value,
                boolean userOriginated) {
            super(source, userOriginated);
            this.value = value;
        }

        /**
         * Returns the payload value.
         * 
         * @return payload value
         */
        public V getValue() {
            return value;
        }
    }

    /**
     * Sets the value of this object. Setting a value fires a value change
     * event.
     * 
     * @param value
     *            new value
     */
    void setValue(V value);

    /**
     * Gets the current value of this object.
     * 
     * @return current value
     */
    V getValue();

    /**
     * Adds a {@link Handler}. Handler is called when value is changed by the
     * user or through the API
     * 
     * @param handler
     *            on change event handler
     * @return registration for the handler
     * @throws IllegalArgumentException
     *             if handler is null
     */
    Registration onChange(Handler<ValueChange<V>> handler);
}
