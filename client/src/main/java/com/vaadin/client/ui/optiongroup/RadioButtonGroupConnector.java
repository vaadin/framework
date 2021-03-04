/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import com.google.gwt.dom.client.Element;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.AbstractSingleSelectConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.VRadioButtonGroup;
import com.vaadin.shared.Range;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.ListingJsonConstants;
import com.vaadin.shared.ui.optiongroup.RadioButtonGroupState;
import com.vaadin.ui.RadioButtonGroup;

import elemental.json.JsonObject;

/**
 * CheckBoxGroup client side connector.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
@Connect(RadioButtonGroup.class)
public class RadioButtonGroupConnector
        extends AbstractSingleSelectConnector<VRadioButtonGroup> {

    private Registration selectionChangeRegistration;
    private Registration dataChangeRegistration;

    private final SelectionServerRpc selectionRpc = getRpcProxy(
            SelectionServerRpc.class);

    @Override
    protected void init() {
        super.init();

        selectionChangeRegistration = getWidget().addSelectionChangeHandler(
                event -> selectionRpc.select(getRowKey(event)));
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        selectionChangeRegistration.remove();
        selectionChangeRegistration = null;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        getWidget().setTabIndex(getState().tabIndex);
        getWidget().client = getConnection();
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        if (dataChangeRegistration != null) {
            dataChangeRegistration.remove();
        }
        dataChangeRegistration = dataSource
                .addDataChangeHandler(this::onDataChange);
        super.setDataSource(dataSource);
    }

    @OnStateChange("readOnly")
    void updateWidgetReadOnly() {
        getWidget().setReadonly(isReadOnly());
    }

    @OnStateChange("enabled")
    void updateWidgetEnabled() {
        getWidget().setEnabled(isEnabled());
    }

    @OnStateChange("selectedItemKey")
    void updateSelectedItem() {
        getWidget().selectItemKey(getState().selectedItemKey);
    }

    @Override
    public RadioButtonGroupState getState() {
        return (RadioButtonGroupState) super.getState();
    }

    /**
     * A data change handler registered to the data source. Updates the data
     * items and selection status when the data source notifies of new changes
     * from the server side.
     *
     * @param range
     *            the new range of data items
     */
    private void onDataChange(Range range) {
        assert range.getStart() == 0 && range.getEnd() == getDataSource()
                .size() : "RadioButtonGroup only supports full updates, but "
                        + "got range " + range;

        final VRadioButtonGroup select = getWidget();
        DataSource<JsonObject> dataSource = getDataSource();
        int size = dataSource.size();
        List<JsonObject> options = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            options.add(dataSource.getRow(i));
        }
        select.buildOptions(options);
        getLayoutManager().setNeedsMeasure(this);
        updateSelectedItem();
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {
        JsonObject item = getWidget().getItem(element);
        if (item != null
                && item.hasKey(ListingJsonConstants.JSONKEY_ITEM_DESCRIPTION)) {
            return new TooltipInfo(item
                    .getString(ListingJsonConstants.JSONKEY_ITEM_DESCRIPTION));
        }
        return super.getTooltipInfo(element);
    }

    @Override
    public boolean hasTooltip() {
        return true;
    }
}
