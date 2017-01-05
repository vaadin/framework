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

package com.vaadin.tests.components.table;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.annotations.Push;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Table;

import elemental.json.JsonObject;

@Push
public class TableRemovedQuicklySendsInvalidRpcCalls extends AbstractTestUI {

    public static final String SUCCESS_CAPTION = "Success!";
    public static final String BUTTON_ID = "blinkbutton";
    public static final String FAILURE_CAPTION = "Test failed";

    private class WrappedConnectorTracker extends ConnectorTracker {
        private ConnectorTracker tracker;

        private boolean initialDirtyHasBeenCalled = false;

        public WrappedConnectorTracker(ConnectorTracker tracker) {
            super(TableRemovedQuicklySendsInvalidRpcCalls.this);
            this.tracker = tracker;
        }

        @Override
        public void markAllConnectorsDirty() {
            tracker.markAllConnectorsDirty();
            if (initialDirtyHasBeenCalled) {
                button.setCaption(FAILURE_CAPTION);
            }
            initialDirtyHasBeenCalled = true;
        }

        // DELEGATED METHODS BELOW:

        @Override
        public void registerConnector(ClientConnector connector) {
            tracker.registerConnector(connector);
        }

        @Override
        public void unregisterConnector(ClientConnector connector) {
            tracker.unregisterConnector(connector);
        }

        @Override
        public boolean isClientSideInitialized(ClientConnector connector) {
            return tracker.isClientSideInitialized(connector);
        }

        @Override
        public void markClientSideInitialized(ClientConnector connector) {
            tracker.markClientSideInitialized(connector);
        }

        @Override
        public void markAllClientSidesUninitialized() {
            tracker.markAllClientSidesUninitialized();
        }

        @Override
        public ClientConnector getConnector(String connectorId) {
            return tracker.getConnector(connectorId);
        }

        @Override
        public void cleanConnectorMap() {
            tracker.cleanConnectorMap();
        }

        @Override
        public void markDirty(ClientConnector connector) {
            tracker.markDirty(connector);
        }

        @Override
        public void markClean(ClientConnector connector) {
            tracker.markClean(connector);
        }

        @Override
        public void markAllConnectorsClean() {
            tracker.markAllConnectorsClean();
        }

        @Override
        public Collection<ClientConnector> getDirtyConnectors() {
            return tracker.getDirtyConnectors();
        }

        @Override
        public boolean hasDirtyConnectors() {
            return tracker.hasDirtyConnectors();
        }

        @Override
        public ArrayList<ClientConnector> getDirtyVisibleConnectors() {
            return tracker.getDirtyVisibleConnectors();
        }

        @Override
        public JsonObject getDiffState(ClientConnector connector) {
            return tracker.getDiffState(connector);
        }

        @Override
        public void setDiffState(ClientConnector connector,
                JsonObject diffState) {
            tracker.setDiffState(connector, diffState);
        }

        @Override
        public boolean isDirty(ClientConnector connector) {
            return tracker.isDirty(connector);
        }

        @Override
        public boolean isWritingResponse() {
            return tracker.isWritingResponse();
        }

        @Override
        public void setWritingResponse(boolean writingResponse) {
            tracker.setWritingResponse(writingResponse);
        }

        @Override
        public StreamVariable getStreamVariable(String connectorId,
                String variableName) {
            return tracker.getStreamVariable(connectorId, variableName);
        }

        @Override
        public void addStreamVariable(String connectorId, String variableName,
                StreamVariable variable) {
            tracker.addStreamVariable(connectorId, variableName, variable);
        }

        @Override
        public void cleanStreamVariable(String connectorId,
                String variableName) {
            tracker.cleanStreamVariable(connectorId, variableName);
        }

        @Override
        public String getSeckey(StreamVariable variable) {
            return tracker.getSeckey(variable);
        }

        @Override
        public int getCurrentSyncId() {
            return tracker.getCurrentSyncId();
        }

        @Override
        public boolean equals(Object obj) {
            return tracker.equals(obj);
        }

        @Override
        public int hashCode() {
            return tracker.hashCode();
        }

        @Override
        public String toString() {
            return tracker.toString();
        }
    }

    private Button button;
    private WrappedConnectorTracker wrappedTracker = null;

    @Override
    protected void setup(VaadinRequest request) {
        button = new Button("Blink a table", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                blinkTable();
            }
        });
        button.setId(BUTTON_ID);
        addComponent(button);
    }

    @Override
    public ConnectorTracker getConnectorTracker() {
        if (wrappedTracker == null) {
            wrappedTracker = new WrappedConnectorTracker(
                    super.getConnectorTracker());
        }
        return wrappedTracker;
    }

    private void blinkTable() {
        final Table table = new Table();
        table.setPageLength(5);
        table.addContainerProperty(new Object(), String.class, null);

        for (int i = 0; i < 50; i++) {
            table.addItem(new Object[] { "Row" }, new Object());
        }

        System.out.println("adding component");
        addComponent(table);

        new Thread() {
            @Override
            public void run() {
                getSession().lock();
                try {
                    Thread.sleep(500);
                    access(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("removing component");
                            removeComponent(table);
                            button.setCaption(SUCCESS_CAPTION);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    getSession().unlock();
                }
            }
        }.start();
    }

    @Override
    protected String getTestDescription() {
        return "Adding and subsequently quickly removing a table "
                + "should not leave any pending RPC calls waiting "
                + "in a Timer. Issue can be reproduced by "
                + "1) pressing the button 2) checking the server "
                + "log for any error messages starting with "
                + "\"RPC call to...\" .";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12337;
    }
}
