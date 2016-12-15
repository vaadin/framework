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
package com.vaadin.ui.components.grid;

import java.io.Serializable;
import java.util.Map;
import java.util.function.BiFunction;

import com.vaadin.data.BinderValidationStatus;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;

/**
 * Generator for creating editor validation and conversion error messages.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <T>
 *            the bean type
 */
@FunctionalInterface
public interface EditorErrorGenerator<T> extends Serializable,
        BiFunction<Map<Component, Grid.Column<T, ?>>, BinderValidationStatus<T>, String> {

    /**
     * Generates an error message from given validation status object.
     *
     * @param fieldToColumn
     *            the map of failed fields and corresponding columns
     * @param status
     *            the binder status object with all failures
     *
     * @return error message string
     */
    @Override
    public String apply(Map<Component, Grid.Column<T, ?>> fieldToColumn,
            BinderValidationStatus<T> status);
}
