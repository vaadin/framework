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

package com.vaadin.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.Container.Indexed;
import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.data.DataProviderRpc;
import com.vaadin.shared.data.DataProviderState;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.ui.components.grid.Grid;

/**
 * Provides Vaadin server-side container data source to a
 * {@link com.vaadin.client.ui.grid.GridConnector}. This is currently
 * implemented as an Extension hardcoded to support a specific connector type.
 * This will be changed once framework support for something more flexible has
 * been implemented.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class RpcDataProviderExtension extends AbstractExtension {

    private final Indexed container;

    /**
     * Creates a new data provider using the given container.
     * 
     * @param container
     *            the container to make available
     */
    public RpcDataProviderExtension(Indexed container) {
        this.container = container;

        // TODO support for reacting to events from the container added later

        registerRpc(new DataRequestRpc() {
            @Override
            public void requestRows(int firstRow, int numberOfRows) {
                pushRows(firstRow, numberOfRows);
            }
        });

        getState().containerSize = container.size();
    }

    private void pushRows(int firstRow, int numberOfRows) {
        List<?> itemIds = container.getItemIds(firstRow, numberOfRows);
        Collection<?> propertyIds = container.getContainerPropertyIds();
        List<String[]> rows = new ArrayList<String[]>(itemIds.size());
        for (Object itemId : itemIds) {
            rows.add(getRowData(propertyIds, itemId));
        }
        getRpcProxy(DataProviderRpc.class).setRowData(firstRow, rows);
    }

    private String[] getRowData(Collection<?> propertyIds, Object itemId) {
        Item item = container.getItem(itemId);
        String[] row = new String[propertyIds.size()];

        int i = 0;
        for (Object propertyId : propertyIds) {
            Object value = item.getItemProperty(propertyId).getValue();
            String stringValue = String.valueOf(value);
            row[i++] = stringValue;
        }
        return row;
    }

    @Override
    protected DataProviderState getState() {
        return (DataProviderState) super.getState();
    }

    /**
     * Makes the data source available to the given {@link Grid} component.
     * 
     * @param component
     *            the remote data grid component to extend
     */
    public void extend(Grid component) {
        super.extend(component);
    }

    /**
     * Informs the client side that new rows have been inserted into the data
     * source.
     * 
     * @param index
     *            the index at which new rows have been inserted
     * @param count
     *            the number of rows inserted at <code>index</code>
     */
    public void insertRowData(int index, int count) {
        getState().containerSize += count;
        getRpcProxy(DataProviderRpc.class).insertRowData(index, count);
    }

    /**
     * Informs the client side that rows have been removed from the data source.
     * 
     * @param firstIndex
     *            the index of the first row removed
     * @param count
     *            the number of rows removed
     */
    public void removeRowData(int firstIndex, int count) {
        getState().containerSize -= count;
        getRpcProxy(DataProviderRpc.class).removeRowData(firstIndex, count);
    }

    /**
     * Informs the client side that data of a row has been modified in the data
     * source.
     * 
     * @param index
     *            the index of the row that was updated
     */
    public void updateRowData(int index) {
        /*
         * TODO: ignore duplicate requests for the same index during the same
         * roundtrip.
         */
        Object itemId = container.getIdByIndex(index);
        String[] row = getRowData(container.getContainerPropertyIds(), itemId);
        getRpcProxy(DataProviderRpc.class).setRowData(index,
                Collections.singletonList(row));
    }
}
