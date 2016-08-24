/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.vaadin.data.selection.SingleSelection;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.data.DataSource;
import com.vaadin.server.data.SortOrder;
import com.vaadin.server.data.DataGenerator;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.ColumnState;
import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;

import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * A grid component for displaying tabular data.
 *
 * @author Vaadin Ltd
 * @since
 *
 * @param <T>
 *            the grid bean type
 */
public class Grid<T> extends AbstractListing<T, SelectionModel<T>>
        implements HasComponents {

    /**
     * A callback interface for generating details for a particular row in Grid.
     *
     * @param <T>
     *            the grid bean type
     */
    @FunctionalInterface
    public interface DetailsGenerator<T>
            extends Function<T, Component>, Serializable {
    }

    /**
     * A helper base class for creating extensions for the Grid component.
     *
     * @param <T>
     */
    public static abstract class AbstractGridExtension<T>
            extends AbstractListingExtension<T> {

        @Override
        public void extend(AbstractListing<T, ?> grid) {
            if (!(grid instanceof Grid)) {
                throw new IllegalArgumentException(
                        getClass().getSimpleName() + " can only extend Grid");
            }
            super.extend(grid);
        }

        /**
         * Adds given component to the connector hierarchy of Grid.
         *
         * @param c
         *            the component to add
         */
        protected void addComponentToGrid(Component c) {
            getParent().addExtensionComponent(c);
        }

        /**
         * Removes given component from the connector hierarchy of Grid.
         *
         * @param c
         *            the component to remove
         */
        protected void removeComponentFromGrid(Component c) {
            getParent().removeExtensionComponent(c);
        }

        @Override
        public Grid<T> getParent() {
            return (Grid<T>) super.getParent();
        }
    }

    private final class GridServerRpcImpl implements GridServerRpc {
        @Override
        public void sort(String[] columnIds, SortDirection[] directions,
                boolean isUserOriginated) {
            assert columnIds.length == directions.length : "Column and sort direction counts don't match.";
            sortOrder.clear();
            if (columnIds.length == 0) {
                // Grid is not sorted anymore.
                getDataCommunicator()
                        .setBackEndSorting(Collections.emptyList());
                getDataCommunicator().setInMemorySorting(null);
                return;
            }

            for (int i = 0; i < columnIds.length; ++i) {
                Column<T, ?> column = columnKeys.get(columnIds[i]);
                sortOrder.add(new SortOrder<>(column, directions[i]));
            }

            // Set sort orders
            // In-memory comparator
            Comparator<T> comparator = sortOrder.stream()
                    .map(order -> order.getSorted()
                            .getComparator(order.getDirection()))
                    .reduce((x, y) -> 0, Comparator::thenComparing);
            getDataCommunicator().setInMemorySorting(comparator);

            // Back-end sort properties
            List<SortOrder<String>> sortProperties = new ArrayList<>();
            sortOrder.stream()
                    .map(order -> order.getSorted()
                            .getSortOrder(order.getDirection()))
                    .forEach(s -> s.forEach(sortProperties::add));
            getDataCommunicator().setBackEndSorting(sortProperties);
        }

        @Override
        public void itemClick(String rowKey, String columnId,
                MouseEventDetails details) {
            // TODO Auto-generated method stub
        }

        @Override
        public void contextClick(int rowIndex, String rowKey, String columnId,
                Section section, MouseEventDetails details) {
            // TODO Auto-generated method stub
        }

        @Override
        public void columnsReordered(List<String> newColumnOrder,
                List<String> oldColumnOrder) {
            // TODO Auto-generated method stub
        }

        @Override
        public void columnVisibilityChanged(String id, boolean hidden,
                boolean userOriginated) {
            // TODO Auto-generated method stub
        }

        @Override
        public void columnResized(String id, double pixels) {
            // TODO Auto-generated method stub
        }
    }

    /**
     * Class for managing visible details rows.
     *
     * @param <T>
     *            the grid bean type
     */
    public static class DetailsManager<T> extends AbstractGridExtension<T> {

        private Set<T> visibleDetails = new HashSet<>();
        private Map<T, Component> components = new HashMap<>();
        private DetailsGenerator<T> generator;

        /**
         * Sets the details component generator.
         *
         * @param generator
         *            the generator for details components
         */
        public void setDetailsGenerator(DetailsGenerator<T> generator) {
            if (this.generator != generator) {
                removeAllComponents();
            }
            this.generator = generator;
            visibleDetails.forEach(this::refresh);
        }

        @Override
        public void remove() {
            removeAllComponents();

            super.remove();
        }

        private void removeAllComponents() {
            // Clean up old components
            components.values().forEach(this::removeComponentFromGrid);
            components.clear();
        }

        @Override
        public void generateData(T data, JsonObject jsonObject) {
            if (generator == null || !visibleDetails.contains(data)) {
                return;
            }

            if (!components.containsKey(data)) {
                Component detailsComponent = generator.apply(data);
                Objects.requireNonNull(detailsComponent,
                        "Details generator can't create null components");
                if (detailsComponent.getParent() != null) {
                    throw new IllegalStateException(
                            "Details component was already attached");
                }
                addComponentToGrid(detailsComponent);
                components.put(data, detailsComponent);
            }

            jsonObject.put(GridState.JSONKEY_DETAILS_VISIBLE,
                    components.get(data).getConnectorId());
        }

        @Override
        public void destroyData(T data) {
            // No clean up needed. Components are removed when hiding details
            // and/or changing details generator
        }

        /**
         * Sets the visibility of details component for given item.
         *
         * @param data
         *            the item to show details for
         * @param visible
         *            {@code true} if details component should be visible;
         *            {@code false} if it should be hidden
         */
        public void setDetailsVisible(T data, boolean visible) {
            boolean refresh = false;
            if (!visible) {
                refresh = visibleDetails.remove(data);
                if (components.containsKey(data)) {
                    removeComponentFromGrid(components.remove(data));
                }
            } else {
                refresh = visibleDetails.add(data);
            }

            if (refresh) {
                refresh(data);
            }
        }

        /**
         * Returns the visibility of details component for given item.
         *
         * @param data
         *            the item to show details for
         *
         * @return {@code true} if details component should be visible;
         *         {@code false} if it should be hidden
         */
        public boolean isDetailsVisible(T data) {
            return visibleDetails.contains(data);
        }

        @Override
        public Grid<T> getParent() {
            return super.getParent();
        }
    }

    /**
     * This extension manages the configuration and data communication for a
     * Column inside of a Grid component.
     *
     * @param <T>
     *            the grid bean type
     * @param <V>
     *            the column value type
     */
    public static class Column<T, V> extends AbstractExtension
            implements DataGenerator<T> {

        private Function<T, V> valueProvider;
        private Function<SortDirection, Stream<SortOrder<String>>> sortOrderProvider;
        private Comparator<T> comparator;

        /**
         * Constructs a new Column configuration with given header caption and
         * value provider.
         *
         * @param caption
         *            the header caption
         * @param valueType
         *            the type of value
         * @param valueProvider
         *            the function to get values from items
         */
        protected Column(String caption, Class<V> valueType,
                Function<T, V> valueProvider) {
            Objects.requireNonNull(caption, "Header caption can't be null");
            Objects.requireNonNull(valueProvider,
                    "Value provider can't be null");
            Objects.requireNonNull(valueType, "Value type can't be null");

            this.valueProvider = valueProvider;
            getState().caption = caption;
            sortOrderProvider = d -> Stream.of();

            if (Comparable.class.isAssignableFrom(valueType)) {
                comparator = (a, b) -> {
                    @SuppressWarnings("unchecked")
                    Comparable<V> comp = (Comparable<V>) valueProvider.apply(a);
                    return comp.compareTo(valueProvider.apply(b));
                };
                getState().sortable = true;
            } else {
                getState().sortable = false;
            }
        }

        @Override
        public void generateData(T data, JsonObject jsonObject) {
            assert getState(
                    false).id != null : "No communication ID set for column "
                            + getState(false).caption;

            if (!jsonObject.hasKey(DataCommunicatorConstants.DATA)) {
                jsonObject.put(DataCommunicatorConstants.DATA,
                        Json.createObject());
            }
            JsonObject obj = jsonObject
                    .getObject(DataCommunicatorConstants.DATA);
            // Since we dont' have renderers yet, use a dummy toString for
            // data.
            obj.put(getState(false).id, valueProvider.apply(data).toString());
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
         *            the grid to extend
         */
        private void extend(Grid<T> grid) {
            super.extend(grid);
        }

        /**
         * Sets the identifier to use with this Column in communication.
         *
         * @param id
         *            the identifier string
         */
        private void setId(String id) {
            Objects.requireNonNull(id, "Communication ID can't be null");
            getState().id = id;
        }

        /**
         * Sets whether the user can sort this column or not.
         *
         * @param sortable
         *            {@code true} if the column can be sorted by the user;
         *            {@code false} if not
         * @return this column
         */
        public Column<T, V> setSortable(boolean sortable) {
            getState().sortable = sortable;
            return this;
        }

        /**
         * Gets whether the user can sort this column or not.
         *
         * @return {@code true} if the column can be sorted by the user;
         *         {@code false} if not
         */
        public boolean isSortable() {
            return getState(false).sortable;
        }

        /**
         * Sets the header caption for this column.
         *
         * @param caption
         *            the header caption
         *
         * @return this column
         */
        public Column<T, V> setCaption(String caption) {
            Objects.requireNonNull(caption, "Header caption can't be null");
            getState().caption = caption;
            return this;
        }

        /**
         * Gets the header caption for this column.
         *
         * @return header caption
         */
        public String getCaption() {
            return getState(false).caption;
        }

        /**
         * Sets a comparator to use with in-memory sorting with this column.
         * Sorting with a back-end is done using
         * {@link Column#setSortProperty(String...)}.
         *
         * @param comparator
         *            the comparator to use when sorting data in this column
         * @return this column
         */
        public Column<T, V> setComparator(Comparator<T> comparator) {
            Objects.requireNonNull(comparator, "Comparator can't be null");
            this.comparator = comparator;
            return this;
        }

        /**
         * Gets the comparator to use with in-memory sorting for this column
         * when sorting in the given direction.
         *
         * @param sortDirection
         *            the direction this column is sorted by
         * @return comparator for this column
         */
        public Comparator<T> getComparator(SortDirection sortDirection) {
            Objects.requireNonNull(comparator,
                    "No comparator defined for sorted column.");
            boolean reverse = sortDirection != SortDirection.ASCENDING;
            return reverse ? comparator.reversed() : comparator;
        }

        /**
         * Sets strings describing back end properties to be used when sorting
         * this column. This method is a short hand for
         * {@link #setSortBuilder(Function)} that takes an array of strings and
         * uses the same sorting direction for all of them.
         *
         * @param properties
         *            the array of strings describing backend properties
         * @return this column
         */
        public Column<T, V> setSortProperty(String... properties) {
            Objects.requireNonNull(properties, "Sort properties can't be null");
            sortOrderProvider = dir -> Arrays.stream(properties)
                    .map(s -> new SortOrder<>(s, dir));
            return this;
        }

        /**
         * Sets the sort orders when sorting this column. The sort order
         * provider is a function which provides {@link SortOrder} objects to
         * describe how to sort by this column.
         *
         * @param provider
         *            the function to use when generating sort orders with the
         *            given direction
         * @return this column
         */
        public Column<T, V> setSortOrderProvider(
                Function<SortDirection, Stream<SortOrder<String>>> provider) {
            Objects.requireNonNull(provider,
                    "Sort order provider can't be null");
            sortOrderProvider = provider;
            return this;
        }

        /**
         * Gets the sort orders to use with back-end sorting for this column
         * when sorting in the given direction.
         *
         * @param direction
         *            the sorting direction
         * @return stream of sort orders
         */
        public Stream<SortOrder<String>> getSortOrder(SortDirection direction) {
            return sortOrderProvider.apply(direction);
        }
    }

    private KeyMapper<Column<T, ?>> columnKeys = new KeyMapper<>();
    private Set<Column<T, ?>> columnSet = new HashSet<>();
    private List<SortOrder<Column<T, ?>>> sortOrder = new ArrayList<>();
    private DetailsManager<T> detailsManager;
    private Set<Component> extensionComponents = new HashSet<>();

    /**
     * Constructor for the {@link Grid} component.
     */
    public Grid() {
        setSelectionModel(new SingleSelection<>(this));
        setDataSource(DataSource.create());
        registerRpc(new GridServerRpcImpl());
        detailsManager = new DetailsManager<>();
        addExtension(detailsManager);
        addDataGenerator(detailsManager);
    }

    /**
     * Adds a new column to this {@link Grid} with given header caption and
     * value provider.
     *
     * @param caption
     *            the header caption
     * @param valueType
     *            the column value class
     * @param valueProvider
     *            the value provider
     * @param <V>
     *            the column value type
     *
     * @return the new column
     */
    public <V> Column<T, V> addColumn(String caption, Class<V> valueType,
            Function<T, V> valueProvider) {
        Column<T, V> c = new Column<>(caption, valueType, valueProvider);

        c.extend(this);
        c.setId(columnKeys.key(c));
        columnSet.add(c);
        addDataGenerator(c);

        return c;
    }

    /**
     * Removes the given column from this {@link Grid}.
     *
     * @param column
     *            the column to remove
     */
    public void removeColumn(Column<T, ?> column) {
        if (columnSet.remove(column)) {
            columnKeys.remove(column);
            removeDataGenerator(column);
            column.remove();
        }
    }

    /**
     * Sets the details component generator.
     *
     * @param generator
     *            the generator for details components
     */
    public void setDetailsGenerator(DetailsGenerator<T> generator) {
        this.detailsManager.setDetailsGenerator(generator);
    }

    /**
     * Sets the visibility of details component for given item.
     *
     * @param data
     *            the item to show details for
     * @param visible
     *            {@code true} if details component should be visible;
     *            {@code false} if it should be hidden
     */
    public void setDetailsVisible(T data, boolean visible) {
        detailsManager.setDetailsVisible(data, visible);
    }

    /**
     * Returns the visibility of details component for given item.
     *
     * @param data
     *            the item to show details for
     *
     * @return {@code true} if details component should be visible;
     *         {@code false} if it should be hidden
     */
    public boolean isDetailsVisible(T data) {
        return detailsManager.isDetailsVisible(data);
    }

    /**
     * Gets an unmodifiable collection of all columns currently in this
     * {@link Grid}.
     *
     * @return unmodifiable collection of columns
     */
    public Collection<Column<T, ?>> getColumns() {
        return Collections.unmodifiableSet(columnSet);
    }

    @Override
    public Iterator<Component> iterator() {
        return Collections.unmodifiableSet(extensionComponents).iterator();
    }

    private void addExtensionComponent(Component c) {
        if (extensionComponents.add(c)) {
            c.setParent(this);
            markAsDirty();
        }
    }

    private void removeExtensionComponent(Component c) {
        if (extensionComponents.remove(c)) {
            c.setParent(null);
            markAsDirty();
        }
    }
}
