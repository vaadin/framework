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

import com.vaadin.event.SerializableEventListener;

/**
 * Listener for field value change events.
 * <p>
 * Use {@link Binder#addFieldValueChangeListener(FieldValueChangeListener)} to
 * register a listener to get events when any field in the binder is changed.
 * 
 * @see FieldValueChangeEvent
 * @see Binder#addFieldValueChangeListener(FieldValueChangeListener)
 * 
 * @author Vaadin Ltd
 * @since 8.0
 * 
 * @param <BEAN>
 *            the bean type in the binder
 */
@FunctionalInterface
public interface FieldValueChangeListener<BEAN>
        extends SerializableEventListener {

    /**
     * Notifies the listener about field value change {@code event}.
     *
     * @param event
     *            a field value change event, not null
     */
    public void fieldValueChange(FieldValueChangeEvent<BEAN> event);
}
