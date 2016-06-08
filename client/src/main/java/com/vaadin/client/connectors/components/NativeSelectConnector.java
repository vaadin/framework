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
package com.vaadin.client.connectors.components;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.client.connectors.data.HasSelection;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.data.selection.SelectionModel;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.data.typed.DataProviderConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.components.nativeselect.NativeSelect;

import elemental.json.JsonObject;

@Connect(NativeSelect.class)
public class NativeSelectConnector extends AbstractComponentConnector implements
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
                JsonObject item = dataSource.getRow(index);
                getWidget().setItemText(index,
                        item.getString(DataProviderConstants.NAME));
                getWidget().setValue(index,
                        item.getString(DataProviderConstants.KEY));
                if (selectionModel.isSelected(item)) {
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
                    JsonObject item = dataSource.getRow(i + firstRowIndex);
                    getWidget().addItem(
                            item.getString(DataProviderConstants.NAME),
                            item.getString(DataProviderConstants.KEY));
                    if (selectionModel.isSelected(item)) {
                        getWidget().setSelectedIndex(i + firstRowIndex);
                    }
                }
            }
        }

        @Override
        public void dataAdded(int firstRowIndex, int numberOfRows) {
            if (getWidget().getItemCount() == firstRowIndex) {
                for (int i = 0; i < numberOfRows; ++i) {
                    JsonObject item = dataSource.getRow(i + firstRowIndex);
                    getWidget().addItem(
                            item.getString(DataProviderConstants.NAME),
                            item.getString(DataProviderConstants.KEY));
                    if (selectionModel.isSelected(item)) {
                        getWidget().setSelectedIndex(i + firstRowIndex);
                    }
                }
            } else {
                resetContent();
            }
        }
    }

    private DataSource<JsonObject> dataSource;
    private SelectionModel selectionModel;
    private boolean scheduled;

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
                if (selectionModel == null) {
                    return;
                }
                int index = getWidget().getSelectedIndex();
                JsonObject selected = dataSource.getRow(index);
                selectionModel.select(selected);
            }
        });
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        if (this.dataSource != null) {
            dataSource.setDataChangeHandler(null);
        }
        this.dataSource = dataSource;
        dataSource.setDataChangeHandler(new NativeSelectDataChangeHandler());
    }

    @Override
    public void setSelectionModel(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

    private void resetContent() {
        if (scheduled) {
            return;
        }
        Scheduler.get().scheduleFinally(new ScheduledCommand() {

            @Override
            public void execute() {
                getWidget().clear();
                for (int i = 0; i < dataSource.size(); ++i) {
                    JsonObject item = dataSource.getRow(i);
                    getWidget().addItem(
                            item.getString(DataProviderConstants.NAME),
                            item.getString(DataProviderConstants.KEY));
                    if (selectionModel.isSelected(item)) {
                        getWidget().setSelectedIndex(i);
                    }
                }
                scheduled = false;
            }
        });
        scheduled = true;
    }
}
