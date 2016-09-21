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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.vaadin.event.ConnectorEvent;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.EventListener;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.data.SortOrder;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.ColumnState;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.SectionState;
import com.vaadin.ui.components.grid.Header;
import com.vaadin.ui.renderers.AbstractRenderer;
import com.vaadin.ui.renderers.Renderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.util.ReflectTools;

import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * A grid component for displaying tabular data.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <T>
 *            the grid bean type
 */
public class Grid<T> extends AbstractSingleSelect<T> implements HasComponents {

    @Deprecated
    private static final Method COLUMN_RESIZE_METHOD = ReflectTools.findMethod(
            ColumnResizeListener.class, "columnResize",
            ColumnResizeEvent.class);

    @Deprecated
    private static final Method ITEM_CLICK_METHOD = ReflectTools
            .findMethod(ItemClickListener.class, "accept", ItemClick.class);

    @Deprecated
    private static final Method COLUMN_VISIBILITY_METHOD = ReflectTools
            .findMethod(ColumnVisibilityChangeListener.class,
                    "columnVisibilityChanged",
                    ColumnVisibilityChangeEvent.class);

    /**
     * An event listener for column resize events in the Grid.
     *
     * @since 7.6
     */
    public interface ColumnResizeListener extends Serializable {

        /**
         * Called when the columns of the grid have been resized.
         *
         * @param event
         *            An event providing more information
         */
        void columnResize(ColumnResizeEvent event);
    }

    /**
     * An event that is fired when a column is resized, either programmatically
     * or by the user.
     *
     * @since 7.6
     */
    public static class ColumnResizeEvent extends Component.Event {

        private final Column<?, ?> column;
        private final boolean userOriginated;

        /**
         *
         * @param source
         *            the grid where the event originated from
         * @param userOriginated
         *            <code>true</code> if event is a result of user
         *            interaction, <code>false</code> if from API call
         */
        public ColumnResizeEvent(Grid<?> source, Column<?, ?> column,
                boolean userOriginated) {
            super(source);
            this.column = column;
            this.userOriginated = userOriginated;
        }

        /**
         * Returns the column that was resized.
         *
         * @return the resized column.
         */
        public Column<?, ?> getColumn() {
            return column;
        }

        /**
         * Returns <code>true</code> if the column resize was done by the user,
         * <code>false</code> if not and it was triggered by server side code.
         *
         * @return <code>true</code> if event is a result of user interaction
         */
        public boolean isUserOriginated() {
            return userOriginated;
        }

    }

    /**
     * An event fired when an item in the Grid has been clicked.
     *
     * @param <T>
     *            the grid bean type
     */
    public static class ItemClick<T> extends ConnectorEvent {

        private final T item;
        private final Column<T, ?> column;
        private final MouseEventDetails mouseEventDetails;

        /**
         * Creates a new {@code ItemClick} event containing the given item and
         * Column originating from the given Grid.
         *
         */
        public ItemClick(Grid<T> source, Column<T, ?> column, T item,
                MouseEventDetails mouseEventDetails) {
            super(source);
            this.column = column;
            this.item = item;
            this.mouseEventDetails = mouseEventDetails;
        }

        /**
         * Returns the clicked item.
         *
         * @return the clicked item
         */
        public T getItem() {
            return item;
        }

        /**
         * Returns the clicked column.
         *
         * @return the clicked column
         */
        public Column<T, ?> getColumn() {
            return column;
        }

        /**
         * Returns the source Grid.
         *
         * @return the grid
         */
        @Override
        public Grid<T> getSource() {
            return (Grid<T>) super.getSource();
        }

        /**
         * Returns the mouse event details.
         *
         * @return the mouse event details
         */
        public MouseEventDetails getMouseEventDetails() {
            return mouseEventDetails;
        }
    }

    /**
     * A listener for item click events.
     *
     * @param <T>
     *            the grid bean type
     *
     * @see ItemClick
     * @see Registration
     */
    @FunctionalInterface
    public interface ItemClickListener<T> extends EventListener<ItemClick<T>> {
        /**
         * Invoked when this listener receives a item click event from a Grid to
         * which it has been added.
         *
         * @param event
         *            the received event, not null
         */
        @Override
        public void accept(ItemClick<T> event);
    }

    /**
     * ContextClickEvent for the Grid Component.
     *
     * @param <T>
     *            the grid bean type
     */
    public static class GridContextClickEvent<T> extends ContextClickEvent {

        private final T item;
        private final int rowIndex;
        private final Column<?, ?> column;
        private final Section section;

        /**
         * Creates a new context click event.
         *
         * @param source
         *            the grid where the context click occurred
         * @param mouseEventDetails
         *            details about mouse position
         * @param section
         *            the section of the grid which was clicked
         * @param rowIndex
         *            the index of the row which was clicked
         * @param item
         *            the item which was clicked
         * @param column
         *            the column which was clicked
         */
        public GridContextClickEvent(Grid<T> source,
                MouseEventDetails mouseEventDetails, Section section,
                int rowIndex, T item, Column<?, ?> column) {
            super(source, mouseEventDetails);
            this.item = item;
            this.section = section;
            this.column = column;
            this.rowIndex = rowIndex;
        }

        /**
         * Returns the item of context clicked row.
         *
         * @return item of clicked row; <code>null</code> if header or footer
         */
        public T getItem() {
            return item;
        }

        /**
         * Returns the clicked column.
         *
         * @return the clicked column
         */
        public Column<?, ?> getColumn() {
            return column;
        }

        /**
         * Return the clicked section of Grid.
         *
         * @return section of grid
         */
        public Section getSection() {
            return section;
        }

        /**
         * Returns the clicked row index.
         * <p>
         * Header and Footer rows for index can be fetched with
         * {@link Grid#getHeaderRow(int)} and {@link Grid#getFooterRow(int)}.
         *
         * @return row index in section
         */
        public int getRowIndex() {
            return rowIndex;
        }

        @Override
        public Grid<T> getComponent() {
            return (Grid<T>) super.getComponent();
        }
    }

    /**
     * An event listener for column visibility change events in the Grid.
     *
     * @since 7.5.0
     */
    public interface ColumnVisibilityChangeListener extends Serializable {

        /**
         * Called when a column has become hidden or unhidden.
         *
         * @param event
         */
        void columnVisibilityChanged(ColumnVisibilityChangeEvent event);
    }

    /**
     * An event that is fired when a column's visibility changes.
     *
     * @since 7.5.0
     */
    public static class ColumnVisibilityChangeEvent extends Component.Event {

        private final Column<?, ?> column;
        private final boolean userOriginated;
        private final boolean hidden;

        /**
         * Constructor for a column visibility change event.
         *
         * @param source
         *            the grid from which this event originates
         * @param column
         *            the column that changed its visibility
         * @param hidden
         *            <code>true</code> if the column was hidden,
         *            <code>false</code> if it became visible
         * @param isUserOriginated
         *            <code>true</code> iff the event was triggered by an UI
         *            interaction
         */
        public ColumnVisibilityChangeEvent(Grid<?> source, Column<?, ?> column,
                boolean hidden, boolean isUserOriginated) {
            super(source);
            this.column = column;
            this.hidden = hidden;
            userOriginated = isUserOriginated;
        }

        /**
         * Gets the column that became hidden or visible.
         *
         * @return the column that became hidden or visible.
         * @see Column#isHidden()
         */
        public Column<?, ?> getColumn() {
            return column;
        }

        /**
         * Was the column set hidden or visible.
         *
         * @return <code>true</code> if the column was hidden <code>false</code>
         *         if it was set visible
         */
        public boolean isHidden() {
            return hidden;
        }

        /**
         * Returns <code>true</code> if the column reorder was done by the user,
         * <code>false</code> if not and it was triggered by server side code.
         *
         * @return <code>true</code> if event is a result of user interaction
         */
        public boolean isUserOriginated() {
            return userOriginated;
        }
    }

    /**
     * A callback interface for generating description texts for an item.
     *
     * @param <T>
     *            the grid bean type
     */
    @FunctionalInterface
    public interface DescriptionGenerator<T>
            extends Function<T, String>, Serializable {
    }

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
    public abstract static class AbstractGridExtension<T>
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
            Column<T, ?> column = columnKeys.containsKey(columnId)
                    ? columnKeys.get(columnId) : null;
            T item = getDataCommunicator().getKeyMapper().get(rowKey);
            fireEvent(new ItemClick<>(Grid.this, column, item, details));
        }

        @Override
        public void contextClick(int rowIndex, String rowKey, String columnId,
                Section section, MouseEventDetails details) {
            T item = null;
            if (rowKey != null) {
                item = getDataCommunicator().getKeyMapper().get(rowKey);
            }
            fireEvent(new GridContextClickEvent<>(Grid.this, details, section,
                    rowIndex, item, getColumn(columnId)));
        }

        @Override
        public void columnsReordered(List<String> newColumnOrder,
                List<String> oldColumnOrder) {
            // TODO Auto-generated method stub
        }

        @Override
        public void columnVisibilityChanged(String id, boolean hidden) {
            Column<T, ?> column = getColumn(id);
            ColumnState columnState = column.getState(false);
            if (columnState.hidden != hidden) {
                columnState.hidden = hidden;
                fireColumnVisibilityChangeEvent(column, hidden, true);
            }
        }

        @Override
        public void columnResized(String id, double pixels) {
            final Column<T, ?> column = getColumn(id);
            if (column != null && column.isResizable()) {
                column.getState().width = pixels;
                fireColumnResizeEvent(column, true);
                markAsDirty();
            }
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
    public static class Column<T, V> extends AbstractGridExtension<T> {

        private final Function<T, ? extends V> valueProvider;

        private Function<SortDirection, Stream<SortOrder<String>>> sortOrderProvider;
        private Comparator<T> comparator;
        private StyleGenerator<T> styleGenerator = item -> null;
        private DescriptionGenerator<T> descriptionGenerator;

        /**
         * Constructs a new Column configuration with given header caption,
         * renderer and value provider.
         *
         * @param caption
         *            the header caption
         * @param valueProvider
         *            the function to get values from items
         * @param renderer
         *            the type of value
         */
        protected Column(String caption, Function<T, ? extends V> valueProvider,
                Renderer<V> renderer) {
            Objects.requireNonNull(caption, "Header caption can't be null");
            Objects.requireNonNull(valueProvider,
                    "Value provider can't be null");
            Objects.requireNonNull(renderer, "Renderer can't be null");

            ColumnState state = getState();

            this.valueProvider = valueProvider;
            state.renderer = renderer;

            state.caption = caption;
            sortOrderProvider = d -> Stream.of();

            // Add the renderer as a child extension of this extension, thus
            // ensuring the renderer will be unregistered when this column is
            // removed
            addExtension(renderer);

            Class<V> valueType = renderer.getPresentationType();

            if (Comparable.class.isAssignableFrom(valueType)) {
                comparator = (a, b) -> {
                    @SuppressWarnings("unchecked")
                    Comparable<V> comp = (Comparable<V>) valueProvider.apply(a);
                    return comp.compareTo(valueProvider.apply(b));
                };
                state.sortable = true;
            } else if (Number.class.isAssignableFrom(valueType)) {
                /*
                 * Value type will be Number whenever using NumberRenderer.
                 * Provide explicit comparison support in this case even though
                 * Number itself isn't Comparable.
                 */
                comparator = (a, b) -> {
                    return compareNumbers((Number) valueProvider.apply(a),
                            (Number) valueProvider.apply(b));
                };
                state.sortable = true;
            } else {
                state.sortable = false;
            }
        }

        @SuppressWarnings("unchecked")
        private static int compareNumbers(Number a, Number b) {
            assert a.getClass() == b.getClass();

            // Most Number implementations are Comparable
            if (a instanceof Comparable && a.getClass().isInstance(b)) {
                return ((Comparable<Number>) a).compareTo(b);
            } else if (a.equals(b)) {
                return 0;
            } else {
                // Fall back to comparing based on potentially truncated values
                int compare = Long.compare(a.longValue(), b.longValue());
                if (compare == 0) {
                    // This might still produce 0 even though the values are not
                    // equals, but there's nothing more we can do about that
                    compare = Double.compare(a.doubleValue(), b.doubleValue());
                }
                return compare;
            }
        }

        @Override
        public void generateData(T data, JsonObject jsonObject) {
            ColumnState state = getState(false);

            String communicationId = state.id;

            assert communicationId != null : "No communication ID set for column "
                    + state.caption;

            @SuppressWarnings("unchecked")
            Renderer<V> renderer = (Renderer<V>) state.renderer;

            JsonObject obj = getDataObject(jsonObject,
                    DataCommunicatorConstants.DATA);

            V providerValue = valueProvider.apply(data);

            JsonValue rendererValue = renderer.encode(providerValue);

            obj.put(communicationId, rendererValue);

            String style = styleGenerator.apply(data);
            if (style != null && !style.isEmpty()) {
                JsonObject styleObj = getDataObject(jsonObject,
                        GridState.JSONKEY_CELLSTYLES);
                styleObj.put(communicationId, style);
            }
            if (descriptionGenerator != null) {
                String description = descriptionGenerator.apply(data);
                if (description != null && !description.isEmpty()) {
                    JsonObject descriptionObj = getDataObject(jsonObject,
                            GridState.JSONKEY_CELLDESCRIPTION);
                    descriptionObj.put(communicationId, description);
                }
            }
        }

        /**
         * Gets a data object with the given key from the given JsonObject. If
         * there is no object with the key, this method creates a new
         * JsonObject.
         *
         * @param jsonObject
         *            the json object
         * @param key
         *            the key where the desired data object is stored
         * @return data object for the given key
         */
        private JsonObject getDataObject(JsonObject jsonObject, String key) {
            if (!jsonObject.hasKey(key)) {
                jsonObject.put(key, Json.createObject());
            }
            return jsonObject.getObject(key);
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
         * Returns the identifier used with this Column in communication.
         *
         * @return the identifier string
         */
        public String getId() {
            return getState(false).id;
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

        /**
         * Sets the style generator that is used for generating class names for
         * cells in this column. Returning null from the generator results in no
         * custom style name being set.
         *
         * @param cellStyleGenerator
         *            the cell style generator to set, not null
         * @return this column
         * @throws NullPointerException
         *             if {@code cellStyleGenerator} is {@code null}
         */
        public Column<T, V> setStyleGenerator(
                StyleGenerator<T> cellStyleGenerator) {
            Objects.requireNonNull(cellStyleGenerator,
                    "Cell style generator must not be null");
            this.styleGenerator = cellStyleGenerator;
            getParent().getDataCommunicator().reset();
            return this;
        }

        /**
         * Gets the style generator that is used for generating styles for
         * cells.
         *
         * @return the cell style generator
         */
        public StyleGenerator<T> getStyleGenerator() {
            return styleGenerator;
        }

        /**
         * Sets the description generator that is used for generating
         * descriptions for cells in this column.
         *
         * @param cellDescriptionGenerator
         *            the cell description generator to set, or
         *            <code>null</code> to remove a previously set generator
         * @return this column
         */
        public Column<T, V> setDescriptionGenerator(
                DescriptionGenerator<T> cellDescriptionGenerator) {
            this.descriptionGenerator = cellDescriptionGenerator;
            getParent().getDataCommunicator().reset();
            return this;
        }

        /**
         * Gets the description generator that is used for generating
         * descriptions for cells.
         *
         * @return the cell description generator, or <code>null</code> if no
         *         generator is set
         */
        public DescriptionGenerator<T> getDescriptionGenerator() {
            return descriptionGenerator;
        }

        /**
         * Sets the ratio with which the column expands.
         * <p>
         * By default, all columns expand equally (treated as if all of them had
         * an expand ratio of 1). Once at least one column gets a defined expand
         * ratio, the implicit expand ratio is removed, and only the defined
         * expand ratios are taken into account.
         * <p>
         * If a column has a defined width ({@link #setWidth(double)}), it
         * overrides this method's effects.
         * <p>
         * <em>Example:</em> A grid with three columns, with expand ratios 0, 1
         * and 2, respectively. The column with a <strong>ratio of 0 is exactly
         * as wide as its contents requires</strong>. The column with a ratio of
         * 1 is as wide as it needs, <strong>plus a third of any excess
         * space</strong>, because we have 3 parts total, and this column
         * reserves only one of those. The column with a ratio of 2, is as wide
         * as it needs to be, <strong>plus two thirds</strong> of the excess
         * width.
         *
         * @param expandRatio
         *            the expand ratio of this column. {@code 0} to not have it
         *            expand at all. A negative number to clear the expand
         *            value.
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         * @see #setWidth(double)
         */
        public Column<T, V> setExpandRatio(int expandRatio)
                throws IllegalStateException {
            checkColumnIsAttached();
            if (expandRatio != getExpandRatio()) {
                getState().expandRatio = expandRatio;
                getParent().markAsDirty();
            }
            return this;
        }

        /**
         * Returns the column's expand ratio.
         *
         * @return the column's expand ratio
         * @see #setExpandRatio(int)
         */
        public int getExpandRatio() {
            return getState(false).expandRatio;
        }

        /**
         * Clears the expand ratio for this column.
         * <p>
         * Equal to calling {@link #setExpandRatio(int) setExpandRatio(-1)}
         *
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public Column<T, V> clearExpandRatio() throws IllegalStateException {
            return setExpandRatio(-1);
        }

        /**
         * Returns the width (in pixels). By default a column is 100px wide.
         *
         * @return the width in pixels of the column
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public double getWidth() throws IllegalStateException {
            checkColumnIsAttached();
            return getState(false).width;
        }

        /**
         * Sets the width (in pixels).
         * <p>
         * This overrides any configuration set by any of
         * {@link #setExpandRatio(int)}, {@link #setMinimumWidth(double)} or
         * {@link #setMaximumWidth(double)}.
         *
         * @param pixelWidth
         *            the new pixel width of the column
         * @return the column itself
         *
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         * @throws IllegalArgumentException
         *             thrown if pixel width is less than zero
         */
        public Column<T, V> setWidth(double pixelWidth)
                throws IllegalStateException, IllegalArgumentException {
            checkColumnIsAttached();
            if (pixelWidth < 0) {
                throw new IllegalArgumentException(
                        "Pixel width should be greated than 0 (in " + toString()
                                + ")");
            }
            if (pixelWidth != getWidth()) {
                getState().width = pixelWidth;
                getParent().markAsDirty();
                getParent().fireColumnResizeEvent(this, false);
            }
            return this;
        }

        /**
         * Returns whether this column has an undefined width.
         *
         * @since 7.6
         * @return whether the width is undefined
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public boolean isWidthUndefined() {
            checkColumnIsAttached();
            return getState(false).width < 0;
        }

        /**
         * Marks the column width as undefined. An undefined width means the
         * grid is free to resize the column based on the cell contents and
         * available space in the grid.
         *
         * @return the column itself
         */
        public Column<T, V> setWidthUndefined() {
            checkColumnIsAttached();
            if (!isWidthUndefined()) {
                getState().width = -1;
                getParent().markAsDirty();
                getParent().fireColumnResizeEvent(this, false);
            }
            return this;
        }

        /**
         * Sets the minimum width for this column.
         * <p>
         * This defines the minimum guaranteed pixel width of the column
         * <em>when it is set to expand</em>.
         *
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         * @see #setExpandRatio(int)
         */
        public Column<T, V> setMinimumWidth(double pixels)
                throws IllegalStateException {
            checkColumnIsAttached();

            final double maxwidth = getMaximumWidth();
            if (pixels >= 0 && pixels > maxwidth && maxwidth >= 0) {
                throw new IllegalArgumentException("New minimum width ("
                        + pixels + ") was greater than maximum width ("
                        + maxwidth + ")");
            }
            getState().minWidth = pixels;
            getParent().markAsDirty();
            return this;
        }

        /**
         * Return the minimum width for this column.
         *
         * @return the minimum width for this column
         * @see #setMinimumWidth(double)
         */
        public double getMinimumWidth() {
            return getState(false).minWidth;
        }

        /**
         * Sets the maximum width for this column.
         * <p>
         * This defines the maximum allowed pixel width of the column <em>when
         * it is set to expand</em>.
         *
         * @param pixels
         *            the maximum width
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         * @see #setExpandRatio(int)
         */
        public Column<T, V> setMaximumWidth(double pixels) {
            checkColumnIsAttached();

            final double minwidth = getMinimumWidth();
            if (pixels >= 0 && pixels < minwidth && minwidth >= 0) {
                throw new IllegalArgumentException("New maximum width ("
                        + pixels + ") was less than minimum width (" + minwidth
                        + ")");
            }

            getState().maxWidth = pixels;
            getParent().markAsDirty();
            return this;
        }

        /**
         * Returns the maximum width for this column.
         *
         * @return the maximum width for this column
         * @see #setMaximumWidth(double)
         */
        public double getMaximumWidth() {
            return getState(false).maxWidth;
        }

        /**
         * Sets whether this column can be resized by the user.
         *
         * @since 7.6
         * @param resizable
         *            {@code true} if this column should be resizable,
         *            {@code false} otherwise
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public Column<T, V> setResizable(boolean resizable) {
            checkColumnIsAttached();
            if (resizable != isResizable()) {
                getState().resizable = resizable;
                getParent().markAsDirty();
            }
            return this;
        }

        /**
         * Gets the caption of the hiding toggle for this column.
         *
         * @since 7.5.0
         * @see #setHidingToggleCaption(String)
         * @return the caption for the hiding toggle for this column
         */
        public String getHidingToggleCaption() {
            return getState(false).hidingToggleCaption;
        }

        /**
         * Sets the caption of the hiding toggle for this column. Shown in the
         * toggle for this column in the grid's sidebar when the column is
         * {@link #isHidable() hidable}.
         * <p>
         * The default value is <code>null</code>, and in that case the column's
         * {@link #getHeaderCaption() header caption} is used.
         * <p>
         * <em>NOTE:</em> setting this to empty string might cause the hiding
         * toggle to not render correctly.
         *
         * @since 7.5.0
         * @param hidingToggleCaption
         *            the text to show in the column hiding toggle
         * @return the column itself
         */
        public Column<T, V> setHidingToggleCaption(String hidingToggleCaption) {
            if (hidingToggleCaption != getHidingToggleCaption()) {
                getState().hidingToggleCaption = hidingToggleCaption;
            }
            return this;
        }

        /**
         * Hides or shows the column. By default columns are visible before
         * explicitly hiding them.
         *
         * @since 7.5.0
         * @param hidden
         *            <code>true</code> to hide the column, <code>false</code>
         *            to show
         * @return this column
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        public Column<T, V> setHidden(boolean hidden) {
            checkColumnIsAttached();
            if (hidden != isHidden()) {
                getState().hidden = hidden;
                getParent().fireColumnVisibilityChangeEvent(this, hidden,
                        false);
            }
            return this;
        }

        /**
         * Returns whether this column is hidden. Default is {@code false}.
         *
         * @since 7.5.0
         * @return <code>true</code> if the column is currently hidden,
         *         <code>false</code> otherwise
         */
        public boolean isHidden() {
            return getState(false).hidden;
        }

        /**
         * Sets whether this column can be hidden by the user. Hidable columns
         * can be hidden and shown via the sidebar menu.
         *
         * @since 7.5.0
         * @param hidable
         *            <code>true</code> iff the column may be hidable by the
         *            user via UI interaction
         * @return this column
         */
        public Column<T, V> setHidable(boolean hidable) {
            if (hidable != isHidable()) {
                getState().hidable = hidable;
            }
            return this;
        }

        /**
         * Returns whether this column can be hidden by the user. Default is
         * {@code false}.
         * <p>
         * <em>Note:</em> the column can be programmatically hidden using
         * {@link #setHidden(boolean)} regardless of the returned value.
         *
         * @since 7.5.0
         * @return <code>true</code> if the user can hide the column,
         *         <code>false</code> if not
         */
        public boolean isHidable() {
            return getState(false).hidable;
        }

        /**
         * Returns whether this column can be resized by the user. Default is
         * {@code true}.
         * <p>
         * <em>Note:</em> the column can be programmatically resized using
         * {@link #setWidth(double)} and {@link #setWidthUndefined()} regardless
         * of the returned value.
         *
         * @since 7.6
         * @return {@code true} if this column is resizable, {@code false}
         *         otherwise
         */
        public boolean isResizable() {
            return getState(false).resizable;
        }

        /**
         * Checks if column is attached and throws an
         * {@link IllegalStateException} if it is not
         *
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        protected void checkColumnIsAttached() throws IllegalStateException {
            if (getParent() == null) {
                throw new IllegalStateException(
                        "Column is no longer attached to a grid.");
            }
        }
    }

    private final class SingleSelection extends AbstractSingleSelection {
        private T selectedItem = null;

        SingleSelection() {
            addDataGenerator((item, json) -> {
                if (isSelected(item)) {
                    json.put(DataCommunicatorConstants.SELECTED, true);
                }
            });
        }

        @Override
        public Optional<T> getSelectedItem() {
            return Optional.ofNullable(selectedItem);
        }

        @Override
        protected boolean isKeySelected(String key) {
            return Objects.equals(key, getSelectedKey());
        }

        @Override
        protected String getSelectedKey() {
            return itemToKey(selectedItem);
        }

        @Override
        protected void doSetSelectedKey(String key) {
            if (selectedItem != null) {
                getDataCommunicator().refresh(selectedItem);
            }
            selectedItem = keyToItem(key);
            if (selectedItem != null) {
                getDataCommunicator().refresh(selectedItem);
            }
        }
    }

    /**
     * A header row in a Grid.
     */
    public interface HeaderRow extends Serializable {

        /**
         * Returns the cell on this row corresponding to the given column id.
         * 
         * @param columnId
         *            the id of the column whose header cell to get
         * @return the header cell
         */
        public HeaderCell getCell(String columnId);

        /**
         * Returns the cell on this row corresponding to the given column.
         * 
         * @param column
         *            the column whose header cell to get
         * @return the header cell
         */
        public default HeaderCell getCell(Column<?, ?> column) {
            return getCell(column.getId());
        }
    }

    /**
     * An individual cell on a Grid header row.
     */
    public interface HeaderCell extends Serializable {

        /**
         * Returns the textual caption of this cell.
         * 
         * @return the header caption
         */
        public String getText();

        /**
         * Sets the textual caption of this cell.
         * 
         * @param text
         *            the header caption to set
         */
        public void setText(String text);
    }

    private KeyMapper<Column<T, ?>> columnKeys = new KeyMapper<>();
    private Set<Column<T, ?>> columnSet = new LinkedHashSet<>();
    private List<SortOrder<Column<T, ?>>> sortOrder = new ArrayList<>();
    private DetailsManager<T> detailsManager;
    private Set<Component> extensionComponents = new HashSet<>();
    private StyleGenerator<T> styleGenerator = item -> null;
    private DescriptionGenerator<T> descriptionGenerator;

    private Header header = new Header() {
        @Override
        protected SectionState getState(boolean markAsDirty) {
            return Grid.this.getState(markAsDirty).header;
        }
    };

    /**
     * Constructor for the {@link Grid} component.
     */
    public Grid() {
        setSelectionModel(new SingleSelection());
        registerRpc(new GridServerRpcImpl());

        appendHeaderRow();

        detailsManager = new DetailsManager<>();
        addExtension(detailsManager);
        addDataGenerator(detailsManager);

        addDataGenerator((item, json) -> {
            String styleName = styleGenerator.apply(item);
            if (styleName != null && !styleName.isEmpty()) {
                json.put(GridState.JSONKEY_ROWSTYLE, styleName);
            }
            if (descriptionGenerator != null) {
                String description = descriptionGenerator.apply(item);
                if (description != null && !description.isEmpty()) {
                    json.put(GridState.JSONKEY_ROWDESCRIPTION, description);
                }
            }
        });
    }

    public <V> void fireColumnVisibilityChangeEvent(Column<T, V> column,
            boolean hidden, boolean userOriginated) {
        fireEvent(new ColumnVisibilityChangeEvent(this, column, hidden,
                userOriginated));
    }

    /**
     * Adds a new column to this {@link Grid} with given header caption, typed
     * renderer and value provider.
     *
     * @param caption
     *            the header caption
     * @param valueProvider
     *            the value provider
     * @param renderer
     *            the column value class
     * @param <V>
     *            the column value type
     *
     * @return the new column
     *
     * @see {@link AbstractRenderer}
     */
    public <V> Column<T, V> addColumn(String caption,
            Function<T, ? extends V> valueProvider,
            AbstractRenderer<? super T, V> renderer) {
        final Column<T, V> column = new Column<>(caption, valueProvider,
                renderer);
        final String columnId = columnKeys.key(column);

        column.extend(this);
        column.setId(columnId);
        columnSet.add(column);
        addDataGenerator(column);

        getHeader().addColumn(columnId);

        if (getHeaderRowCount() > 0) {
            // TODO Default header API to be added in a later patch
            HeaderRow defaultHeader = getHeaderRow(0);
            defaultHeader.getCell(columnId).setText(caption);
        }

        return column;
    }

    /**
     * Adds a new text column to this {@link Grid} with given header caption
     * string value provider. The column will use a {@link TextRenderer}.
     *
     * @param caption
     *            the header caption
     * @param valueProvider
     *            the value provider
     *
     * @return the new column
     */
    public Column<T, String> addColumn(String caption,
            Function<T, String> valueProvider) {
        return addColumn(caption, valueProvider, new TextRenderer());
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

    /**
     * Gets a {@link Column} of this grid by its identifying string.
     *
     * @param columnId
     *            the identifier of the column to get
     * @return the column corresponding to the given column id
     */
    public Column<T, ?> getColumn(String columnId) {
        return columnKeys.get(columnId);
    }

    @Override
    public Iterator<Component> iterator() {
        return Collections.unmodifiableSet(extensionComponents).iterator();
    }

    /**
     * Sets the number of frozen columns in this grid. Setting the count to 0
     * means that no data columns will be frozen, but the built-in selection
     * checkbox column will still be frozen if it's in use. Setting the count to
     * -1 will also disable the selection column.
     * <p>
     * The default value is 0.
     *
     * @param numberOfColumns
     *            the number of columns that should be frozen
     *
     * @throws IllegalArgumentException
     *             if the column count is less than -1 or greater than the
     *             number of visible columns
     */
    public void setFrozenColumnCount(int numberOfColumns) {
        if (numberOfColumns < -1 || numberOfColumns > columnSet.size()) {
            throw new IllegalArgumentException(
                    "count must be between -1 and the current number of columns ("
                            + columnSet.size() + "): " + numberOfColumns);
        }

        getState().frozenColumnCount = numberOfColumns;
    }

    /**
     * Gets the number of frozen columns in this grid. 0 means that no data
     * columns will be frozen, but the built-in selection checkbox column will
     * still be frozen if it's in use. -1 means that not even the selection
     * column is frozen.
     * <p>
     * <em>NOTE:</em> this count includes {@link Column#isHidden() hidden
     * columns} in the count.
     *
     * @see #setFrozenColumnCount(int)
     *
     * @return the number of frozen columns
     */
    public int getFrozenColumnCount() {
        return getState(false).frozenColumnCount;
    }

    /**
     * Sets the number of rows that should be visible in Grid's body. This
     * method will set the height mode to be {@link HeightMode#ROW}.
     *
     * @param rows
     *            The height in terms of number of rows displayed in Grid's
     *            body. If Grid doesn't contain enough rows, white space is
     *            displayed instead. If <code>null</code> is given, then Grid's
     *            height is undefined
     * @throws IllegalArgumentException
     *             if {@code rows} is zero or less
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isInfinite(double) infinite}
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isNaN(double) NaN}
     */
    public void setHeightByRows(double rows) {
        if (rows <= 0.0d) {
            throw new IllegalArgumentException(
                    "More than zero rows must be shown.");
        } else if (Double.isInfinite(rows)) {
            throw new IllegalArgumentException(
                    "Grid doesn't support infinite heights");
        } else if (Double.isNaN(rows)) {
            throw new IllegalArgumentException("NaN is not a valid row count");
        }
        getState().heightMode = HeightMode.ROW;
        getState().heightByRows = rows;
    }

    /**
     * Gets the amount of rows in Grid's body that are shown, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     *
     * @return the amount of rows that are being shown in Grid's body
     * @see #setHeightByRows(double)
     */
    public double getHeightByRows() {
        return getState(false).heightByRows;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <em>Note:</em> This method will set the height mode to be
     * {@link HeightMode#CSS}.
     *
     * @see #setHeightMode(HeightMode)
     */
    @Override
    public void setHeight(float height, Unit unit) {
        getState().heightMode = HeightMode.CSS;
        super.setHeight(height, unit);
    }

    /**
     * Defines the mode in which the Grid widget's height is calculated.
     * <p>
     * If {@link HeightMode#CSS} is given, Grid will respect the values given
     * via a {@code setHeight}-method, and behave as a traditional Component.
     * <p>
     * If {@link HeightMode#ROW} is given, Grid will make sure that the body
     * will display as many rows as {@link #getHeightByRows()} defines.
     * <em>Note:</em> If headers/footers are inserted or removed, the widget
     * will resize itself to still display the required amount of rows in its
     * body. It also takes the horizontal scrollbar into account.
     *
     * @param heightMode
     *            the mode in to which Grid should be set
     */
    public void setHeightMode(HeightMode heightMode) {
        /*
         * This method is a workaround for the fact that Vaadin re-applies
         * widget dimensions (height/width) on each state change event. The
         * original design was to have setHeight and setHeightByRow be equals,
         * and whichever was called the latest was considered in effect.
         *
         * But, because of Vaadin always calling setHeight on the widget, this
         * approach doesn't work.
         */

        getState().heightMode = heightMode;
    }

    /**
     * Returns the current {@link HeightMode} the Grid is in.
     * <p>
     * Defaults to {@link HeightMode#CSS}.
     *
     * @return the current HeightMode
     */
    public HeightMode getHeightMode() {
        return getState(false).heightMode;
    }

    /**
     * Sets the style generator that is used for generating class names for rows
     * in this grid. Returning null from the generator results in no custom
     * style name being set.
     *
     * @see StyleGenerator
     *
     * @param styleGenerator
     *            the row style generator to set, not null
     * @throws NullPointerException
     *             if {@code styleGenerator} is {@code null}
     */
    public void setStyleGenerator(StyleGenerator<T> styleGenerator) {
        Objects.requireNonNull(styleGenerator,
                "Style generator must not be null");
        this.styleGenerator = styleGenerator;
        getDataCommunicator().reset();
    }

    /**
     * Gets the style generator that is used for generating class names for
     * rows.
     *
     * @see StyleGenerator
     *
     * @return the row style generator
     */
    public StyleGenerator<T> getStyleGenerator() {
        return styleGenerator;
    }

    /**
     * Sets the description generator that is used for generating descriptions
     * for rows.
     *
     * @param descriptionGenerator
     *            the row description generator to set, or <code>null</code> to
     *            remove a previously set generator
     */
    public void setDescriptionGenerator(
            DescriptionGenerator<T> descriptionGenerator) {
        this.descriptionGenerator = descriptionGenerator;
        getDataCommunicator().reset();
    }

    /**
     * Gets the description generator that is used for generating descriptions
     * for rows.
     *
     * @return the row description generator, or <code>null</code> if no
     *         generator is set
     */
    public DescriptionGenerator<T> getDescriptionGenerator() {
        return descriptionGenerator;
    }

    //
    // HEADER AND FOOTER
    //

    /**
     * Returns the header row at the given index.
     *
     * @param rowIndex
     *            the index of the row, where the topmost row has index zero
     * @return the header row at the index
     * @throws IndexOutOfBoundsException
     *             if {@code rowIndex < 0 || rowIndex >= getHeaderRowCount()}
     */
    public HeaderRow getHeaderRow(int rowIndex) {
        return getHeader().getRow(rowIndex);
    }

    /**
     * Gets the number of rows in the header section.
     *
     * @return the number of header rows
     */
    public int getHeaderRowCount() {
        return header.getRowCount();
    }

    /**
     * Inserts a new row at the given position to the header section. Shifts the
     * row currently at that position and any subsequent rows down (adds one to
     * their indices). Inserting at {@link #getHeaderRowCount()} appends the row
     * at the bottom of the header.
     *
     * @param index
     *            the index at which to insert the row, where the topmost row
     *            has index zero
     * @return the inserted header row
     * 
     * @throws IndexOutOfBoundsException
     *             if {@code rowIndex < 0 || rowIndex > getHeaderRowCount()}
     * 
     * @see #appendHeaderRow()
     * @see #prependHeaderRow()
     * @see #removeHeaderRow(HeaderRow)
     * @see #removeHeaderRow(int)
     */
    public HeaderRow addHeaderRowAt(int index) {
        return getHeader().addRowAt(index);
    }

    /**
     * Adds a new row at the bottom of the header section.
     *
     * @return the appended header row
     * 
     * @see #prependHeaderRow()
     * @see #addHeaderRowAt(int)
     * @see #removeHeaderRow(HeaderRow)
     * @see #removeHeaderRow(int)
     */
    public HeaderRow appendHeaderRow() {
        return addHeaderRowAt(getHeaderRowCount());
    }

    /**
     * Adds a new row at the top of the header section.
     *
     * @return the prepended header row
     * 
     * @see #appendHeaderRow()
     * @see #addHeaderRowAt(int)
     * @see #removeHeaderRow(HeaderRow)
     * @see #removeHeaderRow(int)
     */
    public HeaderRow prependHeaderRow() {
        return addHeaderRowAt(0);
    }

    /**
     * Removes the given row from the header section.
     *
     * @param row
     *            the header row to be removed, not null
     *
     * @throws IllegalArgumentException
     *             if the header does not contain the row
     * 
     * @see #removeHeaderRow(int)
     * @see #addHeaderRowAt(int)
     * @see #appendHeaderRow()
     * @see #prependHeaderRow()
     */
    public void removeHeaderRow(HeaderRow row) {
        getHeader().removeRow(row);
    }

    /**
     * Removes the row at the given position from the header section.
     *
     * @param rowIndex
     *            the index of the row to remove, where the topmost row has
     *            index zero
     *
     * @throws IndexOutOfBoundsException
     *             if {@code rowIndex < 0 || rowIndex >= getHeaderRowCount()}
     * 
     * @see #removeHeaderRow(HeaderRow)
     * @see #addHeaderRowAt(int)
     * @see #appendHeaderRow()
     * @see #prependHeaderRow()
     */
    public void removeHeaderRow(int rowIndex) {
        getHeader().removeRow(rowIndex);
    }

    /**
     * Returns the header section of this grid. The default header contains a
     * single row displaying the column captions.
     *
     * @return the header section
     */
    protected Header getHeader() {
        return header;
    }

    /**
     * Registers a new column resize listener.
     *
     * @param listener
     *            the listener to register, not null
     * @return a registration for the listener
     */
    public Registration addColumnResizeListener(ColumnResizeListener listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        addListener(ColumnResizeEvent.class, listener, COLUMN_RESIZE_METHOD);
        return () -> removeListener(ColumnResizeEvent.class, listener,
                COLUMN_RESIZE_METHOD);
    }

    /**
     * Adds an item click listener. The listener is called when an item of this
     * {@code Grid} is clicked.
     *
     * @param listener
     *            the item click listener, not null
     * @return a registration for the listener
     */
    public Registration addItemClickListener(
            ItemClickListener<? super T> listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        addListener(GridConstants.ITEM_CLICK_EVENT_ID, ItemClick.class,
                listener, ITEM_CLICK_METHOD);
        return () -> removeListener(ItemClick.class, listener);
    }

    /**
     * Registers a new column visibility change listener
     *
     * @param listener
     *            the listener to register, not null
     * @return a registration for the listener
     */
    public Registration addColumnVisibilityChangeListener(
            ColumnVisibilityChangeListener listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        addListener(ColumnVisibilityChangeEvent.class, listener,
                COLUMN_VISIBILITY_METHOD);
        return () -> removeListener(ColumnVisibilityChangeEvent.class, listener,
                COLUMN_VISIBILITY_METHOD);
    }

    @Override
    protected GridState getState() {
        return getState(true);
    }

    @Override
    protected GridState getState(boolean markAsDirty) {
        return (GridState) super.getState(markAsDirty);
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

    private void fireColumnResizeEvent(Column<?, ?> column,
            boolean userOriginated) {
        fireEvent(new ColumnResizeEvent(this, column, userOriginated));
    }
}
