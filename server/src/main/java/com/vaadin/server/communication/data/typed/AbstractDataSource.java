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
package com.vaadin.server.communication.data.typed;

import java.util.LinkedHashSet;
import java.util.Set;

import com.vaadin.event.handler.Registration;

/**
 * Base class for AbstractDataSource. Provides tracking for
 * {@link DataChangeHandler}s and helper methods to call them.
 * 
 * @since
 */
public abstract class AbstractDataSource<T> implements DataSource<T> {

    protected final Set<DataChangeHandler<T>> handlers = new LinkedHashSet<DataChangeHandler<T>>();

    @Override
    public Registration addDataChangeHandler(DataChangeHandler<T> handler) {
        if (handler != null) {
            handlers.add(handler);
            return () -> handlers.remove(handler);
        }
        return () -> { /* NO-OP */ };
    }

    /**
     * Informs all handlers of a generic change in the data. This usually
     * triggers a full cache invalidation and refresh of all data, which is
     * usually an expensive operation. Should be called when none of the other
     * methods help.
     * 
     * @see #fireDataAppend(Object)
     * @see #fireDataRemove(Object)
     * @see #fireDataUpdate(Object)
     */
    protected void fireDataChange() {
        for (DataChangeHandler<T> handler : handlers) {
            handler.onDataChange();
        }
    }

    /**
     * Informs all handlers of an added data object in the back end. This method
     * should only be called when the newly added data object is the last object
     * in the back end. Other additions can be handled with
     * {@link #fireDataChange()}.
     * 
     * @param data
     *            added data object
     */
    protected void fireDataAppend(T data) {
        for (DataChangeHandler<T> handler : handlers) {
            handler.onDataAppend(data);
        }
    }

    /**
     * Informs all handlers of a data object that was removed from the back end.
     * 
     * @param data
     *            removed data object
     */
    protected void fireDataRemove(T data) {
        for (DataChangeHandler<T> handler : handlers) {
            handler.onDataRemove(data);
        }
    }

    /**
     * Informs all handlers of an existing data object that was updated in the
     * back end.
     * 
     * @param data
     *            updated data object
     */
    protected void fireDataUpdate(T data) {
        for (DataChangeHandler<T> handler : handlers) {
            handler.onDataUpdate(data);
        }
    }
}
