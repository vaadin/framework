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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.vaadin.data.BeanPropertySet;
import com.vaadin.data.HasHierarchicalDataProvider;
import com.vaadin.data.HasValue;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.PropertySet;
import com.vaadin.data.TreeData;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.HierarchicalDataCommunicator;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.event.CollapseEvent;
import com.vaadin.event.CollapseEvent.CollapseListener;
import com.vaadin.event.ExpandEvent;
import com.vaadin.event.ExpandEvent.ExpandListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.treegrid.FocusParentRpc;
import com.vaadin.shared.ui.treegrid.FocusRpc;
import com.vaadin.shared.ui.treegrid.NodeCollapseRpc;
import com.vaadin.shared.ui.treegrid.TreeGridClientRpc;
import com.vaadin.shared.ui.treegrid.TreeGridState;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;
import com.vaadin.ui.renderers.AbstractRenderer;
import com.vaadin.ui.renderers.Renderer;

/**
 * A grid component for displaying hierarchical tabular data.
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            the grid bean type
 */
public class TreeGrid<T> extends Grid<T>
        implements HasHierarchicalDataProvider<T> {

    /**
     * Creates a new {@code TreeGrid} without support for creating columns based
     * on property names. Use an alternative constructor, such as
     * {@link TreeGrid#TreeGrid(Class)}, to create a {@code TreeGrid} that
     * automatically sets up columns based on the type of presented data.
     */
    public TreeGrid() {
        this(new HierarchicalDataCommunicator<>());
    }

    /**
     * Creates a new {@code TreeGrid} that uses reflection based on the provided
     * bean type to automatically set up an initial set of columns. All columns
     * will be configured using the same {@link Object#toString()} renderer that
     * is used by {@link #addColumn(ValueProvider)}.
     *
     * @param beanType
     *            the bean type to use, not {@code null}
     */
    public TreeGrid(Class<T> beanType) {
        this(BeanPropertySet.get(beanType),
                new HierarchicalDataCommunicator<>());
    }

    /**
     * Creates a new {@code TreeGrid} using the given
     * {@code HierarchicalDataProvider}, without support for creating columns
     * based on property names. Use an alternative constructor, such as
     * {@link TreeGrid#TreeGrid(Class)}, to create a {@code TreeGrid} that
     * automatically sets up columns based on the type of presented data.
     *
     * @param dataProvider
     *            the data provider, not {@code null}
     */
    public TreeGrid(HierarchicalDataProvider<T, ?> dataProvider) {
        this();
        setDataProvider(dataProvider);
    }

    /**
     * Creates a {@code TreeGrid} using a custom {@link PropertySet}
     * implementation and custom data communicator.
     * <p>
     * Property set is used for configuring the initial columns and resolving
     * property names for {@link #addColumn(String)} and
     * {@link Column#setEditorComponent(HasValue)}.
     *
     * @param propertySet
     *            the property set implementation to use, not {@code null}
     * @param dataCommunicator
     *            the data communicator to use, not {@code null}
     */
    protected TreeGrid(PropertySet<T> propertySet,
            HierarchicalDataCommunicator<T> dataCommunicator) {
        super(propertySet, dataCommunicator);

        registerRpc(new NodeCollapseRpc() {
            @Override
            public void setNodeCollapsed(String rowKey, int rowIndex,
                    boolean collapse, boolean userOriginated) {
                if (collapse) {
                    if (getDataCommunicator().doCollapse(rowKey, rowIndex)
                            && userOriginated) {
                        fireCollapseEvent(getDataCommunicator().getKeyMapper()
                                .get(rowKey), true);
                    }
                } else {
                    if (getDataCommunicator().doExpand(rowKey, rowIndex,
                            userOriginated) && userOriginated) {
                        fireExpandEvent(getDataCommunicator().getKeyMapper()
                                .get(rowKey), true);
                    }
                }
            }
        });
        registerRpc(new FocusParentRpc() {
            @Override
            public void focusParent(int rowIndex, int cellIndex) {
                Integer parentIndex = getDataCommunicator()
                        .getParentIndex(rowIndex);
                if (parentIndex != null) {
                    getRpcProxy(FocusRpc.class).focusCell(parentIndex,
                            cellIndex);
                }
            }
        });
    }

    /**
     * Creates a new TreeGrid with the given data communicator and without
     * support for creating columns based on property names.
     *
     * @param dataCommunicator
     *            the custom data communicator to set
     */
    protected TreeGrid(HierarchicalDataCommunicator<T> dataCommunicator) {
        this(new PropertySet<T>() {
            @Override
            public Stream<PropertyDefinition<T, ?>> getProperties() {
                // No columns configured by default
                return Stream.empty();
            }

            @Override
            public Optional<PropertyDefinition<T, ?>> getProperty(String name) {
                throw new IllegalStateException(
                        "A TreeGrid created without a bean type class literal or a custom property set"
                                + " doesn't support finding properties by name.");
            }
        }, dataCommunicator);
    }

    /**
     * Creates a {@code TreeGrid} using a custom {@link PropertySet}
     * implementation for creating a default set of columns and for resolving
     * property names with {@link #addColumn(String)} and
     * {@link Column#setEditorComponent(HasValue)}.
     * <p>
     * This functionality is provided as static method instead of as a public
     * constructor in order to make it possible to use a custom property set
     * without creating a subclass while still leaving the public constructors
     * focused on the common use cases.
     *
     * @see TreeGrid#TreeGrid()
     * @see TreeGrid#TreeGrid(Class)
     *
     * @param propertySet
     *            the property set implementation to use, not {@code null}
     * @return a new tree grid using the provided property set, not {@code null}
     */
    public static <BEAN> TreeGrid<BEAN> withPropertySet(
            PropertySet<BEAN> propertySet) {
        return new TreeGrid<BEAN>(propertySet,
                new HierarchicalDataCommunicator<>());
    }

    /**
     * Adds an ExpandListener to this TreeGrid.
     *
     * @see ExpandEvent
     *
     * @param listener
     *            the listener to add
     * @return a registration for the listener
     */
    public Registration addExpandListener(ExpandListener<T> listener) {
        return addListener(ExpandEvent.class, listener,
                ExpandListener.EXPAND_METHOD);
    }

    /**
     * Adds a CollapseListener to this TreeGrid.
     *
     * @see CollapseEvent
     *
     * @param listener
     *            the listener to add
     * @return a registration for the listener
     */
    public Registration addCollapseListener(CollapseListener<T> listener) {
        return addListener(CollapseEvent.class, listener,
                CollapseListener.COLLAPSE_METHOD);
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        if (!(dataProvider instanceof HierarchicalDataProvider)) {
            throw new IllegalArgumentException(
                    "TreeGrid only accepts hierarchical data providers");
        }
        getRpcProxy(TreeGridClientRpc.class).clearPendingExpands();
        super.setDataProvider(dataProvider);
    }

    /**
     * Get the currently set hierarchy column.
     *
     * @return the currently set hierarchy column, or {@code null} if no column
     *         has been explicitly set
     */
    public Column<T, ?> getHierarchyColumn() {
        return getColumn(getState(false).hierarchyColumnId);
    }

    /**
     * Set the column that displays the hierarchy of this grid's data. By
     * default the hierarchy will be displayed in the first column.
     * <p>
     * Setting a hierarchy column by calling this method also sets the column to
     * be visible and not hidable.
     * <p>
     * <strong>Note:</strong> Changing the Renderer of the hierarchy column is
     * not supported.
     *
     * @param column
     *            the column to use for displaying hierarchy
     */
    public void setHierarchyColumn(Column<T, ?> column) {
        Objects.requireNonNull(column, "column may not be null");
        if (!getColumns().contains(column)) {
            throw new IllegalArgumentException(
                    "Given column is not a column of this TreeGrid");
        }
        column.setHidden(false);
        column.setHidable(false);
        getState().hierarchyColumnId = getInternalIdForColumn(column);
    }

    /**
     * Set the column that displays the hierarchy of this grid's data. By
     * default the hierarchy will be displayed in the first column.
     * <p>
     * Setting a hierarchy column by calling this method also sets the column to
     * be visible and not hidable.
     * <p>
     * <strong>Note:</strong> Changing the Renderer of the hierarchy column is
     * not supported.
     *
     * @see Column#setId(String)
     *
     * @param id
     *            id of the column to use for displaying hierarchy
     */
    public void setHierarchyColumn(String id) {
        Objects.requireNonNull(id, "id may not be null");
        if (getColumn(id) == null) {
            throw new IllegalArgumentException("No column found for given id");
        }
        setHierarchyColumn(getColumn(id));
    }

    /**
     * Sets the item collapse allowed provider for this TreeGrid. The provider
     * should return {@code true} for any item that the user can collapse.
     * <p>
     * <strong>Note:</strong> This callback will be accessed often when sending
     * data to the client. The callback should not do any costly operations.
     * <p>
     * This method is a shortcut to method with the same name in
     * {@link HierarchicalDataCommunicator}.
     *
     * @param provider
     *            the item collapse allowed provider, not {@code null}
     *
     * @see HierarchicalDataCommunicator#setItemCollapseAllowedProvider(ItemCollapseAllowedProvider)
     */
    public void setItemCollapseAllowedProvider(
            ItemCollapseAllowedProvider<T> provider) {
        getDataCommunicator().setItemCollapseAllowedProvider(provider);
    }

    /**
     * Expands the given items.
     * <p>
     * If an item is currently expanded, does nothing. If an item does not have
     * any children, does nothing.
     *
     * @param items
     *            the items to expand
     */
    public void expand(T... items) {
        expand(Arrays.asList(items));
    }

    /**
     * Expands the given items.
     * <p>
     * If an item is currently expanded, does nothing. If an item does not have
     * any children, does nothing.
     *
     * @param items
     *            the items to expand
     */
    public void expand(Collection<T> items) {
        List<String> expandedKeys = new ArrayList<>();
        List<T> expandedItems = new ArrayList<>();
        items.forEach(item -> getDataCommunicator().setPendingExpand(item)
                .ifPresent(key -> {
                    expandedKeys.add(key);
                    expandedItems.add(item);
                }));
        getRpcProxy(TreeGridClientRpc.class).setExpanded(expandedKeys);
        expandedItems.forEach(item -> fireExpandEvent(item, false));
    }

    /**
     * Collapse the given items.
     * <p>
     * For items that are already collapsed, does nothing.
     *
     * @param items
     *            the collection of items to collapse
     */
    public void collapse(T... items) {
        collapse(Arrays.asList(items));
    }

    /**
     * Collapse the given items.
     * <p>
     * For items that are already collapsed, does nothing.
     *
     * @param items
     *            the collection of items to collapse
     */
    public void collapse(Collection<T> items) {
        List<String> collapsedKeys = new ArrayList<>();
        List<T> collapsedItems = new ArrayList<>();
        items.forEach(item -> getDataCommunicator().collapseItem(item)
                .ifPresent(key -> {
                    collapsedKeys.add(key);
                    collapsedItems.add(item);
                }));
        getRpcProxy(TreeGridClientRpc.class).setCollapsed(collapsedKeys);
        collapsedItems.forEach(item -> fireCollapseEvent(item, false));
    }

    @Override
    protected TreeGridState getState() {
        return (TreeGridState) super.getState();
    }

    @Override
    protected TreeGridState getState(boolean markAsDirty) {
        return (TreeGridState) super.getState(markAsDirty);
    }

    @Override
    public HierarchicalDataCommunicator<T> getDataCommunicator() {
        return (HierarchicalDataCommunicator<T>) super.getDataCommunicator();
    }

    @Override
    public HierarchicalDataProvider<T, ?> getDataProvider() {
        if (!(super.getDataProvider() instanceof HierarchicalDataProvider)) {
            return null;
        }
        return (HierarchicalDataProvider<T, ?>) super.getDataProvider();
    }

    @Override
    protected void doReadDesign(Element design, DesignContext context) {
        super.doReadDesign(design, context);
        Attributes attrs = design.attributes();
        if (attrs.hasKey("hierarchy-column")) {
            setHierarchyColumn(DesignAttributeHandler
                    .readAttribute("hierarchy-column", attrs, String.class));
        }
    }

    @Override
    protected void readData(Element body,
            List<DeclarativeValueProvider<T>> providers) {
        getSelectionModel().deselectAll();
        List<T> selectedItems = new ArrayList<>();
        TreeData<T> data = new TreeData<T>();

        for (Element row : body.children()) {
            T item = deserializeDeclarativeRepresentation(row.attr("item"));
            T parent = null;
            if (row.hasAttr("parent")) {
                parent = deserializeDeclarativeRepresentation(
                        row.attr("parent"));
            }
            data.addItem(parent, item);
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

        setDataProvider(new TreeDataProvider<>(data));
        selectedItems.forEach(getSelectionModel()::select);
    }

    @Override
    protected void doWriteDesign(Element design, DesignContext designContext) {
        super.doWriteDesign(design, designContext);
        if (getColumnByInternalId(getState(false).hierarchyColumnId) != null) {
            String hierarchyColumn = getColumnByInternalId(
                    getState(false).hierarchyColumnId).getId();
            DesignAttributeHandler.writeAttribute("hierarchy-column",
                    design.attributes(), hierarchyColumn, null, String.class,
                    designContext);
        }
    }

    @Override
    protected void writeData(Element body, DesignContext designContext) {
        getDataProvider().fetch(new HierarchicalQuery<>(null, null))
                .forEach(item -> writeRow(body, item, null, designContext));
    }

    private void writeRow(Element container, T item, T parent,
            DesignContext context) {
        Element tableRow = container.appendElement("tr");
        tableRow.attr("item", serializeDeclarativeRepresentation(item));
        if (parent != null) {
            tableRow.attr("parent", serializeDeclarativeRepresentation(parent));
        }
        if (getSelectionModel().isSelected(item)) {
            tableRow.attr("selected", "");
        }
        for (Column<T, ?> column : getColumns()) {
            Object value = column.getValueProvider().apply(item);
            tableRow.appendElement("td")
                    .append(Optional.ofNullable(value).map(Object::toString)
                            .map(DesignFormatter::encodeForTextNode)
                            .orElse(""));
        }
        getDataProvider().fetch(new HierarchicalQuery<>(null, item)).forEach(
                childItem -> writeRow(container, childItem, item, context));
    }

    @Override
    protected <V> Column<T, V> createColumn(ValueProvider<T, V> valueProvider,
            AbstractRenderer<? super T, ? super V> renderer) {
        return new Column<T, V>(valueProvider, renderer) {

            @Override
            public com.vaadin.ui.Grid.Column<T, V> setRenderer(
                    Renderer<? super V> renderer) {
                // Disallow changing renderer for the hierarchy column
                if (getInternalIdForColumn(this).equals(
                        TreeGrid.this.getState(false).hierarchyColumnId)) {
                    throw new IllegalStateException(
                            "Changing the renderer of the hierarchy column is not allowed.");
                }

                return super.setRenderer(renderer);
            }
        };
    }

    /**
     * Emit an expand event.
     *
     * @param item
     *            the item that was expanded
     * @param userOriginated
     *            whether the expand was triggered by a user interaction or the
     *            server
     */
    private void fireExpandEvent(T item, boolean userOriginated) {
        fireEvent(new ExpandEvent<>(this, item, userOriginated));
    }

    /**
     * Emit a collapse event.
     *
     * @param item
     *            the item that was collapsed
     * @param userOriginated
     *            whether the collapse was triggered by a user interaction or
     *            the server
     */
    private void fireCollapseEvent(T item, boolean userOriginated) {
        fireEvent(new CollapseEvent<>(this, item, userOriginated));
    }

    /**
     * Gets the item collapse allowed provider.
     *
     * @return the item collapse allowed provider
     */
    public ItemCollapseAllowedProvider<T> getItemCollapseAllowedProvider() {
        return getDataCommunicator().getItemCollapseAllowedProvider();
    }
}
