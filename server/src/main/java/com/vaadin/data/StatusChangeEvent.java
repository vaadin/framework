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
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.vaadin.data.Binder.Binding;

/**
 * Binder status change event.
 * <p>
 * The {@link Binder} status is changed whenever any of the following happens:
 * <ul>
 * <li>if it's bound and any of its bound field or select has been changed
 * <li>{@link #save(Object)} or {@link #saveIfValid(Object)} is called
 * <li>{@link #load(Object)} is called
 * <li>{@link #bind(Object)} is called
 * <li>{@link #unbind(Object)} is called
 * <li>{@link Binding#bind(Function, BiConsumer)} is called
 * <li>{@link Binder#validate()} or {@link Binding#validate()} is called
 * </ul>
 * 
 * @see StatusChangeListener#statusChange(StatusChangeEvent)
 * @see Binder#addStatusChangeListener(StatusChangeListener)
 * 
 * @author Vaadin Ltd
 *
 */
public class StatusChangeEvent extends EventObject {

    private final boolean hasValidationErrors;

    /**
     * Create a new status change event for given {@code binder} using its
     * current validation status.
     * 
     * @param binder
     *            the event source binder
     * @param hasValidationErrors
     *            the binder validation status
     */
    public StatusChangeEvent(Binder<?> binder, boolean hasValidationErrors) {
        super(binder);
        this.hasValidationErrors = hasValidationErrors;
    }

    /**
     * Gets the binder validation status.
     * 
     * @return {@code true} if the binder has validation errors, {@code false}
     *         otherwise
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
