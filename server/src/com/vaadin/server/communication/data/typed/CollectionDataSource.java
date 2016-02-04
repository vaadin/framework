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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * DataSource that stores its data in a List. This DataSource does not support
 * paging.
 * 
 * @since
 */
public class CollectionDataSource<T> extends AbstractDataSource<T> {

    protected List<T> backend;

    public CollectionDataSource() {
        backend = new ArrayList<T>();
    }

    public CollectionDataSource(Collection<T> data) {
        this();
        backend.addAll(data);
    }

    @Override
    public void save(T data) {
        if (backend.contains(data)) {
            fireDataUpdate(data);
        } else {
            backend.add(data);
            fireDataAppend(data);
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
        return backend.iterator();
    }
}
