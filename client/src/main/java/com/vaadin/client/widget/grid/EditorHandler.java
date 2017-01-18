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

import java.util.Collection;

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
     * An implementation must call either {@link #success()} or {@link #fail()},
     * according to whether the operation was a success or failed during
     * execution, respectively.
     *
     * @param <T>
     *            the row data type
     */
    public interface EditorRequest<T> {
        /**
         * Returns the index of the row being requested.
         *
         * @return the row index
         */
        public int getRowIndex();

        /**
         * Returns the DOM index of the column being focused.
         *
         * @return the column index (excluding hidden columns)
         */
        public int getColumnIndex();

        /**
         * Returns the row data related to the row being requested.
         *
         * @return the row data
         */
        public T getRow();

        /**
         * Returns the grid instance related to this editor request.
         *
         * @return the grid instance
         */
        public Grid<T> getGrid();

        /**
         * Returns the editor widget used to edit the values of the given
         * column.
         *
         * @param column
         *            the column whose widget to get
         * @return the widget related to the column
         */
        public Widget getWidget(Grid.Column<?, T> column);

        /**
         * Informs Grid that the editor request was a success.
         */
        public void success();

        /**
         * Informs Grid that an error occurred while trying to process the
         * request.
         *
         * @param errorMessage
         *            and error message to show to the user, or
         *            <code>null</code> to not show any message.
         * @param errorColumns
         *            a collection of columns for which an error indicator
         *            should be shown, or <code>null</code> if no columns should
         *            be marked as erroneous.
         */
        public void failure(String errorMessage,
                Collection<Grid.Column<?, T>> errorColumns);

        /**
         * Checks whether the request is completed or not.
         *
         * @return <code>true</code> iff the request is completed
         */
        public boolean isCompleted();
    }

    /**
     * Binds row data to the editor widgets. Called by the editor when it is
     * opened for editing.
     * <p>
     * The implementation <em>must</em> call either
     * {@link EditorRequest#success()} or
     * {@link EditorRequest#failure(String, Collection)} to signal a successful
     * or a failed (respectively) bind action.
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
     * In contrast to {@link #bind(EditorRequest)} and
     * {@link #save(EditorRequest)}, any calls to
     * {@link EditorRequest#success()} or
     * {@link EditorRequest#failure(String, Collection)} have no effect on the
     * outcome of the cancel action. The editor is already closed when this
     * method is called.
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
     * The implementation <em>must</em> call either
     * {@link EditorRequest#success()} or {@link EditorRequest#fail()} to signal
     * a successful or a failed (respectively) save action.
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
