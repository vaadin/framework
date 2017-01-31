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

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.ui.Component;

/**
 * An event fired when the value of some {@code HasValue} which are bound to a
 * {@link Binder} changes.
 * 
 * @author Vaadin Ltd
 *
 * @param <BEAN>
 *            the bean type
 * @param <V>
 *            the value type
 * @since 8.0
 * 
 * @see Binder#addBinderChangeListener(BinderChangeListener)
 * @see BinderChangeListener
 */
public class BinderChangeEvent<BEAN, V> extends ValueChangeEvent<V> {

    private final Binder<BEAN> binder;

    /**
     * Creates a new {@code BinderChangeEvent} event containing the given value,
     * originating from the given source component and the binder.
     * 
     * @param binder
     *            the binder which {@code hasValue} is bound to
     * @param component
     *            the component, not null
     * @param hasValue
     *            the HasValue instance bearing the value, not null
     * @param oldValue
     *            the previous value held by the source of this event
     * @param userOriginated
     *            {@code true} if this event originates from the client,
     *            {@code false} otherwise.
     */
    public BinderChangeEvent(Binder<BEAN> binder, Component component,
            HasValue<V> hasValue, V oldValue, boolean userOriginated) {
        super(component, hasValue, oldValue, userOriginated);
        this.binder = binder;
    }

    /**
     * Gets the binder which field is bound to.
     * 
     * @return the binder which field is bound to
     */
    public Binder<BEAN> getBinder() {
        return binder;
    }
}
