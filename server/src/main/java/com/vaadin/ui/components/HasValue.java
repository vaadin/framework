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
package com.vaadin.ui.components;

import java.io.Serializable;

import com.vaadin.event.typed.Handler;
import com.vaadin.event.typed.Registration;

/**
 * Generic interface for Components that have a value.
 *
 * @since
 * @param <V>
 *            value type
 */
public interface HasValue<V> extends Serializable {

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
    Registration onChange(Handler<V> handler);
}
