/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import java.util.List;

import com.vaadin.shared.communication.ClientRpc;

/**
 * RPC interface used for pushing container data to the client.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface DataProviderRpc extends ClientRpc {

    /**
     * Sends updated row data to a client.
     * 
     * @param firstRowIndex
     *            the index of the first updated row
     * @param rowData
     *            the updated row data
     */
    public void setRowData(int firstRowIndex, List<String[]> rowData);

    /**
     * Informs the client to remove row data.
     * 
     * @param firstRowIndex
     *            the index of the first removed row
     * @param count
     *            the number of rows removed from <code>firstRowIndex</code> and
     *            onwards
     */
    public void removeRowData(int firstRowIndex, int count);

    /**
     * Informs the client to insert new row data.
     * 
     * @param firstRowIndex
     *            the index of the first new row
     * @param count
     *            the number of rows inserted at <code>firstRowIndex</code>
     */
    public void insertRowData(int firstRowIndex, int count);
}
