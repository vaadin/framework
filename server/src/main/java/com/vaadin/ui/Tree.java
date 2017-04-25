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

import java.util.Collection;
import java.util.Set;

import com.vaadin.data.Binder;
import com.vaadin.data.HasDataProvider;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.CollapseEvent;
import com.vaadin.event.CollapseEvent.CollapseListener;
import com.vaadin.event.ExpandEvent;
import com.vaadin.event.ExpandEvent.ExpandListener;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;

/**
 * Tree component. A Tree can be used to select an item (or multiple items) from
 * a hierarchical set of items.
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            the data type
 */
public class Tree<T> extends Composite implements HasDataProvider<T> {

    private TreeGrid<T> treeGrid = new TreeGrid<>();
    private ValueProvider<T, String> captionProvider = Object::toString;
    private ValueProvider<T, Resource> iconProvider = t -> null;

    /**
     * Constructs a new Tree Component.
     */
    public Tree() {
        setCompositionRoot(treeGrid);
        treeGrid.addColumn(t -> {
            Label label = new Label(captionProvider.apply(t));
            label.setIcon(iconProvider.apply(t));
            return label;
        }, new ComponentRenderer()).setId("column");
        treeGrid.setHierarchyColumn("column");
        while (treeGrid.getHeaderRowCount() > 0) {
            treeGrid.removeHeaderRow(0);
        }
        // TODO: Primary style name
        // treeGrid.setPrimaryStyleName("v-tree");

        setWidth("100%");
        setHeightUndefined();
        treeGrid.setHeightMode(HeightMode.UNDEFINED);

        treeGrid.addExpandListener(e -> fireExpandEvent(e.getExpandedItem(),
                e.isUserOriginated()));
        treeGrid.addCollapseListener(e -> fireCollapseEvent(
                e.getCollapsedItem(), e.isUserOriginated()));
    }

    /**
     * Constructs a new Tree Component.
     *
     * @param caption
     *            the caption for component
     */
    public Tree(String caption) {
        this();

        setCaption(caption);
    }

    @Override
    public DataProvider<T, ?> getDataProvider() {
        return treeGrid.getDataProvider();
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        treeGrid.setDataProvider(dataProvider);
    }

    /**
     * Adds an ExpandListener to this Tree.
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
     * Adds a CollapseListener to this Tree.
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
     * Fires an expand event with given item.
     *
     * @param item
     *            the expanded item
     * @param userOriginated
     *            whether the expand was triggered by a user interaction or the
     *            server
     */
    protected void fireExpandEvent(T item, boolean userOriginated) {
        fireEvent(new ExpandEvent<>(this, item, userOriginated));
    }

    /**
     * Fires a collapse event with given item.
     *
     * @param item
     *            the collapsed item
     * @param userOriginated
     *            whether the collapse was triggered by a user interaction or
     *            the server
     */
    protected void fireCollapseEvent(T item, boolean userOriginated) {
        fireEvent(new CollapseEvent<>(this, item, userOriginated));
    }

    /**
     * Expands the given item.
     * <p>
     * If the item is currently expanded, does nothing. If the item does not
     * have any children, does nothing.
     *
     * @param item
     *            the item to expand
     */
    public void expand(T item) {
        treeGrid.expand(item);
    }

    /**
     * Collapses the given item.
     * <p>
     * If the item is already collapsed, does nothing.
     *
     * @param item
     *            the item to collapse
     */
    public void collapse(T item) {
        treeGrid.collapse(item);
    }

    /**
     * This method is a shorthand that delegates to the currently set selection
     * model.
     *
     * @see #getSelectionModel()
     *
     * @return set of selected items
     */
    public Set<T> getSelectedItems() {
        return treeGrid.getSelectedItems();
    }

    /**
     * This method is a shorthand that delegates to the currently set selection
     * model.
     *
     * @param item
     *            item to select
     *
     * @see SelectionModel#select(Object)
     * @see #getSelectionModel()
     */
    public void select(T item) {
        treeGrid.select(item);
    }

    /**
     * This method is a shorthand that delegates to the currently set selection
     * model.
     *
     * @param item
     *            item to deselect
     *
     * @see SelectionModel#deselect(Object)
     * @see #getSelectionModel()
     */
    public void deselect(T item) {
        treeGrid.deselect(item);
    }

    /**
     * Adds a selection listener to the current selection model.
     * <p>
     * <strong>NOTE:</strong> If selection mode is switched with
     * {@link setSelectionMode(SelectionMode)}, then this listener is not
     * triggered anymore when selection changes!
     *
     * @param listener
     *            the listener to add
     * @return a registration handle to remove the listener
     *
     * @throws UnsupportedOperationException
     *             if selection has been disabled with
     *             {@link SelectionMode.NONE}
     */
    public Registration addSelectionListener(SelectionListener<T> listener) {
        return treeGrid.addSelectionListener(listener);
    }

    /**
     * Use this tree as a single select in {@link Binder}. Throws
     * {@link IllegalStateException} if the tree is not using
     * {@link SelectionMode#SINGLE}.
     *
     * @return the single select wrapper that can be used in binder
     */
    public SingleSelect<T> asSingleSelect() {
        return treeGrid.asSingleSelect();
    }

    /**
     * Returns the selection model for this Tree.
     *
     * @return the selection model, not <code>null</code>
     */
    public SelectionModel<T> getSelectionModel() {
        return treeGrid.getSelectionModel();
    }

    @Override
    public void setItems(Collection<T> items) {
        treeGrid.setItems(items);
    }
}
