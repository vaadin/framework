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
package com.vaadin.client.connectors.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.EditorHandler;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.editor.EditorClientRpc;
import com.vaadin.shared.ui.grid.editor.EditorServerRpc;
import com.vaadin.shared.ui.grid.editor.EditorState;
import com.vaadin.ui.components.grid.EditorImpl;

import elemental.json.JsonObject;

/**
 * Connector for Grid Editor.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
@Connect(EditorImpl.class)
public class EditorConnector extends AbstractExtensionConnector {

    /**
     * EditorHandler for communicating with the server-side implementation.
     */
    private class CustomEditorHandler implements EditorHandler<JsonObject> {
        private EditorServerRpc rpc = getRpcProxy(EditorServerRpc.class);
        private EditorRequest<JsonObject> currentRequest = null;
        private boolean serverInitiated = false;

        public CustomEditorHandler() {
            registerRpc(EditorClientRpc.class, new EditorClientRpc() {
                @Override
                public void cancel() {
                    serverInitiated = true;
                    getParent().getWidget().cancelEditor();
                }

                @Override
                public void confirmBind(final boolean bindSucceeded) {
                    endRequest(bindSucceeded);
                }

                @Override
                public void confirmSave(boolean saveSucceeded) {
                    endRequest(saveSucceeded);
                }

                @Override
                public void setErrorMessage(String errorMessage,
                        List<String> errorColumnsIds) {
                    Collection<Column<?, JsonObject>> errorColumns;
                    if (errorColumnsIds != null) {
                        errorColumns = new ArrayList<>();
                        for (String colId : errorColumnsIds) {
                            errorColumns.add(getParent().getColumn(colId));
                        }
                    } else {
                        errorColumns = null;
                    }
                    getParent().getWidget().getEditor()
                            .setEditorError(errorMessage, errorColumns);
                }
            });
        }

        @Override
        public void bind(EditorRequest<JsonObject> request) {
            startRequest(request);
            rpc.bind(getRowKey(request.getRow()));
        }

        @Override
        public void save(EditorRequest<JsonObject> request) {
            startRequest(request);
            rpc.save();
        }

        @Override
        public void cancel(EditorRequest<JsonObject> request,
                boolean afterBeingSaved) {
            if (!handleServerInitiated(request)) {
                // No startRequest as we don't get (or need)
                // a confirmation from the server
                rpc.cancel(afterBeingSaved);
            }
        }

        @Override
        public Widget getWidget(Column<?, JsonObject> column) {
            String connId = getState().columnFields
                    .get(getParent().getColumnId(column));
            if (connId == null) {
                return null;
            }
            return getConnector(connId).getWidget();
        }

        private ComponentConnector getConnector(String id) {
            return (ComponentConnector) ConnectorMap.get(getConnection())
                    .getConnector(id);
        }

        /**
         * Used to handle the case where the editor calls us because it was
         * invoked by the server via RPC and not by the client. In that case,
         * the request can be simply synchronously completed.
         *
         * @param request
         *            the request object
         * @return true if the request was originally triggered by the server,
         *         false otherwise
         */
        private boolean handleServerInitiated(EditorRequest<?> request) {
            assert request != null : "Cannot handle null request";
            assert currentRequest == null : "Earlier request not yet finished";

            if (serverInitiated) {
                serverInitiated = false;
                request.success();
                return true;
            } else {
                return false;
            }
        }

        private void startRequest(EditorRequest<JsonObject> request) {
            assert currentRequest == null : "Earlier request not yet finished";

            currentRequest = request;
        }

        private void endRequest(boolean succeeded) {
            assert currentRequest != null : "Current request was null";
            /*
             * Clear current request first to ensure the state is valid if
             * another request is made in the callback.
             */
            EditorRequest<JsonObject> request = currentRequest;
            currentRequest = null;
            if (succeeded) {
                request.success();
            } else {
                request.failure();
            }
        }
    }

    @OnStateChange("buffered")
    void updateBuffered() {
        getParent().getWidget().getEditor().setBuffered(getState().buffered);
    }

    @OnStateChange("enabled")
    void updateEnabled() {
        getParent().getWidget().getEditor().setEnabled(getState().enabled);
    }

    @OnStateChange("saveCaption")
    void updateSaveCaption() {
        getParent().getWidget().getEditor()
                .setSaveCaption(getState().saveCaption);
    }

    @OnStateChange("cancelCaption")
    void updateCancelCaption() {
        getParent().getWidget().getEditor()
                .setCancelCaption(getState().cancelCaption);
    }

    @Override
    protected void extend(ServerConnector target) {
        Grid<JsonObject> grid = getParent().getWidget();
        grid.getEditor().setHandler(new CustomEditorHandler());
    }

    @Override
    public GridConnector getParent() {
        return (GridConnector) super.getParent();
    }

    @Override
    public EditorState getState() {
        return (EditorState) super.getState();
    }

    /**
     * Returns the key of the given data row.
     *
     * @param row
     *            the row
     * @return the row key
     */
    protected static String getRowKey(JsonObject row) {
        return row.getString(DataCommunicatorConstants.KEY);
    }
}
