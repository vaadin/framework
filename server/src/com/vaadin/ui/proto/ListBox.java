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
package com.vaadin.ui.proto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.server.communication.data.typed.CollectionDataSource;
import com.vaadin.server.communication.data.typed.DataProvider;
import com.vaadin.server.communication.data.typed.DataSource;
import com.vaadin.server.communication.data.typed.TypedDataGenerator;
import com.vaadin.shared.ui.proto.listbox.ListBoxConstants;
import com.vaadin.shared.ui.proto.listbox.ListBoxSelectRpc;
import com.vaadin.ui.AbstractComponent;

import elemental.json.JsonObject;

/**
 * ListBox is a simple select component typed to a data type.
 * 
 * @since
 */
public class ListBox<T> extends AbstractComponent {

    private static class DefaultNameProvider<T> implements NameProvider<T> {
        @Override
        public String getName(T value) {
            return value.toString();
        }
    }

    public interface NameProvider<T> extends Serializable {
        String getName(T value);
    }

    public interface ValueChange<T> extends Serializable {
        void valueChange(T value);
    }

    private T selected;
    private DataProvider<T> dataProvider;
    private Set<ValueChange<T>> listeners = new LinkedHashSet<ValueChange<T>>();

    public ListBox(Collection<T> data) {
        this(new CollectionDataSource<T>(data), new DefaultNameProvider<T>());
    }

    public ListBox(DataSource<T> data) {
        this(data, new DefaultNameProvider<T>());
    }

    public ListBox(Collection<T> data, NameProvider<T> nameProvider) {
        this(new CollectionDataSource<T>(data), nameProvider);
    }

    public ListBox(DataSource<T> data, NameProvider<T> nameProvider) {
        setDataSource(data, nameProvider);
        registerRpc(new ListBoxSelectRpc() {

            @Override
            public void select(String key) {
                if (key != null && !key.isEmpty()) {
                    // Key mapper gives the object represented by the given
                    // key communicated to the client-side
                    selected = dataProvider.getKeyMapper().get(key);
                    fireSelectionChange(selected);
                } else {
                    selected = null;
                }
            }
        });
    }

    public ListBox() {
        this(new CollectionDataSource<T>(new ArrayList<T>()),
                new DefaultNameProvider<T>());

    }

    public void setDataSource(Collection<T> data) {
        setDataSource(data, new DefaultNameProvider<T>());
    }

    public void setDataSource(DataSource<T> data) {
        setDataSource(data, new DefaultNameProvider<T>());
    }

    public void setDataSource(Collection<T> data, NameProvider<T> nameProvider) {
        setDataSource(new CollectionDataSource<T>(data), nameProvider);
    }

    public void setDataSource(DataSource<T> data,
            final NameProvider<T> nameProvider) {
        dataProvider = DataProvider.create(data, this);
        dataProvider.addDataGenerator(new TypedDataGenerator<T>() {

            @Override
            public void generateData(T bean, JsonObject rowData) {
                rowData.put(ListBoxConstants.NAME_KEY,
                        nameProvider.getName(bean));
            }

            @Override
            public void destroyData(T bean) {
                // No data needs to be destroyed.
            }
        });
    }

    public void addValueChangeListener(ValueChange<T> listener) {
        listeners.add(listener);
    }

    public void removeValueChangeListener(ValueChange<T> listener) {
        listeners.remove(listener);
    }

    public T getSelected() {
        return selected;
    }

    protected void fireSelectionChange(T selected) {
        List<ValueChange<T>> set = new ArrayList<ValueChange<T>>(listeners);
        for (ValueChange<T> listener : set) {
            listener.valueChange(selected);
        }
    }
}
