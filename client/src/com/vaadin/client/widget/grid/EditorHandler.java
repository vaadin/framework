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
package com.vaadin.client.widget.grid;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.widgets.Grid;

/**
 * An interface for binding widgets and data to the grid row editor. Used by the
 * editor to support different row types, data sources and custom data binding
 * mechanisms.
 * 
 * @param <T>
 *            the row data type
 * 
 * @since
 * @author Vaadin Ltd
 */
public interface EditorHandler<T> {

    /**
     * A request class for handling asynchronous data binding. The request is
     * callback-based to facilitate usage with remote or otherwise asynchronous
     * data sources.
     * <p>
     * TODO Should have a mechanism for signaling a failed request to the caller
     */
    public static class EditorRequest<T> {

        /**
         * A callback interface used to notify the caller about completed
         * requests.
         */
        public interface RequestCallback<T> {
            public void onResponse(EditorRequest<T> request);
        }

        private Grid<T> grid;
        private int rowIndex;
        private RequestCallback<T> callback;

        /**
         * Creates a new editor request.
         * 
         * @param rowIndex
         *            the index of the edited row
         * @param callback
         *            the callback invoked when the request is ready, or null if
         *            no need to call back
         */
        public EditorRequest(Grid<T> grid, int rowIndex,
                RequestCallback<T> callback) {
            this.grid = grid;
            this.rowIndex = rowIndex;
            this.callback = callback;
        }

        /**
         * Returns the index of the row being requested.
         * 
         * @return the row index
         */
        public int getRowIndex() {
            return rowIndex;
        }

        /**
         * Returns the row data related to the row being requested.
         * 
         * @return the row data
         */
        public T getRow() {
            return grid.getDataSource().getRow(rowIndex);
        }

        /**
         * Returns the grid instance related to this editor request.
         * 
         * @return the grid instance
         */
        public Grid<T> getGrid() {
            return grid;
        }

        /**
         * Returns the editor widget used to edit the values of the given
         * column.
         * 
         * @param column
         *            the column whose widget to get
         * @return the widget related to the column
         */
        public Widget getWidget(Grid.Column<?, T> column) {
            Widget w = grid.getEditorWidget(column);
            assert w != null;
            return w;
        }

        /**
         * Invokes the stored callback if it is not null.
         */
        public void invokeCallback() {
            if (callback != null) {
                callback.onResponse(this);
            }
        }
    }

    /**
     * Binds row data to the editor widgets. Called by the editor when it is
     * opened for editing.
     * <p>
     * An implementation must call {@link EditorRequest#invokeCallback()
     * request.invokeCallback()} when the binding is complete (possibly
     * asynchronously).
     * 
     * @param request
     *            the data binding request
     * 
     * @see Grid#editRow(int)
     */
    public void bind(EditorRequest<T> request);

    /**
     * Cancels a currently active edit if any. Called by the grid editor when
     * editing is cancelled.
     * <p>
     * An implementation must call {@link EditorRequest#invokeCallback()
     * request.invokeCallback()} when the cancel is done (possibly
     * asynchronously).
     * 
     * @param request
     *            the cancel request
     * 
     * @see Grid#cancelEditor()
     */
    public void cancel(EditorRequest<T> request);

    /**
     * Saves changes in the currently active edit to the data source. Called by
     * the grid editor when changes are saved.
     * 
     * @param request
     *            the save request
     */
    public void save(EditorRequest<T> request);

    /**
     * Returns a widget instance that is used to edit the values in the given
     * column. A null return value means the column is not editable.
     * 
     * @param column
     *            the column whose values should be edited
     * @return the editor widget for the column or null if the column is not
     *         editable
     */
    public Widget getWidget(Grid.Column<?, T> column);
}
