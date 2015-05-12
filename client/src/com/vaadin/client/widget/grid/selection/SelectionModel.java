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
package com.vaadin.client.widget.grid.selection;

import java.util.Collection;

import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widgets.Grid;

/**
 * Common interface for all selection models.
 * <p>
 * Selection models perform tracking of selected rows in the Grid, as well as
 * dispatching events when the selection state changes.
 * 
 * @author Vaadin Ltd
 * @param <T>
 *            Grid's row type
 * @since 7.4
 */
public interface SelectionModel<T> {

    /**
     * Return true if the provided row is considered selected under the
     * implementing selection model.
     * 
     * @param row
     *            row object instance
     * @return <code>true</code>, if the row given as argument is considered
     *         selected.
     */
    public boolean isSelected(T row);

    /**
     * Return the {@link Renderer} responsible for rendering the selection
     * column.
     * 
     * @return a renderer instance. If null is returned, a selection column will
     *         not be drawn.
     */
    public Renderer<Boolean> getSelectionColumnRenderer();

    /**
     * Tells this SelectionModel which Grid it belongs to.
     * <p>
     * Implementations are free to have this be a no-op. This method is called
     * internally by Grid.
     * 
     * @param grid
     *            a {@link Grid} instance; <code>null</code> when removing from
     *            Grid
     */
    public void setGrid(Grid<T> grid);

    /**
     * Resets the SelectionModel to the initial state.
     * <p>
     * This method can be called internally, for example, when the attached
     * Grid's data source changes.
     */
    public void reset();

    /**
     * Returns a Collection containing all selected rows.
     * 
     * @return a non-null collection.
     */
    public Collection<T> getSelectedRows();

    /**
     * Selection model that allows a maximum of one row to be selected at any
     * one time.
     * 
     * @param <T>
     *            type parameter corresponding with Grid row type
     */
    public interface Single<T> extends SelectionModel<T> {

        /**
         * Selects a row.
         * 
         * @param row
         *            a {@link Grid} row object
         * @return true, if this row as not previously selected.
         */
        public boolean select(T row);

        /**
         * Deselects a row.
         * <p>
         * This is a no-op unless {@link row} is the currently selected row.
         * 
         * @param row
         *            a {@link Grid} row object
         * @return true, if the currently selected row was deselected.
         */
        public boolean deselect(T row);

        /**
         * Returns the currently selected row.
         * 
         * @return a {@link Grid} row object or null, if nothing is selected.
         */
        public T getSelectedRow();

        /**
         * Sets whether it's allowed to deselect the selected row through the
         * UI. Deselection is allowed by default.
         * 
         * @param deselectAllowed
         *            <code>true</code> if the selected row can be deselected
         *            without selecting another row instead; otherwise
         *            <code>false</code>.
         */
        public void setDeselectAllowed(boolean deselectAllowed);

        /**
         * Sets whether it's allowed to deselect the selected row through the
         * UI.
         * 
         * @return <code>true</code> if deselection is allowed; otherwise
         *         <code>false</code>
         */
        public boolean isDeselectAllowed();

    }

    /**
     * Selection model that allows for several rows to be selected at once.
     * 
     * @param <T>
     *            type parameter corresponding with Grid row type
     */
    public interface Multi<T> extends SelectionModel<T> {

        /**
         * A multi selection model that can send selections and deselections in
         * a batch, instead of committing them one-by-one.
         * 
         * @param <T>
         *            type parameter corresponding with Grid row type
         */
        public interface Batched<T> extends Multi<T> {
            /**
             * Starts a batch selection.
             * <p>
             * Any commands to any select or deselect method will be batched
             * into one, and a final selection event will be fired when
             * {@link #commitBatchSelect()} is called.
             * <p>
             * <em>Note:</em> {@link SelectionEvent SelectionChangeEvents} will
             * still be fired for each selection/deselection. You should check
             * whether the event is a part of a batch or not with
             * {@link SelectionEvent#isBatchedSelection()}.
             */
            public void startBatchSelect();

            /**
             * Commits and ends a batch selection.
             * <p>
             * Any and all selections and deselections since the last invocation
             * of {@link #startBatchSelect()} will be fired at once as one
             * collated {@link SelectionEvent}.
             */
            public void commitBatchSelect();

            /**
             * Checks whether or not a batch has been started.
             * 
             * @return <code>true</code> iff a batch has been started
             */
            public boolean isBeingBatchSelected();

            /**
             * Gets all the rows that would become selected in this batch.
             * 
             * @return a collection of the rows that would become selected
             */
            public Collection<T> getSelectedRowsBatch();

            /**
             * Gets all the rows that would become deselected in this batch.
             * 
             * @return a collection of the rows that would become deselected
             */
            public Collection<T> getDeselectedRowsBatch();
        }

        /**
         * Selects one or more rows.
         * 
         * @param rows
         *            {@link Grid} row objects
         * @return true, if the set of selected rows was changed.
         */
        public boolean select(T... rows);

        /**
         * Deselects one or more rows.
         * 
         * @param rows
         *            Grid row objects
         * @return true, if the set of selected rows was changed.
         */
        public boolean deselect(T... rows);

        /**
         * De-selects all rows.
         * 
         * @return true, if any row was previously selected.
         */
        public boolean deselectAll();

        /**
         * Select all rows in a {@link Collection}.
         * 
         * @param rows
         *            a collection of Grid row objects
         * @return true, if the set of selected rows was changed.
         */
        public boolean select(Collection<T> rows);

        /**
         * Deselect all rows in a {@link Collection}.
         * 
         * @param rows
         *            a collection of Grid row objects
         * @return true, if the set of selected rows was changed.
         */
        public boolean deselect(Collection<T> rows);

    }

    /**
     * Interface for a selection model that does not allow anything to be
     * selected.
     * 
     * @param <T>
     *            type parameter corresponding with Grid row type
     */
    public interface None<T> extends SelectionModel<T> {

    }

}
