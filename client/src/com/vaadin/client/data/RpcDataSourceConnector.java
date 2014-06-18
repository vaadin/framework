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

import java.util.ArrayList;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.grid.GridConnector;
import com.vaadin.shared.data.DataProviderRpc;
import com.vaadin.shared.data.DataProviderState;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.Range;

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

    private final AbstractRemoteDataSource<JSONObject> dataSource = new AbstractRemoteDataSource<JSONObject>() {
        @Override
        protected void requestRows(int firstRowIndex, int numberOfRows) {
            Range cached = getCachedRange();

            getRpcProxy(DataRequestRpc.class).requestRows(firstRowIndex,
                    numberOfRows, cached.getStart(), cached.length());
        }

        @Override
        public Object getRowKey(JSONObject row) {
            /*
             * FIXME will be properly implemented by another patch (Henrik Paul:
             * 16.6.2014)
             */
            return row;
        }
    };

    @Override
    protected void extend(ServerConnector target) {
        dataSource.setEstimatedSize(getState().containerSize);
        ((GridConnector) target).getWidget().setDataSource(dataSource);

        registerRpc(DataProviderRpc.class, new DataProviderRpc() {
            @Override
            public void setRowData(int firstRow, String rowsJson) {
                JSONValue parsedJson = JSONParser.parseStrict(rowsJson);
                JSONArray rowArray = parsedJson.isArray();
                assert rowArray != null : "Was unable to parse JSON into an array: "
                        + parsedJson;

                ArrayList<JSONObject> rows = new ArrayList<JSONObject>(rowArray
                        .size());
                for (int i = 0; i < rowArray.size(); i++) {
                    JSONValue rowValue = rowArray.get(i);
                    JSONObject rowObject = rowValue.isObject();
                    assert rowObject != null : "Was unable to parse JSON into an object: "
                            + rowValue;
                    rows.add(rowObject);
                }

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
