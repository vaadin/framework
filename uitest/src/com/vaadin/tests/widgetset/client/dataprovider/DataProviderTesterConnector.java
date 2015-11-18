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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.data.HasDataSource;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.Range;
import com.vaadin.tests.dataprovider.DataProviderTestUI.DataProviderTester;

import elemental.json.JsonObject;

@Connect(DataProviderTester.class)
public class DataProviderTesterConnector extends AbstractComponentConnector
        implements HasDataSource {

    public static class DataProviderTesterWidget extends FlowPanel {

        private DataSource<JsonObject> datasource;
        private Range available;
        private int pageLength = 20;
        private int page = 0;

        private boolean isScheduled = false;
        private ScheduledCommand redrawCmd = new ScheduledCommand() {

            @Override
            public void execute() {
                while (getWidgetCount() > 1) {
                    remove(1);
                }

                if (available != null) {
                    for (int i = available.getStart(); i < available.getEnd(); ++i) {
                        add(new HTML(i + ": " + datasource.getRow(i).toJson()));
                    }
                }

                isScheduled = false;
            }
        };

        public DataProviderTesterWidget() {
            final FlowPanel w = new FlowPanel();
            add(w);
            final Button previous = new Button("<");
            previous.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (page > 0) {
                        --page;
                        requestData();
                    }
                }
            });
            final Button next = new Button(">");
            next.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    if (page < (datasource.size() / pageLength) - 1) {
                        ++page;
                        requestData();
                    }
                }

            });

            w.add(previous);
            w.add(next);
        }

        public void setDataSource(DataSource<JsonObject> ds) {

            datasource = ds;
            ds.setDataChangeHandler(new DataChangeHandler() {

                @Override
                public void resetDataAndSize(int estimatedNewDataSize) {
                    redraw();
                }

                @Override
                public void dataUpdated(int firstRowIndex, int numberOfRows) {
                    redraw();
                }

                @Override
                public void dataRemoved(int firstRowIndex, int numberOfRows) {
                    redraw();
                }

                @Override
                public void dataAvailable(int firstRowIndex, int numberOfRows) {
                    available = Range.withLength(firstRowIndex, numberOfRows);
                }

                @Override
                public void dataAdded(int firstRowIndex, int numberOfRows) {
                    redraw();
                }
            });

            datasource.ensureAvailability(0, pageLength);
        }

        protected void requestData() {
            datasource
                    .ensureAvailability(page * pageLength, Math.min(
                            datasource.size() - page * pageLength, pageLength));
        }

        protected void redraw() {
            if (!isScheduled) {
                Scheduler.get().scheduleFinally(redrawCmd);
                isScheduled = true;
            }
        }
    }

    @Override
    public DataProviderTesterWidget getWidget() {
        return (DataProviderTesterWidget) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void setDataSource(DataSource<JsonObject> ds) {
        getWidget().setDataSource(ds);
    }
}
