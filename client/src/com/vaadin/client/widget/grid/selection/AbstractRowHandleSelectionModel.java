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

import com.vaadin.client.data.DataSource.RowHandle;

/**
 * An abstract class that adds a consistent API for common methods that's needed
 * by Vaadin's server-based selection models to work.
 * <p>
 * <em>Note:</em> This should be an interface instead of an abstract class, if
 * only we could define protected methods in an interface.
 * 
 * @author Vaadin Ltd
 * @param <T>
 *            The grid's row type
 * @since 7.4
 */
public abstract class AbstractRowHandleSelectionModel<T> implements
        SelectionModel<T> {
    /**
     * Select a row, based on its
     * {@link com.vaadin.client.data.DataSource.RowHandle RowHandle}.
     * <p>
     * <em>Note:</em> this method may not fire selection change events.
     * 
     * @param handle
     *            the handle to select by
     * @return <code>true</code> iff the selection state was changed by this
     *         call
     * @throws UnsupportedOperationException
     *             if the selection model does not support either handles or
     *             selection
     */
    protected abstract boolean selectByHandle(RowHandle<T> handle);

    /**
     * Deselect a row, based on its
     * {@link com.vaadin.client.data.DataSource.RowHandle RowHandle}.
     * <p>
     * <em>Note:</em> this method may not fire selection change events.
     * 
     * @param handle
     *            the handle to deselect by
     * @return <code>true</code> iff the selection state was changed by this
     *         call
     * @throws UnsupportedOperationException
     *             if the selection model does not support either handles or
     *             deselection
     */
    protected abstract boolean deselectByHandle(RowHandle<T> handle)
            throws UnsupportedOperationException;
}
