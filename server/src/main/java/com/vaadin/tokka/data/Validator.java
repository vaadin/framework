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

package com.vaadin.tokka.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.vaadin.tokka.data.Validator.Result;

/**
 * A functional interface for validating user input. When a validator instance
 * is applied to a value of the corresponding type, it returns a <i>result</i>
 * signifying that the value either passed or failed the validation.
 * 
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the type of the value to validate
 */
@FunctionalInterface
public interface Validator<T>
        extends Function<T, Validator.Result>, Serializable {

    /**
     * The result of applying a validator to a value.
     *
     */
    public interface Result extends Serializable {
        /**
         * Creates a result signaling that the value passed the validation.
         * 
         * @return a "passed" result
         */
        public static Result ok() {
            return new OK();
        }

        /**
         * Returns a result signaling that the value failed to validate, with
         * the given error message. The error message should be aimed at the end
         * user, not the developer, and contain immediately useful info on how
         * the user should amend their input.
         * 
         * @param message
         *            the error message
         * @return a "failed" result
         */
        public static Result error(String message) {
            return new Error(message);
        }

        /**
         * Returns whether this result signals a passing validation.
         * 
         * @return whether this result is "ok" or not
         */
        public boolean isOk();

        /**
         * Returns a possibly empty list of validation messages.
         * 
         * @return a list of validation messages
         */
        public List<String> getMessages();
    }

    /**
     * Returns a validator that passes any value.
     * 
     * @return an always-passing validator
     */
    public static <T> Validator<T> alwaysPass() {
        return v -> Result.ok();
    }

    /**
     * Builds a validator out of a predicate function and an error message. If
     * the predicate returns true, the validator returns {@code Result.ok()}; if
     * it returns false or throws an exception, {@code Result.error()} is
     * returned with the given message.
     * 
     * @param guard
     *            the predicate used to validate
     * @param errorMessage
     *            the message returned if validation fails
     * @return the new validator using the predicate
     */
    public static <T> Validator<T> from(Predicate<T> guard,
            String errorMessage) {
        return value -> {
            try {
                if (guard.test(value)) {
                    return Result.ok();
                } else {
                    return Result.error(errorMessage);
                }
            } catch (Exception e) {
                return Result
                        .error(errorMessage + ": " + e.getLocalizedMessage());
            }
        };
    }
}

/**
 * Represents a successful validation result.
 */
class OK implements Result {

    @Override
    public List<String> getMessages() {
        return Collections.emptyList();
    }

    @Override
    public boolean isOk() {
        return true;
    }
};

/**
 * Represents a failed validation result.
 */
class Error implements Validator.Result {

    private List<String> messages = new ArrayList<>();

    Error(String message) {
        messages.add(message);
    }

    @Override
    public List<String> getMessages() {
        return messages;
    }

    @Override
    public boolean isOk() {
        return false;
    }
}
