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
package com.vaadin.tokka.ui.components.nativeselect;

import java.util.function.Function;

import com.vaadin.shared.tokka.data.DataProviderConstants;
import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.server.communication.data.SingleSelection;
import com.vaadin.tokka.server.communication.data.TypedDataGenerator;
import com.vaadin.tokka.ui.components.AbstractListing;

import elemental.json.JsonObject;

public class NativeSelect<T> extends AbstractListing<T> {

    private Function<T, String> nameProvider = T::toString;

    public NativeSelect() {
        setSelectionModel(new SingleSelection<>());
        addDataGenerator(new TypedDataGenerator<T>() {

            @Override
            public void generateData(T data, JsonObject jsonObject) {
                jsonObject.put(DataProviderConstants.NAME,
                        nameProvider.apply(data));
            }

            @Override
            public void destroyData(T data) {
            }
        });
    }

    public NativeSelect(DataSource<T, ?> dataSource) {
        this();
        setDataSource(dataSource);
    }
}
