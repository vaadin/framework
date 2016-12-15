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

import com.vaadin.data.Binder;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.ui.SingleSelect;

/**
 * Single selection model interface for Grid.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <T>
 *            the type of items in grid
 */
public interface SingleSelectionModel<T> extends GridSelectionModel<T>,
        com.vaadin.data.SelectionModel.Single<T> {

    /**
     * Gets a wrapper to use this single selection model as a single select in
     * {@link Binder}.
     *
     * @return the single select wrapper
     */
    SingleSelect<T> asSingleSelect();

    /**
     * {@inheritDoc}
     * <p>
     * Use {@link #addSingleSelectionListener(SingleSelectionListener)} for more
     * specific single selection event.
     *
     * @see #addSingleSelectionListener(SingleSelectionListener)
     */
    @Override
    public default Registration addSelectionListener(
            SelectionListener<T> listener) {
        return addSingleSelectionListener(e -> listener.selectionChange(e));
    }

    /**
     * Adds a single selection listener that is called when the value of this
     * select is changed either by the user or programmatically.
     *
     * @param listener
     *            the value change listener, not {@code null}
     * @return a registration for the listener
     */
    public Registration addSingleSelectionListener(
            SingleSelectionListener<T> listener);
}
