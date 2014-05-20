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

package com.vaadin.client.data;

import java.util.List;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.grid.GridConnector;
import com.vaadin.shared.data.DataProviderRpc;
import com.vaadin.shared.data.DataProviderState;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.ui.Connect;

/**
 * Connects a Vaadin server-side container data source to a Grid. This is
 * currently implemented as an Extension hardcoded to support a specific
 * connector type. This will be changed once framework support for something
 * more flexible has been implemented.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
@Connect(com.vaadin.data.RpcDataProviderExtension.class)
public class RpcDataSourceConnector extends AbstractExtensionConnector {

    private final AbstractRemoteDataSource<String[]> dataSource = new AbstractRemoteDataSource<String[]>() {
        @Override
        protected void requestRows(int firstRowIndex, int numberOfRows) {
            getRpcProxy(DataRequestRpc.class).requestRows(firstRowIndex,
                    numberOfRows);
        }
    };

    @Override
    protected void extend(ServerConnector target) {
        dataSource.setEstimatedSize(getState().containerSize);
        ((GridConnector) target).getWidget().setDataSource(dataSource);

        registerRpc(DataProviderRpc.class, new DataProviderRpc() {
            @Override
            public void setRowData(int firstRow, List<String[]> rows) {
                dataSource.setRowData(firstRow, rows);
            }

            @Override
            public void removeRowData(int firstRow, int count) {
                dataSource.removeRowData(firstRow, count);
            }

            @Override
            public void insertRowData(int firstRow, int count) {
                dataSource.insertRowData(firstRow, count);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractConnector#getState()
     */
    @Override
    public DataProviderState getState() {
        return (DataProviderState) super.getState();
    }
}
