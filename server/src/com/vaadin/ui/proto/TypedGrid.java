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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.util.BeanUtil;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Extension;
import com.vaadin.server.communication.data.typed.CollectionDataSource;
import com.vaadin.server.communication.data.typed.DataProvider;
import com.vaadin.server.communication.data.typed.DataSource;
import com.vaadin.server.communication.data.typed.TypedDataGenerator;
import com.vaadin.shared.ui.proto.TypedGridClientRpc;
import com.vaadin.shared.ui.proto.TypedGridServerRpc;
import com.vaadin.shared.ui.proto.TypedGridState;
import com.vaadin.ui.AbstractComponent;

/**
 * A typed version of the Grid component. This TypedGrid is using the new typed
 * data system.
 * <p>
 * Note: This component does not yet provide all original Grid features.
 * 
 * @since
 * @param <T>
 *            type of data objects stored in this grid, e.g. Person bean
 */
public class TypedGrid<T> extends AbstractComponent {

    // TODO: Refactor this to use some common "Select" API and extract it from
    // TypedGrid
    public static class SelectEvent<T> extends EventObject {

        private final T selected;
        private final TypedGrid<T> component;

        SelectEvent(TypedGrid<T> source, T selected) {
            super(source);
            component = source;
            this.selected = selected;
        }

        @Override
        public TypedGrid<T> getSource() {
            return component;
        }

        public T getSelected() {
            return selected;
        }

    }

    // TODO: Extract from TypedGrid when extracting the SelectEvent
    public static interface SelectionListener<T> extends Serializable {
        void select(SelectEvent<T> select);
    }

    public abstract static class AbstractTypedGridExtension<T> extends
            AbstractExtension {
        public AbstractTypedGridExtension(TypedGrid<T> grid) {
            super(grid);
        }
    }

    private T selected;

    private final Map<String, Column<T, ?>> columns = new HashMap<String, Column<T, ?>>();
    private final Set<SelectionListener<T>> listeners = new HashSet<SelectionListener<T>>();
    private DataProvider<T> dp;

    /**
     * Constructs a new empty Grid.
     */
    public TypedGrid() {
        this(new ArrayList<T>());
    }

    /**
     * Constructs a new Grid using the data from given collection.
     * 
     * @param data
     *            collection of data
     */
    public TypedGrid(Collection<T> data) {
        this(new CollectionDataSource<T>(data));
    }

    /**
     * Constructs a new Grid using the given data source.
     * 
     * @param data
     *            data source to use
     */
    public TypedGrid(DataSource<T> data) {
        internalSetDataSource(data);
        init();
        setSizeFull();
    }

    /**
     * Constructs a new Grid using the given data source.
     * <p>
     * This constructor uses the given class parameter to autodetect the columns
     * needed to display the data.
     * 
     * @param cls
     *            data type class
     * @param data
     *            data source to use
     */
    public TypedGrid(Class<T> cls, DataSource<T> data) {
        this(data);
        autodetectColumns(cls);
    }

    /**
     * Constructs a new Grid using the data from given collection.
     * <p>
     * This constructor uses the given class parameter to autodetect the columns
     * needed to display the data.
     * 
     * @param cls
     *            data type class
     * @param data
     *            collection of data
     */
    public TypedGrid(Class<T> cls, Collection<T> data) {
        this(cls, new CollectionDataSource<T>(data));
    }

    public void setDataSource(DataSource<T> data) {
        internalSetDataSource(data);
    }

    public void setDataSource(Collection<T> data) {
        internalSetDataSource(new CollectionDataSource<T>(data));
    }

    protected void init() {
        registerRpc(new TypedGridServerRpc() {

            @Override
            public void setSelected(String rowKey) {
                if (rowKey != null && !rowKey.isEmpty()) {
                    selected = dp.getKeyMapper().get(rowKey);
                } else {
                    selected = null;
                }
                fireSelectionEvent(selected);
            }
        });
    }

    private void internalSetDataSource(DataSource<T> data) {
        if (dp != null) {
            dp.remove();
        }

        dp = DataProvider.create(data, this);
    }

    private void autodetectColumns(Class<T> cls) {
        List<PropertyDescriptor> props;
        try {
            props = BeanUtil.getBeanPropertyDescriptor(cls);
            for (final PropertyDescriptor p : props) {
                if (p.getName().equals("class")) {
                    continue;
                }

                addColumn(p.getName(), (Class<Object>) p.getPropertyType(),
                        new ValueProvider<T, Object>() {

                            @Override
                            public Object getValue(T data) {
                                try {
                                    return p.getReadMethod().invoke(data);
                                } catch (Exception e) {
                                    // TODO: Improve exception handling
                                    return null;
                                }
                            }
                        });
            }
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <V> Column<T, V> addColumn(String name, Class<V> valueType,
            ValueProvider<T, V> valueProvider) {
        Column<T, V> column = new Column<T, V>(this, name, valueType,
                valueProvider);
        columns.put(name, column);
        return column;
    }

    public void removeColumn(String colName) {
        if (columns.containsKey(colName)) {
            columns.remove(colName).remove();
        }
    }

    public void setColumnOrder(String... columns) {
        getRpcProxy(TypedGridClientRpc.class).setColumnOrder(columns);
    }

    @Override
    protected TypedGridState getState() {
        return (TypedGridState) super.getState();
    }

    @Override
    protected void addExtension(Extension extension) {
        super.addExtension(extension);

        if (extension instanceof TypedDataGenerator) {
            dp.addDataGenerator((TypedDataGenerator<T>) extension);
        }
    }

    @Override
    public void removeExtension(Extension extension) {
        super.removeExtension(extension);

        if (extension instanceof TypedDataGenerator) {
            dp.removeDataGenerator((TypedDataGenerator<T>) extension);
        }
    }

    public void addSelectionListener(SelectionListener<T> listener) {
        listeners.add(listener);
    }

    public void removeSelectionListener(SelectionListener<T> listener) {
        listeners.remove(listener);
    }

    protected void fireSelectionEvent(T selected) {
        List<SelectionListener<T>> set = new ArrayList<SelectionListener<T>>(
                listeners);
        for (SelectionListener<T> l : set) {
            l.select(new SelectEvent<T>(this, selected));
        }
    }

    public T getSelected() {
        return selected;
    }
}