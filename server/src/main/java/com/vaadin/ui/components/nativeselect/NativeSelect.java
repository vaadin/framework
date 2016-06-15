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
package com.vaadin.ui.components.nativeselect;

import java.util.function.Function;

import com.vaadin.server.communication.data.typed.DataProvider;
import com.vaadin.server.communication.data.typed.DataSource;
import com.vaadin.server.communication.data.typed.SelectionModel;
import com.vaadin.server.communication.data.typed.SingleSelection;
import com.vaadin.server.communication.data.typed.TypedDataGenerator;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.components.Listing;

import elemental.json.JsonObject;

public class NativeSelect<T> extends AbstractComponent implements Listing<T> {

    private DataSource<T> dataSource;
    private DataProvider<T> dataProvider;
    private SelectionModel<T> selectionModel;
    private Function<T, String> nameProvider = T::toString;

    public NativeSelect() {
        internalSetSelectionModel(new SingleSelection<>());
    }

    public NativeSelect(DataSource<T> dataSource) {
        this();
        internalSetDataSource(dataSource);
    }

    @Override
    public void setDataSource(DataSource<T> data) {
        internalSetDataSource(data);
    }

    private void internalSetDataSource(DataSource<T> data) {
        if (dataProvider != null) {
            dataProvider.remove();
            dataProvider = null;
        }
        dataSource = data;
        if (dataSource != null) {
            dataProvider = DataProvider.create(dataSource, this);
            dataProvider.addDataGenerator(new TypedDataGenerator<T>() {

                @Override
                public void generateData(T data, JsonObject jsonObject) {
                    jsonObject.put("n", nameProvider.apply(data));
                }

                @Override
                public void destroyData(T data) {
                }
            });
        }
    }

    @Override
    public DataSource<T> getDataSource() {
        return dataSource;
    }

    @Override
    public SelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public void setSelectionModel(SelectionModel<T> model) {
        internalSetSelectionModel(model);
    }

    private void internalSetSelectionModel(SelectionModel<T> model) {
        if (selectionModel != null) {
            selectionModel.remove();
        }
        selectionModel = model;
        if (model != null) {
            model.setParentListing(this);
        }
    }

}
