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

import com.vaadin.data.Result;

/**
 * This validator is used for validating properties that do not allow null
 * values.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 */
@SuppressWarnings("serial")
public class NotNullValidator extends AbstractValidator<String> {

    /**
     * Creates a new NullValidator.
     *
     * @param errorMessage
     *            the error message to display on invalidation.
     */
    public NotNullValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public Result<String> apply(String value) {
        return Objects.isNull(value) ? Result.error(getMessage(value))
                : Result.ok(value);
    }

}
