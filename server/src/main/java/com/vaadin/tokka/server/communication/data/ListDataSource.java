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
package com.vaadin.tokka.server.communication.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * {@link DataSource} wrapper for {@link Collection}s.
 *
 * @param <T>
 *            data type
 */
public class ListDataSource<T> extends AbstractDataSource<T> {

    private List<T> backend;

    /**
     * Constructs a new ListDataSource. This method makes a protective copy of
     * the contents of the Collection.
     * 
     * @param collection
     *            initial data
     */
    public ListDataSource(Collection<T> collection) {
        backend = new ArrayList<T>(collection);
    }

    @Override
    public void save(T data) {
        if (!backend.contains(data)) {
            backend.add(data);
            fireDataAppend(data);
        } else {
            fireDataUpdate(data);
        }
    }

    @Override
    public void remove(T data) {
        if (backend.contains(data)) {
            backend.remove(data);
            fireDataRemove(data);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(backend).iterator();
    }
}
