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
package com.vaadin.shared.ui.treegrid;

import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC to handle client originated collapse and expand actions on hierarchical
 * rows in TreeGrid.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@FunctionalInterface
public interface NodeCollapseRpc extends ServerRpc {

    /**
     * Sets the collapse state of a hierarchical row in TreeGrid.
     *
     * @param rowKey
     *            the row's key
     * @param rowIndex
     *            index where the row is in grid (all rows)
     * @param collapse
     *            {@code true} to collapse, {@code false} to expand
     * @param userOriginated
     *            {@code true} if this RPC was triggered by a user interaction,
     *            {@code false} otherwise
     */
    void setNodeCollapsed(String rowKey, int rowIndex, boolean collapse,
            boolean userOriginated);
}
