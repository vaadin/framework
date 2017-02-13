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
package com.vaadin.ui.components.treegrid;

import com.vaadin.data.HierarchyData;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.server.JsonCodec;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.Grid.AbstractGridExtension;
import com.vaadin.ui.TreeGrid;

import elemental.json.JsonObject;

/**
 * 
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 */
public class HierarchyDataGenerator<T> extends AbstractGridExtension<T> {

    private HierarchyDataGenerator(TreeGrid<T> grid) {
        super.extend(grid);
    }

    public static <T> HierarchyDataGenerator<T> extend(TreeGrid<T> grid) {
        return new HierarchyDataGenerator<>(grid);
    }

    @Override
    public void generateData(T item, JsonObject rowData) {
        HierarchyData hierarchyData = new HierarchyData();

        // calculate depth
        hierarchyData.setDepth(getDataProvider().getDepth(item));

        // set collapsed state
        hierarchyData.setCollapsed(getDataProvider().isCollapsed(item)); // Collapsible

        // set leaf state
        hierarchyData.setLeaf(!getDataProvider().hasChildren(item)); // Hierarchical

        // add hierarchy information to row as metadata
        rowData.put(GridState.JSONKEY_ROWDESCRIPTION,
                JsonCodec
                        .encode(hierarchyData, null, HierarchyData.class,
                                getUI().getConnectorTracker())
                        .getEncodedValue());
    }

    @Override
    public void destroyData(Object itemId) {
        // Nothing to clean up
    }

    /**
     * Get container data source that implements
     * {@link com.vaadin.data.Container.Indexed} and
     * {@link com.vaadin.data.Container.Hierarchical} as well.
     *
     * @return TreeGrid's hierarchical data provider
     */
    private HierarchicalDataProvider<T, ?> getDataProvider() {
        // TreeGrid's data source has to implement both Indexed and Hierarchical
        // so it is safe to cast.
        return (HierarchicalDataProvider<T, ?>) getParent().getDataProvider();
    }
}
