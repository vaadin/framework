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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.tokka.ui.components.grid.GridServerRpc;
import com.vaadin.tokka.data.DataSource;
import com.vaadin.tokka.data.SortOrder;
import com.vaadin.tokka.data.selection.SingleSelection;
import com.vaadin.tokka.server.communication.data.KeyMapper;
import com.vaadin.tokka.ui.components.AbstractListing;

public class Grid<T> extends AbstractListing<T> {

    private KeyMapper<Column<T, ?>> columnKeys = new KeyMapper<>();
    private Set<Column<T, ?>> columnSet = new HashSet<>();
    private List<SortOrder<Column<T, ?>>> sortOrder = new ArrayList<>();

    public Grid() {
        setSelectionModel(new SingleSelection<T>());
        registerRpc(new GridServerRpc() {

            @Override
            public void setSortOrder(List<String> columnIds,
                    List<SortDirection> directions) {
                assert columnIds.size() == directions.size() : "Column and sort direction counts don't match.";
                sortOrder.clear();
                if (columnIds.size() == 0) {
                    // Somehow there's suddenly no sort order anymore..
                    getDataCommunicator().setBackEndSorting(
                            Collections.emptyList());
                    getDataCommunicator().setInMemorySorting(null);
                    return;
                }

                for (int i = 0; i < columnIds.size(); ++i) {
                    Column<T, ?> column = columnKeys.get(columnIds.get(i));
                    sortOrder.add(new SortOrder<>(column, directions.get(i)));
                }

                updateSortOrder();
            }
        });
    }

    public <V> Column<T, V> addColumn(String caption, Function<T, V> getter) {
        Column<T, V> c = new Column<T, V>(caption, getter);

        c.extend(this);
        c.setCommunicationId(columnKeys.key(c));
        columnSet.add(c);
        addDataGenerator(c);

        return c;
    }

    public void removeColumn(Column<T, ?> column) {
        if (columnSet.remove(column)) {
            columnKeys.remove(column);
            removeDataGenerator(column);
            column.remove();
        }
    }

    @Override
    public void setDataSource(DataSource<T, ?> data) {
        super.setDataSource(data);
        updateSortOrder();
    }

    protected void updateSortOrder() {
        if (getDataCommunicator().getDataSource() == null) {
            return;
        }

        if (getDataCommunicator().getDataSource().isInMemory()) {
            Comparator<T> comparator = sortOrder
                    .stream()
                    .map(order -> order.getSorted().getComparator(
                            order.getDirection()))
                    .reduce((x, y) -> 0, Comparator::thenComparing);
            getDataCommunicator().setInMemorySorting(comparator);
        } else {
            List<SortOrder<String>> sortProperties = new ArrayList<>();
            sortOrder
                    .stream()
                    .map(order -> order.getSorted().getSortOrder(
                            order.getDirection()))
                    .forEach(sortProperties::addAll);
            getDataCommunicator().setBackEndSorting(sortProperties);
        }
    }
}
