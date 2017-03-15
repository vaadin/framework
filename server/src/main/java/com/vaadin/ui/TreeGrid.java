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
import java.util.Objects;
import java.util.stream.Stream;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.shared.ui.treegrid.NodeCollapseRpc;
import com.vaadin.shared.ui.treegrid.TreeGridCommunicationConstants;
import com.vaadin.shared.ui.treegrid.TreeGridState;
import com.vaadin.ui.renderers.AbstractRenderer;
import com.vaadin.ui.renderers.Renderer;

import elemental.json.Json;
import elemental.json.JsonObject;

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
        super();

        // Attaches hierarchy data to the row
        addDataGenerator((item, rowData) -> {

            JsonObject hierarchyData = Json.createObject();
            hierarchyData.put(TreeGridCommunicationConstants.ROW_DEPTH,
                    getDataProvider().getDepth(item));

            boolean isLeaf = !getDataProvider().hasChildren(item);
            if (isLeaf) {
                hierarchyData.put(TreeGridCommunicationConstants.ROW_LEAF,
                        true);
            } else {
                hierarchyData.put(TreeGridCommunicationConstants.ROW_COLLAPSED,
                        getDataProvider().isCollapsed(item));
                hierarchyData.put(TreeGridCommunicationConstants.ROW_LEAF,
                        false);
            }

            // add hierarchy information to row as metadata
            rowData.put(
                    TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION,
                    hierarchyData);
        });

        registerRpc(new NodeCollapseRpc() {
            @Override
            public void toggleCollapse(String rowKey) {
                T item = getDataCommunicator().getKeyMapper().get(rowKey);
                TreeGrid.this.toggleCollapse(item);
            }
        });
    }

    // TODO: construct a "flat" in memory hierarchical data provider?
    @Override
    public void setItems(Collection<T> items) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setItems(Stream<T> items) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setItems(T... items) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        if (!(dataProvider instanceof HierarchicalDataProvider)) {
            throw new IllegalArgumentException(
                    "TreeGrid only accepts hierarchical data providers");
        }
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

    @Override
    protected TreeGridState getState() {
        return (TreeGridState) super.getState();
    }

    @Override
    protected TreeGridState getState(boolean markAsDirty) {
        return (TreeGridState) super.getState(markAsDirty);
    }

    /**
     * Toggle the expansion of an item in this grid. If the item is already
     * expanded, it will be collapsed.
     * <p>
     * Toggling expansion on a leaf item in the hierarchy will have no effect.
     *
     * @param item
     *            the item to toggle expansion for
     */
    public void toggleCollapse(T item) {
        getDataProvider().setCollapsed(item,
                !getDataProvider().isCollapsed(item));
        getDataCommunicator().reset();
    }

    @Override
    public HierarchicalDataProvider<T, ?> getDataProvider() {
        DataProvider<T, ?> dataProvider = super.getDataProvider();
        // FIXME DataCommunicator by default has a CallbackDataProvider if no
        // DataProvider is set, resulting in a class cast exception if we don't
        // check it here.

        // Once fixed, remove this method from the exclude list in
        // StateGetDoesNotMarkDirtyTest
        if (!(dataProvider instanceof HierarchicalDataProvider)) {
            throw new IllegalStateException("No data provider has been set.");
        }
        return (HierarchicalDataProvider<T, ?>) dataProvider;
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
}
