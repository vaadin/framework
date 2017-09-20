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

import com.vaadin.data.Binder.Binding;
import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.server.Setter;

/**
 * Binder status change event.
 * <p>
 * The {@link Binder} status is changed whenever any of the following happens:
 * <ul>
 * <li>if any of its bound fields or selects have been changed
 * <li>{@link Binder#writeBean(Object)} or
 * {@link Binder#writeBeanIfValid(Object)} is called
 * <li>{@link Binder#readBean(Object)} is called
 * <li>{@link Binder#setBean(Object)} is called
 * <li>{@link Binder#removeBean()} is called
 * <li>{@link BindingBuilder#bind(ValueProvider, Setter)} is called
 * <li>{@link Binder#validate()} or {@link Binding#validate()} is called
 * </ul>
 *
 * @see StatusChangeListener#statusChange(StatusChangeEvent)
 * @see Binder#addStatusChangeListener(StatusChangeListener)
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 */
public class StatusChangeEvent extends EventObject {

    private final boolean hasValidationErrors;

    /**
     * Create a new status change event for given {@code binder}, storing
     * information of whether the change that triggered this event caused
     * validation errors.
     *
     * @param binder
     *            the event source binder
     * @param hasValidationErrors
     *            the validation status associated with this event
     */
    public StatusChangeEvent(Binder<?> binder, boolean hasValidationErrors) {
        super(binder);
        this.hasValidationErrors = hasValidationErrors;
    }

    /**
     * Gets the associated validation status.
     *
     * @return {@code true} if the change that triggered this event caused
     *         validation errors, {@code false} otherwise
     */
    public boolean hasValidationErrors() {
        return hasValidationErrors;
    }

    @Override
    public Binder<?> getSource() {
        return (Binder<?>) super.getSource();
    }

    /**
     * Gets the binder.
     *
     * @return the binder
     */
    public Binder<?> getBinder() {
        return getSource();
    }

}
