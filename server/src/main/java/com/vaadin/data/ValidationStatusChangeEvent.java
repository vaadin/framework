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

import java.util.EventObject;
import java.util.Objects;
import java.util.Optional;

import com.vaadin.data.Binder.Binding;

/**
 * Validation status change event which is fired each time when validation is
 * done. Use {@link Binding#withStatusChangeHandler(StatusChangeHandler)} method
 * to add your validation handler to listen to the event.
 *
 * @see Binding#withStatusChangeHandler(StatusChangeHandler)
 * @see StatusChangeHandler
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public class ValidationStatusChangeEvent extends EventObject {

    private final ValidationStatus status;
    private final String message;

    /**
     * Creates a new status change event.
     * <p>
     * The {@code message} must be null if the {@code status} is
     * {@link ValidationStatus#OK}.
     *
     * @param source
     *            field whose status has changed, not {@code null}
     * @param status
     *            updated status value, not {@code null}
     * @param message
     *            error message if status is ValidationStatus.ERROR, may be
     *            {@code null}
     */
    public ValidationStatusChangeEvent(HasValue<?> source,
            ValidationStatus status, String message) {
        super(source);
        Objects.requireNonNull(source, "Event source may not be null");
        Objects.requireNonNull(status, "Status may not be null");
        if (Objects.equals(status, ValidationStatus.OK) && message != null) {
            throw new IllegalStateException(
                    "Message must be null if status is not an error");
        }
        this.status = status;
        this.message = message;
    }

    /**
     * Returns validation status of the event.
     *
     * @return validation status
     */
    public ValidationStatus getStatus() {
        return status;
    }

    /**
     * Returns error validation message if status is
     * {@link ValidationStatus#ERROR}.
     *
     * @return an optional validation error status or an empty optional if
     *         status is not an error
     */
    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    @Override
    public HasValue<?> getSource() {
        return (HasValue<?>) super.getSource();
    }

}
