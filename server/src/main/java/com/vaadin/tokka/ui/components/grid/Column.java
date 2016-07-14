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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.tokka.data.DataProviderConstants;
import com.vaadin.shared.tokka.ui.components.grid.ColumnState;
import com.vaadin.tokka.server.communication.data.SortOrder;
import com.vaadin.tokka.server.communication.data.TypedDataGenerator;

import elemental.json.Json;
import elemental.json.JsonObject;

public class Column<T, V> extends AbstractExtension implements
        TypedDataGenerator<T> {

    private Function<T, V> getter;
    private Function<SortDirection, Stream<SortOrder<String>>> sortOrderProvider;

    Column(String caption, Function<T, V> getter) {
        this.getter = getter;
        getState().caption = caption;
        getState().sortable = true;
    }

    @Override
    public void generateData(T data, JsonObject jsonObject) {
        assert getState(false).communicationId != null : "No communication ID set for column "
                + getState(false).caption;

        if (!jsonObject.hasKey(DataProviderConstants.DATA)) {
            jsonObject.put(DataProviderConstants.DATA, Json.createObject());
        }
        JsonObject obj = jsonObject.getObject(DataProviderConstants.DATA);
        // TODO: Renderers
        obj.put(getState(false).communicationId, getter.apply(data).toString());
    }

    @Override
    public void destroyData(T data) {
    }

    public ColumnState getState() {
        return getState(true);
    }

    public ColumnState getState(boolean markAsDirty) {
        return (ColumnState) super.getState(markAsDirty);
    }

    void extend(Grid<T> grid) {
        super.extend(grid);
    }

    void setCommunicationId(String key) {
        getState().communicationId = key;
    }

    /**
     * Sets whether the user can sort this Column or not.
     * 
     * @param sortable
     *            is column sortable
     * @return the column
     */
    public Column<T, V> setSortable(boolean sortable) {
        getState().sortable = sortable;
        return this;
    }

    /**
     * Returns the state of sorting for this Column.
     * 
     * @return {@code true} if column can be sorted by user; {@code false} if
     *         not
     */
    public boolean isSortable() {
        return getState(false).sortable;
    }

    public String getCaption() {
        return getState(false).caption;
    }

    Comparator<T> getComparator(SortDirection sortDirection) {
        Comparator<T> c = new Comparator<T>() {

            @SuppressWarnings("unchecked")
            @Override
            public int compare(T o1, T o2) {
                Comparable<V> comp = (Comparable<V>) getter.apply(o1);
                return comp.compareTo(getter.apply(o2));
            }
        };
        return sortDirection == SortDirection.ASCENDING ? c : c.reversed();
    }

    public Column<T, V> setSortProperty(String... properties) {
        sortOrderProvider = dir -> Arrays.asList(properties).stream()
                .map(s -> new SortOrder<>(s, dir));
        return this;
    }

    public Column<T, V> setSortBuilder(
            Function<SortDirection, Stream<SortOrder<String>>> provider) {
        sortOrderProvider = provider;
        return this;
    }

    public List<SortOrder<String>> getSortOrder(SortDirection direction) {
        return sortOrderProvider.apply(direction).collect(Collectors.toList());
    }
}
