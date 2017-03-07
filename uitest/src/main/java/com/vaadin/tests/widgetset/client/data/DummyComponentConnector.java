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
package com.vaadin.tests.widgetset.client.data;

import com.google.gwt.user.client.ui.FlowPanel;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.VLabel;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.data.DummyData.DummyComponent;

import elemental.json.JsonObject;

@Connect(DummyComponent.class)
public class DummyComponentConnector extends AbstractListingConnector {

    @Override
    public FlowPanel getWidget() {
        return (FlowPanel) super.getWidget();
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        super.setDataSource(dataSource);

        dataSource.addDataChangeHandler(range -> {
            assert range.getStart() == 0 && range.getEnd() == dataSource
                    .size() : "Widget only supports full updates.";
            getWidget().clear();
            for (int i = range.getStart(); i < range.getEnd(); ++i) {
                VLabel label = new VLabel();
                getWidget().add(label);
                JsonObject row = dataSource.getRow(i);
                String text = getRowData(row).asString();
                if (isRowSelected(row)) {
                    text = "<b>" + text + "</b>";
                    label.addStyleName("selected");
                }
                label.setHTML(text);
            }
        });
    }
}
