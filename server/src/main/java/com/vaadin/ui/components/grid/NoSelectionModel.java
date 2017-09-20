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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.Registration;

/**
 * Selection model that doesn't allow selecting anything from the grid.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 * @param <T>
 *            the type of items in the grid
 */
public class NoSelectionModel<T> extends AbstractExtension
        implements GridSelectionModel<T> {

    @Override
    public Set<T> getSelectedItems() {
        return Collections.emptySet();
    }

    @Override
    public Optional<T> getFirstSelectedItem() {
        return Optional.empty();
    }

    @Override
    public void select(T item) {
    }

    @Override
    public void deselect(T item) {
    }

    @Override
    public void deselectAll() {
    }

    @Override
    public Registration addSelectionListener(SelectionListener<T> listener) {
        throw new UnsupportedOperationException(
                "This selection model doesn't allow selection, cannot add selection listeners to it");
    }

    @Override
    public void setUserSelectionAllowed(boolean allowed) {
    }

    @Override
    public boolean isUserSelectionAllowed() {
        return false;
    }

}
