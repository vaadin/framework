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
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.Listing;
import com.vaadin.data.SelectionModel;
import com.vaadin.event.ConnectorEvent;
import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.EncodeResult;
import com.vaadin.server.Extension;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.SerializableComparator;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.data.DataCommunicator;
import com.vaadin.server.data.DataProvider;
import com.vaadin.server.data.Query;
import com.vaadin.server.data.SortOrder;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.AbstractGridExtensionState;
import com.vaadin.shared.ui.grid.ColumnState;
import com.vaadin.shared.ui.grid.DetailsManagerState;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.SectionState;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.components.grid.AbstractSelectionModel;
import com.vaadin.ui.components.grid.EditorImpl;
import com.vaadin.ui.components.grid.Footer;
import com.vaadin.ui.components.grid.Header;
import com.vaadin.ui.components.grid.Header.Row;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.NoSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import com.vaadin.ui.declarative.DesignFormatter;
import com.vaadin.ui.renderers.AbstractRenderer;
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
public class Grid<T> extends AbstractListing<T>
        implements HasComponents, Listing<T, DataProvider<T, ?>> {

    @Deprecated
    private static final Method COLUMN_REORDER_METHOD = ReflectTools.findMethod(
            ColumnReorderListener.class, "columnReorder",
            ColumnReorderEvent.class);

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
     * An event listener for column reorder events in the Grid.
     */
    @FunctionalInterface
    public interface ColumnReorderListener extends Serializable {

        /**
         * Called when the columns of the grid have been reordered.
         *
         * @param event
         *            An event providing more information
         */
        void columnReorder(ColumnReorderEvent event);
    }

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
     * The server-side interface that controls Grid's selection state.
     * SelectionModel should extend {@link AbstractGridExtension}.
     * <p>
     *
     * @param <T>
     *            the grid bean type
     * @see AbstractSelectionModel
     * @see SingleSelectionModel
     * @see MultiSelectionModel
     */
    public interface GridSelectionModel<T>
            extends SelectionModel<T>, Extension {

        /**
         * Removes this selection model from the grid.
         * <p>
         * Must call super {@link Extension#remove()} to detach the extension,
         * and fire an selection change event for the selection model (with an
         * empty selection).
         */
        @Override
        public void remove();
    }

    /**
     * Single selection model interface for Grid.
     *
     * @param <T>
     *            the type of items in grid
     */
    public interface SingleSelectionModel<T> extends GridSelectionModel<T>,
            com.vaadin.data.SelectionModel.Single<T> {

        /**
         * Gets a wrapper to use this single selection model as a single select
         * in {@link Binder}.
         *
         * @return the single select wrapper
         */
        SingleSelect<T> asSingleSelect();

        /**
         * {@inheritDoc}
         * <p>
         * Use {@link #addSingleSelectionListener(SingleSelectionListener)} for
         * more specific single selection event.
         *
         * @see #addSingleSelectionListener(SingleSelectionListener)
         */
        @Override
        public default Registration addSelectionListener(
                SelectionListener<T> listener) {
            return addSingleSelectionListener(e -> listener.accept(e));
        }

        /**
         * Adds a single selection listener that is called when the value of
         * this select is changed either by the user or programmatically.
         *
         * @param listener
         *            the value change listener, not {@code null}
         * @return a registration for the listener
         */
        public Registration addSingleSelectionListener(
                SingleSelectionListener<T> listener);
    }

    /**
     * Multiselection model interface for Grid.
     *
     * @param <T>
     *            the type of items in grid
     */
    public interface MultiSelectionModel<T> extends GridSelectionModel<T>,
            com.vaadin.data.SelectionModel.Multi<T> {

        /**
         * Gets a wrapper to use this multiselection model as a multiselect in
         * {@link Binder}.
         *
         * @return the multiselect wrapper
         */
        MultiSelect<T> asMultiSelect();

        /**
         * {@inheritDoc}
         * <p>
         * Use {@link #addMultiSelectionListener(MultiSelectionListener)} for
         * more specific event on multiselection.
         *
         * @see #addMultiSelectionListener(MultiSelectionListener)
         */
        @Override
        public default Registration addSelectionListener(
                SelectionListener<T> listener) {
            return addMultiSelectionListener(e -> listener.accept(e));
        }

        /**
         * Adds a selection listener that will be called when the selection is
         * changed either by the user or programmatically.
         *
         * @param listener
         *            the value change listener, not {@code null}
         * @return a registration for the listener
         */
        public Registration addMultiSelectionListener(
                MultiSelectionListener<T> listener);
    }

    /**
     * An event listener for column resize events in the Grid.
     */
    @FunctionalInterface
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
     * A listener for item click events.
     *
     * @param <T>
     *            the grid bean type
     *
     * @see ItemClick
     * @see Registration
     */
    @FunctionalInterface
    public interface ItemClickListener<T>
            extends Consumer<ItemClick<T>>, ConnectorEventListener {
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
    @FunctionalInterface
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
            extends SerializableFunction<T, String> {
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
            BinaryOperator<SerializableComparator<T>> operator = (comparator1,
                    comparator2) -> SerializableComparator.asInstance(
                            (Comparator<T> & Serializable) comparator1
                                    .thenComparing(comparator2));
            SerializableComparator<T> comparator = sortOrder.stream()
                    .map(order -> order.getSorted()
                            .getComparator(order.getDirection()))
                    .reduce((x, y) -> 0, operator);
            getDataCommunicator().setInMemorySorting(comparator);

            // Back-end sort properties
            List<SortOrder<String>> sortProperties = new ArrayList<>();
            sortOrder.stream()
                    .map(order -> order.getSorted()
                            .getSortOrder(order.getDirection()))
                    .forEach(s -> s.forEach(sortProperties::add));
            getDataCommunicator().setBackEndSorting(sortProperties);

            // Close grid editor if it's open.
            if (getEditor().isOpen()) {
                getEditor().cancel();
            }
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
                    type = getState(false).getClass()
                            .getDeclaredField(diffStateKey).getGenericType();
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
    public static class Column<T, V> extends AbstractGridExtension<T> {

        private final SerializableFunction<T, ? extends V> valueProvider;

        private SerializableFunction<SortDirection, Stream<SortOrder<String>>> sortOrderProvider;
        private SerializableComparator<T> comparator;
        private StyleGenerator<T> styleGenerator = item -> null;
        private DescriptionGenerator<T> descriptionGenerator;

        private SerializableFunction<T, Component> componentGenerator;

        private String id;

        /**
         * Constructs a new Column configuration with given header caption,
         * renderer and value provider.
         *
         * @param valueProvider
         *            the function to get values from items
         * @param renderer
         *            the type of value
         */
        protected Column(SerializableFunction<T, ? extends V> valueProvider,
                Renderer<V> renderer) {
            Objects.requireNonNull(valueProvider,
                    "Value provider can't be null");
            Objects.requireNonNull(renderer, "Renderer can't be null");

            ColumnState state = getState();

            this.valueProvider = valueProvider;
            state.renderer = renderer;

            state.caption = "";
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

            String communicationId = getConnectorId();

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
        public String getInternalId() {
            return getState(false).id;
        }

        /**
         * Sets the identifier to use with this Column in communication.
         *
         * @param id
         *            the identifier string
         */
        private void setInternalId(String id) {
            Objects.requireNonNull(id, "Communication ID can't be null");
            getState().id = id;
        }

        /**
         * Returns the user-defined identifier for this column.
         *
         * @return the identifier string
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the user-defined identifier to map this column. The identifier
         * can be used for example in {@link Grid#getColumn(String)}.
         *
         * @param id
         *            the identifier string
         */
        public Column<T, V> setId(String id) {
            Objects.requireNonNull(id, "Column identifier cannot be null");
            if (this.id != null) {
                throw new IllegalStateException(
                        "Column identifier cannot be changed");
            }
            this.id = id;
            getParent().setColumnId(id, this);
            return this;
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
            getState().caption = caption;

            HeaderRow row = getParent().getDefaultHeaderRow();
            if (row != null) {
                row.getCell(getInternalId()).setText(caption);
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
                SerializableFunction<SortDirection, Stream<SortOrder<String>>> provider) {
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
         * Sets whether this Column has a component displayed in Editor or not.
         *
         * @param editable
         *            {@code true} if column is editable; {@code false} if not
         * @return this column
         *
         * @see #setEditorComponent(Component)
         * @see #setEditorComponentGenerator(SerializableFunction)
         */
        public Column<T, V> setEditable(boolean editable) {
            Objects.requireNonNull(componentGenerator,
                    "Column has no editor component defined");
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
         * Sets a static editor component for this column.
         * <p>
         * <strong>Note:</strong> The same component cannot be used for multiple
         * columns.
         *
         * @param component
         *            the editor component
         * @return this column
         *
         * @see Editor#getBinder()
         * @see Editor#setBinder(Binder)
         * @see #setEditorComponentGenerator(SerializableFunction)
         */
        public Column<T, V> setEditorComponent(Component component) {
            Objects.requireNonNull(component,
                    "null is not a valid editor field");
            return setEditorComponentGenerator(t -> component);
        }

        /**
         * Sets a component generator to provide editor component for this
         * Column. This method can be used to generate any dynamic component to
         * be displayed in the editor row.
         * <p>
         * <strong>Note:</strong> The same component cannot be used for multiple
         * columns.
         *
         * @param componentGenerator
         *            the editor component generator
         * @return this column
         *
         * @see #setEditorComponent(Component)
         */
        public Column<T, V> setEditorComponentGenerator(
                SerializableFunction<T, Component> componentGenerator) {
            Objects.requireNonNull(componentGenerator);
            this.componentGenerator = componentGenerator;
            return setEditable(true);
        }

        /**
         * Gets the editor component generator for this Column.
         *
         * @return editor component generator
         */
        public SerializableFunction<T, Component> getEditorComponentGenerator() {
            return componentGenerator;
        }

        /**
         * Checks if column is attached and throws an
         * {@link IllegalStateException} if it is not.
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

            DesignAttributeHandler.writeAttribute("column-id", attributes,
                    getInternalId(), null, String.class, designContext);

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
                setEditorComponentGenerator(item -> new TextField(
                        Optional.ofNullable(valueProvider.apply(item))
                                .map(Object::toString).orElse("")));
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
    }

    /**
     * A header row in a Grid.
     */
    public interface HeaderRow extends Serializable {

        /**
         * Returns the cell on this row corresponding to the given column id.
         *
         * @param columnId
         *            the id of the column whose header cell to get, not null
         * @return the header cell
         * @throws IllegalArgumentException
         *             if there is no such column in the grid
         */
        public HeaderCell getCell(String columnId);

        /**
         * Returns the cell on this row corresponding to the given column.
         *
         * @param column
         *            the column whose header cell to get, not null
         * @return the header cell
         * @throws IllegalArgumentException
         *             if there is no such column in the grid
         */
        public HeaderCell getCell(Column<?, ?> column);

        /**
         * Merges column cells in the row. Original cells are hidden, and new
         * merged cell is shown instead. The cell has a width of all merged
         * cells together, inherits styles of the first merged cell but has
         * empty caption.
         *
         * @param cellsToMerge
         *            the cells which should be merged. The cells should not be
         *            merged to any other cell set.
         * @return the remaining visible cell after the merge
         *
         * @see #join(Grid.HeaderCell...)
         * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
         */
        HeaderCell join(Set<HeaderCell> cellsToMerge);

        /**
         * Merges column cells in the row. Original cells are hidden, and new
         * merged cell is shown instead. The cell has a width of all merged
         * cells together, inherits styles of the first merged cell but has
         * empty caption.
         *
         * @param cellsToMerge
         *            the cells which should be merged. The cells should not be
         *            merged to any other cell set.
         * @return the remaining visible cell after the merge
         *
         * @see #join(Set)
         * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
         */
        HeaderCell join(HeaderCell... cellsToMerge);

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
         *            the header caption to set, not null
         */
        public void setText(String text);

        /**
         * Returns the HTML content displayed in this cell.
         *
         * @return the html
         *
         */
        public String getHtml();

        /**
         * Sets the HTML content displayed in this cell.
         *
         * @param html
         *            the html to set
         */
        public void setHtml(String html);

        /**
         * Returns the component displayed in this cell.
         *
         * @return the component
         */
        public Component getComponent();

        /**
         * Sets the component displayed in this cell.
         *
         * @param component
         *            the component to set
         */
        public void setComponent(Component component);

        /**
         * Returns the type of content stored in this cell.
         *
         * @return cell content type
         */
        public GridStaticCellType getCellType();

        /**
         * Gets the column id where this cell is.
         *
         * @return column id for this cell
         */
        public String getColumnId();
    }

    /**
     * A footer row in a Grid.
     */
    public interface FooterRow extends Serializable {

        /**
         * Returns the cell on this row corresponding to the given column id.
         *
         * @param columnId
         *            the id of the column whose footer cell to get, not null
         * @return the footer cell
         * @throws IllegalArgumentException
         *             if there is no such column in the grid
         */
        public FooterCell getCell(String columnId);

        /**
         * Returns the cell on this row corresponding to the given column.
         *
         * @param column
         *            the column whose footer cell to get, not null
         * @return the footer cell
         * @throws IllegalArgumentException
         *             if there is no such column in the grid
         */
        public FooterCell getCell(Column<?, ?> column);

        /**
         * Merges column cells in the row. Original cells are hidden, and new
         * merged cell is shown instead. The cell has a width of all merged
         * cells together, inherits styles of the first merged cell but has
         * empty caption.
         *
         * @param cellsToMerge
         *            the cells which should be merged. The cells should not be
         *            merged to any other cell set.
         * @return the remaining visible cell after the merge
         *
         * @see #join(Grid.FooterCell...)
         * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
         */
        FooterCell join(Set<FooterCell> cellsToMerge);

        /**
         * Merges column cells in the row. Original cells are hidden, and new
         * merged cell is shown instead. The cell has a width of all merged
         * cells together, inherits styles of the first merged cell but has
         * empty caption.
         *
         * @param cellsToMerge
         *            the cells which should be merged. The cells should not be
         *            merged to any other cell set.
         * @return the remaining visible cell after the merge
         *
         * @see #join(Set)
         * @see com.vaadin.ui.AbstractComponent#setCaption(String) setCaption
         */
        FooterCell join(FooterCell... cellsToMerge);
    }

    /**
     * An individual cell on a Grid footer row.
     */
    public interface FooterCell extends Serializable {

        /**
         * Returns the textual caption of this cell.
         *
         * @return the footer caption
         */
        public String getText();

        /**
         * Sets the textual caption of this cell.
         *
         * @param text
         *            the footer caption to set, not null
         */
        public void setText(String text);

        /**
         * Returns the HTML content displayed in this cell.
         *
         * @return the html
         *
         */
        public String getHtml();

        /**
         * Sets the HTML content displayed in this cell.
         *
         * @param html
         *            the html to set
         */
        public void setHtml(String html);

        /**
         * Returns the component displayed in this cell.
         *
         * @return the component
         */
        public Component getComponent();

        /**
         * Sets the component displayed in this cell.
         *
         * @param component
         *            the component to set
         */
        public void setComponent(Component component);

        /**
         * Returns the type of content stored in this cell.
         *
         * @return cell content type
         */
        public GridStaticCellType getCellType();

        /**
         * Gets the column id where this cell is.
         *
         * @return column id for this cell
         */
        public String getColumnId();
    }

    /**
     * Generator for creating editor validation and conversion error messages.
     *
     * @param <T>
     *            the bean type
     */
    public interface EditorErrorGenerator<T> extends Serializable,
            BiFunction<Map<Component, Column<T, ?>>, BinderValidationStatus<T>, String> {

        /**
         * Generates an error message from given validation status object.
         *
         * @param fieldToColumn
         *            the map of failed fields and corresponding columns
         * @param status
         *            the binder status object with all failures
         *
         * @return error message string
         */
        @Override
        public String apply(Map<Component, Column<T, ?>> fieldToColumn,
                BinderValidationStatus<T> status);
    }

    /**
     * An editor in a Grid.
     *
     * @param <T>
     */
    public interface Editor<T> extends Serializable {

        /**
         * Sets the underlying Binder to this Editor.
         *
         * @param binder
         *            the binder for updating editor fields; not {@code null}
         * @return this editor
         */
        public Editor<T> setBinder(Binder<T> binder);

        /**
         * Returns the underlying Binder from Editor.
         *
         * @return the binder; not {@code null}
         */
        public Binder<T> getBinder();

        /**
         * Sets the Editor buffered mode. When the editor is in buffered mode,
         * edits are only committed when the user clicks the save button. In
         * unbuffered mode valid changes are automatically committed.
         *
         * @param buffered
         *            {@code true} if editor should be buffered; {@code false}
         *            if not
         * @return this editor
         */
        public Editor<T> setBuffered(boolean buffered);

        /**
         * Enables or disabled the Editor. A disabled editor cannot be opened.
         *
         * @param enabled
         *            {@code true} if editor should be enabled; {@code false} if
         *            not
         * @return this editor
         */
        public Editor<T> setEnabled(boolean enabled);

        /**
         * Returns whether Editor is buffered or not.
         *
         * @see #setBuffered(boolean)
         *
         * @return {@code true} if editor is buffered; {@code false} if not
         */
        public boolean isBuffered();

        /**
         * Returns whether Editor is enabled or not.
         *
         * @return {@code true} if editor is enabled; {@code false} if not
         */
        public boolean isEnabled();

        /**
         * Returns whether Editor is open or not.
         *
         * @return {@code true} if editor is open; {@code false} if not
         */
        public boolean isOpen();

        /**
         * Saves any changes from the Editor fields to the edited bean.
         *
         * @return {@code true} if save succeeded; {@code false} if not
         */
        public boolean save();

        /**
         * Close the editor discarding any unsaved changes.
         */
        public void cancel();

        /**
         * Sets the caption of the save button in buffered mode.
         *
         * @param saveCaption
         *            the save button caption
         * @return this editor
         */
        public Editor<T> setSaveCaption(String saveCaption);

        /**
         * Sets the caption of the cancel button in buffered mode.
         *
         * @param cancelCaption
         *            the cancel button caption
         * @return this editor
         */
        public Editor<T> setCancelCaption(String cancelCaption);

        /**
         * Gets the caption of the save button in buffered mode.
         *
         * @return the save button caption
         */
        public String getSaveCaption();

        /**
         * Gets the caption of the cancel button in buffered mode.
         *
         * @return the cancel button caption
         */
        public String getCancelCaption();

        /**
         * Sets the error message generator for this editor.
         * <p>
         * The default message is a concatenation of column field validation
         * failures and bean validation failures.
         *
         * @param errorGenerator
         *            the function to generate error messages; not {@code null}
         * @return this editor
         *
         * @see EditorErrorGenerator
         */
        public Editor<T> setErrorGenerator(
                EditorErrorGenerator<T> errorGenerator);

        /**
         * Gets the error message generator of this editor.
         *
         * @return the function that generates error messages; not {@code null}
         *
         * @see EditorErrorGenerator
         */
        public EditorErrorGenerator<T> getErrorGenerator();
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
        protected Collection<Column<T, ?>> getColumns() {
            return Grid.this.getColumns();
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
        protected Collection<Column<T, ?>> getColumns() {
            return Grid.this.getColumns();
        }
    };

    private final Set<Column<T, ?>> columnSet = new LinkedHashSet<>();
    private final Map<String, Column<T, ?>> columnKeys = new HashMap<>();
    private final Map<String, Column<T, ?>> columnIds = new HashMap<>();

    private final List<SortOrder<Column<T, ?>>> sortOrder = new ArrayList<>();
    private final DetailsManager<T> detailsManager;
    private final Set<Component> extensionComponents = new HashSet<>();
    private StyleGenerator<T> styleGenerator = item -> null;
    private DescriptionGenerator<T> descriptionGenerator;

    private final Header header = new HeaderImpl();
    private final Footer footer = new FooterImpl();

    private int counter = 0;

    private GridSelectionModel<T> selectionModel;

    private Editor<T> editor;

    /**
     * Constructor for the {@link Grid} component.
     */
    public Grid() {
        registerRpc(new GridServerRpcImpl());

        setDefaultHeaderRow(appendHeaderRow());

        setSelectionModel(new SingleSelectionModelImpl<>());

        detailsManager = new DetailsManager<>();
        addExtension(detailsManager);
        addDataGenerator(detailsManager);

        editor = createEditor();
        if (editor instanceof Extension) {
            addExtension((Extension) editor);
        }

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
     * Adds a new text column to this {@link Grid} with a value provider. The
     * column will use a {@link TextRenderer}. The value is converted to a
     * String using {@link Object#toString()}. Sorting in memory is executed by
     * comparing the String values.
     *
     * @param valueProvider
     *            the value provider
     *
     * @return the new column
     */
    public Column<T, String> addColumn(
            SerializableFunction<T, ?> valueProvider) {
        return addColumn(t -> String.valueOf(valueProvider.apply(t)),
                new TextRenderer());
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
    public <V> Column<T, V> addColumn(
            SerializableFunction<T, ? extends V> valueProvider,
            AbstractRenderer<? super T, V> renderer) {
        Column<T, V> column = new Column<>(valueProvider, renderer);
        addColumn(getGeneratedIdentifier(), column);
        return column;
    }

    private void addColumn(String identifier, Column<T, ?> column) {
        if (getColumns().contains(column)) {
            return;
        }

        column.extend(this);
        columnSet.add(column);
        columnKeys.put(identifier, column);
        column.setInternalId(identifier);
        addDataGenerator(column);

        getState().columnOrder.add(identifier);
        getHeader().addColumn(identifier);

        if (getDefaultHeaderRow() != null) {
            getDefaultHeaderRow().getCell(identifier)
                    .setText(column.getCaption());
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
            columnKeys.remove(columnId);
            column.remove();
            getHeader().removeColumn(columnId);
            getFooter().removeColumn(columnId);
            getState(true).columnOrder.remove(columnId);
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
    public List<Column<T, ?>> getColumns() {
        return Collections.unmodifiableList(getState(false).columnOrder.stream()
                .map(this::getColumn).collect(Collectors.toList()));
    }

    /**
     * Gets a {@link Column} of this grid by its identifying string.
     *
     * @param columnId
     *            the identifier of the column to get
     * @return the column corresponding to the given column identifier
     */
    public Column<T, ?> getColumn(String columnId) {
        return columnIds.get(columnId);
    }

    @Override
    public Iterator<Component> iterator() {
        Set<Component> componentSet = new LinkedHashSet<>(extensionComponents);
        Header header = getHeader();
        for (int i = 0; i < header.getRowCount(); ++i) {
            HeaderRow row = header.getRow(i);
            getColumns().forEach(column -> {
                HeaderCell cell = row.getCell(column);
                if (cell.getCellType() == GridStaticCellType.WIDGET) {
                    componentSet.add(cell.getComponent());
                }
            });
        }
        Footer footer = getFooter();
        for (int i = 0; i < footer.getRowCount(); ++i) {
            FooterRow row = footer.getRow(i);
            getColumns().forEach(column -> {
                FooterCell cell = row.getCell(column);
                if (cell.getCellType() == GridStaticCellType.WIDGET) {
                    componentSet.add(cell.getComponent());
                }
            });
        }
        return Collections.unmodifiableSet(componentSet).iterator();
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
     * Returns the footer section of this grid. The default footer contains a
     * single row, set as the {@linkplain #setDefaultFooterRow(FooterRow)
     * default row}.
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
     * Sets the columns and their order for the grid. Columns currently in this
     * grid that are not present in columns are removed. Similarly, any new
     * column in columns will be added to this grid.
     *
     * @param columns
     *            the columns to set
     */
    public void setColumns(Column<T, ?>... columns) {
        List<Column<T, ?>> currentColumns = getColumns();
        Set<Column<T, ?>> removeColumns = new HashSet<>(currentColumns);
        Set<Column<T, ?>> addColumns = Arrays.stream(columns)
                .collect(Collectors.toSet());

        removeColumns.removeAll(addColumns);
        removeColumns.stream().forEach(this::removeColumn);

        addColumns.removeAll(currentColumns);
        addColumns.stream().forEach(c -> addColumn(getIdentifier(c), c));

        setColumnOrder(columns);
    }

    private String getIdentifier(Column<T, ?> column) {
        return columnKeys.entrySet().stream()
                .filter(entry -> entry.getValue().equals(column))
                .map(entry -> entry.getKey()).findFirst()
                .orElse(getGeneratedIdentifier());
    }

    private String getGeneratedIdentifier() {
        String columnId = "c" + counter;
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
        List<String> columnOrder = new ArrayList<>();
        for (Column<T, ?> column : columns) {
            if (columnSet.contains(column)) {
                columnOrder.add(column.getInternalId());
            } else {
                throw new IllegalArgumentException(
                        "setColumnOrder should not be called "
                                + "with columns that are not in the grid.");
            }
        }

        List<String> stateColumnOrder = getState().columnOrder;
        if (stateColumnOrder.size() != columnOrder.size()) {
            stateColumnOrder.removeAll(columnOrder);
            columnOrder.addAll(stateColumnOrder);
        }

        getState().columnOrder = columnOrder;
        fireColumnReorderEvent(false);
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
     *             {@link SelectionMode.NONE}
     */
    public Registration addSelectionListener(SelectionListener<T> listener)
            throws UnsupportedOperationException {
        return getSelectionModel().addSelectionListener(listener);
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
     * Creates a new Editor instance. Can be overridden to create a custom
     * Editor. If the Editor is a {@link AbstractGridExtension}, it will be
     * automatically added to {@link DataCommunicator}.
     *
     * @return editor
     */
    protected Editor<T> createEditor() {
        return new EditorImpl<>();
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
    protected List<T> readItems(Element design, DesignContext context) {
        return Collections.emptyList();
    }

    @Override
    public DataProvider<T, ?> getDataProvider() {
        return internalGetDataProvider();
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        internalSetDataProvider(dataProvider);
    }

    @Override
    protected void doReadDesign(Element design, DesignContext context) {
        Attributes attrs = design.attributes();
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

    @Override
    protected void doWriteDesign(Element design, DesignContext designContext) {
        Attributes attr = design.attributes();
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
            DeclarativeValueProvider<T> provider = new DeclarativeValueProvider<>();
            Column<T, String> column = new Column<>(provider,
                    new HtmlRenderer());
            addColumn(getGeneratedIdentifier(), column);
            if (id != null) {
                column.setId(id);
            }
            providers.add(provider);
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
    }

    private void readData(Element body,
            List<DeclarativeValueProvider<T>> providers) {
        getSelectionModel().deselectAll();
        List<T> items = new ArrayList<>();
        for (Element row : body.children()) {
            T item = deserializeDeclarativeRepresentation(row.attr("item"));
            if (row.hasAttr("selected")) {
                getSelectionModel().select(item);
            }
            Elements cells = row.children();
            int i = 0;
            for (Element cell : cells) {
                providers.get(i).addValue(item, cell.html());
                i++;
            }
        }

        setItems(items);
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
            getDataProvider().fetch(new Query<>()).forEach(
                    item -> writeRow(bodyElement, item, designContext));
        }

        if (getFooter().getRowCount() > 0) {
            getFooter().writeDesign(tableElement.appendElement("tfoot"),
                    designContext);
        }
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
                    .append((Optional.ofNullable(value).map(Object::toString)
                            .map(DesignFormatter::encodeForTextNode)
                            .orElse("")));
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

}
