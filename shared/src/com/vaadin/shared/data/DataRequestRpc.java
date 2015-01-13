/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.shared.data;

import com.vaadin.shared.annotations.NoLoadingIndicator;
import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC interface used for requesting container data to the client.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface DataRequestRpc extends ServerRpc {

    /**
     * Request rows from the server.
     * 
     * @param firstRowIndex
     *            the index of the first requested row
     * @param numberOfRows
     *            the number of requested rows
     * @param firstCachedRowIndex
     *            the index of the first cached row
     * @param cacheSize
     *            the number of cached rows
     */
    @NoLoadingIndicator
    public void requestRows(int firstRowIndex, int numberOfRows,
            int firstCachedRowIndex, int cacheSize);

    /**
     * Informs the server that an item referenced with a key pinned status has
     * changed. This is a delayed call that happens along with next rpc call to
     * server.
     * 
     * @param key
     *            key mapping to item
     * @param isPinned
     *            pinned status of referenced item
     */
    @Delayed
    public void setPinned(String key, boolean isPinned);
}
