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

import java.util.Collections;
import java.util.List;

/**
 * Indicates validation errors in a {@link Binder} when save is requested.
 * 
 * @see Binder#save(Object)
 * 
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public class ValidationException extends Exception {

    private final List<ValidationError<?>> errors;

    /**
     * Constructs a new exception with validation {@code errors} list.
     * 
     * @param errors
     *            validation errors list
     */
    public ValidationException(List<ValidationError<?>> errors) {
        super("Validation has failed for some fields");
        this.errors = Collections.unmodifiableList(errors);
    }

    /**
     * Returns the validation errors list which caused the exception.
     * 
     * @return validation errors list
     */
    public List<ValidationError<?>> getValidationError() {
        return errors;
    }
}
