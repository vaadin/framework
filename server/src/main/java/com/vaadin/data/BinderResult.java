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

import java.util.Optional;

import com.vaadin.data.Binder.Binding;

/**
 * A result that keeps track of the possible binding (field) it belongs to.
 *
 * @param <FIELDVALUE>
 *            the value type of the field
 * @param <VALUE>
 *            the result value type and the data type of the binding, matches
 *            the field type if a converter has not been set
 */
public class BinderResult<FIELDVALUE, VALUE> extends SimpleResult<VALUE> {

    private final Binding<?, FIELDVALUE, VALUE> binding;

    /**
     * Creates a new binder result.
     *
     * @param binding
     *            the binding where the result originated, may be {@code null}
     * @param value
     *            the resut value, can be <code>null</code>
     * @param message
     *            the error message of the result, may be {@code null}
     */
    public BinderResult(Binding<?, FIELDVALUE, VALUE> binding, VALUE value,
            String message) {
        super(value, message);
        this.binding = binding;
    }

    /**
     * Return the binding this result originated from, or an empty optional if
     * none.
     *
     * @return the optional binding
     */
    public Optional<Binding<?, FIELDVALUE, VALUE>> getBinding() {
        return Optional.ofNullable(binding);
    }

    /**
     * Return the field this result originated from, or an empty optional if
     * none.
     *
     * @return the optional field
     */
    public Optional<HasValue<FIELDVALUE>> getField() {
        return binding == null ? Optional.empty()
                : Optional.ofNullable(binding.getField());
    }

}