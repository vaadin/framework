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

import com.vaadin.data.HierarchyData;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.server.JsonCodec;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.treegrid.NodeCollapseRpc;
import com.vaadin.shared.ui.treegrid.TreeGridState;

/**
 * 
 * @author Vaadin Ltd
 * @since 8.1
 * 
 * @param <T>
 */
public class TreeGrid<T> extends Grid<T> {

    public TreeGrid() {
        super();

        // Attaches hierarchy data to the row
        addDataGenerator((item, rowData) -> {
            HierarchyData hierarchyData = new HierarchyData();

            // calculate depth
            hierarchyData.setDepth(getDataProvider().getDepth(item));

            // set collapsed state
            hierarchyData.setCollapsed(getDataProvider().isCollapsed(item));

            // set leaf state
            hierarchyData.setLeaf(!getDataProvider().hasChildren(item));

            // add hierarchy information to row as metadata
            rowData.put(GridState.JSONKEY_ROWDESCRIPTION,
                    JsonCodec
                            .encode(hierarchyData, null, HierarchyData.class,
                                    getUI().getConnectorTracker())
                            .getEncodedValue());
        });

        registerRpc(new NodeCollapseRpc() {
            @Override
            public void toggleCollapse(String rowKey) {
                T item = getDataCommunicator().getKeyMapper().get(rowKey);
                toggleExpansion(item);
            }
        });
    }

    // TODO: construct a "flat" in memory hierarchical data provider?
    @Override
    public void setItems(Collection<T> items) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setItems(Stream<T> items) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setItems(T... items) {
        throw new UnsupportedOperationException();
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
        if (getColumn(id) == null) {
            throw new RuntimeException("No column found for given id");
        }
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

    public void toggleExpansion(T item) {
        getDataProvider().setCollapsed(item,
                !getDataProvider().isCollapsed(item));
        getDataCommunicator().reset();
    }

    @Override
    public HierarchicalDataProvider<T, ?> getDataProvider() {
        return (HierarchicalDataProvider<T, ?>) super.getDataProvider();
    }
}
