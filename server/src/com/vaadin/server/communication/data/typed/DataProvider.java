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
package com.vaadin.server.communication.data.typed;

import java.util.Collection;

import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.ui.AbstractComponent;

import elemental.json.JsonArray;

/**
 * DataProvider for Collection "container".
 * 
 * @since
 */
public class DataProvider<T> extends AbstractExtension {

    private Collection<T> data;

    /**
     * Creates a new DataProvider, connecting it to given Collection and
     * Component
     * 
     * @param data
     *            collection of data to use
     * @param component
     *            component to extend
     */
    public DataProvider(Collection<T> data, AbstractComponent component) {
        this.data = data;
        extend(component);

        registerRpc(new DataRequestRpc() {
            @Override
            public void requestRows(int firstRowIndex, int numberOfRows,
                    int firstCachedRowIndex, int cacheSize) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public void dropRows(JsonArray rowKeys) {
                throw new UnsupportedOperationException("Not implemented");
            }
        });
    }

}
