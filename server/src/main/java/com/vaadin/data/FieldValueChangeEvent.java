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

import java.util.EventObject;

/**
 * Field value change event.
 * <p>
 * Use {@link Binder#addFieldValueChangeListener(FieldValueChangeListener)} to
 * register a listener to get events when any field in the binder is changed.
 * 
 * @see FieldValueChangeListener
 * @see Binder#addFieldValueChangeListener(FieldValueChangeListener)
 * 
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <BEAN>
 *            the bean type in the binder
 */
public class FieldValueChangeEvent<BEAN> extends EventObject {

    private final Binder<BEAN> binder;

    /**
     * Create a new field value change event for given {@code binder} using the
     * event source.
     *
     * @param binder
     *            the binder which the field is bound to
     * @param field
     *            the event source field
     */
    public FieldValueChangeEvent(Binder<BEAN> binder, HasValue<?> field) {
        super(field);
        this.binder = binder;
    }

    /**
     * The field on which the event initially occurred.
     *
     * @return the field on which the event initially occurred.
     */
    @Override
    public HasValue<?> getSource() {
        return (HasValue<?>) super.getSource();
    }

    /**
     * Gets the binder of the event.
     * 
     * @return the binder
     */
    public Binder<BEAN> getBinder() {
        return binder;
    }
}
