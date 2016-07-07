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
package com.vaadin.tokka.ui.components.grid;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.vaadin.tokka.server.communication.data.DataProvider;
import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.server.communication.data.SingleSelection;
import com.vaadin.tokka.ui.components.AbstractListing;

public class Grid<T> extends AbstractListing<T> {

    private DataSource<T> dataSource;
    private Map<String, Column<T, ?>> columns = new LinkedHashMap<>();

    public Grid() {
        setSelectionModel(new SingleSelection<T>());
    }

    @Override
    public void setDataSource(DataSource<T> data) {
        this.dataSource = data;
        setDataProvider(new DataProvider<>(data));
    }

    @Override
    public DataSource<T> getDataSource() {
        return dataSource;
    }

    public <V> Column<T, V> addColumn(String caption, Function<T, V> getter) {
        Column<T, V> c = new Column<T, V>(caption, getter, this);
        columns.put(c.getConnectorId(), c);
        addDataGenerator(c);
        return c;
    }

    public void removeColumn(Column<T, ?> column) {
        if (columns.containsValue(column)) {
            removeDataGenerator(columns.remove(column.getConnectorId()));
            column.remove();
        }
    }
}
