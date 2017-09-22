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

package com.vaadin.client.ui.nativeselect;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.connectors.AbstractSingleSelectConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.VNativeSelect;
import com.vaadin.shared.Range;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.nativeselect.NativeSelectState;

import elemental.json.JsonObject;

/**
 * The client-side connector for the {@code NativeSelect} component.
 *
 * @author Vaadin Ltd.
 *
 * @see com.vaadin.ui.NativeSelect
 * @see com.vaadin.client.ui.VNativeSelect
 *
 * @since 8.0
 */
@Connect(com.vaadin.ui.NativeSelect.class)
public class NativeSelectConnector
        extends AbstractSingleSelectConnector<VNativeSelect> {

    private HandlerRegistration selectionChangeRegistration;
    private Registration dataChangeRegistration;

    private final SelectionServerRpc selectionRpc = getRpcProxy(
            SelectionServerRpc.class);

    @Override
    protected void init() {
        super.init();
        selectionChangeRegistration = getWidget().getListBox()
                .addChangeHandler(e -> selectionRpc
                        .select(getWidget().getListBox().getSelectedValue()));
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        selectionChangeRegistration.removeHandler();
        selectionChangeRegistration = null;
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
        getWidget().getListBox().setEnabled(isEnabled() && !isReadOnly());
    }

    @OnStateChange("selectedItemKey")
    void updateSelectedItem() {
        getWidget().setSelectedItem(getState().selectedItemKey);
    }

    @OnStateChange("tabIndex")
    void updateTabIndex() {
        getWidget().setTabIndex(getState().tabIndex);
    }

    @OnStateChange({ "emptySelectionCaption", "emptySelectionAllowed" })
    private void onEmptySelectionCaptionChange() {
        ListBox listBox = getWidget().getListBox();
        boolean hasEmptyItem = listBox.getItemCount() > 0
                && listBox.getValue(0).isEmpty();
        if (hasEmptyItem && getState().emptySelectionAllowed) {
            listBox.setItemText(0, getState().emptySelectionCaption);
        } else if (hasEmptyItem && !getState().emptySelectionAllowed) {
            listBox.removeItem(0);
        } else if (!hasEmptyItem && getState().emptySelectionAllowed) {
            listBox.insertItem(getState().emptySelectionCaption, 0);
            listBox.setValue(0, "");
        }
    }

    @Override
    public NativeSelectState getState() {
        return (NativeSelectState) super.getState();
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
                .size() : "NativeSelect only supports full updates, but got range "
                        + range;

        final VNativeSelect select = getWidget();
        final int itemCount = select.getListBox().getItemCount();

        int increment = getState().emptySelectionAllowed ? 1 : 0;
        for (int i = range.getStart() + increment; i < range.getEnd()
                + increment; i++) {

            final JsonObject row = getDataSource().getRow(i - increment);

            if (i < itemCount) {
                // Reuse and update an existing item
                select.getListBox().setItemText(i, getRowData(row).asString());
                select.getListBox().setValue(i, getRowKey(row));
            } else {
                // Add new items if the new dataset is larger than the old
                select.getListBox().addItem(getRowData(row).asString(),
                        getRowKey(row));
            }
        }

        for (int i = select.getListBox().getItemCount() - 1; i >= range.getEnd()
                + increment; i--) {
            // Remove extra items if the new dataset is smaller than the old
            select.getListBox().removeItem(i);
        }
        updateSelectedItem();
    }
}
