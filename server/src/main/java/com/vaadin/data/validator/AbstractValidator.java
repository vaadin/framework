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
package com.vaadin.data.validator;

import java.util.Objects;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.server.SerializableFunction;

/**
 * An abstract base class for typed validators.
 *
 * @param <T>
 *            The value type
 * @author Vaadin Ltd.
 * @since 8.0
 */
public abstract class AbstractValidator<T> implements Validator<T> {

    private final SerializableFunction<T, String> messageProvider;

    /**
     * Constructs a validator with the given error message. The substring "{0}"
     * is replaced by the value that failed validation.
     *
     * @param errorMessage
     *            the message to be included in a failed result, not null
     */
    protected AbstractValidator(String errorMessage) {
        Objects.requireNonNull(errorMessage, "error message cannot be null");
        this.messageProvider = value -> errorMessage.replace("{0}",
                String.valueOf(value));
    }

    /**
     * Returns the error message for the given value.
     *
     * @param value
     *            an invalid value
     * @return the formatted error message
     */
    protected String getMessage(T value) {
        return messageProvider.apply(value);
    }

    /**
     * A helper method for creating a {@code Result} from a value and a validity
     * flag. If the flag is true, returns {@code Result.ok}, otherwise yields
     * {@code Result.error} bearing the error message returned by
     * {@link #getMessage(T)}.
     * <p>
     * For instance, the following {@code apply} method only accepts even
     * numbers:
     *
     * <pre>
     * &#64;Override
     * public Result&lt;T&gt; apply(Integer value) {
     *     return toResult(value, value % 2 == 0);
     * }
     * </pre>
     *
     * @param value
     *            the validated value
     * @param isValid
     *            whether the value is valid or not
     * @return the validation result
     */
    protected ValidationResult toResult(T value, boolean isValid) {
        return isValid ? ValidationResult.ok()
                : ValidationResult.error(getMessage(value));
    }
}
