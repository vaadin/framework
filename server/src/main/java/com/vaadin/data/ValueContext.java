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
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * Value context for {@code Converter}s. Contains relevant information for
 * converting values.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 */
public class ValueContext implements Serializable {

    private final Component component;
    private final Locale locale;

    /**
     * Constructor for {@code ValueContext} without a {@code Locale}.
     */
    public ValueContext() {
        component = null;
        locale = findLocale();
    }

    /**
     * Constructor for {@code ValueContext} without a {@code Component}.
     *
     * @param locale
     *            The locale used with conversion. Can be null.
     */
    public ValueContext(Locale locale) {
        component = null;
        this.locale = locale;
    }

    /**
     * Constructor for {@code ValueContext}.
     *
     * @param component
     *            The component related to current value. Can be null.
     */
    public ValueContext(Component component) {
        Objects.requireNonNull(component,
                "Component can't be null in ValueContext construction");
        this.component = component;
        locale = findLocale();
    }

    private Locale findLocale() {
        Locale l = null;
        if (component != null) {
            l = component.getLocale();
        }
        if (l == null && UI.getCurrent() != null) {
            l = UI.getCurrent().getLocale();
        }
        if (l == null) {
            l = Locale.getDefault();
        }
        return l;
    }

    /**
     * Returns an {@code Optional} for the {@code Component} related to value
     * conversion.
     *
     * @return the optional of component
     */
    public Optional<Component> getComponent() {
        return Optional.ofNullable(component);
    }

    /**
     * Returns an {@code Optional} for the {@code Locale} used in the value
     * conversion.
     *
     * @return the optional of locale
     */
    public Optional<Locale> getLocale() {
        return Optional.ofNullable(locale);
    }
}
