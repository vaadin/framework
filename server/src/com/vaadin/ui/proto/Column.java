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
package com.vaadin.ui.proto;

import com.vaadin.server.communication.data.typed.TypedDataGenerator;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.proto.TypedGridColumnState;
import com.vaadin.ui.proto.TypedGrid.AbstractTypedGridExtension;

import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * Grid Column.
 * 
 * @param <T>
 *            grid data type
 * @param <V>
 *            column value type
 */
public class Column<T, V> extends AbstractTypedGridExtension<T> implements
        TypedDataGenerator<T> {

    private ValueProvider<T, V> valueProvider;

    Column(TypedGrid<T> grid, String id, Class<V> valueType,
            ValueProvider<T, V> valueProvider) {
        super(grid);
        getState().id = id;
        this.valueProvider = valueProvider;
        // TODO: determine renderer from valueType
    }

    @Override
    protected TypedGridColumnState getState() {
        return (TypedGridColumnState) super.getState();
    }

    @Override
    public void generateData(T data, JsonObject jsonObject) {
        JsonObject dataObject;
        if (!jsonObject.hasKey(GridState.JSONKEY_DATA)) {
            jsonObject.put(GridState.JSONKEY_DATA, Json.createObject());
        }
        dataObject = jsonObject.getObject(GridState.JSONKEY_DATA);

        V value = valueProvider.getValue(data);
        dataObject.put(getState().id, value != null ? value.toString() : "");
    }

    @Override
    public void destroyData(T data) {
    }
}
