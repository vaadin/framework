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
import java.util.List;

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

    /**
     * A callback interface to let {@link GridConnector} know that detail
     * visibilities might have changed.
     * 
     * @since 7.5.0
     * @author Vaadin Ltd
     */
    interface DetailsListener {

        /**
         * A request to verify (and correct) the visibility for a row, given
         * updated metadata.
         * 
         * @param rowIndex
         *            the index of the row that should be checked
         * @param row
         *            the row object to check visibility for
         * @see GridState#JSONKEY_DETAILS_VISIBLE
         */
        void reapplyDetailsVisibility(int rowIndex, JsonObject row);
    }

    public class RpcDataSource extends AbstractRemoteDataSource<JsonObject> {

        protected RpcDataSource() {
            registerRpc(DataProviderRpc.class, new DataProviderRpc() {
                @Override
                public void setRowData(int firstRow, JsonArray rowArray) {
                    ArrayList<JsonObject> rows = new ArrayList<JsonObject>(
                            rowArray.length());
                    for (int i = 0; i < rowArray.length(); i++) {
                        JsonObject rowObject = rowArray.getObject(i);
                        rows.add(rowObject);
                    }

                    RpcDataSource.this.setRowData(firstRow, rows);
                }

                @Override
                public void removeRowData(int firstRow, int count) {
                    RpcDataSource.this.removeRowData(firstRow, count);
                }

                @Override
                public void insertRowData(int firstRow, int count) {
                    RpcDataSource.this.insertRowData(firstRow, count);
                }

                @Override
                public void resetDataAndSize(int size) {
                    RpcDataSource.this.resetDataAndSize(size);
                }
            });
        }

        private DataRequestRpc rpcProxy = getRpcProxy(DataRequestRpc.class);
        private DetailsListener detailsListener;

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

            /*
             * Show the progress indicator if there is a pending data request
             * and some of the visible rows are being requested. The RPC in
             * itself will not trigger the indicator since it might just fetch
             * some rows in the background to fill the cache.
             * 
             * The indicator will be hidden by the framework when the response
             * is received (unless another request is already on its way at that
             * point).
             */
            if (getRequestedAvailability().intersects(
                    Range.withLength(firstRowIndex, numberOfRows))) {
                getConnection().getLoadingIndicator().ensureTriggered();
            }
        }

        @Override
        public void ensureAvailability(int firstRowIndex, int numberOfRows) {
            super.ensureAvailability(firstRowIndex, numberOfRows);

            /*
             * We trigger the indicator already at this point since the actual
             * RPC will not be sent right away when waiting for the response to
             * a previous request.
             * 
             * Only triggering here would not be enough since the check that
             * sets isWaitingForData is deferred. We don't want to trigger the
             * loading indicator here if we don't know that there is actually a
             * request going on since some other bug might then cause the
             * loading indicator to not be hidden.
             */
            if (isWaitingForData()
                    && !Range.withLength(firstRowIndex, numberOfRows)
                            .isSubsetOf(getCachedRange())) {
                getConnection().getLoadingIndicator().ensureTriggered();
            }
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

        void setDetailsListener(DetailsListener detailsListener) {
            this.detailsListener = detailsListener;
        }

        @Override
        protected void setRowData(int firstRowIndex, List<JsonObject> rowData) {
            super.setRowData(firstRowIndex, rowData);

            /*
             * Intercepting details information from the data source, rerouting
             * them back to the GridConnector (as a details listener)
             */
            for (int i = 0; i < rowData.size(); i++) {
                detailsListener.reapplyDetailsVisibility(firstRowIndex + i,
                        rowData.get(i));
            }
        }
    }

    private final RpcDataSource dataSource = new RpcDataSource();

    @Override
    protected void extend(ServerConnector target) {
        GridConnector gridConnector = (GridConnector) target;
        dataSource.setDetailsListener(gridConnector.getDetailsListener());
        gridConnector.setDataSource(dataSource);
    }
}
