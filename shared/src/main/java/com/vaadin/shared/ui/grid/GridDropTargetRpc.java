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
package com.vaadin.shared.ui.grid;

import java.util.List;
import java.util.Map;

import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC for firing server side drop event when client side drop event happens on
 * drop target Grid.
 *
 * @author Vaadin Ltd.
 * @since 8.1
 */
public interface GridDropTargetRpc extends ServerRpc {

    /**
     * Called when drop event happens on client side.
     *
     * @param types
     *         List of data types from {@code DataTransfer.types} object.
     * @param data
     *         Map containing all types and corresponding data from the {@code
     *         DataTransfer} object.
     * @param dropEffect
     *         the desired drop effect
     * @param rowKey
     *         Key of the row on which the drop event occured.
     * @param dropLocation
     *         Location of the drop within the row.
     */
    public void drop(List<String> types, Map<String, String> data,
            String dropEffect, String rowKey,
            DropLocation dropLocation);
}
