/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.Optional;
import java.util.Set;

import com.vaadin.event.selection.SelectionListener;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Grid;
import com.vaadin.ui.SingleSelect;

/**
 * Wrapper class to wrap Grid into a SingleSelect. This class also provides
 * useful access to API of SingleSelectionModel.
 *
 * @param <T>
 *            the bean type of grid
 * @since
 */
public class GridSingleSelect<T> implements SingleSelect<T> {

    private SingleSelectionModel<T> model;

    /**
     * Constructs a SingleSelect wrapper for given Grid.
     *
     * @param grid
     *            the grid to wrap
     */
    public GridSingleSelect(Grid<T> grid) {
        GridSelectionModel<T> selectionModel = grid.getSelectionModel();
        if (!(selectionModel instanceof SingleSelectionModel)) {
            throw new IllegalStateException(
                    "Grid is not in singleiselect mode, it needs to be explicitly set to such with setSelectionModel(SingleSelectionModel) before being able to use singleselection features.");
        }
        model = (SingleSelectionModel<T>) selectionModel;
    }

    /* API for SingleSelectionModel */

    /**
     * Sets whether it's allowed to deselect the selected row through the UI.
     * Deselection is allowed by default.
     *
     * @param deselectAllowed
     *            <code>true</code> if the selected row can be deselected
     *            without selecting another row instead; otherwise
     *            <code>false</code>.
     */
    public void setDeselectAllowed(boolean deselectAllowed) {
        model.setDeselectAllowed(deselectAllowed);
    }

    /**
     * Gets whether it's allowed to deselect the selected row through the UI.
     *
     * @return <code>true</code> if deselection is allowed; otherwise
     *         <code>false</code>
     */
    public boolean isDeselectAllowed() {
        return model.isDeselectAllowed();
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
            SingleSelectionListener<T> listener) {
        return model.addSingleSelectionListener(listener);
    }

    /**
     * Returns the currently selected item, or an empty optional if no item is
     * selected.
     *
     * @return an optional of the selected item if any, an empty optional
     *         otherwise
     */
    public Optional<T> getSelectedItem() {
        return model.getSelectedItem();
    }

    /**
     * Sets the current selection to the given item, or clears selection if
     * given {@code null}.
     *
     * @param item
     *            the item to select or {@code null} to clear selection
     */
    public void setSelectedItem(T item) {
        model.setSelectedItem(item);
    }

    /* Generic SelectionModel API */

    /**
     * Sets whether the user is allowed to change the selection.
     * <p>
     * The check is done only for the client side actions. It doesn't affect
     * selection requests sent from the server side.
     *
     * @param allowed
     *            <code>true</code> if the user is allowed to change the
     *            selection, <code>false</code> otherwise
     */
    public void setUserSelectionAllowed(boolean allowed) {
        model.setUserSelectionAllowed(allowed);
    }

    /**
     * Checks if the user is allowed to change the selection.
     * <p>
     * The check is done only for the client side actions. It doesn't affect
     * selection requests sent from the server side.
     *
     * @return <code>true</code> if the user is allowed to change the selection,
     *         <code>false</code> otherwise
     */
    public boolean isUserSelectionAllowed() {
        return model.isUserSelectionAllowed();
    }

    /**
     * Returns a singleton set of the currently selected item or an empty set if
     * no item is selected.
     *
     * @return a singleton set of the selected item if any, an empty set
     *         otherwise
     *
     * @see #getSelectedItem()
     */
    public Set<T> getSelectedItems() {
        return model.getSelectedItems();
    }

    /**
     * Get first selected data item.
     *
     * @return the first selected item.
     */
    public Optional<T> getFirstSelectedItem() {
        return model.getFirstSelectedItem();
    }

    /**
     * Selects the given item. Depending on the implementation, may cause other
     * items to be deselected. If the item is already selected, does nothing.
     *
     * @param item
     *            the item to select, not null
     */
    public void deselect(T item) {
        model.deselect(item);
    }

    /**
     * Deselects all currently selected items, if any.
     */
    public void deselectAll() {
        model.deselectAll();
    }

    /**
     * Adds a generic listener to this selection model, accepting both single
     * and multiselection events.
     * <p>
     * Use {@link #addSingleSelectionListener(SingleSelectionListener)} for more
     * specific single selection event.
     *
     * @see #addSingleSelectionListener(SingleSelectionListener)
     *
     * @param listener
     *            the listener to add
     * @return a registration handle for removing the listener
     */
    public Registration addSelectionListener(SelectionListener<T> listener) {
        return model.addSelectionListener(listener);
    }

    /**
     * Returns whether the given item is currently selected.
     *
     * @param item
     *            the item to check, not null
     * @return {@code true} if the item is selected, {@code false} otherwise
     */
    public boolean isSelected(T item) {
        return model.isSelected(item);
    }

    /**
     * Selects the given item. If another item was already selected, that item
     * is deselected.
     *
     * @param item
     *            the item to select
     */
    public void select(T item) {
        model.select(item);
    }

    /* SingleSelect implementation */

    @Override
    public void setValue(T value) {
        model.asSingleSelect().setValue(value);
    }

    @Override
    public T getValue() {
        return model.asSingleSelect().getValue();
    }

    @Override
    public Registration addValueChangeListener(
            ValueChangeListener<T> listener) {
        return model.asSingleSelect().addValueChangeListener(listener);
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        model.asSingleSelect()
                .setRequiredIndicatorVisible(requiredIndicatorVisible);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return model.asSingleSelect().isRequiredIndicatorVisible();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        model.asSingleSelect().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return model.asSingleSelect().isReadOnly();
    }
}
