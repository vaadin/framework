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
package com.vaadin.client.ui.grid.selection;

import java.util.Collection;

import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.Renderer;

/**
 * Common interface for all selection models.
 * <p>
 * Selection models perform tracking of selected rows in the Grid, as well as
 * dispatching events when the selection state changes.
 * 
 * @author Vaadin Ltd
 * @since 7.4
 * @param <T>
 *            Grid's row type
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
     *            a {@link Grid} instance
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

    }

    /**
     * Selection model that allows for several rows to be selected at once.
     * 
     * @param <T>
     *            type parameter corresponding with Grid row type
     */
    public interface Multi<T> extends SelectionModel<T> {

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
