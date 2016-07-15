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

/**
 * This extension manages the configuration and data communication for a Column
 * inside of a Grid component.
 *
 * @param <T>
 *            grid bean type
 * @param <V>
 *            column value type
 */
public class Column<T, V> extends AbstractExtension implements
        TypedDataGenerator<T> {

    private Function<T, V> getter;
    private Function<SortDirection, Stream<SortOrder<String>>> sortOrderProvider;
    private Comparator<T> comparator;

    /**
     * Constructs a new Column configuration with given header caption and value
     * provider.
     * 
     * @param caption
     *            header caption
     * @param getter
     *            function to get values from data objects
     */
    Column(String caption, Function<T, V> getter) {
        this.getter = getter;
        getState().caption = caption;
        getState().sortable = true;

        comparator = (a, b) -> {
            // FIXME: We should check if the value is acutally comparable.
            Comparable<V> comp = (Comparable<V>) getter.apply(a);
            return comp.compareTo(getter.apply(b));
        };
    }

    @Override
    public void generateData(T data, JsonObject jsonObject) {
        assert getState(false).communicationId != null : "No communication ID set for column "
                + getState(false).caption;

        if (!jsonObject.hasKey(DataProviderConstants.DATA)) {
            jsonObject.put(DataProviderConstants.DATA, Json.createObject());
        }
        JsonObject obj = jsonObject.getObject(DataProviderConstants.DATA);
        // FIXME: Renderers
        obj.put(getState(false).communicationId, getter.apply(data).toString());
    }

    @Override
    public void destroyData(T data) {
    }

    @Override
    protected ColumnState getState() {
        return getState(true);
    }

    @Override
    protected ColumnState getState(boolean markAsDirty) {
        return (ColumnState) super.getState(markAsDirty);
    }

    /**
     * This method extends the given Grid with this Column.
     * 
     * @param grid
     *            grid to extend
     */
    void extend(Grid<T> grid) {
        super.extend(grid);
    }

    /**
     * Sets the identifier to use with this Column in communication.
     * 
     * @param key
     *            identifier string
     */
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

    /**
     * Gets the header caption for this column.
     * 
     * @return caption
     */
    public String getCaption() {
        return getState(false).caption;
    }

    /**
     * Sets a comparator to use with in-memory sorting with this column.
     * 
     * @param comparator
     *            comparator to sort data by this column
     * @return column
     */
    public Column<T, V> setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
        return this;
    }

    /**
     * Gets a comparator that defines how this column is sorted depending on the
     * sort direction.
     * 
     * @param sortDirection
     *            direction this column is sorted to
     * @return comparator
     */
    public Comparator<T> getComparator(SortDirection sortDirection) {
        boolean reverse = sortDirection != SortDirection.ASCENDING;
        return reverse ? comparator.reversed() : comparator;
    }

    /**
     * Sets strings describing back end properties to be used when sorting this
     * column. This method is a short hand for {@link #setSortBuilder(Function)}
     * that takes an array of strings and uses the same sorting direction for
     * all of them.
     * 
     * @param properties
     *            array of strings describing backend properties
     * @return column
     */
    public Column<T, V> setSortProperty(String... properties) {
        sortOrderProvider = dir -> Arrays.asList(properties).stream()
                .map(s -> new SortOrder<>(s, dir));
        return this;
    }

    /**
     * Sets the sort orders when sorting this column. Sort order builder is a
     * function that provider {@link SortOrder} objects to describe how to sort
     * by this column.
     * 
     * @param provider
     *            function to generate sort orders with given direction
     * @return column
     */
    public Column<T, V> setSortBuilder(
            Function<SortDirection, Stream<SortOrder<String>>> provider) {
        sortOrderProvider = provider;
        return this;
    }

    /**
     * Gets the sort orders for this column when sorting to given direction.
     * 
     * @param direction
     *            sorting direction
     * @return list of sort orders
     */
    public List<SortOrder<String>> getSortOrder(SortDirection direction) {
        return sortOrderProvider.apply(direction).collect(Collectors.toList());
    }
}
