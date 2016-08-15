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
package com.vaadin.data.validator;

import java.util.Objects;

import com.vaadin.data.HasRequired;
import com.vaadin.data.Result;
import com.vaadin.data.Validator;

/**
 * Simple validator to check against {@code null} value and empty {@link String}
 * value.
 * <p>
 * This validator works similar to {@link NotNullValidator} but in addition it
 * also check whether the value is not an empty String.
 * <p>
 * The main purpose of this validator is its usage with {@link HasRequired}
 * field instances.
 * <p>
 * If the field is required, it is visually indicated in the user interface.
 * Furthermore, required fields requires "non-empty" validator. So in addition
 * to call {@link HasRequired#setRequired(boolean)} method one should add an
 * instance of this validator explicitly so the code looks like this:
 * 
 * <pre>
 * <code>
 * Binder<Bean,String, String> binder = new Binder<>();
 * TextField name = new TextField();
 * name.setRequired(true);
 * binder.forField(name).withValidator(
 *      new NonEmptyValidator("Name cannot be empty"))
 *              .bind(Bean::getName, Bean::setName);
 * </code>
 * </pre>
 * 
 * @see HasRequired
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
    public Result<T> apply(T value) {
        if (Objects.isNull(value) || Objects.equals(value, "")) {
            return Result.error(message);
        } else {
            return Result.ok(value);
        }
    }

}
