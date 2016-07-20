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
package com.vaadin.client.tokka.connectors.components;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.tokka.connectors.data.HasSelection;
import com.vaadin.shared.tokka.data.DataProviderConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tokka.ui.components.nativeselect.NativeSelect;

import elemental.json.JsonObject;

/**
 * Connector class for NativeSelect.
 */
@Connect(NativeSelect.class)
public class NativeSelectConnector extends AbstractListingConnector implements
        HasSelection {

    /**
     * DataChangeHandler for NativeSelect client-side. This class handles
     * updates from the server.
     */
    private final class NativeSelectDataChangeHandler implements
            DataChangeHandler {
        @Override
        public void resetDataAndSize(int size) {
            int count = getWidget().getItemCount();
            if (count < size) {
                for (int i = count; i < size; ++i) {
                    getWidget().addItem("");
                }
            } else if (count > size) {
                for (int i = count; i > size; --i) {
                    getWidget().removeItem(i - 1);
                }
            }
            requestAllData();
        }

        @Override
        public void dataUpdated(int firstRowIndex, int numberOfRows) {
            updateContent(firstRowIndex, numberOfRows);
        }

        @Override
        public void dataRemoved(int firstRowIndex, int numberOfRows) {
            for (int i = 0; i < numberOfRows; ++i) {
                getWidget().removeItem(i + firstRowIndex);
            }
        }

        @Override
        public void dataAvailable(int firstRowIndex, int numberOfRows) {
            updateContent(firstRowIndex, numberOfRows);
        }

        @Override
        public void dataAdded(int firstRowIndex, int numberOfRows) {
            requestAllData();
        }
    }

    private DataChangeHandler dataChangeHandler = new NativeSelectDataChangeHandler();

    @Override
    public ListBox getWidget() {
        // FIXME: Should we use ListBox or modify VNativeSelect to work
        return (ListBox) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();

        getWidget().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                if (getSelectionModel() == null) {
                    return;
                }
                int index = getWidget().getSelectedIndex();
                JsonObject selected = getDataSource().getRow(index);
                getSelectionModel().select(selected);
            }
        });
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        super.setDataSource(dataSource);
        dataSource.setDataChangeHandler(dataChangeHandler);
    }

    @Override
    protected void removeDataSource(DataSource<JsonObject> dataSource) {
        // Make sure we no longer get updates from the old data source.
        dataSource.setDataChangeHandler(null);
        super.removeDataSource(dataSource);
    }

    /**
     * Sends a request to the DataSource to get all data from the server-side.
     */
    private void requestAllData() {
        getDataSource().ensureAvailability(0, getDataSource().size());
    }

    /**
     * Updates the item values and texts for all items in given range.
     * 
     * @param firstRowIndex
     *            first row to update
     * @param numberOfRows
     *            count of updated rows
     */
    private void updateContent(int firstRowIndex, int numberOfRows) {
        for (int i = 0; i < numberOfRows; ++i) {
            int index = i + firstRowIndex;
            JsonObject item = getDataSource().getRow(i + firstRowIndex);
            getWidget().setItemText(index,
                    item.getString(DataProviderConstants.NAME));
            getWidget().setValue(index,
                    item.getString(DataProviderConstants.KEY));
            if (getSelectionModel().isSelected(item)) {
                getWidget().setSelectedIndex(i + firstRowIndex);
            }
        }
    }
}
