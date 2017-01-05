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

import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

/**
 * Simple validator to check against {@code null} value and empty {@link String}
 * value.
 * <p>
 * This validator works similar to {@link NotNullValidator} but in addition it
 * also check whether the value is not an empty String.
 * <p>
 * This validator can be suitable for fields that have been marked as required
 * with {@link HasValue#setRequiredIndicatorVisible(boolean)}.
 * <p>
 * Note that
 * {@link BindingBuilder#asRequired(com.vaadin.data.ErrorMessageProvider)} does
 * almost the same thing, but verifies against the value NOT being equal to what
 * {@link HasValue#getEmptyValue()} returns and sets the required indicator
 * visible with {@link HasValue#setRequiredIndicatorVisible(boolean)}.
 *
 * @see HasValue#setRequiredIndicatorVisible(boolean)
 * @see BindingBuilder#asRequired(com.vaadin.data.ErrorMessageProvider)
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public class NotEmptyValidator<T> implements Validator<T> {

    private final String message;

    /**
     * @param message
     *            error validation message
     */
    public NotEmptyValidator(String message) {
        this.message = message;
    }

    @Override
    public ValidationResult apply(T value, ValueContext context) {
        if (Objects.isNull(value) || Objects.equals(value, "")) {
            return ValidationResult.error(message);
        } else {
            return ValidationResult.ok();
        }
    }

}
