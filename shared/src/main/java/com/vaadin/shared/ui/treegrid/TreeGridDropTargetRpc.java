/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import java.util.List;
import java.util.Map;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.grid.DropLocation;

/**
 * RPC for firing server side drop event when client side drop event happens on
 * drop target TreeGrid.
 *
 * @author Vaadin Ltd.
 * @since 8.1
 */
public interface TreeGridDropTargetRpc extends ServerRpc {

    /**
     * Called when drop event happens on client side.
     *
     * @param types
     *            list of data types from {@code DataTransfer.types} object
     * @param data
     *            map containing all types and corresponding data from the
     *            {@code
     *         DataTransfer} object
     * @param dropEffect
     *            the desired drop effect
     * @param rowKey
     *            key of the row on which the drop event occurred
     * @param depth
     *            depth of the row in the hierarchy
     * @param collapsed
     *            whether the target row is collapsed
     * @param dropLocation
     *            location of the drop within the row
     * @param mouseEventDetails
     *            Mouse event details object containing information about the
     *            drop event
     */
    public void drop(List<String> types, Map<String, String> data,
            String dropEffect, String rowKey, Integer depth, Boolean collapsed,
            DropLocation dropLocation, MouseEventDetails mouseEventDetails);
}
