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
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface EditorHandler<T> {

    /**
     * A request class passed as a parameter to the editor handler methods. The
     * request is callback-based to facilitate usage with remote or otherwise
     * asynchronous data sources.
     * <p>
     * In any of the EditorHandler methods, an implementation may call
     * {@link EditorRequest#startAsync()} to signal the caller that the request
     * is handled asynchronously. In that case, {@link EditorRequest#complete()}
     * must be called when the request is complete.
     * <p>
     * TODO Should have a mechanism for signaling a failed request to the caller
     * 
     * @param <T>
     *            the row data type
     */
    public static class EditorRequest<T> {

        /**
         * A callback interface used to notify the invoker of the editor handler
         * of completed editor requests.
         * 
         * @param <T>
         *            the row data type
         */
        public interface RequestCallback<T> {
            public void onResponse(EditorRequest<T> request);
        }

        private Grid<T> grid;
        private int rowIndex;
        private RequestCallback<T> callback;
        private boolean async = false;
        private boolean completed = false;

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

        public boolean isAsync() {
            return async;
        }

        /**
         * Marks this request as asynchronous. If this method is invoked, the
         * caller must also ensure that {@link #complete()} is invoked once the
         * request is finished.
         */
        public void startAsync() {
            async = true;
        }

        /**
         * Completes this request. The request can only be completed once. This
         * method should only be called by an EditorHandler implementer if the
         * request handling is asynchronous in nature and {@link #startAsync()}
         * is previously invoked for this request. Synchronous requests are
         * completed automatically by the editor.
         * 
         * @throws IllegalStateException
         *             if the request is already completed
         */
        public void complete() {
            if (completed) {
                throw new IllegalStateException(
                        "An EditorRequest must be completed exactly once");
            }
            completed = true;
            if (callback != null) {
                callback.onResponse(this);
            }
        }
    }

    /**
     * Binds row data to the editor widgets. Called by the editor when it is
     * opened for editing.
     * <p>
     * An implementation may call {@link EditorRequest#startAsync()
     * request.startAsync()} to signal the caller that the request is handled
     * asynchronously. In that case, {@link EditorRequest#complete()} must be
     * called once the binding is complete.
     * 
     * @param request
     *            the data binding request
     * 
     * @see Grid#editRow(int)
     */
    public void bind(EditorRequest<T> request);

    /**
     * Called by the editor when editing is cancelled. This method may have an
     * empty implementation in case no special processing is required.
     * <p>
     * An implementation may call {@link EditorRequest#startAsync()
     * request.startAsync()} to signal the caller that the request is handled
     * asynchronously. In that case, {@link EditorRequest#complete()} must be
     * called once the cancel operation is complete.
     * 
     * @param request
     *            the cancel request
     * 
     * @see Grid#cancelEditor()
     */
    public void cancel(EditorRequest<T> request);

    /**
     * Commits changes in the currently active edit to the data source. Called
     * by the editor when changes are saved.
     * <p>
     * An implementation may call {@link EditorRequest#startAsync()
     * request.startAsync()} to signal the caller that the request is handled
     * asynchronously. In that case, {@link EditorRequest#complete()} must be
     * called once the commit operation is complete.
     * 
     * @param request
     *            the save request
     * 
     * @see Grid#saveEditor()
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
