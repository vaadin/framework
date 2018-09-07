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

import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MultiSelect;
import com.vaadin.ui.components.grid.MultiSelectionModel.SelectAllCheckBoxVisibility;

/**
 * Wrapper class to wrap Grid into a MultiSelect. This class also provides
 * useful access to API of MultiSelectionModel.
 *
 * @param <T>
 *            the bean type of grid
 * @since
 */
public class GridMultiSelect<T> implements MultiSelect<T> {

    private MultiSelectionModel<T> model;

    /**
     * Constructs a MultiSelect wrapper for given Grid.
     *
     * @param grid
     *            the grid to wrap
     */
    public GridMultiSelect(Grid<T> grid) {
        GridSelectionModel<T> selectionModel = grid.getSelectionModel();
        if (!(selectionModel instanceof MultiSelectionModel)) {
            throw new IllegalStateException(
                    "Grid is not in multiselect mode, it needs to be explicitly set to such with setSelectionModel(MultiSelectionModel) before being able to use multiselection features.");
        }
        model = (MultiSelectionModel<T>) selectionModel;
    }

    /* API for MultiSelectionModelImpl */

    /**
     * Get first selected data item.
     *
     * @return the first selected item.
     */
    public Optional<T> getFirstSelectedItem() {
        return model.getFirstSelectedItem();
    }

    /**
     * Selects all available the items.
     */
    public void selectAll() {
        model.selectAll();
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
     * Selects the given item. If another item was already selected, that item
     * is deselected.
     *
     * @param item
     *            the item to select
     */
    public void select(T item) {
        model.select(item);
    }

    /**
     * Deselects all currently selected items, if any.
     */
    public void deselectAll() {
        model.deselectAll();
    }

    /**
     * Adds the given items to the set of currently selected items.
     * <p>
     * By default this does not clear any previous selection. To do that, use
     * {@link #deselectAll()}.
     * <p>
     * If the all the items were already selected, this is a NO-OP.
     * <p>
     * This is a short-hand for {@link #updateSelection(Set, Set)} with nothing
     * to deselect.
     *
     * @param items
     *            to add to selection, not {@code null}
     */
    public void selectItems(T... items) {
        model.selectItems(items);
    }

    /**
     * Removes the given items from the set of currently selected items.
     * <p>
     * If the none of the items were selected, this is a NO-OP.
     * <p>
     * This is a short-hand for {@link #updateSelection(Set, Set)} with nothing
     * to select.
     *
     * @param items
     *            to remove from selection, not {@code null}
     */
    public void deselectItems(T... items) {
        model.deselectItems(items);
    }

    /**
     * Sets the select all checkbox visibility mode.
     * <p>
     * The default value is {@link SelectAllCheckBoxVisibility#DEFAULT}, which
     * means that the checkbox is only visible if the grid's data provider is
     * in- memory.
     *
     * @param visibility
     *            the visiblity mode to use
     * @see SelectAllCheckBoxVisibility
     */
    public void setSelectAllCheckBoxVisibility(
            SelectAllCheckBoxVisibility visibility) {
        model.setSelectAllCheckBoxVisibility(visibility);
    }

    /**
     * Gets the current mode for the select all checkbox visibility.
     *
     * @return the select all checkbox visibility mode
     * @see SelectAllCheckBoxVisibility
     * @see #isSelectAllCheckBoxVisible()
     */
    public SelectAllCheckBoxVisibility getSelectAllCheckBoxVisibility() {
        return model.getSelectAllCheckBoxVisibility();
    }

    /**
     * Returns whether the select all checkbox will be visible with the current
     * setting of
     * {@link #setSelectAllCheckBoxVisibility(SelectAllCheckBoxVisibility)}.
     *
     * @return {@code true} if the checkbox will be visible with the current
     *         settings
     * @see SelectAllCheckBoxVisibility
     * @see #setSelectAllCheckBoxVisibility(SelectAllCheckBoxVisibility)
     */
    public boolean isSelectAllCheckBoxVisible() {
        return model.isSelectAllCheckBoxVisible();
    }

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
     * Adds a generic listener to this selection model, accepting both single
     * and multiselection events.
     *
     * @param listener
     *            the listener to add
     * @return a registration handle for removing the listener
     */
    public Registration addSelectionListener(SelectionListener<T> listener) {
        return model.addSelectionListener(listener);
    }

    /**
     * Adds a selection listener that will be called when the selection is
     * changed either by the user or programmatically.
     *
     * @param listener
     *            the value change listener, not {@code null}
     * @return a registration for the listener
     */
    public Registration addMultiSelectionListener(
            MultiSelectionListener<T> listener) {
        return model.addMultiSelectionListener(listener);
    }

    /* MultiSelect implementation */

    @Override
    public void setValue(Set<T> value) {
        model.asMultiSelect().setValue(value);
    }

    @Override
    public Set<T> getValue() {
        return model.asMultiSelect().getValue();
    }

    @Override
    public Registration addValueChangeListener(
            ValueChangeListener<Set<T>> listener) {
        return model.asMultiSelect().addValueChangeListener(listener);
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        model.asMultiSelect()
                .setRequiredIndicatorVisible(requiredIndicatorVisible);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return model.asMultiSelect().isRequiredIndicatorVisible();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        model.asMultiSelect().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return model.asMultiSelect().isReadOnly();
    }

    @Override
    public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
        model.asMultiSelect().updateSelection(addedItems, removedItems);
    }

    @Override
    public Set<T> getSelectedItems() {
        return model.asMultiSelect().getSelectedItems();
    }

    @Override
    public Registration addSelectionListener(
            MultiSelectionListener<T> listener) {
        return model.asMultiSelect().addSelectionListener(listener);
    }
}
