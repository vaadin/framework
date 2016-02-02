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
package com.vaadin.client.connectors.data.typed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.data.HasDataSource;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.datasources.ListDataSource;
import com.vaadin.server.communication.data.typed.SimpleDataProvider;
import com.vaadin.shared.data.DataProviderClientRpc;
import com.vaadin.shared.data.DataProviderConstants;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.ui.Connect;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * A simple connector for DataProvider class. Based on {@link ListDataSource}
 * and does not support lazy loading or paging.
 * 
 * @since
 */
@Connect(SimpleDataProvider.class)
public class DataSourceConnector extends AbstractExtensionConnector {

    private Map<String, JsonObject> keyToJson = new HashMap<String, JsonObject>();
    private Set<String> droppedKeys = new HashSet<String>();
    private ListDataSource<JsonObject> ds = new ListDataSource<JsonObject>();
    private boolean pendingDrop = false;

    @Override
    protected void extend(ServerConnector target) {
        registerRpc(DataProviderClientRpc.class, new DataProviderClientRpc() {

            @Override
            public void resetSize(long size) {
                ds.asList().clear();
                // Inform the server-side that all keys are now dropped.
                Set<String> keySet = new HashSet<String>(keyToJson.keySet());
                for (String key : keySet) {
                    dropKey(key);
                }
                sendDroppedKeys();
            }

            @Override
            public void setData(long firstIndex, JsonArray data) {
                List<JsonObject> l = ds.asList();
                assert firstIndex <= l.size() : "Gap in data. First Index: "
                        + firstIndex + ", Size: " + l.size();
                for (long i = 0; i < data.length(); ++i) {
                    JsonObject object = data.getObject((int) i);
                    if (i + firstIndex == l.size()) {
                        l.add(object);
                    } else if (i + firstIndex < l.size()) {
                        int index = (int) (i + firstIndex);
                        dropKey(getKey(l.get(index)));
                        l.set(index, object);
                    }
                    keyToJson.put(getKey(object), object);
                }
                sendDroppedKeys();
            }

            @Override
            public void add(JsonObject dataObject) {
                ds.asList().add(dataObject);
            }

            @Override
            public void drop(String key) {
                if (keyToJson.containsKey(key)) {
                    ds.asList().remove(keyToJson.get(key));
                    dropKey(key);
                    sendDroppedKeys();
                }
            }

            @Override
            public void updateData(JsonArray data) {
                List<JsonObject> list = ds.asList();
                for (int i = 0; i < data.length(); ++i) {
                    JsonObject json = data.getObject(i);
                    String key = getKey(json);

                    if (keyToJson.containsKey(key)) {
                        int index = list.indexOf(keyToJson.get(key));
                        list.set(index, json);
                    } else {
                        dropKey(key);
                    }
                }
                sendDroppedKeys();
            }
        });

        ServerConnector parent = getParent();
        if (parent instanceof HasDataSource) {
            ((HasDataSource) parent).setDataSource(ds);
        } else {
            assert false : "Parent not implementing HasDataSource";
        }
    }

    /**
     * Marks a key as dropped. Call to
     * {@link DataSourceConnector#sendDroppedKeys()} should be called to make
     * sure the information is sent to the server-side.
     * 
     * @param key
     *            dropped key
     */
    private void dropKey(String key) {
        droppedKeys.add(key);
        if (keyToJson.containsKey(key)) {
            keyToJson.remove(key);
        }
    }

    /**
     * Sends dropped keys to the server-side with a deferred scheduled command.
     * Multiple calls to this method will only result to one command being
     * executed.
     */
    private void sendDroppedKeys() {
        if (pendingDrop) {
            return;
        }

        pendingDrop = true;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                pendingDrop = false;
                if (droppedKeys.isEmpty()) {
                    return;
                }

                JsonArray keyArray = Json.createArray();
                int i = 0;
                for (String key : droppedKeys) {
                    keyArray.set(i++, key);
                }

                getRpcProxy(DataRequestRpc.class).dropRows(keyArray);

                // Force RPC since it's delayed.
                getConnection().getServerRpcQueue().flush();

                droppedKeys.clear();
            }
        });
    }

    /**
     * Gets the mapping key from given {@link JsonObject}.
     * 
     * @param jsonObject
     *            json object to get the key from
     */
    protected String getKey(JsonObject jsonObject) {
        if (jsonObject.hasKey(DataProviderConstants.KEY)) {
            return jsonObject.getString(DataProviderConstants.KEY);
        }
        return null;
    }
}
