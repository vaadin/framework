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
package com.vaadin.ui;

import java.lang.reflect.Method;
import java.util.Objects;

import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.SelectionModel.Multi;
import com.vaadin.util.ReflectTools;

/**
 * Base class for listing components that allow selecting multiple items.
 *
 * @param <T>
 *            item type
 * @author Vaadin Ltd
 * @since 8.0
 */
public abstract class AbstractMultiSelect<T>
        extends AbstractListing<T, Multi<T>> {

    @Deprecated
    private static final Method SELECTION_CHANGE_METHOD = ReflectTools
            .findMethod(MultiSelectionListener.class, "accept",
                    MultiSelectionEvent.class);

    /**
     * Creates a new multi select with an empty data source.
     */
    protected AbstractMultiSelect() {
        super();
    }

    /**
     * Adds a selection listener that will be called when the selection is
     * changed either by the user or programmatically.
     *
     * @param listener
     *            the value change listener, not <code>null</code>
     * @return a registration for the listener
     */
    public Registration addSelectionListener(
            MultiSelectionListener<T> listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        addListener(MultiSelectionEvent.class, listener,
                SELECTION_CHANGE_METHOD);
        return () -> removeListener(MultiSelectionEvent.class, listener);
    }

}