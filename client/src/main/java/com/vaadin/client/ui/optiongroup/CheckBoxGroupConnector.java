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

package com.vaadin.client.ui.optiongroup;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.VCheckBoxGroup;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.optiongroup.CheckBoxGroupState;
import com.vaadin.ui.CheckBoxGroup;

import elemental.json.JsonObject;

@Connect(CheckBoxGroup.class)
public class CheckBoxGroupConnector
        extends AbstractListingConnector<SelectionModel.Multi<JsonObject>> {

    private Registration selectionChangeRegistration;

    @Override
    protected void init() {
        super.init();
        selectionChangeRegistration =
                getWidget().addNotifyHandler(this::selectionChanged);
    }

    private void selectionChanged(JsonObject newSelection) {
        getSelectionModel().select(newSelection);
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        getWidget().setTabIndex(getState().tabIndex);
        getWidget().setReadonly(isReadOnly());
        getWidget().client = getConnection();
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        dataSource.addDataChangeHandler(range -> updateOptionGroup());
        super.setDataSource(dataSource);
    }

    private void updateOptionGroup() {
        List<JsonObject> items = new ArrayList<>(getDataSource().size());
        for (int i = 0; i < getDataSource().size(); ++i) {
            JsonObject item = getDataSource().getRow(i);
            items.add(item);
        }
        getWidget().buildOptions(items);
    }

    @Override
    public VCheckBoxGroup getWidget() {
        return (VCheckBoxGroup) super.getWidget();
    }

    @Override
    public CheckBoxGroupState getState() {
        return (CheckBoxGroupState) super.getState();
    }
}
