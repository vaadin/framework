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
package com.vaadin.tokka.ui.components.grid;

import java.util.function.Function;

import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.tokka.data.DataProviderConstants;
import com.vaadin.shared.tokka.ui.components.grid.ColumnState;
import com.vaadin.tokka.server.communication.data.TypedDataGenerator;

import elemental.json.Json;
import elemental.json.JsonObject;

public class Column<T, V> extends AbstractExtension implements
        TypedDataGenerator<T> {

    private Function<T, V> getter;

    Column(String caption, Function<T, V> getter) {
        this.getter = getter;
        getState().caption = caption;
    }

    @Override
    public void generateData(T data, JsonObject jsonObject) {
        if (!jsonObject.hasKey(DataProviderConstants.DATA)) {
            jsonObject.put(DataProviderConstants.DATA, Json.createObject());
        }
        JsonObject obj = jsonObject.getObject(DataProviderConstants.DATA);
        // TODO: Renderers
        obj.put(getState(false).communicationId, getter.apply(data).toString());
    }

    @Override
    public void destroyData(T data) {
    }

    public ColumnState getState() {
        return getState(true);
    }

    public ColumnState getState(boolean markAsDirty) {
        return (ColumnState) super.getState(markAsDirty);
    }

    void extend(Grid<T> grid) {
        super.extend(grid);
    }

    void setCommunicationId(String key) {
        getState().communicationId = key;
    }
}
