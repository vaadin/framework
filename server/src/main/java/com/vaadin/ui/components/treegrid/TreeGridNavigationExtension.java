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

import com.vaadin.shared.ui.treegrid.NodeCollapseRpc;
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
public class TreeGridNavigationExtension<T> extends AbstractGridExtension<T> {

    private TreeGridNavigationExtension(final TreeGrid<T> grid) {
        registerRpc(new NodeCollapseRpc() {
            @Override
            public void toggleCollapse(String rowKey) {
                T item = getParent().getDataCommunicator().getKeyMapper()
                        .get(rowKey);
                grid.toggleExpansion(item);
            }
        });
        super.extend(grid);
    }

    public static <T> TreeGridNavigationExtension<T> extend(TreeGrid<T> grid) {
        return new TreeGridNavigationExtension<>(grid);
    }

    @Override
    public void generateData(Object item, JsonObject jsonObject) {
        // TODO Auto-generated method stub
    }
}
