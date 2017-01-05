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

import com.vaadin.server.SerializableFunction;

/**
 * Provider interface for generating localizable error messages using
 * {@link ValueContext}.
 *
 * @since 8.0
 * @author Vaadin Ltd.
 */
@FunctionalInterface
public interface ErrorMessageProvider
        extends SerializableFunction<ValueContext, String> {

    /**
     * Returns a generated error message for given {@code ValueContext}.
     *
     * @param context
     *            the value context
     *
     * @return generated error message
     */
    @Override
    public String apply(ValueContext context);
}
