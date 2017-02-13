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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.server.EncodeResult;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.ColumnState;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.treegrid.TreeGridState;
import com.vaadin.ui.components.treegrid.HierarchyDataGenerator;
import com.vaadin.ui.components.treegrid.TreeGridNavigationExtension;

import elemental.json.JsonObject;

/**
 * 
 * @author Vaadin Ltd
 * @since 8.1
 * 
 * @param <T>
 */
public class TreeGrid<T> extends Grid<T> {

    private static final Logger logger = Logger
            .getLogger(TreeGrid.class.getName());

    public TreeGrid() {
        super();

        // Replace GridServerRpc with custom one to fix column reorder issue
        // (#6).
        swapServerRpc();

        // Attaches hierarchy data to the row
        HierarchyDataGenerator.extend(this);

        // Override keyboard navigation
        TreeGridNavigationExtension.extend(this);
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        if (!(dataProvider instanceof HierarchicalDataProvider)) {
            throw new RuntimeException(
                    "TreeGrid only accepts hierarchical data providers");
        }
        super.setDataProvider(dataProvider);
    }

    public void setHierarchyColumn(String id) {
        Objects.requireNonNull(id, "id may not be null");
        getState().hierarchyColumnId = id;
    }

    @Override
    protected TreeGridState getState() {
        return (TreeGridState) super.getState();
    }

    public void toggleExpansion(T item) {
        getDataProvider().setCollapsed(item,
                !getDataProvider().isCollapsed(item)); // Collapsible
        getDataCommunicator().reset();
    }

    /**
     * Replaces GridServerRpc instance with a custom one created by
     * {@link #createRpc(GridServerRpc)}.
     * <p>
     * Used as a temporary fix for https://github.com/vaadin/tree-grid/issues/6.
     */
    private void swapServerRpc() {
        try {
            // Get original RPC
            ServerRpcManager gridServerRpcManager = getRpcManager(
                    GridServerRpc.class.getName());
            Method getImplementation = gridServerRpcManager.getClass()
                    .getDeclaredMethod("getImplementation");
            getImplementation.setAccessible(true);
            GridServerRpc oldRpc = (GridServerRpc) getImplementation
                    .invoke(gridServerRpcManager);

            // Override RPC
            GridServerRpc newRpc = createRpc(oldRpc);

            // Replace old RPC with new one
            registerRpc(newRpc, GridServerRpc.class);

        } catch (NoSuchMethodException | InvocationTargetException
                | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Custom server rpc that wraps GridServerRpc. Overrides
     * {@link GridServerRpc#columnsReordered(List, List)} and
     * {@link GridServerRpc#columnVisibilityChanged(String, boolean, boolean)}
     * methods and delegates all others to the wrapped instance.
     */
    private GridServerRpc createRpc(final GridServerRpc innerRpc) {
        return new GridServerRpc() {
            @Override
            public void sort(String[] columnIds, SortDirection[] directions,
                    boolean userOriginated) {
                // Delegated to wrapped RPC
                innerRpc.sort(columnIds, directions, userOriginated);
            }

            @Override
            public void itemClick(String rowKey, String columnId,
                    MouseEventDetails details) {
                // Delegated to wrapped RPC
                innerRpc.itemClick(rowKey, columnId, details);
            }

            @Override
            public void contextClick(int rowIndex, String rowKey,
                    String columnId, GridConstants.Section section,
                    MouseEventDetails details) {
                // Delegated to wrapped RPC
                innerRpc.contextClick(rowIndex, rowKey, columnId, section,
                        details);
            }

            /**
             * Different from the one in Grid that it uses
             * {@link Class#getField(String)} to be able to access field in
             * super class.
             */
            @Override
            public void columnsReordered(List<String> newColumnOrder,
                    List<String> oldColumnOrder) {
                // Copied from wrapped RPC and modified
                final String diffStateKey = "columnOrder";
                ConnectorTracker connectorTracker = getUI()
                        .getConnectorTracker();
                JsonObject diffState = connectorTracker
                        .getDiffState(TreeGrid.this);
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
                        type = (getState(false).getClass()
                                .getField(diffStateKey).getGenericType());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    EncodeResult encodeResult = JsonCodec.encode(
                            getState(false).columnOrder, diffState, type,
                            connectorTracker);

                    diffState.put(diffStateKey, encodeResult.getEncodedValue());
                    fireEvent(new ColumnReorderEvent(TreeGrid.this, true));
                } else {
                    // make sure the client is reverted to the order that the
                    // server thinks it is
                    diffState.remove(diffStateKey);
                    markAsDirty();
                }
            }

            /**
             * Different from the one in Grid that it uses
             * {@link Class#getField(String)} to be able to access field in
             * super class.
             */
            @SuppressWarnings("unchecked")
            @Override
            public void columnVisibilityChanged(String columnInternalId,
                    boolean hidden) {
                // Copied from wrapped RPC and modified
                try {
                    final Column<T, ?> column;
                    Method getColumnByColumnId = Grid.class.getDeclaredMethod(
                            "getColumnByColumnId", String.class);
                    column = (Column<T, ?>) getColumnByColumnId
                            .invoke(TreeGrid.this,
                            columnInternalId);

                    Method getState = Column.class
                            .getDeclaredMethod("getState");

                    final ColumnState columnState = (ColumnState) getState
                            .invoke(column);

                    if (columnState.hidden != hidden) {
                        columnState.hidden = hidden;
                        fireEvent(new ColumnVisibilityChangeEvent(TreeGrid.this,
                                column, hidden, true));
                    }

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void columnResized(String id, double pixels) {
                // Delegated to wrapped RPC
                innerRpc.columnResized(id, pixels);
            }
        };
    }

    @Override
    public HierarchicalDataProvider<T, ?> getDataProvider() {
        return (HierarchicalDataProvider<T, ?>) super.getDataProvider();
    }
}
