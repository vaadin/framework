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

/**
 * Base class for AbstractDataSource. Provides tracking for
 * {@link DataChangeHandler}s and helper methods to call them.
 * 
 * @since
 */
public abstract class AbstractDataSource<T> implements DataSource<T> {

    protected Set<DataChangeHandler<T>> handlers = new LinkedHashSet<DataChangeHandler<T>>();

    @Override
    public void addDataChangeHandler(DataChangeHandler<T> handler) {
        handlers.add(handler);
    }

    @Override
    public void removeDataChangeHandler(DataChangeHandler<T> handler) {
        handlers.remove(handler);
    }

    protected void fireDataChange() {
        for (DataChangeHandler<T> handler : handlers) {
            handler.onDataChange();
        }
    }

    protected void fireDataAdd(T data) {
        for (DataChangeHandler<T> handler : handlers) {
            handler.onDataAdd(data);
        }
    }

    protected void fireDataRemove(T data) {
        for (DataChangeHandler<T> handler : handlers) {
            handler.onDataRemove(data);
        }
    }

    protected void fireDataUpdate(T data) {
        for (DataChangeHandler<T> handler : handlers) {
            handler.onDataUpdate(data);
        }
    }
}
