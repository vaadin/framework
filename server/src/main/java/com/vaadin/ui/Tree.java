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

import java.util.Set;

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.TreeGrid.CollapseEvent;
import com.vaadin.ui.TreeGrid.CollapseListener;
import com.vaadin.ui.TreeGrid.ExpandEvent;
import com.vaadin.ui.TreeGrid.ExpandListener;
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
        // TODO: Rework expand event to work with the Tree as its source
        return treeGrid.addExpandListener(listener);
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
        // TODO: Rework collapse event to work with the Tree as its source
        return treeGrid.addCollapseListener(listener);
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

    // TODO: JavaDocs

    public Set<T> getSelectedItems() {
        return treeGrid.getSelectedItems();
    }

    public void select(T item) {
        treeGrid.select(item);
    }

    public void deselect(T item) {
        treeGrid.deselect(item);
    }

    public void deselectAll() {
        treeGrid.deselectAll();
    }

    public Registration addSelectionListener(SelectionListener<T> listener) {
        return treeGrid.addSelectionListener(listener);
    }

    public SingleSelect<T> asSingleSelect() {
        return treeGrid.asSingleSelect();
    }

    // TODO: Is multiple item selection needed?
    // public MultiSelect<T> asMultiSelect() {
    // return treeGrid.asMultiSelect();
    // }
    //
    // public GridSelectionModel<T> setSelectionMode(SelectionMode
    // selectionMode) {
    // return treeGrid.setSelectionMode(selectionMode);
    // }
    //
    // public GridSelectionModel<T> getSelectionModel() {
    // return treeGrid.getSelectionModel();
    // }

}
