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
package com.vaadin.tests.widgetset.client.dataprovider;

import com.google.gwt.user.client.ui.FlowPanel;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.data.HasDataSource;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.VLabel;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.dataprovider.DummyDataProviderUI.DummyDataComponent;

import elemental.json.JsonObject;

@Connect(DummyDataComponent.class)
public class DummyDataConnector extends AbstractComponentConnector implements
        HasDataSource {

    private DataSource<JsonObject> dataSource;

    @Override
    public FlowPanel getWidget() {
        return (FlowPanel) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void setDataSource(DataSource<JsonObject> ds) {
        dataSource = ds;
        dataSource.setDataChangeHandler(new DataChangeHandler() {

            @Override
            public void resetDataAndSize(int estimatedNewDataSize) {
            }

            @Override
            public void dataUpdated(int firstRowIndex, int numberOfRows) {
            }

            @Override
            public void dataRemoved(int firstRowIndex, int numberOfRows) {
            }

            @Override
            public void dataAvailable(int firstRowIndex, int numberOfRows) {
            }

            @Override
            public void dataAdded(int firstRowIndex, int numberOfRows) {
                for (int i = 0; i < numberOfRows; ++i) {
                    getWidget().add(
                            new VLabel(dataSource.getRow(i + firstRowIndex)
                                    .toJson()));
                }
            }
        });
    }

}
