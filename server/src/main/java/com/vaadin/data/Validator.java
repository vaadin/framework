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
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A functional interface for validating user input or other potentially invalid
 * data. When a validator instance is applied to a value of the corresponding
 * type, it returns a <i>result</i> signifying that the value either passed or
 * failed the validation.
 * <p>
 * For instance, the following validator checks if a number is positive:
 *
 * <pre>
 * Validator&lt;Integer&gt; v = num -> {
 *     if (num >= 0)
 *         return Result.ok(num);
 *     else
 *         return Result.error("number must be positive");
 * };
 * </pre>
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the type of the value to validate
 *
 * @see Result
 */
@FunctionalInterface
public interface Validator<T> extends Function<T, Result<T>>, Serializable {

    /**
     * Returns a validator that chains this validator with the given function.
     * Specifically, the function may be another validator. The resulting
     * validator first applies this validator, and if the value passes, then the
     * given validator.
     * <p>
     * For instance, the following chained validator checks if a number is
     * between 0 and 10, inclusive:
     *
     * <pre>
     * Validator&lt;Integer&gt; v = Validator.from(num -> num >= 0, "number must be >= 0")
     *         .chain(Validator.from(num -> num <= 10, "number must be <= 10"));
     * </pre>
     *
     * @param next
     *            the validator to apply next, not null
     * @return a chained validator
     *
     * @see #from(Predicate, String)
     */
    public default Validator<T> chain(Function<T, Result<T>> next) {
        Objects.requireNonNull(next, "next cannot be null");
        return val -> apply(val).flatMap(next);
    }

    /**
     * Validates the given value. Returns a {@code Result} instance representing
     * the outcome of the validation.
     *
     * @param value
     *            the input value to validate
     * @return the validation result
     */
    @Override
    public Result<T> apply(T value);

    /**
     * Returns a validator that passes any value.
     *
     * @param <T>
     *            the value type
     * @return an always-passing validator
     */
    public static <T> Validator<T> alwaysPass() {
        return v -> Result.ok(v);
    }

    /**
     * Builds a validator out of a conditional function and an error message. If
     * the function returns true, the validator returns {@code Result.ok()}; if
     * it returns false or throws an exception, {@code Result.error()} is
     * returned with the given message.
     * <p>
     * For instance, the following validator checks if a number is between 0 and
     * 10, inclusive:
     *
     * <pre>
     * Validator&lt;Integer&gt; v = Validator.from(num -> num >= 0 && num <= 10,
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
    public static <T> Validator<T> from(Predicate<T> guard,
            String errorMessage) {
        Objects.requireNonNull(guard, "guard cannot be null");
        Objects.requireNonNull(errorMessage, "errorMessage cannot be null");
        return value -> {
            try {
                if (guard.test(value)) {
                    return Result.ok(value);
                } else {
                    return Result.error(errorMessage);
                }
            } catch (Exception e) {
                return Result.error(errorMessage);
            }
        };
    }
}
