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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.AbstractFocusableListingConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.HasRequiredIndicator;
import com.vaadin.client.ui.VCheckBoxGroup;
import com.vaadin.shared.data.selection.MultiSelectServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.optiongroup.CheckBoxGroupState;
import com.vaadin.ui.CheckBoxGroup;

import elemental.json.JsonObject;

/**
 * CheckBoxGroup client side connector.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
@Connect(CheckBoxGroup.class)
// We don't care about the framework-provided selection model at this point
// TODO refactor to extend AbstractMultiSelectConnector, maybe when
// SelectionModel is removed from client side framwork8-issues#421
public class CheckBoxGroupConnector
        extends AbstractFocusableListingConnector<VCheckBoxGroup>
        implements HasRequiredIndicator {

    @Override
    protected void init() {
        super.init();
        getWidget().addSelectionChangeHandler(this::selectionChanged);
    }

    private void selectionChanged(JsonObject changedItem, Boolean selected) {
        MultiSelectServerRpc rpc = getRpcProxy(MultiSelectServerRpc.class);
        String key = getRowKey(changedItem);
        HashSet<String> change = new HashSet<>();
        change.add(key);
        if (Boolean.TRUE.equals(selected)) {
            rpc.updateSelection(change, Collections.emptySet());
        } else {
            rpc.updateSelection(Collections.emptySet(), change);
        }
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
    public CheckBoxGroupState getState() {
        return (CheckBoxGroupState) super.getState();
    }

    // TODO remove once this extends AbstractMultiSelectConnector
    @Override
    public boolean isRequiredIndicatorVisible() {
        return getState().required && !isReadOnly();
    }
}
