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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.tokka.connectors.data.HasSelection;
import com.vaadin.shared.data.typed.DataProviderConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tokka.ui.components.nativeselect.NativeSelect;

import elemental.json.JsonObject;

@Connect(NativeSelect.class)
public class NativeSelectConnector extends AbstractListingConnector implements
        HasSelection {

    private final class NativeSelectDataChangeHandler implements
            DataChangeHandler {
        @Override
        public void resetDataAndSize(int estimatedNewDataSize) {
            resetContent();
        }

        @Override
        public void dataUpdated(int firstRowIndex, int numberOfRows) {
            for (int i = 0; i < numberOfRows; ++i) {
                int index = i + firstRowIndex;
                if (i == getWidget().getItemCount()) {
                    getWidget().addItem("");
                }

                JsonObject item = getDataSource().getRow(index);
                getWidget().setItemText(index,
                        item.getString(DataProviderConstants.NAME));
                getWidget().setValue(index,
                        item.getString(DataProviderConstants.KEY));
                if (getSelectionModel().isSelected(item)) {
                    getWidget().setSelectedIndex(index);
                }
            }
        }

        @Override
        public void dataRemoved(int firstRowIndex, int numberOfRows) {
            for (int i = 0; i < numberOfRows; ++i) {
                int index = i + firstRowIndex;
                getWidget().removeItem(index);
            }
        }

        @Override
        public void dataAvailable(int firstRowIndex, int numberOfRows) {
            if (getWidget().getItemCount() == firstRowIndex) {
                for (int i = 0; i < numberOfRows; ++i) {
                    JsonObject item = getDataSource().getRow(i + firstRowIndex);
                    getWidget().addItem(
                            item.getString(DataProviderConstants.NAME),
                            item.getString(DataProviderConstants.KEY));
                    if (getSelectionModel().isSelected(item)) {
                        getWidget().setSelectedIndex(i + firstRowIndex);
                    }
                }
            } else {
                resetContent();
            }
        }

        @Override
        public void dataAdded(int firstRowIndex, int numberOfRows) {
            if (getWidget().getItemCount() == firstRowIndex) {
                for (int i = 0; i < numberOfRows; ++i) {
                    JsonObject item = getDataSource().getRow(i + firstRowIndex);
                    getWidget().addItem(
                            item.getString(DataProviderConstants.NAME),
                            item.getString(DataProviderConstants.KEY));
                    if (getSelectionModel().isSelected(item)) {
                        getWidget().setSelectedIndex(i + firstRowIndex);
                    }
                }
            } else {
                resetContent();
            }
        }
    }

    private boolean scheduled;
    private DataChangeHandler dataChangeHandler = new NativeSelectDataChangeHandler();

    @Override
    public ListBox getWidget() {
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
        Scheduler.get().scheduleFinally(new ScheduledCommand() {

            @Override
            public void execute() {
                getDataSource().ensureAvailability(0, getDataSource().size());
            }
        });
    }

    @Override
    protected void removeDataSource(DataSource<JsonObject> dataSource) {
        dataSource.setDataChangeHandler(null);
        super.removeDataSource(dataSource);
    }

    private void resetContent() {
        if (scheduled) {
            return;
        }
        Scheduler.get().scheduleFinally(new ScheduledCommand() {

            @Override
            public void execute() {
                getWidget().clear();
                for (int i = 0; i < getDataSource().size(); ++i) {
                    JsonObject item = getDataSource().getRow(i);
                    if (item == null) {
                        continue;
                    }
                    getWidget().addItem(
                            item.getString(DataProviderConstants.NAME),
                            item.getString(DataProviderConstants.KEY));
                    if (getSelectionModel().isSelected(item)) {
                        getWidget().setSelectedIndex(i);
                    }
                }
                scheduled = false;
            }
        });
        scheduled = true;
    }
}
