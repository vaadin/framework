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
package com.vaadin.v7.server.communication.data;

import java.io.Serializable;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Grid.AbstractGridExtension;

import elemental.json.JsonObject;

/**
 * Interface for {@link AbstractGridExtension}s that allows adding data to row
 * objects being sent to client by the {@link RpcDataProviderExtension}.
 * <p>
 * This class also provides a way to remove any unneeded data once the data
 * object is no longer used on the client-side.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public interface DataGenerator extends Serializable {

    /**
     * Adds data to row object for given item and item id being sent to client.
     *
     * @param itemId
     *            item id of item
     * @param item
     *            item being sent to client
     * @param rowData
     *            row object being sent to client
     */
    public void generateData(Object itemId, Item item, JsonObject rowData);

    /**
     * Informs the DataGenerator that an item id has been dropped and is no
     * longer needed. This method should clean up any unneeded stored data
     * related to the item.
     *
     * @param itemId
     *            removed item id
     */
    public void destroyData(Object itemId);
}
