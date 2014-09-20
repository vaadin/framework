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
package com.vaadin.client.ui.grid;

import com.google.gwt.user.client.ui.Widget;

/**
 * An interface for binding widgets and data to the editor row. Used by the
 * editor row to support different row types, data sources and custom data
 * binding mechanisms.
 * 
 * @param <T>
 *            the row data type
 * 
 * @since
 * @author Vaadin Ltd
 */
public interface EditorRowHandler<T> {

    /**
     * A request class for handling asynchronous data binding. The request is
     * callback-based to facilitate usage with remote or otherwise asynchronous
     * data sources.
     * <p>
     * TODO Should have a mechanism for signaling a failed request to the caller
     */
    public static class EditorRowRequest {

        /**
         * A callback interface used to notify the caller about completed
         * requests.
         */
        public interface RequestCallback {
            public void onResponse(EditorRowRequest request);
        }

        private int rowIndex;
        private RequestCallback callback;

        /**
         * Creates a new editor row request.
         * 
         * @param rowIndex
         *            the index of the edited row
         * @param callback
         *            the callback invoked when the request is ready, or null if
         *            no need to call back
         */
        public EditorRowRequest(int rowIndex, RequestCallback callback) {
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
         * Invokes the stored callback if it is not null.
         */
        public void invokeCallback() {
            if (callback != null) {
                callback.onResponse(this);
            }
        }
    }

    /**
     * Binds row data to the editor row widgets. Called by the editor row when
     * it is opened for editing.
     * <p>
     * An implementation must call {@link EditorRowRequest#invokeCallback()
     * request.invokeCallback()} when the binding is complete (possibly
     * asynchronously).
     * 
     * @param request
     *            the data binding request
     * 
     * @see EditorRow#editRow(int)
     */
    public void bind(EditorRowRequest request);

    /**
     * Cancels a currently active edit if any. Called by the editor row when
     * editing is cancelled.
     * <p>
     * An implementation must call {@link EditorRowRequest#invokeCallback()
     * request.invokeCallback()} when the cancel is done (possibly
     * asynchronously).
     *
     * @param request
     *            the cancel request
     * 
     * @see EditorRow#cancel()
     */
    public void cancel(EditorRowRequest request);

    /**
     * Commits changes in the currently active edit to the data source. Called
     * by the editor row when changes are saved.
     * 
     * @param request
     *            the commit request
     */
    public void commit(EditorRowRequest request);

    /**
     * Discards any unsaved changes and reloads editor content from the data
     * source.
     * <p>
     * Implementation note: This method may simply call
     * {@link #bind(EditorRowRequest) bind} if no other processing needs to be
     * done.
     * 
     * @param request
     *            the discard request
     */
    public void discard(EditorRowRequest request);

    /**
     * Returns a widget instance that is used to edit the values in the given
     * column. A null return value means the column is not editable.
     *
     * @param column
     *            the column whose values should be edited
     * @return the editor widget for the column or null if the column is not
     *         editable
     */
    public Widget getWidget(GridColumn<?, T> column);
}
