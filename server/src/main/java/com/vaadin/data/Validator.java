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
import java.util.Objects;
import java.util.function.BiFunction;

import com.vaadin.server.SerializablePredicate;

/**
 * A functional interface for validating user input or other potentially invalid
 * data. When a validator instance is applied to a value of the corresponding
 * type, it returns a <i>result</i> signifying that the value either passed or
 * failed the validation.
 * <p>
 * For instance, the following validator checks if a number is positive:
 *
 * <pre>
 * Validator&lt;Integer&gt; v = num -&gt; {
 *     if (num &gt;= 0)
 *         return ValidationResult.ok();
 *     else
 *         return ValidationResult.error("number must be positive");
 * };
 * </pre>
 *
 * @author Vaadin Ltd.
 *
 * @since 8.0
 *
 * @param <T>
 *            the type of the value to validate
 *
 * @see ValidationResult
 */
@FunctionalInterface
public interface Validator<T>
        extends BiFunction<T, ValueContext, ValidationResult>, Serializable {

    /**
     * Validates the given value. Returns a {@code ValidationResult} instance
     * representing the outcome of the validation.
     *
     * @param value
     *            the input value to validate
     * @param context
     *            the value context for validation
     * @return the validation result
     */
    @Override
    public ValidationResult apply(T value, ValueContext context);

    /**
     * Returns a validator that passes any value.
     *
     * @param <T>
     *            the value type
     * @return an always-passing validator
     */
    public static <T> Validator<T> alwaysPass() {
        return (value, context) -> ValidationResult.ok();
    }

    /**
     * Builds a validator out of a conditional function and an error message. If
     * the function returns true, the validator returns {@code Result.ok()}; if
     * it returns false or throws an exception,
     * {@link ValidationResult#error(String)} is returned with the given
     * message.
     * <p>
     * For instance, the following validator checks if a number is between 0 and
     * 10, inclusive:
     *
     * <pre>
     * Validator&lt;Integer&gt; v = Validator.from(num -&gt; num &gt;= 0 && num &lt;= 10,
     *         "number must be between 0 and 10");
     * </pre>
     *
     * @param <T>
     *            the value type
     * @param guard
     *            the function used to validate, not null
     * @param errorMessage
     *            the message returned if validation fails, not null
     * @return the new validator using the function
     */
    public static <T> Validator<T> from(SerializablePredicate<T> guard,
            String errorMessage) {
        Objects.requireNonNull(guard, "guard cannot be null");
        Objects.requireNonNull(errorMessage, "errorMessage cannot be null");
        return from(guard, ctx -> errorMessage);
    }

    /**
     * Builds a validator out of a conditional function and an error message
     * provider. If the function returns true, the validator returns
     * {@code Result.ok()}; if it returns false or throws an exception,
     * {@code Result.error()} is returned with the message from the provider.
     *
     * @param <T>
     *            the value type
     * @param guard
     *            the function used to validate, not null
     * @param errorMessageProvider
     *            the provider to generate error messages, not null
     * @return the new validator using the function
     */
    public static <T> Validator<T> from(SerializablePredicate<T> guard,
            ErrorMessageProvider errorMessageProvider) {
        Objects.requireNonNull(guard, "guard cannot be null");
        Objects.requireNonNull(errorMessageProvider,
                "errorMessageProvider cannot be null");
        return (value, context) -> {
            try {
                if (guard.test(value)) {
                    return ValidationResult.ok();
                } else {
                    return ValidationResult
                            .error(errorMessageProvider.apply(context));
                }
            } catch (Exception e) {
                return ValidationResult
                        .error(errorMessageProvider.apply(context));
            }
        };
    }
}
