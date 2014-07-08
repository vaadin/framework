/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.ui.components.grid.selection;

import java.io.Serializable;
import java.util.Collection;

import com.vaadin.ui.components.grid.Grid;

/**
 * The server-side interface that controls Grid's selection state.
 * 
 * @since
 * @author Vaadin Ltd
 */
public interface SelectionModel extends Serializable {
    /**
     * Checks whether an item is selected or not.
     * 
     * @param itemId
     *            the item id to check for
     * @return <code>true</code> iff the item is selected
     */
    boolean isSelected(Object itemId);

    /**
     * Returns a collection of all the currently selected itemIds.
     * 
     * @return a collection of all the currently selected itemIds
     */
    Collection<Object> getSelectedRows();

    /**
     * Injects the current {@link Grid} instance into the SelectionModel.
     * <p>
     * <em>Note:</em> This method should not be called manually.
     * 
     * @param grid
     *            the Grid in which the SelectionModel currently is, or
     *            <code>null</code> when a selection model is being detached
     *            from a Grid.
     */
    void setGrid(Grid grid);

    /**
     * Resets the SelectiomModel to an initial state.
     * <p>
     * Most often this means that the selection state is cleared, but
     * implementations are free to interpret the "initial state" as they wish.
     * Some, for example, may want to keep the first selected item as selected.
     */
    void reset();

    /**
     * A SelectionModel that supports multiple selections to be made.
     * <p>
     * This interface has a contract of having the same behavior, no matter how
     * the selection model is interacted with. In other words, if something is
     * forbidden to do in e.g. the user interface, it must also be forbidden to
     * do in the server-side and client-side APIs.
     */
    public interface Multi extends SelectionModel {

        /**
         * Marks items as selected.
         * <p>
         * This method does not clear any previous selection state, only adds to
         * it.
         * 
         * @param itemIds
         *            the itemId(s) to mark as selected
         * @return <code>true</code> if the selection state changed.
         *         <code>false</code> if all the given itemIds already were
         *         selected
         * @throws IllegalArgumentException
         *             if the <code>itemIds</code> varargs array is
         *             <code>null</code>
         * @see #deselect(Object...)
         */
        boolean select(Object... itemIds) throws IllegalArgumentException;

        /**
         * Marks items as selected.
         * <p>
         * This method does not clear any previous selection state, only adds to
         * it.
         * 
         * @param itemIds
         *            the itemIds to mark as selected
         * @return <code>true</code> if the selection state changed.
         *         <code>false</code> if all the given itemIds already were
         *         selected
         * @throws IllegalArgumentException
         *             if <code>itemIds</code> is <code>null</code>
         * @see #deselect(Collection)
         */
        boolean select(Collection<?> itemIds) throws IllegalArgumentException;

        /**
         * Marks items as deselected.
         * 
         * @param itemIds
         *            the itemId(s) to remove from being selected
         * @return <code>true</code> if the selection state changed.
         *         <code>false</code> if none the given itemIds were selected
         *         previously
         * @throws IllegalArgumentException
         *             if the <code>itemIds</code> varargs array is
         *             <code>null</code>
         * @see #select(Object...)
         */
        boolean deselect(Object... itemIds) throws IllegalArgumentException;

        /**
         * Marks items as deselected.
         * 
         * @param itemIds
         *            the itemId(s) to remove from being selected
         * @return <code>true</code> if the selection state changed.
         *         <code>false</code> if none the given itemIds were selected
         *         previously
         * @throws IllegalArgumentException
         *             if <code>itemIds</code> is <code>null</code>
         * @see #select(Collection)
         */
        boolean deselect(Collection<?> itemIds) throws IllegalArgumentException;

        /**
         * Marks all the items in the current Container as selected
         * 
         * @return <code>true</code> iff some items were previously not selected
         * @see #deselectAll()
         */
        boolean selectAll();

        /**
         * Marks all the items in the current Container as deselected
         * 
         * @return <code>true</code> iff some items were previously selected
         * @see #selectAll()
         */
        boolean deselectAll();
    }

    /**
     * A SelectionModel that supports for only single rows to be selected at a
     * time.
     * <p>
     * This interface has a contract of having the same behavior, no matter how
     * the selection model is interacted with. In other words, if something is
     * forbidden to do in e.g. the user interface, it must also be forbidden to
     * do in the server-side and client-side APIs.
     */
    public interface Single extends SelectionModel {
        /**
         * Marks an item as selected.
         * 
         * @param itemIds
         *            the itemId to mark as selected
         * @return <code>true</code> if the selection state changed.
         *         <code>false</code> if the itemId already was selected
         * @throws IllegalStateException
         *             if the selection was illegal. One such reason might be
         *             that the implementation already had an item selected, and
         *             that needs to be explicitly deselected before
         *             re-selecting something
         * @see #deselect(Object)
         */
        boolean select(Object itemId) throws IllegalStateException;

        /**
         * Marks an item as deselected.
         * 
         * @param itemId
         *            the itemId to remove from being selected
         * @return <code>true</code> if the selection state changed.
         *         <code>false</code> if the itemId already was selected
         * @throws IllegalStateException
         *             if the deselection was illegal. One such reason might be
         *             that the implementation enforces that an item is always
         *             selected
         * @see #select(Object)
         */
        boolean deselect(Object itemId) throws IllegalStateException;

        /**
         * Gets the item id of the currently selected item.
         * 
         * @return the item id of the currently selected item, or
         *         <code>null</code> if nothing is selected
         */
        Object getSelectedRow();
    }

    /**
     * A SelectionModel that does not allow for rows to be selected.
     * <p>
     * This interface has a contract of having the same behavior, no matter how
     * the selection model is interacted with. In other words, if the developer
     * is unable to select something programmatically, it is not allowed for the
     * end-user to select anything, either.
     */
    public interface None extends SelectionModel {

        /**
         * {@inheritDoc}
         * 
         * @return always <code>false</code>.
         */
        @Override
        public boolean isSelected(Object itemId);

        /**
         * {@inheritDoc}
         * 
         * @return always an empty collection.
         */
        @Override
        public Collection<Object> getSelectedRows();
    }
}
