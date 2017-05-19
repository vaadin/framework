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
import java.lang.reflect.Type;
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
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.vaadin.data.BeanPropertySet;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.HasDataProvider;
import com.vaadin.data.HasValue;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.PropertySet;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.DataCommunicator;
import com.vaadin.data.provider.DataGenerator;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.event.ConnectorEvent;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.SortEvent;
import com.vaadin.event.SortEvent.SortListener;
import com.vaadin.event.SortEvent.SortNotifier;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.EncodeResult;
import com.vaadin.server.Extension;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.SerializableComparator;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.server.Setter;
import com.vaadin.server.VaadinServiceClassLoaderUtil;
import com.vaadin.shared.Connector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.AbstractGridExtensionState;
import com.vaadin.shared.ui.grid.ColumnResizeMode;
import com.vaadin.shared.ui.grid.ColumnState;
import com.vaadin.shared.ui.grid.DetailsManagerState;
import com.vaadin.shared.ui.grid.GridClientRpc;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.shared.ui.grid.SectionState;
import com.vaadin.ui.components.grid.ColumnReorderListener;
import com.vaadin.ui.components.grid.ColumnResizeListener;
import com.vaadin.ui.components.grid.ColumnVisibilityChangeListener;
import com.vaadin.ui.components.grid.DescriptionGenerator;
import com.vaadin.ui.components.grid.DetailsGenerator;
import com.vaadin.ui.components.grid.Editor;
import com.vaadin.ui.components.grid.EditorImpl;
import com.vaadin.ui.components.grid.Footer;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.components.grid.Header;
import com.vaadin.ui.components.grid.Header.Row;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.NoSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;
import com.vaadin.ui.components.grid.SortOrderProvider;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import com.vaadin.ui.declarative.DesignFormatter;
import com.vaadin.ui.renderers.AbstractRenderer;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
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
public class Grid<T> extends AbstractListing<T> implements HasComponents,
        HasDataProvider<T>, SortNotifier<GridSortOrder<T>> {

    private static final String DECLARATIVE_DATA_ITEM_TYPE = "data-item-type";

    /**
     * A callback method for fetching items. The callback is provided with a
     * list of sort orders, offset index and limit.
     *
     * @param <T>
     *            the grid bean type
     */
    @FunctionalInterface
    public interface FetchItemsCallback<T> extends Serializable {

        /**
         * Returns a stream of items ordered by given sort orders, limiting the
         * results with given offset and limit.
         * <p>
         * This method is called after the size of the data set is asked from a
         * related size callback. The offset and limit are promised to be within
         * the size of the data set.
         *
         * @param sortOrder
         *            a list of sort orders
         * @param offset
         *            the first index to fetch
         * @param limit
         *            the fetched item count
         * @return stream of items
         */
        public Stream<T> fetchItems(List<QuerySortOrder> sortOrder, int offset,
                int limit);
    }

    @Deprecated
    private static final Method COLUMN_REORDER_METHOD = ReflectTools.findMethod(
            ColumnReorderListener.class, "columnReorder",
            ColumnReorderEvent.class);

    private static final Method SORT_ORDER_CHANGE_METHOD = ReflectTools
            .findMethod(SortListener.class, "sort", SortEvent.class);

    @Deprecated
    private static final Method COLUMN_RESIZE_METHOD = ReflectTools.findMethod(
            ColumnResizeListener.class, "columnResize",
            ColumnResizeEvent.class);

    @Deprecated
    private static final Method ITEM_CLICK_METHOD = ReflectTools
            .findMethod(ItemClickListener.class, "itemClick", ItemClick.class);

    @Deprecated
    private static final Method COLUMN_VISIBILITY_METHOD = ReflectTools
            .findMethod(ColumnVisibilityChangeListener.class,
                    "columnVisibilityChanged",
                    ColumnVisibilityChangeEvent.class);

    /**
     * Selection mode representing the built-in selection models in grid.
     * <p>
     * These enums can be used in {@link Grid#setSelectionMode(SelectionMode)}
     * to easily switch between the build-in selection models.
     *
     * @see Grid#setSelectionMode(SelectionMode)
     * @see Grid#setSelectionModel(GridSelectionModel)
     */
    public enum SelectionMode {

        /**
         * Single selection mode that maps to build-in
         * {@link SingleSelectionModel}.
         *
         * @see SingleSelectionModelImpl
         */
        SINGLE {
            @Override
            protected <T> GridSelectionModel<T> createModel() {
                return new SingleSelectionModelImpl<>();
            }
        },

        /**
         * Multiselection mode that maps to build-in {@link MultiSelectionModel}
         * .
         *
         * @see MultiSelectionModelImpl
         */
        MULTI {
            @Override
            protected <T> GridSelectionModel<T> createModel() {
                return new MultiSelectionModelImpl<>();
            }
        },

        /**
         * Selection model that doesn't allow selection.
         *
         * @see NoSelectionModel
         */
        NONE {
            @Override
            protected <T> GridSelectionModel<T> createModel() {
                return new NoSelectionModel<>();
            }
        };

        /**
         * Creates the selection model to use with this enum.
         *
         * @param <T>
         *            the type of items in the grid
         * @return the selection model
         */
        protected abstract <T> GridSelectionModel<T> createModel();
    }

    /**
     * An event that is fired when the columns are reordered.
     */
    public static class ColumnReorderEvent extends Component.Event {

        private final boolean userOriginated;

        /**
         *
         * @param source
         *            the grid where the event originated from
         * @param userOriginated
         *            <code>true</code> if event is a result of user
         *            interaction, <code>false</code> if from API call
         */
        public ColumnReorderEvent(Grid source, boolean userOriginated) {
            super(source);
            this.userOriginated = userOriginated;
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
     * An event that is fired when a column is resized, either programmatically
     * or by the user.
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
     * ContextClickEvent for the Grid Component.
     *
     * @param <T>
     *            the grid bean type
     */
    public static class GridContextClickEvent<T> extends ContextClickEvent {

        private final T item;
        private final int rowIndex;
        private final Column<T, ?> column;
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
                int rowIndex, T item, Column<T, ?> column) {
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
        public Column<T, ?> getColumn() {
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
     * A helper base class for creating extensions for the Grid component.
     *
     * @param <T>
     */
    public abstract static class AbstractGridExtension<T>
            extends AbstractListingExtension<T> {

        @Override
        public void extend(AbstractListing<T> grid) {
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

        @Override
        protected AbstractGridExtensionState getState() {
            return (AbstractGridExtensionState) super.getState();
        }

        @Override
        protected AbstractGridExtensionState getState(boolean markAsDirty) {
            return (AbstractGridExtensionState) super.getState(markAsDirty);
        }

        protected String getInternalIdForColumn(Column<T, ?> column) {
            return getParent().getInternalIdForColumn(column);
        }
    }

    private final class GridServerRpcImpl implements GridServerRpc {
        @Override
        public void sort(String[] columnInternalIds, SortDirection[] directions,
                boolean isUserOriginated) {

            assert columnInternalIds.length == directions.length : "Column and sort direction counts don't match.";

            List<GridSortOrder<T>> list = new ArrayList<>(directions.length);
            for (int i = 0; i < columnInternalIds.length; ++i) {
                Column<T, ?> column = columnKeys.get(columnInternalIds[i]);
                list.add(new GridSortOrder<>(column, directions[i]));
            }
            setSortOrder(list, isUserOriginated);
        }

        @Override
        public void itemClick(String rowKey, String columnInternalId,
                MouseEventDetails details) {
            Column<T, ?> column = getColumnByInternalId(columnInternalId);
            T item = getDataCommunicator().getKeyMapper().get(rowKey);
            fireEvent(new ItemClick<>(Grid.this, column, item, details));
        }

        @Override
        public void contextClick(int rowIndex, String rowKey,
                String columnInternalId, Section section,
                MouseEventDetails details) {
            T item = null;
            if (rowKey != null) {
                item = getDataCommunicator().getKeyMapper().get(rowKey);
            }
            fireEvent(new GridContextClickEvent<>(Grid.this, details, section,
                    rowIndex, item, getColumnByInternalId(columnInternalId)));
        }

        @Override
        public void columnsReordered(List<String> newColumnOrder,
                List<String> oldColumnOrder) {
            final String diffStateKey = "columnOrder";
            ConnectorTracker connectorTracker = getUI().getConnectorTracker();
            JsonObject diffState = connectorTracker.getDiffState(Grid.this);
            // discard the change if the columns have been reordered from
            // the server side, as the server side is always right
            if (getState(false).columnOrder.equals(oldColumnOrder)) {
                // Don't mark as dirty since client has the state already
                getState(false).columnOrder = newColumnOrder;
                // write changes to diffState so that possible reverting the
                // column order is sent to client
                assert diffState
                        .hasKey(diffStateKey) : "Field name has changed";
                Type type = null;
                try {
                    type = getState(false).getClass().getField(diffStateKey)
                            .getGenericType();
                } catch (NoSuchFieldException | SecurityException e) {
                    e.printStackTrace();
                }
                EncodeResult encodeResult = JsonCodec.encode(
                        getState(false).columnOrder, diffState, type,
                        connectorTracker);

                diffState.put(diffStateKey, encodeResult.getEncodedValue());
                fireColumnReorderEvent(true);
            } else {
                // make sure the client is reverted to the order that the
                // server thinks it is
                diffState.remove(diffStateKey);
                markAsDirty();
            }
        }

        @Override
        public void columnVisibilityChanged(String internalId, boolean hidden) {
            Column<T, ?> column = getColumnByInternalId(internalId);
            ColumnState columnState = column.getState(false);
            if (columnState.hidden != hidden) {
                columnState.hidden = hidden;
                fireColumnVisibilityChangeEvent(column, hidden, true);
            }
        }

        @Override
        public void columnResized(String internalId, double pixels) {
            final Column<T, ?> column = getColumnByInternalId(internalId);
            if (column != null && column.isResizable()) {
                column.getState().width = pixels;
                fireColumnResizeEvent(column, true);
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

        private final Set<T> visibleDetails = new HashSet<>();
        private final Map<T, Component> components = new HashMap<>();
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
        public void generateData(T item, JsonObject jsonObject) {
            if (generator == null || !visibleDetails.contains(item)) {
                return;
            }

            if (!components.containsKey(item)) {
                Component detailsComponent = generator.apply(item);
                Objects.requireNonNull(detailsComponent,
                        "Details generator can't create null components");
                if (detailsComponent.getParent() != null) {
                    throw new IllegalStateException(
                            "Details component was already attached");
                }
                addComponentToGrid(detailsComponent);
                components.put(item, detailsComponent);
            }

            jsonObject.put(GridState.JSONKEY_DETAILS_VISIBLE,
                    components.get(item).getConnectorId());
        }

        @Override
        public void destroyData(T item) {
            // No clean up needed. Components are removed when hiding details
            // and/or changing details generator
        }

        /**
         * Sets the visibility of details component for given item.
         *
         * @param item
         *            the item to show details for
         * @param visible
         *            {@code true} if details component should be visible;
         *            {@code false} if it should be hidden
         */
        public void setDetailsVisible(T item, boolean visible) {
            boolean refresh = false;
            if (!visible) {
                refresh = visibleDetails.remove(item);
                if (components.containsKey(item)) {
                    removeComponentFromGrid(components.remove(item));
                }
            } else {
                refresh = visibleDetails.add(item);
            }

            if (refresh) {
                refresh(item);
            }
        }

        /**
         * Returns the visibility of details component for given item.
         *
         * @param item
         *            the item to show details for
         *
         * @return {@code true} if details component should be visible;
         *         {@code false} if it should be hidden
         */
        public boolean isDetailsVisible(T item) {
            return visibleDetails.contains(item);
        }

        @Override
        public Grid<T> getParent() {
            return super.getParent();
        }

        @Override
        protected DetailsManagerState getState() {
            return (DetailsManagerState) super.getState();
        }

        @Override
        protected DetailsManagerState getState(boolean markAsDirty) {
            return (DetailsManagerState) super.getState(markAsDirty);
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
    public static class Column<T, V> extends AbstractExtension {

        private final ValueProvider<T, V> valueProvider;

        private SortOrderProvider sortOrderProvider = direction -> {
            String id = getId();
            if (id == null) {
                return Stream.empty();
            } else {
                return Stream.of(new QuerySortOrder(id, direction));
            }
        };

        private SerializableComparator<T> comparator;
        private StyleGenerator<T> styleGenerator = item -> null;
        private DescriptionGenerator<T> descriptionGenerator;
        private DataGenerator<T> dataGenerator = new DataGenerator<T>() {

            @Override
            public void generateData(T item, JsonObject jsonObject) {
                ColumnState state = getState(false);

                String communicationId = getConnectorId();

                assert communicationId != null : "No communication ID set for column "
                        + state.caption;

                @SuppressWarnings("unchecked")
                Renderer<V> renderer = (Renderer<V>) state.renderer;

                JsonObject obj = getDataObject(jsonObject,
                        DataCommunicatorConstants.DATA);

                V providerValue = valueProvider.apply(item);

                // Make Grid track components.
                if (renderer instanceof ComponentRenderer
                        && providerValue instanceof Component) {
                    addComponent(item, (Component) providerValue);
                }
                JsonValue rendererValue = renderer.encode(providerValue);

                obj.put(communicationId, rendererValue);

                String style = styleGenerator.apply(item);
                if (style != null && !style.isEmpty()) {
                    JsonObject styleObj = getDataObject(jsonObject,
                            GridState.JSONKEY_CELLSTYLES);
                    styleObj.put(communicationId, style);
                }
                if (descriptionGenerator != null) {
                    String description = descriptionGenerator.apply(item);
                    if (description != null && !description.isEmpty()) {
                        JsonObject descriptionObj = getDataObject(jsonObject,
                                GridState.JSONKEY_CELLDESCRIPTION);
                        descriptionObj.put(communicationId, description);
                    }
                }
            }

            @Override
            public void destroyData(T item) {
                removeComponent(item);
            }

            @Override
            public void destroyAllData() {
                // Make a defensive copy of keys, as the map gets cleared when
                // removing components.
                new HashSet<>(activeComponents.keySet())
                        .forEach(component -> removeComponent(component));
            }
        };

        private Binding<T, ?> editorBinding;
        private Map<T, Component> activeComponents = new HashMap<>();

        private String userId;

        /**
         * Constructs a new Column configuration with given renderer and value
         * provider.
         *
         * @param valueProvider
         *            the function to get values from items, not
         *            <code>null</code>
         * @param renderer
         *            the type of value, not <code>null</code>
         */
        protected Column(ValueProvider<T, V> valueProvider,
                Renderer<? super V> renderer) {
            Objects.requireNonNull(valueProvider,
                    "Value provider can't be null");
            Objects.requireNonNull(renderer, "Renderer can't be null");

            ColumnState state = getState();

            this.valueProvider = valueProvider;
            state.renderer = renderer;

            state.caption = "";

            // Add the renderer as a child extension of this extension, thus
            // ensuring the renderer will be unregistered when this column is
            // removed
            addExtension(renderer);

            Class<? super V> valueType = renderer.getPresentationType();

            if (Comparable.class.isAssignableFrom(valueType)) {
                comparator = (a, b) -> compareComparables(
                        valueProvider.apply(a), valueProvider.apply(b));
            } else if (Number.class.isAssignableFrom(valueType)) {
                /*
                 * Value type will be Number whenever using NumberRenderer.
                 * Provide explicit comparison support in this case even though
                 * Number itself isn't Comparable.
                 */
                comparator = (a, b) -> compareNumbers(
                        (Number) valueProvider.apply(a),
                        (Number) valueProvider.apply(b));
            } else {
                comparator = (a, b) -> compareMaybeComparables(
                        valueProvider.apply(a), valueProvider.apply(b));
            }
        }

        private static int compareMaybeComparables(Object a, Object b) {
            if (hasCommonComparableBaseType(a, b)) {
                return compareComparables(a, b);
            } else {
                return compareComparables(Objects.toString(a, ""),
                        Objects.toString(b, ""));
            }
        }

        private static boolean hasCommonComparableBaseType(Object a, Object b) {
            if (a instanceof Comparable<?> && b instanceof Comparable<?>) {
                Class<?> aClass = a.getClass();
                Class<?> bClass = b.getClass();

                if (aClass == bClass) {
                    return true;
                }

                Class<?> baseType = ReflectTools.findCommonBaseType(aClass,
                        bClass);
                if (Comparable.class.isAssignableFrom(baseType)) {
                    return true;
                }
            }
            if ((a == null && b instanceof Comparable<?>)
                    || (b == null && a instanceof Comparable<?>)) {
                return true;
            }

            return false;
        }

        @SuppressWarnings("unchecked")
        private static int compareComparables(Object a, Object b) {
            return ((Comparator) Comparator
                    .nullsLast(Comparator.naturalOrder())).compare(a, b);
        }

        @SuppressWarnings("unchecked")
        private static int compareNumbers(Number a, Number b) {
            Number valueA = a != null ? a : Double.POSITIVE_INFINITY;
            Number valueB = b != null ? b : Double.POSITIVE_INFINITY;
            // Most Number implementations are Comparable
            if (valueA instanceof Comparable
                    && valueA.getClass().isInstance(valueB)) {
                return ((Comparable<Number>) valueA).compareTo(valueB);
            } else if (valueA.equals(valueB)) {
                return 0;
            } else {
                // Fall back to comparing based on potentially truncated values
                int compare = Long.compare(valueA.longValue(),
                        valueB.longValue());
                if (compare == 0) {
                    // This might still produce 0 even though the values are not
                    // equals, but there's nothing more we can do about that
                    compare = Double.compare(valueA.doubleValue(),
                            valueB.doubleValue());
                }
                return compare;
            }
        }

        private void addComponent(T item, Component component) {
            if (activeComponents.containsKey(item)) {
                if (activeComponents.get(item).equals(component)) {
                    // Reusing old component
                    return;
                }
                removeComponent(item);
            }
            activeComponents.put(item, component);
            getGrid().addExtensionComponent(component);
        }

        private void removeComponent(T item) {
            Component component = activeComponents.remove(item);
            if (component != null) {
                getGrid().removeExtensionComponent(component);
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
        private String getInternalId() {
            return getState(false).internalId;
        }

        /**
         * Sets the identifier to use with this Column in communication.
         *
         * @param id
         *            the identifier string
         */
        private void setInternalId(String id) {
            Objects.requireNonNull(id, "Communication ID can't be null");
            getState().internalId = id;
        }

        /**
         * Returns the user-defined identifier for this column.
         *
         * @return the identifier string
         */
        public String getId() {
            return userId;
        }

        /**
         * Sets the user-defined identifier to map this column. The identifier
         * can be used for example in {@link Grid#getColumn(String)}.
         * <p>
         * The id is also used as the {@link #setSortProperty(String...) backend
         * sort property} for this column if no sort property or sort order
         * provider has been set for this column.
         *
         * @see #setSortProperty(String...)
         * @see #setSortOrderProvider(SortOrderProvider)
         *
         * @param id
         *            the identifier string
         * @return this column
         */
        public Column<T, V> setId(String id) {
            Objects.requireNonNull(id, "Column identifier cannot be null");
            if (this.userId != null) {
                throw new IllegalStateException(
                        "Column identifier cannot be changed");
            }
            this.userId = id;
            getGrid().setColumnId(id, this);

            return this;
        }

        /**
         * Gets the function used to produce the value for data in this column
         * based on the row item.
         *
         * @return the value provider function
         *
         * @since 8.0.3
         */
        public ValueProvider<T, V> getValueProvider() {
            return valueProvider;
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
         *            the header caption, not null
         *
         * @return this column
         */
        public Column<T, V> setCaption(String caption) {
            Objects.requireNonNull(caption, "Header caption can't be null");
            if (caption.equals(getState(false).caption)) {
                return this;
            }
            getState().caption = caption;

            HeaderRow row = getGrid().getDefaultHeaderRow();
            if (row != null) {
                row.getCell(this).setText(caption);
            }

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
        public Column<T, V> setComparator(
                SerializableComparator<T> comparator) {
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
        public SerializableComparator<T> getComparator(
                SortDirection sortDirection) {
            Objects.requireNonNull(comparator,
                    "No comparator defined for sorted column.");
            boolean reverse = sortDirection != SortDirection.ASCENDING;
            return reverse ? (t1, t2) -> comparator.reversed().compare(t1, t2)
                    : comparator;
        }

        /**
         * Sets strings describing back end properties to be used when sorting
         * this column.
         * <p>
         * By default, the {@link #setId(String) column id} will be used as the
         * sort property.
         *
         * @param properties
         *            the array of strings describing backend properties
         * @return this column
         */
        public Column<T, V> setSortProperty(String... properties) {
            Objects.requireNonNull(properties, "Sort properties can't be null");
            sortOrderProvider = dir -> Arrays.stream(properties)
                    .map(s -> new QuerySortOrder(s, dir));
            return this;
        }

        /**
         * Sets the sort orders when sorting this column. The sort order
         * provider is a function which provides {@link QuerySortOrder} objects
         * to describe how to sort by this column.
         * <p>
         * By default, the {@link #setId(String) column id} will be used as the
         * sort property.
         *
         * @param provider
         *            the function to use when generating sort orders with the
         *            given direction
         * @return this column
         */
        public Column<T, V> setSortOrderProvider(SortOrderProvider provider) {
            Objects.requireNonNull(provider,
                    "Sort order provider can't be null");
            sortOrderProvider = provider;
            return this;
        }

        /**
         * Gets the sort orders to use with back-end sorting for this column
         * when sorting in the given direction.
         *
         * @see #setSortProperty(String...)
         * @see #setId(String)
         * @see #setSortOrderProvider(SortOrderProvider)
         *
         * @param direction
         *            the sorting direction
         * @return stream of sort orders
         */
        public Stream<QuerySortOrder> getSortOrder(SortDirection direction) {
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
            getGrid().getDataCommunicator().reset();
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
            getGrid().getDataCommunicator().reset();
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
                getGrid().markAsDirty();
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
                getGrid().markAsDirty();
                getGrid().fireColumnResizeEvent(this, false);
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
                getGrid().markAsDirty();
                getGrid().fireColumnResizeEvent(this, false);
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
            getGrid().markAsDirty();
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
            getGrid().markAsDirty();
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
                getGrid().markAsDirty();
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
         * {@link #getCaption() header caption} is used.
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
                getGrid().fireColumnVisibilityChangeEvent(this, hidden, false);
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
         * Sets whether this Column has a component displayed in Editor or not.
         * A column can only be editable if an editor component or binding has
         * been set.
         *
         * @param editable
         *            {@code true} if column is editable; {@code false} if not
         * @return this column
         *
         * @see #setEditorComponent(HasValue, Setter)
         * @see #setEditorBinding(Binding)
         */
        public Column<T, V> setEditable(boolean editable) {
            Objects.requireNonNull(editorBinding,
                    "Column has no editor binding or component defined");
            getState().editable = editable;
            return this;
        }

        /**
         * Gets whether this Column has a component displayed in Editor or not.
         *
         * @return {@code true} if the column displays an editor component;
         *         {@code false} if not
         */
        public boolean isEditable() {
            return getState(false).editable;
        }

        /**
         * Sets an editor binding for this column. The {@link Binding} is used
         * when a row is in editor mode to define how to populate an editor
         * component based on the edited row and how to update an item based on
         * the value in the editor component.
         * <p>
         * To create a binding to use with a column, define a binding for the
         * editor binder (<code>grid.getEditor().getBinder()</code>) using e.g.
         * {@link Binder#forField(HasValue)}. You can also use
         * {@link #setEditorComponent(HasValue, Setter)} if no validator or
         * converter is needed for the binding.
         * <p>
         * The {@link HasValue} that the binding is defined to use must be a
         * {@link Component}.
         *
         * @param binding
         *            the binding to use for this column
         * @return this column
         *
         * @see #setEditorComponent(HasValue, Setter)
         * @see Binding
         * @see Grid#getEditor()
         * @see Editor#getBinder()
         */
        public Column<T, V> setEditorBinding(Binding<T, ?> binding) {
            Objects.requireNonNull(binding, "null is not a valid editor field");

            if (!(binding.getField() instanceof Component)) {
                throw new IllegalArgumentException(
                        "Binding target must be a component.");
            }

            this.editorBinding = binding;

            return setEditable(true);
        }

        /**
         * Gets the binder binding that is currently used for this column.
         *
         * @return the used binder binding, or <code>null</code> if no binding
         *         is configured
         *
         * @see #setEditorBinding(Binding)
         */
        public Binding<T, ?> getEditorBinding() {
            return editorBinding;
        }

        /**
         * Sets a component and setter to use for editing values of this column
         * in the editor row. This is a shorthand for use in simple cases where
         * no validator or converter is needed. Use
         * {@link #setEditorBinding(Binding)} to support more complex cases.
         * <p>
         * <strong>Note:</strong> The same component cannot be used for multiple
         * columns.
         *
         * @param editorComponent
         *            the editor component
         * @param setter
         *            a setter that stores the component value in the row item
         * @return this column
         *
         * @see #setEditorBinding(Binding)
         * @see Grid#getEditor()
         * @see Binder#bind(HasValue, ValueProvider, Setter)
         */
        public <C extends HasValue<V> & Component> Column<T, V> setEditorComponent(
                C editorComponent, Setter<T, V> setter) {
            Objects.requireNonNull(editorComponent,
                    "Editor component cannot be null");
            Objects.requireNonNull(setter, "Setter cannot be null");

            Binding<T, V> binding = getGrid().getEditor().getBinder()
                    .bind(editorComponent, valueProvider::apply, setter);

            return setEditorBinding(binding);
        }

        /**
         * Sets a component to use for editing values of this columns in the
         * editor row. This method can only be used if the column has an
         * {@link #setId(String) id} and the {@link Grid} has been created using
         * {@link Grid#Grid(Class)} or some other way that allows finding
         * properties based on property names.
         * <p>
         * This is a shorthand for use in simple cases where no validator or
         * converter is needed. Use {@link #setEditorBinding(Binding)} to
         * support more complex cases.
         * <p>
         * <strong>Note:</strong> The same component cannot be used for multiple
         * columns.
         *
         * @param editorComponent
         *            the editor component
         * @return this column
         *
         * @see #setEditorBinding(Binding)
         * @see Grid#getEditor()
         * @see Binder#bind(HasValue, String)
         * @see Grid#Grid(Class)
         */
        public <F, C extends HasValue<F> & Component> Column<T, V> setEditorComponent(
                C editorComponent) {
            Objects.requireNonNull(editorComponent,
                    "Editor component cannot be null");

            String propertyName = getId();
            if (propertyName == null) {
                throw new IllegalStateException(
                        "setEditorComponent without a setter can only be used if the column has an id. "
                                + "Use another setEditorComponent(Component, Setter) or setEditorBinding(Binding) instead.");
            }

            Binding<T, F> binding = getGrid().getEditor().getBinder()
                    .bind(editorComponent, propertyName);

            return setEditorBinding(binding);
        }

        /**
         * Sets the Renderer for this Column. Setting the renderer will cause
         * all currently available row data to be recreated and sent to the
         * client.
         *
         * @param renderer
         *            the new renderer
         * @return this column
         *
         * @since 8.0.3
         */
        public Column<T, V> setRenderer(Renderer<? super V> renderer) {
            Objects.requireNonNull(renderer, "Renderer can't be null");

            // Remove old renderer
            Connector oldRenderer = getState().renderer;
            if (oldRenderer != null && oldRenderer instanceof Extension) {
                removeExtension((Extension) oldRenderer);
            }

            // Set new renderer
            getState().renderer = renderer;
            addExtension(renderer);

            // Trigger redraw
            getGrid().getDataCommunicator().reset();

            return this;
        }

        /**
         * Gets the grid that this column belongs to.
         *
         * @return the grid that this column belongs to, or <code>null</code> if
         *         this column has not yet been associated with any grid
         */
        protected Grid<T> getGrid() {
            return (Grid<T>) getParent();
        }

        /**
         * Checks if column is attached and throws an
         * {@link IllegalStateException} if it is not.
         *
         * @throws IllegalStateException
         *             if the column is no longer attached to any grid
         */
        protected void checkColumnIsAttached() throws IllegalStateException {
            if (getGrid() == null) {
                throw new IllegalStateException(
                        "Column is no longer attached to a grid.");
            }
        }

        /**
         * Writes the design attributes for this column into given element.
         *
         * @since 7.5.0
         *
         * @param element
         *            Element to write attributes into
         *
         * @param designContext
         *            the design context
         */
        protected void writeDesign(Element element,
                DesignContext designContext) {
            Attributes attributes = element.attributes();

            ColumnState defaultState = new ColumnState();

            if (getId() == null) {
                setId("column" + getGrid().getColumns().indexOf(this));
            }

            DesignAttributeHandler.writeAttribute("column-id", attributes,
                    getId(), null, String.class, designContext);

            // Sortable is a special attribute that depends on the data
            // provider.
            DesignAttributeHandler.writeAttribute("sortable", attributes,
                    isSortable(), null, boolean.class, designContext);
            DesignAttributeHandler.writeAttribute("editable", attributes,
                    isEditable(), defaultState.editable, boolean.class,
                    designContext);
            DesignAttributeHandler.writeAttribute("resizable", attributes,
                    isResizable(), defaultState.resizable, boolean.class,
                    designContext);

            DesignAttributeHandler.writeAttribute("hidable", attributes,
                    isHidable(), defaultState.hidable, boolean.class,
                    designContext);
            DesignAttributeHandler.writeAttribute("hidden", attributes,
                    isHidden(), defaultState.hidden, boolean.class,
                    designContext);
            DesignAttributeHandler.writeAttribute("hiding-toggle-caption",
                    attributes, getHidingToggleCaption(),
                    defaultState.hidingToggleCaption, String.class,
                    designContext);

            DesignAttributeHandler.writeAttribute("width", attributes,
                    getWidth(), defaultState.width, Double.class,
                    designContext);
            DesignAttributeHandler.writeAttribute("min-width", attributes,
                    getMinimumWidth(), defaultState.minWidth, Double.class,
                    designContext);
            DesignAttributeHandler.writeAttribute("max-width", attributes,
                    getMaximumWidth(), defaultState.maxWidth, Double.class,
                    designContext);
            DesignAttributeHandler.writeAttribute("expand", attributes,
                    getExpandRatio(), defaultState.expandRatio, Integer.class,
                    designContext);
        }

        /**
         * Reads the design attributes for this column from given element.
         *
         * @since 7.5.0
         * @param design
         *            Element to read attributes from
         * @param designContext
         *            the design context
         */
        @SuppressWarnings("unchecked")
        protected void readDesign(Element design, DesignContext designContext) {
            Attributes attributes = design.attributes();

            if (design.hasAttr("sortable")) {
                setSortable(DesignAttributeHandler.readAttribute("sortable",
                        attributes, boolean.class));
            } else {
                setSortable(false);
            }
            if (design.hasAttr("editable")) {
                /*
                 * This is a fake editor just to have something (otherwise
                 * "setEditable" throws an exception.
                 *
                 * Let's use TextField here because we support only Strings as
                 * inline data type. It will work incorrectly for other types
                 * but we don't support them anyway.
                 */
                setEditorComponent((HasValue<V> & Component) new TextField(),
                        (item, value) -> {
                            // Ignore user value since we don't know the setter
                        });
                setEditable(DesignAttributeHandler.readAttribute("editable",
                        attributes, boolean.class));
            }
            if (design.hasAttr("resizable")) {
                setResizable(DesignAttributeHandler.readAttribute("resizable",
                        attributes, boolean.class));
            }

            if (design.hasAttr("hidable")) {
                setHidable(DesignAttributeHandler.readAttribute("hidable",
                        attributes, boolean.class));
            }
            if (design.hasAttr("hidden")) {
                setHidden(DesignAttributeHandler.readAttribute("hidden",
                        attributes, boolean.class));
            }
            if (design.hasAttr("hiding-toggle-caption")) {
                setHidingToggleCaption(DesignAttributeHandler.readAttribute(
                        "hiding-toggle-caption", attributes, String.class));
            }

            // Read size info where necessary.
            if (design.hasAttr("width")) {
                setWidth(DesignAttributeHandler.readAttribute("width",
                        attributes, Double.class));
            }
            if (design.hasAttr("min-width")) {
                setMinimumWidth(DesignAttributeHandler
                        .readAttribute("min-width", attributes, Double.class));
            }
            if (design.hasAttr("max-width")) {
                setMaximumWidth(DesignAttributeHandler
                        .readAttribute("max-width", attributes, Double.class));
            }
            if (design.hasAttr("expand")) {
                if (design.attr("expand").isEmpty()) {
                    setExpandRatio(1);
                } else {
                    setExpandRatio(DesignAttributeHandler.readAttribute(
                            "expand", attributes, Integer.class));
                }
            }
        }

        /**
         * Gets the DataGenerator for this Column.
         * 
         * @return data generator
         */
        private DataGenerator<T> getDataGenerator() {
            return dataGenerator;
        }
    }

    private class HeaderImpl extends Header {

        @Override
        protected Grid<T> getGrid() {
            return Grid.this;
        }

        @Override
        protected SectionState getState(boolean markAsDirty) {
            return Grid.this.getState(markAsDirty).header;
        }

        @Override
        protected Column<?, ?> getColumnByInternalId(String internalId) {
            return getGrid().getColumnByInternalId(internalId);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected String getInternalIdForColumn(Column<?, ?> column) {
            return getGrid().getInternalIdForColumn((Column<T, ?>) column);
        }
    };

    private class FooterImpl extends Footer {

        @Override
        protected Grid<T> getGrid() {
            return Grid.this;
        }

        @Override
        protected SectionState getState(boolean markAsDirty) {
            return Grid.this.getState(markAsDirty).footer;
        }

        @Override
        protected Column<?, ?> getColumnByInternalId(String internalId) {
            return getGrid().getColumnByInternalId(internalId);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected String getInternalIdForColumn(Column<?, ?> column) {
            return getGrid().getInternalIdForColumn((Column<T, ?>) column);
        }
    };

    private final Set<Column<T, ?>> columnSet = new LinkedHashSet<>();
    private final Map<String, Column<T, ?>> columnKeys = new HashMap<>();
    private final Map<String, Column<T, ?>> columnIds = new HashMap<>();

    private final List<GridSortOrder<T>> sortOrder = new ArrayList<>();
    private final DetailsManager<T> detailsManager;
    private final Set<Component> extensionComponents = new HashSet<>();
    private StyleGenerator<T> styleGenerator = item -> null;
    private DescriptionGenerator<T> descriptionGenerator;

    private final Header header = new HeaderImpl();
    private final Footer footer = new FooterImpl();

    private int counter = 0;

    private GridSelectionModel<T> selectionModel;

    private Editor<T> editor;

    private PropertySet<T> propertySet;

    private Class<T> beanType = null;

    /**
     * Creates a new grid without support for creating columns based on property
     * names. Use an alternative constructor, such as {@link Grid#Grid(Class)},
     * to create a grid that automatically sets up columns based on the type of
     * presented data.
     *
     * @see #Grid(Class)
     * @see #withPropertySet(PropertySet)
     */
    public Grid() {
        this(new DataCommunicator<>());
    }

    /**
     * Creates a new grid that uses reflection based on the provided bean type
     * to automatically set up an initial set of columns. All columns will be
     * configured using the same {@link Object#toString()} renderer that is used
     * by {@link #addColumn(ValueProvider)}.
     *
     * @param beanType
     *            the bean type to use, not <code>null</code>
     * @see #Grid()
     * @see #withPropertySet(PropertySet)
     */
    public Grid(Class<T> beanType) {
        this(BeanPropertySet.get(beanType));
        this.beanType = beanType;
    }

    /**
     * Creates a new grid with the given data communicator and without support
     * for creating columns based on property names.
     *
     * @param dataCommunicator
     *            the custom data communicator to set
     * @see #Grid()
     * @see #Grid(PropertySet, DataCommunicator)
     * @since 8.1
     */
    protected Grid(DataCommunicator<T> dataCommunicator) {
        this(new PropertySet<T>() {
            @Override
            public Stream<PropertyDefinition<T, ?>> getProperties() {
                // No columns configured by default
                return Stream.empty();
            }

            @Override
            public Optional<PropertyDefinition<T, ?>> getProperty(String name) {
                throw new IllegalStateException(
                        "A Grid created without a bean type class literal or a custom property set"
                                + " doesn't support finding properties by name.");
            }
        }, dataCommunicator);
    }

    /**
     * Creates a grid using a custom {@link PropertySet} implementation for
     * configuring the initial columns and resolving property names for
     * {@link #addColumn(String)} and
     * {@link Column#setEditorComponent(HasValue)}.
     *
     * @see #withPropertySet(PropertySet)
     *
     * @param propertySet
     *            the property set implementation to use, not <code>null</code>.
     */
    protected Grid(PropertySet<T> propertySet) {
        this(propertySet, new DataCommunicator<>());
    }

    /**
     * Creates a grid using a custom {@link PropertySet} implementation and
     * custom data communicator.
     * <p>
     * Property set is used for configuring the initial columns and resolving
     * property names for {@link #addColumn(String)} and
     * {@link Column#setEditorComponent(HasValue)}.
     *
     * @see #withPropertySet(PropertySet)
     *
     * @param propertySet
     *            the property set implementation to use, not <code>null</code>.
     * @param dataCommunicator
     *            the data communicator to use, not<code>null</code>
     * @since 8.1
     */
    protected Grid(PropertySet<T> propertySet,
            DataCommunicator<T> dataCommunicator) {
        super(dataCommunicator);
        registerRpc(new GridServerRpcImpl());
        setDefaultHeaderRow(appendHeaderRow());
        setSelectionModel(new SingleSelectionModelImpl<>());

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

        setPropertySet(propertySet);

        // Automatically add columns for all available properties
        propertySet.getProperties().map(PropertyDefinition::getName)
                .forEach(this::addColumn);
    }

    /**
     * Sets the property set to use for this grid. Does not create or update
     * columns in any way but will delete and re-create the editor.
     * <p>
     * This is only meant to be called from constructors and readDesign, at a
     * stage where it does not matter if you throw away the editor.
     *
     * @param propertySet
     *            the property set to use
     *
     * @since 8.0.3
     */
    protected void setPropertySet(PropertySet<T> propertySet) {
        Objects.requireNonNull(propertySet, "propertySet cannot be null");
        this.propertySet = propertySet;

        if (editor instanceof Extension) {
            removeExtension((Extension) editor);
        }
        editor = createEditor();
        if (editor instanceof Extension) {
            addExtension((Extension) editor);
        }

    }

    /**
     * Creates a grid using a custom {@link PropertySet} implementation for
     * creating a default set of columns and for resolving property names with
     * {@link #addColumn(String)} and
     * {@link Column#setEditorComponent(HasValue)}.
     * <p>
     * This functionality is provided as static method instead of as a public
     * constructor in order to make it possible to use a custom property set
     * without creating a subclass while still leaving the public constructors
     * focused on the common use cases.
     *
     * @see Grid#Grid()
     * @see Grid#Grid(Class)
     *
     * @param propertySet
     *            the property set implementation to use, not <code>null</code>.
     * @return a new grid using the provided property set, not <code>null</code>
     */
    public static <BEAN> Grid<BEAN> withPropertySet(
            PropertySet<BEAN> propertySet) {
        return new Grid<>(propertySet);
    }

    /**
     * Creates a new {@code Grid} using the given caption
     *
     * @param caption
     *            the caption of the grid
     */
    public Grid(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new {@code Grid} using the given caption and
     * {@code DataProvider}
     *
     * @param caption
     *            the caption of the grid
     * @param dataProvider
     *            the data provider, not {@code null}
     */
    public Grid(String caption, DataProvider<T, ?> dataProvider) {
        this(caption);
        setDataProvider(dataProvider);
    }

    /**
     * Creates a new {@code Grid} using the given {@code DataProvider}
     *
     * @param dataProvider
     *            the data provider, not {@code null}
     */
    public Grid(DataProvider<T, ?> dataProvider) {
        this();
        setDataProvider(dataProvider);
    }

    /**
     * Creates a new {@code Grid} using the given caption and collection of
     * items
     *
     * @param caption
     *            the caption of the grid
     * @param items
     *            the data items to use, not {@çode null}
     */
    public Grid(String caption, Collection<T> items) {
        this(caption, DataProvider.ofCollection(items));
    }

    /**
     * Gets the bean type used by this grid.
     * <p>
     * The bean type is used to automatically set up a column added using a
     * property name.
     *
     * @return the used bean type or <code>null</code> if no bean type has been
     *         defined
     *
     * @since 8.0.3
     */
    public Class<T> getBeanType() {
        return beanType;
    }

    public <V> void fireColumnVisibilityChangeEvent(Column<T, V> column,
            boolean hidden, boolean userOriginated) {
        fireEvent(new ColumnVisibilityChangeEvent(this, column, hidden,
                userOriginated));
    }

    /**
     * Adds a new column with the given property name. The column will use a
     * {@link TextRenderer}. The value is converted to a String using
     * {@link Object#toString()}. The property name will be used as the
     * {@link Column#getId() column id} and the {@link Column#getCaption()
     * column caption} will be set based on the property definition.
     * <p>
     * This method can only be used for a <code>Grid</code> created using
     * {@link Grid#Grid(Class)} or {@link #withPropertySet(PropertySet)}.
     *
     * @param propertyName
     *            the property name of the new column, not <code>null</code>
     * @return the newly added column, not <code>null</code>
     */
    public Column<T, ?> addColumn(String propertyName) {
        return addColumn(propertyName, new TextRenderer());
    }

    /**
     * Adds a new column with the given property name and renderer. The property
     * name will be used as the {@link Column#getId() column id} and the
     * {@link Column#getCaption() column caption} will be set based on the
     * property definition.
     * <p>
     * This method can only be used for a <code>Grid</code> created using
     * {@link Grid#Grid(Class)} or {@link #withPropertySet(PropertySet)}.
     *
     * @param propertyName
     *            the property name of the new column, not <code>null</code>
     * @param renderer
     *            the renderer to use, not <code>null</code>
     * @return the newly added column, not <code>null</code>
     */
    public Column<T, ?> addColumn(String propertyName,
            AbstractRenderer<? super T, ?> renderer) {
        Objects.requireNonNull(propertyName, "Property name cannot be null");
        Objects.requireNonNull(renderer, "Renderer cannot be null");

        if (getColumn(propertyName) != null) {
            throw new IllegalStateException(
                    "There is already a column for " + propertyName);
        }

        PropertyDefinition<T, ?> definition = propertySet
                .getProperty(propertyName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Could not resolve property name " + propertyName
                                + " from " + propertySet));

        if (!renderer.getPresentationType()
                .isAssignableFrom(definition.getType())) {
            throw new IllegalArgumentException(renderer.toString()
                    + " cannot be used with a property of type "
                    + definition.getType().getName());
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Column<T, ?> column = addColumn(definition.getGetter(),
                (AbstractRenderer) renderer).setId(definition.getName())
                        .setCaption(definition.getCaption());

        return column;
    }

    /**
     * Adds a new text column to this {@link Grid} with a value provider. The
     * column will use a {@link TextRenderer}. The value is converted to a
     * String using {@link Object#toString()}. In-memory sorting will use the
     * natural ordering of elements if they are mutually comparable and
     * otherwise fall back to comparing the string representations of the
     * values.
     *
     * @param valueProvider
     *            the value provider
     *
     * @return the new column
     */
    public <V> Column<T, V> addColumn(ValueProvider<T, V> valueProvider) {
        return addColumn(valueProvider, new TextRenderer());
    }

    /**
     * Adds a new column to this {@link Grid} with typed renderer and value
     * provider.
     *
     * @param valueProvider
     *            the value provider
     * @param renderer
     *            the column value class
     * @param <V>
     *            the column value type
     *
     * @return the new column
     *
     * @see AbstractRenderer
     */
    public <V> Column<T, V> addColumn(ValueProvider<T, V> valueProvider,
            AbstractRenderer<? super T, ? super V> renderer) {
        String generatedIdentifier = getGeneratedIdentifier();
        Column<T, V> column = createColumn(valueProvider, renderer);
        addColumn(generatedIdentifier, column);
        return column;
    }

    /**
     * Adds a column that shows components.
     * <p>
     * This is a shorthand for {@link #addColum()} with a
     * {@link ComponentRenderer}.
     *
     * @param componentProvider
     *            a value provider that will return a component for the given
     *            item
     * @return the new column
     * @param <V>
     *            the column value type, extends component
     * @since 8.1
     */
    public <V extends Component> Column<T, V> addComponentColumn(
            ValueProvider<T, V> componentProvider) {
        return addColumn(componentProvider, new ComponentRenderer());
    }

    /**
     * Creates a column instance from a value provider and a renderer.
     *
     * @param valueProvider
     *            the value provider
     * @param renderer
     *            the renderer
     * @return a new column instance
     * @param <V>
     *            the column value type
     *
     * @since 8.0.3
     */
    protected <V> Column<T, V> createColumn(ValueProvider<T, V> valueProvider,
            AbstractRenderer<? super T, ? super V> renderer) {
        return new Column<>(valueProvider, renderer);
    }

    private void addColumn(String identifier, Column<T, ?> column) {
        if (getColumns().contains(column)) {
            return;
        }

        column.extend(this);
        columnSet.add(column);
        columnKeys.put(identifier, column);
        column.setInternalId(identifier);
        addDataGenerator(column.getDataGenerator());

        getState().columnOrder.add(identifier);
        getHeader().addColumn(identifier);
        getFooter().addColumn(identifier);

        if (getDefaultHeaderRow() != null) {
            getDefaultHeaderRow().getCell(column).setText(column.getCaption());
        }
    }

    /**
     * Removes the given column from this {@link Grid}.
     *
     * @param column
     *            the column to remove
     */
    public void removeColumn(Column<T, ?> column) {
        if (columnSet.remove(column)) {
            String columnId = column.getInternalId();
            int displayIndex = getState(false).columnOrder.indexOf(columnId);
            assert displayIndex != -1 : "Tried to remove a column which is not included in columnOrder. This should not be possible as all columns should be in columnOrder.";
            columnKeys.remove(columnId);
            columnIds.remove(column.getId());
            column.remove();
            removeDataGenerator(column.getDataGenerator());
            getHeader().removeColumn(columnId);
            getFooter().removeColumn(columnId);
            getState(true).columnOrder.remove(columnId);

            if (displayIndex < getFrozenColumnCount()) {
                setFrozenColumnCount(getFrozenColumnCount() - 1);
            }
        }
    }

    /**
     * Removes the column with the given column id.
     *
     * @see #removeColumn(Column)
     * @see Column#setId(String)
     *
     * @param columnId
     *            the id of the column to remove, not <code>null</code>
     */
    public void removeColumn(String columnId) {
        removeColumn(getColumnOrThrow(columnId));
    }

    /**
     * Removes all columns from this Grid.
     *
     * @since 8.0.2
     */
    public void removeAllColumns() {
        for (Column<T, ?> column : getColumns()) {
            removeColumn(column);
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
     * @param item
     *            the item to show details for
     * @param visible
     *            {@code true} if details component should be visible;
     *            {@code false} if it should be hidden
     */
    public void setDetailsVisible(T item, boolean visible) {
        detailsManager.setDetailsVisible(item, visible);
    }

    /**
     * Returns the visibility of details component for given item.
     *
     * @param item
     *            the item to show details for
     *
     * @return {@code true} if details component should be visible;
     *         {@code false} if it should be hidden
     */
    public boolean isDetailsVisible(T item) {
        return detailsManager.isDetailsVisible(item);
    }

    /**
     * Gets an unmodifiable collection of all columns currently in this
     * {@link Grid}.
     *
     * @return unmodifiable collection of columns
     */
    public List<Column<T, ?>> getColumns() {
        return Collections.unmodifiableList(getState(false).columnOrder.stream()
                .map(columnKeys::get).collect(Collectors.toList()));
    }

    /**
     * Gets a {@link Column} of this grid by its identifying string.
     *
     * @see Column#setId(String)
     *
     * @param columnId
     *            the identifier of the column to get
     * @return the column corresponding to the given column identifier, or
     *         <code>null</code> if there is no such column
     */
    public Column<T, ?> getColumn(String columnId) {
        return columnIds.get(columnId);
    }

    private Column<T, ?> getColumnOrThrow(String columnId) {
        Objects.requireNonNull(columnId, "Column id cannot be null");
        Column<T, ?> column = getColumn(columnId);
        if (column == null) {
            throw new IllegalStateException(
                    "There is no column with the id " + columnId);
        }
        return column;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note that the order of the returned components it not specified.
     */
    @Override
    public Iterator<Component> iterator() {
        Set<Component> componentSet = new LinkedHashSet<>(extensionComponents);
        Header header = getHeader();
        for (int i = 0; i < header.getRowCount(); ++i) {
            HeaderRow row = header.getRow(i);
            componentSet.addAll(row.getComponents());
        }
        Footer footer = getFooter();
        for (int i = 0; i < footer.getRowCount(); ++i) {
            FooterRow row = footer.getRow(i);
            componentSet.addAll(row.getComponents());
        }
        return Collections.unmodifiableSet(componentSet).iterator();
    }

    /**
     * Sets the number of frozen columns in this grid. Setting the count to 0
     * means that no data columns will be frozen, but the built-in selection
     * checkbox column will still be frozen if it's in use. Setting the count to
     * -1 will also disable the selection column.
     * <p>
     * <em>NOTE:</em> this count includes {@link Column#isHidden() hidden
     * columns} in the count.
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
     * Sets the height of a row. If -1 (default), the row height is calculated
     * based on the theme for an empty row before the Grid is displayed.
     * <p>
     * Note that all header, body and footer rows get the same height if
     * explicitly set. In automatic mode, each section is calculated separately
     * based on an empty row of that type.
     *
     * @param rowHeight
     *            The height of a row in pixels or -1 for automatic calculation
     */
    public void setRowHeight(double rowHeight) {
        getState().rowHeight = rowHeight;
    }

    /**
     * Returns the currently explicitly set row height or -1 if automatic.
     *
     * @return explicitly set row height in pixels or -1 if in automatic mode
     */
    public double getRowHeight() {
        return getState(false).rowHeight;
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
     * @param index
     *            the index of the row, where the topmost row has index zero
     * @return the header row at the index
     * @throws IndexOutOfBoundsException
     *             if {@code rowIndex < 0 || rowIndex >= getHeaderRowCount()}
     */
    public HeaderRow getHeaderRow(int index) {
        return getHeader().getRow(index);
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
     * Removes the given row from the header section. Removing a default row
     * sets the Grid to have no default row.
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
     * @param index
     *            the index of the row to remove, where the topmost row has
     *            index zero
     *
     * @throws IndexOutOfBoundsException
     *             if {@code index < 0 || index >= getHeaderRowCount()}
     *
     * @see #removeHeaderRow(HeaderRow)
     * @see #addHeaderRowAt(int)
     * @see #appendHeaderRow()
     * @see #prependHeaderRow()
     */
    public void removeHeaderRow(int index) {
        getHeader().removeRow(index);
    }

    /**
     * Returns the current default row of the header.
     *
     * @return the default row or null if no default row set
     *
     * @see #setDefaultHeaderRow(HeaderRow)
     */
    public HeaderRow getDefaultHeaderRow() {
        return header.getDefaultRow();
    }

    /**
     * Sets the default row of the header. The default row is a special header
     * row that displays column captions and sort indicators. By default Grid
     * has a single row which is also the default row. When a header row is set
     * as the default row, any existing cell content is replaced by the column
     * captions.
     *
     * @param row
     *            the new default row, or null for no default row
     *
     * @throws IllegalArgumentException
     *             if the header does not contain the row
     */
    public void setDefaultHeaderRow(HeaderRow row) {
        header.setDefaultRow((Row) row);
    }

    /**
     * Returns the header section of this grid. The default header contains a
     * single row, set as the {@linkplain #setDefaultHeaderRow(HeaderRow)
     * default row}.
     *
     * @return the header section
     */
    protected Header getHeader() {
        return header;
    }

    /**
     * Returns the footer row at the given index.
     *
     * @param index
     *            the index of the row, where the topmost row has index zero
     * @return the footer row at the index
     * @throws IndexOutOfBoundsException
     *             if {@code rowIndex < 0 || rowIndex >= getFooterRowCount()}
     */
    public FooterRow getFooterRow(int index) {
        return getFooter().getRow(index);
    }

    /**
     * Gets the number of rows in the footer section.
     *
     * @return the number of footer rows
     */
    public int getFooterRowCount() {
        return getFooter().getRowCount();
    }

    /**
     * Inserts a new row at the given position to the footer section. Shifts the
     * row currently at that position and any subsequent rows down (adds one to
     * their indices). Inserting at {@link #getFooterRowCount()} appends the row
     * at the bottom of the footer.
     *
     * @param index
     *            the index at which to insert the row, where the topmost row
     *            has index zero
     * @return the inserted footer row
     *
     * @throws IndexOutOfBoundsException
     *             if {@code rowIndex < 0 || rowIndex > getFooterRowCount()}
     *
     * @see #appendFooterRow()
     * @see #prependFooterRow()
     * @see #removeFooterRow(FooterRow)
     * @see #removeFooterRow(int)
     */
    public FooterRow addFooterRowAt(int index) {
        return getFooter().addRowAt(index);
    }

    /**
     * Adds a new row at the bottom of the footer section.
     *
     * @return the appended footer row
     *
     * @see #prependFooterRow()
     * @see #addFooterRowAt(int)
     * @see #removeFooterRow(FooterRow)
     * @see #removeFooterRow(int)
     */
    public FooterRow appendFooterRow() {
        return addFooterRowAt(getFooterRowCount());
    }

    /**
     * Adds a new row at the top of the footer section.
     *
     * @return the prepended footer row
     *
     * @see #appendFooterRow()
     * @see #addFooterRowAt(int)
     * @see #removeFooterRow(FooterRow)
     * @see #removeFooterRow(int)
     */
    public FooterRow prependFooterRow() {
        return addFooterRowAt(0);
    }

    /**
     * Removes the given row from the footer section. Removing a default row
     * sets the Grid to have no default row.
     *
     * @param row
     *            the footer row to be removed, not null
     *
     * @throws IllegalArgumentException
     *             if the footer does not contain the row
     *
     * @see #removeFooterRow(int)
     * @see #addFooterRowAt(int)
     * @see #appendFooterRow()
     * @see #prependFooterRow()
     */
    public void removeFooterRow(FooterRow row) {
        getFooter().removeRow(row);
    }

    /**
     * Removes the row at the given position from the footer section.
     *
     * @param index
     *            the index of the row to remove, where the topmost row has
     *            index zero
     *
     * @throws IndexOutOfBoundsException
     *             if {@code index < 0 || index >= getFooterRowCount()}
     *
     * @see #removeFooterRow(FooterRow)
     * @see #addFooterRowAt(int)
     * @see #appendFooterRow()
     * @see #prependFooterRow()
     */
    public void removeFooterRow(int index) {
        getFooter().removeRow(index);
    }

    /**
     * Returns the footer section of this grid.
     *
     * @return the footer section
     */
    protected Footer getFooter() {
        return footer;
    }

    /**
     * Registers a new column reorder listener.
     *
     * @param listener
     *            the listener to register, not null
     * @return a registration for the listener
     */
    public Registration addColumnReorderListener(
            ColumnReorderListener listener) {
        return addListener(ColumnReorderEvent.class, listener,
                COLUMN_REORDER_METHOD);
    }

    /**
     * Registers a new column resize listener.
     *
     * @param listener
     *            the listener to register, not null
     * @return a registration for the listener
     */
    public Registration addColumnResizeListener(ColumnResizeListener listener) {
        return addListener(ColumnResizeEvent.class, listener,
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
        return addListener(GridConstants.ITEM_CLICK_EVENT_ID, ItemClick.class,
                listener, ITEM_CLICK_METHOD);
    }

    /**
     * Registers a new column visibility change listener.
     *
     * @param listener
     *            the listener to register, not null
     * @return a registration for the listener
     */
    public Registration addColumnVisibilityChangeListener(
            ColumnVisibilityChangeListener listener) {
        return addListener(ColumnVisibilityChangeEvent.class, listener,
                COLUMN_VISIBILITY_METHOD);
    }

    /**
     * Returns whether column reordering is allowed. Default value is
     * <code>false</code>.
     *
     * @return true if reordering is allowed
     */
    public boolean isColumnReorderingAllowed() {
        return getState(false).columnReorderingAllowed;
    }

    /**
     * Sets whether or not column reordering is allowed. Default value is
     * <code>false</code>.
     *
     * @param columnReorderingAllowed
     *            specifies whether column reordering is allowed
     */
    public void setColumnReorderingAllowed(boolean columnReorderingAllowed) {
        if (isColumnReorderingAllowed() != columnReorderingAllowed) {
            getState().columnReorderingAllowed = columnReorderingAllowed;
        }
    }

    /**
     * Sets the columns and their order based on their column ids. Columns
     * currently in this grid that are not present in the list of column ids are
     * removed. This includes any column that has no id. Similarly, any new
     * column in columns will be added to this grid. New columns can only be
     * added for a <code>Grid</code> created using {@link Grid#Grid(Class)} or
     * {@link #withPropertySet(PropertySet)}.
     *
     *
     * @param columnIds
     *            the column ids to set
     *
     * @see Column#setId(String)
     */
    public void setColumns(String... columnIds) {
        // Must extract to an explicitly typed variable because otherwise javac
        // cannot determine which overload of setColumnOrder to use
        Column<T, ?>[] newColumnOrder = Stream.of(columnIds)
                .map((Function<String, Column<T, ?>>) id -> {
                    Column<T, ?> column = getColumn(id);
                    if (column == null) {
                        column = addColumn(id);
                    }
                    return column;
                }).toArray(Column[]::new);
        setColumnOrder(newColumnOrder);

        // The columns to remove are now at the end of the column list
        getColumns().stream().skip(columnIds.length)
                .forEach(this::removeColumn);
    }

    private String getIdentifier(Column<T, ?> column) {
        return columnKeys.entrySet().stream()
                .filter(entry -> entry.getValue().equals(column))
                .map(entry -> entry.getKey()).findFirst()
                .orElse(getGeneratedIdentifier());
    }

    private String getGeneratedIdentifier() {
        String columnId = "" + counter;
        counter++;
        return columnId;
    }

    /**
     * Sets a new column order for the grid. All columns which are not ordered
     * here will remain in the order they were before as the last columns of
     * grid.
     *
     * @param columns
     *            the columns in the order they should be
     */
    public void setColumnOrder(Column<T, ?>... columns) {
        setColumnOrder(Stream.of(columns));
    }

    private void setColumnOrder(Stream<Column<T, ?>> columns) {
        List<String> columnOrder = new ArrayList<>();
        columns.forEach(column -> {
            if (columnSet.contains(column)) {
                columnOrder.add(column.getInternalId());
            } else {
                throw new IllegalStateException(
                        "setColumnOrder should not be called "
                                + "with columns that are not in the grid.");
            }

        });

        List<String> stateColumnOrder = getState().columnOrder;
        if (stateColumnOrder.size() != columnOrder.size()) {
            stateColumnOrder.removeAll(columnOrder);
            columnOrder.addAll(stateColumnOrder);
        }

        getState().columnOrder = columnOrder;
        fireColumnReorderEvent(false);
    }

    /**
     * Sets a new column order for the grid based on their column ids. All
     * columns which are not ordered here will remain in the order they were
     * before as the last columns of grid.
     *
     * @param columnIds
     *            the column ids in the order they should be
     *
     * @see Column#setId(String)
     */
    public void setColumnOrder(String... columnIds) {
        setColumnOrder(Stream.of(columnIds).map(this::getColumnOrThrow));
    }

    /**
     * Returns the selection model for this grid.
     *
     * @return the selection model, not null
     */
    public GridSelectionModel<T> getSelectionModel() {
        assert selectionModel != null : "No selection model set by "
                + getClass().getName() + " constructor";
        return selectionModel;
    }

    /**
     * Use this grid as a single select in {@link Binder}.
     * <p>
     * Throws {@link IllegalStateException} if the grid is not using a
     * {@link SingleSelectionModel}.
     *
     * @return the single select wrapper that can be used in binder
     * @throws IllegalStateException
     *             if not using a single selection model
     */
    public SingleSelect<T> asSingleSelect() {
        GridSelectionModel<T> model = getSelectionModel();
        if (!(model instanceof SingleSelectionModel)) {
            throw new IllegalStateException(
                    "Grid is not in single select mode, it needs to be explicitly set to such with setSelectionModel(SingleSelectionModel) before being able to use single selection features.");
        }

        return ((SingleSelectionModel<T>) model).asSingleSelect();
    }

    public Editor<T> getEditor() {
        return editor;
    }

    /**
     * User this grid as a multiselect in {@link Binder}.
     * <p>
     * Throws {@link IllegalStateException} if the grid is not using a
     * {@link MultiSelectionModel}.
     *
     * @return the multiselect wrapper that can be used in binder
     * @throws IllegalStateException
     *             if not using a multiselection model
     */
    public MultiSelect<T> asMultiSelect() {
        GridSelectionModel<T> model = getSelectionModel();
        if (!(model instanceof MultiSelectionModel)) {
            throw new IllegalStateException(
                    "Grid is not in multiselect mode, it needs to be explicitly set to such with setSelectionModel(MultiSelectionModel) before being able to use multiselection features.");
        }
        return ((MultiSelectionModel<T>) model).asMultiSelect();
    }

    /**
     * Sets the selection model for the grid.
     * <p>
     * This method is for setting a custom selection model, and is
     * {@code protected} because {@link #setSelectionMode(SelectionMode)} should
     * be used for easy switching between built-in selection models.
     * <p>
     * The default selection model is {@link SingleSelectionModelImpl}.
     * <p>
     * To use a custom selection model, you can e.g. extend the grid call this
     * method with your custom selection model.
     *
     * @param model
     *            the selection model to use, not {@code null}
     *
     * @see #setSelectionMode(SelectionMode)
     */
    @SuppressWarnings("unchecked")
    protected void setSelectionModel(GridSelectionModel<T> model) {
        Objects.requireNonNull(model, "selection model cannot be null");
        if (selectionModel != null) { // null when called from constructor
            selectionModel.remove();
        }

        selectionModel = model;

        if (selectionModel instanceof AbstractListingExtension) {
            ((AbstractListingExtension<T>) selectionModel).extend(this);
        } else {
            addExtension(selectionModel);
        }

    }

    /**
     * Sets the grid's selection mode.
     * <p>
     * The built-in selection models are:
     * <ul>
     * <li>{@link SelectionMode#SINGLE} -> {@link SingleSelectionModelImpl},
     * <b>the default model</b></li>
     * <li>{@link SelectionMode#MULTI} -> {@link MultiSelectionModelImpl}, with
     * checkboxes in the first column for selection</li>
     * <li>{@link SelectionMode#NONE} -> {@link NoSelectionModel}, preventing
     * selection</li>
     * </ul>
     * <p>
     * To use your custom selection model, you can use
     * {@link #setSelectionModel(GridSelectionModel)}, see existing selection
     * model implementations for example.
     *
     * @param selectionMode
     *            the selection mode to switch to, not {@code null}
     * @return the used selection model
     *
     * @see SelectionMode
     * @see GridSelectionModel
     * @see #setSelectionModel(GridSelectionModel)
     */
    public GridSelectionModel<T> setSelectionMode(SelectionMode selectionMode) {
        Objects.requireNonNull(selectionMode, "Selection mode cannot be null.");
        GridSelectionModel<T> model = selectionMode.createModel();
        setSelectionModel(model);

        return model;
    }

    /**
     * This method is a shorthand that delegates to the currently set selection
     * model.
     *
     * @see #getSelectionModel()
     * @see GridSelectionModel
     */
    public Set<T> getSelectedItems() {
        return getSelectionModel().getSelectedItems();
    }

    /**
     * This method is a shorthand that delegates to the currently set selection
     * model.
     *
     * @see #getSelectionModel()
     * @see GridSelectionModel
     */
    public void select(T item) {
        getSelectionModel().select(item);
    }

    /**
     * This method is a shorthand that delegates to the currently set selection
     * model.
     *
     * @see #getSelectionModel()
     * @see GridSelectionModel
     */
    public void deselect(T item) {
        getSelectionModel().deselect(item);
    }

    /**
     * This method is a shorthand that delegates to the currently set selection
     * model.
     *
     * @see #getSelectionModel()
     * @see GridSelectionModel
     */
    public void deselectAll() {
        getSelectionModel().deselectAll();
    }

    /**
     * Adds a selection listener to the current selection model.
     * <p>
     * <em>NOTE:</em> If selection mode is switched with
     * {@link #setSelectionMode(SelectionMode)}, then this listener is not
     * triggered anymore when selection changes!
     * <p>
     * This is a shorthand for
     * {@code grid.getSelectionModel().addSelectionListener()}. To get more
     * detailed selection events, use {@link #getSelectionModel()} and either
     * {@link SingleSelectionModel#addSingleSelectionListener(SingleSelectionListener)}
     * or
     * {@link MultiSelectionModel#addMultiSelectionListener(MultiSelectionListener)}
     * depending on the used selection mode.
     *
     * @param listener
     *            the listener to add
     * @return a registration handle to remove the listener
     * @throws UnsupportedOperationException
     *             if selection has been disabled with
     *             {@link SelectionMode#NONE}
     */
    public Registration addSelectionListener(SelectionListener<T> listener)
            throws UnsupportedOperationException {
        return getSelectionModel().addSelectionListener(listener);
    }

    /**
     * Sort this Grid in ascending order by a specified column.
     *
     * @param column
     *            a column to sort against
     *
     */
    public void sort(Column<T, ?> column) {
        sort(column, SortDirection.ASCENDING);
    }

    /**
     * Sort this Grid in user-specified direction by a column.
     *
     * @param column
     *            a column to sort against
     * @param direction
     *            a sort order value (ascending/descending)
     *
     */
    public void sort(Column<T, ?> column, SortDirection direction) {
        setSortOrder(Collections
                .singletonList(new GridSortOrder<>(column, direction)));
    }

    /**
     * Sort this Grid in ascending order by a specified column defined by id.
     *
     * @param columnId
     *            the id of the column to sort against
     *
     * @see Column#setId(String)
     */
    public void sort(String columnId) {
        sort(columnId, SortDirection.ASCENDING);
    }

    /**
     * Sort this Grid in a user-specified direction by a column defined by id.
     *
     * @param columnId
     *            the id of the column to sort against
     * @param direction
     *            a sort order value (ascending/descending)
     *
     * @see Column#setId(String)
     */
    public void sort(String columnId, SortDirection direction) {
        sort(getColumnOrThrow(columnId), direction);
    }

    /**
     * Clear the current sort order, and re-sort the grid.
     */
    public void clearSortOrder() {
        sortOrder.clear();
        sort(false);
    }

    /**
     * Sets the sort order to use.
     *
     * @param order
     *            a sort order list.
     *
     * @throws IllegalArgumentException
     *             if order is null
     */
    public void setSortOrder(List<GridSortOrder<T>> order) {
        setSortOrder(order, false);
    }

    /**
     * Sets the sort order to use, given a {@link GridSortOrderBuilder}.
     * Shorthand for {@code setSortOrder(builder.build())}.
     *
     * @see GridSortOrderBuilder
     *
     * @param builder
     *            the sort builder to retrieve the sort order from
     * @throws NullPointerException
     *             if builder is null
     */
    public void setSortOrder(GridSortOrderBuilder<T> builder) {
        Objects.requireNonNull(builder, "Sort builder cannot be null");
        setSortOrder(builder.build());
    }

    /**
     * Adds a sort order change listener that gets notified when the sort order
     * changes.
     *
     * @param listener
     *            the sort order change listener to add
     */
    @Override
    public Registration addSortListener(
            SortListener<GridSortOrder<T>> listener) {
        return addListener(SortEvent.class, listener, SORT_ORDER_CHANGE_METHOD);
    }

    /**
     * Get the current sort order list.
     *
     * @return a sort order list
     */
    public List<GridSortOrder<T>> getSortOrder() {
        return Collections.unmodifiableList(sortOrder);
    }

    /**
     * Scrolls to a certain item, using {@link ScrollDestination#ANY}.
     * <p>
     * If the item has visible details, its size will also be taken into
     * account.
     *
     * @param row
     *            id of item to scroll to.
     * @throws IllegalArgumentException
     *             if the provided id is not recognized by the data source.
     */
    public void scrollTo(int row) throws IllegalArgumentException {
        scrollTo(row, ScrollDestination.ANY);
    }

    /**
     * Scrolls to a certain item, using user-specified scroll destination.
     * <p>
     * If the row has visible details, its size will also be taken into account.
     *
     * @param row
     *            id of item to scroll to.
     * @param destination
     *            value specifying desired position of scrolled-to row, not
     *            {@code null}
     * @throws IllegalArgumentException
     *             if the provided row is outside the item range
     */
    public void scrollTo(int row, ScrollDestination destination) {
        Objects.requireNonNull(destination,
                "ScrollDestination can not be null");

        if (row > getDataProvider().size(new Query())) {
            throw new IllegalArgumentException("Row outside dataProvider size");
        }

        getRpcProxy(GridClientRpc.class).scrollToRow(row, destination);
    }

    /**
     * Scrolls to the beginning of the first data row.
     */
    public void scrollToStart() {
        getRpcProxy(GridClientRpc.class).scrollToStart();
    }

    /**
     * Scrolls to the end of the last data row.
     */
    public void scrollToEnd() {
        getRpcProxy(GridClientRpc.class).scrollToEnd();
    }

    @Override
    protected GridState getState() {
        return getState(true);
    }

    @Override
    protected GridState getState(boolean markAsDirty) {
        return (GridState) super.getState(markAsDirty);
    }

    /**
     * Sets the column resize mode to use. The default mode is
     * {@link ColumnResizeMode#ANIMATED}.
     *
     * @param mode
     *            a ColumnResizeMode value
     * @since 7.7.5
     */
    public void setColumnResizeMode(ColumnResizeMode mode) {
        getState().columnResizeMode = mode;
    }

    /**
     * Returns the current column resize mode. The default mode is
     * {@link ColumnResizeMode#ANIMATED}.
     *
     * @return a ColumnResizeMode value
     * @since 7.7.5
     */
    public ColumnResizeMode getColumnResizeMode() {
        return getState(false).columnResizeMode;
    }

    /**
     * Creates a new Editor instance. Can be overridden to create a custom
     * Editor. If the Editor is a {@link AbstractGridExtension}, it will be
     * automatically added to {@link DataCommunicator}.
     *
     * @return editor
     */
    protected Editor<T> createEditor() {
        return new EditorImpl<>(propertySet);
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

    private void fireColumnReorderEvent(boolean userOriginated) {
        fireEvent(new ColumnReorderEvent(this, userOriginated));
    }

    private void fireColumnResizeEvent(Column<?, ?> column,
            boolean userOriginated) {
        fireEvent(new ColumnResizeEvent(this, column, userOriginated));
    }

    @Override
    protected void readItems(Element design, DesignContext context) {
        // Grid handles reading of items in Grid#readData
    }

    @Override
    public DataProvider<T, ?> getDataProvider() {
        return internalGetDataProvider();
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        internalSetDataProvider(dataProvider);
    }

    /**
     * Sets a CallbackDataProvider using the given fetch items callback and a
     * size callback.
     * <p>
     * This method is a shorthand for making a {@link CallbackDataProvider} that
     * handles a partial {@link Query} object.
     *
     * @param fetchItems
     *            a callback for fetching items
     * @param sizeCallback
     *            a callback for getting the count of items
     *
     * @see CallbackDataProvider
     * @see #setDataProvider(DataProvider)
     */
    public void setDataProvider(FetchItemsCallback<T> fetchItems,
            SerializableSupplier<Integer> sizeCallback) {
        internalSetDataProvider(
                new CallbackDataProvider<>(
                        q -> fetchItems.fetchItems(q.getSortOrders(),
                                q.getOffset(), q.getLimit()),
                        q -> sizeCallback.get()));
    }

    @Override
    protected void doReadDesign(Element design, DesignContext context) {
        Attributes attrs = design.attributes();
        if (design.hasAttr(DECLARATIVE_DATA_ITEM_TYPE)) {
            String itemType = design.attr(DECLARATIVE_DATA_ITEM_TYPE);
            setBeanType(itemType);
        }

        if (attrs.hasKey("selection-mode")) {
            setSelectionMode(DesignAttributeHandler.readAttribute(
                    "selection-mode", attrs, SelectionMode.class));
        }
        Attributes attr = design.attributes();
        if (attr.hasKey("selection-allowed")) {
            setReadOnly(DesignAttributeHandler
                    .readAttribute("selection-allowed", attr, Boolean.class));
        }

        if (attrs.hasKey("rows")) {
            setHeightByRows(DesignAttributeHandler.readAttribute("rows", attrs,
                    double.class));
        }

        readStructure(design, context);

        // Read frozen columns after columns are read.
        if (attrs.hasKey("frozen-columns")) {
            setFrozenColumnCount(DesignAttributeHandler
                    .readAttribute("frozen-columns", attrs, int.class));
        }
    }

    /**
     * Sets the bean type to use for property mapping.
     * <p>
     * This method is responsible also for setting or updating the property set
     * so that it matches the given bean type.
     * <p>
     * Protected mostly for Designer needs, typically should not be overridden
     * or even called.
     *
     * @param beanTypeClassName
     *            the fully qualified class name of the bean type
     *
     * @since 8.0.3
     */
    @SuppressWarnings("unchecked")
    protected void setBeanType(String beanTypeClassName) {
        setBeanType((Class<T>) resolveClass(beanTypeClassName));
    }

    /**
     * Sets the bean type to use for property mapping.
     * <p>
     * This method is responsible also for setting or updating the property set
     * so that it matches the given bean type.
     * <p>
     * Protected mostly for Designer needs, typically should not be overridden
     * or even called.
     *
     * @param beanType
     *            the bean type class
     *
     * @since 8.0.3
     */
    protected void setBeanType(Class<T> beanType) {
        this.beanType = beanType;
        setPropertySet(BeanPropertySet.get(beanType));
    }

    private Class<?> resolveClass(String qualifiedClassName) {
        try {
            Class<?> resolvedClass = Class.forName(qualifiedClassName, true,
                    VaadinServiceClassLoaderUtil.findDefaultClassLoader());
            return resolvedClass;
        } catch (ClassNotFoundException | SecurityException e) {
            throw new IllegalArgumentException(
                    "Unable to find class " + qualifiedClassName, e);
        }

    }

    @Override
    protected void doWriteDesign(Element design, DesignContext designContext) {
        Attributes attr = design.attributes();
        if (this.beanType != null) {
            design.attr(DECLARATIVE_DATA_ITEM_TYPE,
                    this.beanType.getCanonicalName());
        }
        DesignAttributeHandler.writeAttribute("selection-allowed", attr,
                isReadOnly(), false, Boolean.class, designContext);

        Attributes attrs = design.attributes();
        Grid<?> defaultInstance = designContext.getDefaultInstance(this);

        DesignAttributeHandler.writeAttribute("frozen-columns", attrs,
                getFrozenColumnCount(), defaultInstance.getFrozenColumnCount(),
                int.class, designContext);

        if (HeightMode.ROW.equals(getHeightMode())) {
            DesignAttributeHandler.writeAttribute("rows", attrs,
                    getHeightByRows(), defaultInstance.getHeightByRows(),
                    double.class, designContext);
        }

        SelectionMode mode = getSelectionMode();

        if (mode != null) {
            DesignAttributeHandler.writeAttribute("selection-mode", attrs, mode,
                    SelectionMode.SINGLE, SelectionMode.class, designContext);
        }

        writeStructure(design, designContext);
    }

    @Override
    protected T deserializeDeclarativeRepresentation(String item) {
        if (item == null) {
            return super.deserializeDeclarativeRepresentation(
                    new String(UUID.randomUUID().toString()));
        }
        return super.deserializeDeclarativeRepresentation(new String(item));
    }

    @Override
    protected boolean isReadOnly() {
        SelectionMode selectionMode = getSelectionMode();
        if (SelectionMode.SINGLE.equals(selectionMode)) {
            return asSingleSelect().isReadOnly();
        } else if (SelectionMode.MULTI.equals(selectionMode)) {
            return asMultiSelect().isReadOnly();
        }
        return false;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        SelectionMode selectionMode = getSelectionMode();
        if (SelectionMode.SINGLE.equals(selectionMode)) {
            asSingleSelect().setReadOnly(readOnly);
        } else if (SelectionMode.MULTI.equals(selectionMode)) {
            asMultiSelect().setReadOnly(readOnly);
        }
    }

    private void readStructure(Element design, DesignContext context) {
        if (design.children().isEmpty()) {
            return;
        }
        if (design.children().size() > 1
                || !design.child(0).tagName().equals("table")) {
            throw new DesignException(
                    "Grid needs to have a table element as its only child");
        }
        Element table = design.child(0);

        Elements colgroups = table.getElementsByTag("colgroup");
        if (colgroups.size() != 1) {
            throw new DesignException(
                    "Table element in declarative Grid needs to have a"
                            + " colgroup defining the columns used in Grid");
        }

        List<DeclarativeValueProvider<T>> providers = new ArrayList<>();
        for (Element col : colgroups.get(0).getElementsByTag("col")) {
            String id = DesignAttributeHandler.readAttribute("column-id",
                    col.attributes(), null, String.class);

            // If there is a property with a matching name available,
            // map to that
            Optional<PropertyDefinition<T, ?>> property = propertySet
                    .getProperties().filter(p -> p.getName().equals(id))
                    .findFirst();
            Column<T, ?> column;
            if (property.isPresent()) {
                column = addColumn(id);
            } else {
                DeclarativeValueProvider<T> provider = new DeclarativeValueProvider<>();
                column = new Column<>(provider, new HtmlRenderer());
                addColumn(getGeneratedIdentifier(), column);
                if (id != null) {
                    column.setId(id);
                }
                providers.add(provider);
            }
            column.readDesign(col, context);
        }

        for (Element child : table.children()) {
            if (child.tagName().equals("thead")) {
                getHeader().readDesign(child, context);
            } else if (child.tagName().equals("tbody")) {
                readData(child, providers);
            } else if (child.tagName().equals("tfoot")) {
                getFooter().readDesign(child, context);
            }
        }

        // Sync default header captions to column captions
        if (getDefaultHeaderRow() != null) {
            for (Column<T, ?> c : getColumns()) {
                HeaderCell headerCell = getDefaultHeaderRow().getCell(c);
                if (headerCell.getCellType() == GridStaticCellType.TEXT) {
                    c.setCaption(headerCell.getText());
                }
            }
        }
    }

    /**
     * Reads the declarative representation of a grid's data from the given
     * element and stores it in the given {@link DeclarativeValueProvider}s.
     * Each member in the list of value providers corresponds to a column in the
     * grid.
     *
     * @since 8.1
     *
     * @param body
     *            the element to read data from
     * @param providers
     *            list of {@link DeclarativeValueProvider}s to store the data of
     *            each column to
     *
     * @since 8.1
     */
    protected void readData(Element body,
            List<DeclarativeValueProvider<T>> providers) {
        getSelectionModel().deselectAll();
        List<T> items = new ArrayList<>();
        List<T> selectedItems = new ArrayList<>();
        for (Element row : body.children()) {
            T item = deserializeDeclarativeRepresentation(row.attr("item"));
            items.add(item);
            if (row.hasAttr("selected")) {
                selectedItems.add(item);
            }
            Elements cells = row.children();
            int i = 0;
            for (Element cell : cells) {
                providers.get(i).addValue(item, cell.html());
                i++;
            }
        }

        setItems(items);
        selectedItems.forEach(getSelectionModel()::select);
    }

    private void writeStructure(Element design, DesignContext designContext) {
        if (getColumns().isEmpty()) {
            return;
        }
        Element tableElement = design.appendElement("table");
        Element colGroup = tableElement.appendElement("colgroup");

        getColumns().forEach(column -> column
                .writeDesign(colGroup.appendElement("col"), designContext));

        // Always write thead. Reads correctly when there no header rows
        getHeader().writeDesign(tableElement.appendElement("thead"),
                designContext);

        if (designContext.shouldWriteData(this)) {
            Element bodyElement = tableElement.appendElement("tbody");
            writeData(bodyElement, designContext);
        }

        if (getFooter().getRowCount() > 0) {
            getFooter().writeDesign(tableElement.appendElement("tfoot"),
                    designContext);
        }
    }

    /**
     * Writes the data contained in this grid. Used when serializing a grid to
     * its declarative representation, if
     * {@link DesignContext#shouldWriteData(Component)} returns {@code true} for
     * the grid that is being written.
     *
     * @since 8.1
     *
     * @param body
     *            the body element to write the declarative representation of
     *            data to
     * @param designContext
     *            the design context
     *
     * @since 8.1
     */
    protected void writeData(Element body, DesignContext designContext) {
        getDataProvider().fetch(new Query<>())
                .forEach(item -> writeRow(body, item, designContext));
    }

    private void writeRow(Element container, T item, DesignContext context) {
        Element tableRow = container.appendElement("tr");
        tableRow.attr("item", serializeDeclarativeRepresentation(item));
        if (getSelectionModel().isSelected(item)) {
            tableRow.attr("selected", "");
        }
        for (Column<T, ?> column : getColumns()) {
            Object value = column.valueProvider.apply(item);
            tableRow.appendElement("td")
                    .append(Optional.ofNullable(value).map(Object::toString)
                            .map(DesignFormatter::encodeForTextNode)
                            .orElse(""));
        }
    }

    private SelectionMode getSelectionMode() {
        GridSelectionModel<T> selectionModel = getSelectionModel();
        SelectionMode mode = null;
        if (selectionModel.getClass().equals(SingleSelectionModelImpl.class)) {
            mode = SelectionMode.SINGLE;
        } else if (selectionModel.getClass()
                .equals(MultiSelectionModelImpl.class)) {
            mode = SelectionMode.MULTI;
        } else if (selectionModel.getClass().equals(NoSelectionModel.class)) {
            mode = SelectionMode.NONE;
        }
        return mode;
    }

    /**
     * Sets a user-defined identifier for given column.
     *
     * @see Column#setId(String)
     *
     * @param column
     *            the column
     * @param id
     *            the user-defined identifier
     */
    protected void setColumnId(String id, Column<T, ?> column) {
        if (columnIds.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate ID for columns");
        }
        columnIds.put(id, column);
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> result = super.getCustomAttributes();
        // "rename" for frozen column count
        result.add("frozen-column-count");
        result.add("frozen-columns");
        // "rename" for height-mode
        result.add("height-by-rows");
        result.add("rows");
        // add a selection-mode attribute
        result.add("selection-mode");

        return result;
    }

    /**
     * Returns a column identified by its internal id. This id should not be
     * confused with the user-defined identifier.
     *
     * @param columnId
     *            the internal id of column
     * @return column identified by internal id
     */
    protected Column<T, ?> getColumnByInternalId(String columnId) {
        return columnKeys.get(columnId);
    }

    /**
     * Returns the internal id for given column. This id should not be confused
     * with the user-defined identifier.
     *
     * @param column
     *            the column
     * @return internal id of given column
     */
    protected String getInternalIdForColumn(Column<T, ?> column) {
        return column.getInternalId();
    }

    private void setSortOrder(List<GridSortOrder<T>> order,
            boolean userOriginated) {
        Objects.requireNonNull(order, "Sort order list cannot be null");
        sortOrder.clear();
        if (order.isEmpty()) {
            // Grid is not sorted anymore.
            getDataCommunicator().setBackEndSorting(Collections.emptyList());
            getDataCommunicator().setInMemorySorting(null);
            fireEvent(new SortEvent<>(this, new ArrayList<>(sortOrder),
                    userOriginated));
            return;
        }
        sortOrder.addAll(order);
        sort(userOriginated);
    }

    private void sort(boolean userOriginated) {
        // Set sort orders
        // In-memory comparator
        getDataCommunicator().setInMemorySorting(createSortingComparator());

        // Back-end sort properties
        List<QuerySortOrder> sortProperties = new ArrayList<>();
        sortOrder.stream().map(
                order -> order.getSorted().getSortOrder(order.getDirection()))
                .forEach(s -> s.forEach(sortProperties::add));
        getDataCommunicator().setBackEndSorting(sortProperties);

        // Close grid editor if it's open.
        if (getEditor().isOpen()) {
            getEditor().cancel();
        }
        fireEvent(new SortEvent<>(this, new ArrayList<>(sortOrder),
                userOriginated));
    }

    /**
     * Creates a comparator for grid to sort rows.
     *
     * @return the comparator based on column sorting information.
     */
    protected SerializableComparator<T> createSortingComparator() {
        BinaryOperator<SerializableComparator<T>> operator = (comparator1,
                comparator2) -> {
            /*
             * thenComparing is defined to return a serializable comparator as
             * long as both original comparators are also serializable
             */
            return comparator1.thenComparing(comparator2)::compare;
        };
        return sortOrder.stream().map(
                order -> order.getSorted().getComparator(order.getDirection()))
                .reduce((x, y) -> 0, operator);
    }
}
