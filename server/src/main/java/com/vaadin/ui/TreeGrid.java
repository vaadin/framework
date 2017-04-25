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

import com.vaadin.data.HierarchyData;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.HierarchicalDataCommunicator;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import com.vaadin.data.provider.InMemoryHierarchicalDataProvider;
import com.vaadin.event.CollapseEvent;
import com.vaadin.event.CollapseEvent.CollapseListener;
import com.vaadin.event.ExpandEvent;
import com.vaadin.event.ExpandEvent.ExpandListener;
import com.vaadin.server.SerializablePredicate;
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
public class TreeGrid<T> extends Grid<T> {

    public TreeGrid() {
        super(new HierarchicalDataCommunicator<>());

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

    /**
     * Sets the data items of this component provided as a collection.
     * <p>
     * The provided items are wrapped into a
     * {@link InMemoryHierarchicalDataProvider} backed by a flat
     * {@link HierarchyData} structure. The data provider instance is used as a
     * parameter for the {@link #setDataProvider(DataProvider)} method. It means
     * that the items collection can be accessed later on via
     * {@link InMemoryHierarchicalDataProvider#getData()}:
     *
     * <pre>
     * <code>
     * TreeGrid<String> treeGrid = new TreeGrid<>();
     * treeGrid.setItems(Arrays.asList("a","b"));
     * ...
     *
     * HierarchyData<String> data = ((InMemoryHierarchicalDataProvider<String>)treeGrid.getDataProvider()).getData();
     * </code>
     * </pre>
     * <p>
     * The returned HierarchyData instance may be used as-is to add, remove or
     * modify items in the hierarchy. These modifications to the object are not
     * automatically reflected back to the TreeGrid. Items modified should be
     * refreshed with {@link HierarchicalDataProvider#refreshItem(Object)} and
     * when adding or removing items
     * {@link HierarchicalDataProvider#refreshAll()} should be called.
     *
     * @param items
     *            the data items to display, not null
     */
    @Override
    public void setItems(Collection<T> items) {
        Objects.requireNonNull(items, "Given collection may not be null");
        setDataProvider(new InMemoryHierarchicalDataProvider<>(
                new HierarchyData<T>().addItems(null, items)));
    }

    /**
     * Sets the data items of this component provided as a stream.
     * <p>
     * The provided items are wrapped into a
     * {@link InMemoryHierarchicalDataProvider} backed by a flat
     * {@link HierarchyData} structure. The data provider instance is used as a
     * parameter for the {@link #setDataProvider(DataProvider)} method. It means
     * that the items collection can be accessed later on via
     * {@link InMemoryHierarchicalDataProvider#getData()}:
     *
     * <pre>
     * <code>
     * TreeGrid<String> treeGrid = new TreeGrid<>();
     * treeGrid.setItems(Stream.of("a","b"));
     * ...
     *
     * HierarchyData<String> data = ((InMemoryHierarchicalDataProvider<String>)treeGrid.getDataProvider()).getData();
     * </code>
     * </pre>
     * <p>
     * The returned HierarchyData instance may be used as-is to add, remove or
     * modify items in the hierarchy. These modifications to the object are not
     * automatically reflected back to the TreeGrid. Items modified should be
     * refreshed with {@link HierarchicalDataProvider#refreshItem(Object)} and
     * when adding or removing items
     * {@link HierarchicalDataProvider#refreshAll()} should be called.
     *
     * @param items
     *            the data items to display, not null
     */
    @Override
    public void setItems(Stream<T> items) {
        Objects.requireNonNull(items, "Given stream may not be null");
        setDataProvider(new InMemoryHierarchicalDataProvider<>(
                new HierarchyData<T>().addItems(null, items)));
    }

    /**
     * Sets the data items of this listing.
     * <p>
     * The provided items are wrapped into a
     * {@link InMemoryHierarchicalDataProvider} backed by a flat
     * {@link HierarchyData} structure. The data provider instance is used as a
     * parameter for the {@link #setDataProvider(DataProvider)} method. It means
     * that the items collection can be accessed later on via
     * {@link InMemoryHierarchicalDataProvider#getData()}:
     *
     * <pre>
     * <code>
     * TreeGrid<String> treeGrid = new TreeGrid<>();
     * treeGrid.setItems("a","b");
     * ...
     *
     * HierarchyData<String> data = ((InMemoryHierarchicalDataProvider<String>)treeGrid.getDataProvider()).getData();
     * </code>
     * </pre>
     * <p>
     * The returned HierarchyData instance may be used as-is to add, remove or
     * modify items in the hierarchy. These modifications to the object are not
     * automatically reflected back to the TreeGrid. Items modified should be
     * refreshed with {@link HierarchicalDataProvider#refreshItem(Object)} and
     * when adding or removing items
     * {@link HierarchicalDataProvider#refreshAll()} should be called.
     *
     * @param items
     *            the data items to display, not null
     */
    @Override
    public void setItems(@SuppressWarnings("unchecked") T... items) {
        Objects.requireNonNull(items, "Given items may not be null");
        setDataProvider(new InMemoryHierarchicalDataProvider<>(
                new HierarchyData<T>().addItems(null, items)));
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
        getColumn(id).setHidden(false);
        getColumn(id).setHidable(false);
        getState().hierarchyColumnId = getInternalIdForColumn(getColumn(id));
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
     * @see HierarchicalDataCommunicator#setItemCollapseAllowedProvider(SerializablePredicate)
     */
    public void setItemCollapseAllowedProvider(
            SerializablePredicate<T> provider) {
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
        HierarchyData<T> data = new HierarchyData<T>();

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

        setDataProvider(new InMemoryHierarchicalDataProvider<>(data));
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
}
