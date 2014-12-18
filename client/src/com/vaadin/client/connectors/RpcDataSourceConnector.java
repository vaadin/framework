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

package com.vaadin.client.connectors;

import java.util.ArrayList;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.data.AbstractRemoteDataSource;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.data.DataProviderRpc;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.Range;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonType;
import elemental.json.JsonValue;

/**
 * Connects a Vaadin server-side container data source to a Grid. This is
 * currently implemented as an Extension hardcoded to support a specific
 * connector type. This will be changed once framework support for something
 * more flexible has been implemented.
 * 
 * @since
 * @author Vaadin Ltd
 */
@Connect(com.vaadin.data.RpcDataProviderExtension.class)
public class RpcDataSourceConnector extends AbstractExtensionConnector {

    public class RpcDataSource extends AbstractRemoteDataSource<JsonObject> {

        protected RpcDataSource() {
            registerRpc(DataProviderRpc.class, new DataProviderRpc() {
                @Override
                public void setRowData(int firstRow, String rowsJson) {
                    JsonValue parsedJson = Json.instance().parse(rowsJson);
                    assert parsedJson.getType() == JsonType.ARRAY : "Was unable to parse JSON into an array: "
                            + parsedJson;
                    JsonArray rowArray = (JsonArray) parsedJson;

                    ArrayList<JsonObject> rows = new ArrayList<JsonObject>(
                            rowArray.length());
                    for (int i = 0; i < rowArray.length(); i++) {
                        JsonObject rowObject = rowArray.getObject(i);
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

                @Override
                public void resetDataAndSize(int size) {
                    dataSource.resetDataAndSize(size);
                }
            });
        }

        private DataRequestRpc rpcProxy = getRpcProxy(DataRequestRpc.class);

        @Override
        protected void requestRows(int firstRowIndex, int numberOfRows,
                RequestRowsCallback<JsonObject> callback) {
            /*
             * If you're looking at this code because you want to learn how to
             * use AbstactRemoteDataSource, please look somewhere else instead.
             * 
             * We're not doing things in the conventional way with the callback
             * here since Vaadin doesn't directly support RPC with return
             * values. We're instead asking the server to push us some data, and
             * when we receive pushed data, we just push it along to the
             * underlying cache in the same way no matter if it was a genuine
             * push or just a result of us requesting rows.
             */

            Range cached = getCachedRange();

            rpcProxy.requestRows(firstRowIndex, numberOfRows,
                    cached.getStart(), cached.length());
        }

        @Override
        public String getRowKey(JsonObject row) {
            if (row.hasKey(GridState.JSONKEY_ROWKEY)) {
                return row.getString(GridState.JSONKEY_ROWKEY);
            } else {
                return null;
            }
        }

        public RowHandle<JsonObject> getHandleByKey(Object key) {
            JsonObject row = Json.createObject();
            row.put(GridState.JSONKEY_ROWKEY, (String) key);
            return new RowHandleImpl(row, key);
        }

        @Override
        protected void pinHandle(RowHandleImpl handle) {
            // Server only knows if something is pinned or not. No need to pin
            // multiple times.
            boolean pinnedBefore = handle.isPinned();
            super.pinHandle(handle);
            if (!pinnedBefore) {
                rpcProxy.setPinned(getRowKey(handle.getRow()), true);
            }
        }

        @Override
        protected void unpinHandle(RowHandleImpl handle) {
            // Row data is no longer available after it has been unpinned.
            String key = getRowKey(handle.getRow());
            super.unpinHandle(handle);
            if (!handle.isPinned()) {
                rpcProxy.setPinned(key, false);
            }

        }
    }

    private final RpcDataSource dataSource = new RpcDataSource();

    @Override
    protected void extend(ServerConnector target) {
        ((GridConnector) target).setDataSource(dataSource);
    }
}
