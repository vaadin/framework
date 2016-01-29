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

import java.util.logging.Logger;

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

    @Override
    public VLabel getWidget() {
        return (VLabel) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();

        getWidget().setText("foo");
    }

    @Override
    public void setDataSource(DataSource<JsonObject> ds) {
        Logger.getLogger("foo").warning(
                "I'm not using the data source for anything!");
        // TODO: implement access to data source
    }

}
