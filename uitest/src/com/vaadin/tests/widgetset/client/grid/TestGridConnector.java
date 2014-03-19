/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.widgetset.client.grid;

import com.google.gwt.user.client.Random;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.tests.widgetset.server.grid.TestGrid;

/**
 * @since 7.2
 * @author Vaadin Ltd
 */
@Connect(TestGrid.class)
public class TestGridConnector extends AbstractComponentConnector {
    @Override
    protected void init() {
        super.init();
        registerRpc(TestGridClientRpc.class, new TestGridClientRpc() {
            @Override
            public void insertRows(int offset, int amount) {
                getWidget().insertRows(offset, amount);
            }

            @Override
            public void removeRows(int offset, int amount) {
                getWidget().removeRows(offset, amount);
            }

            @Override
            public void removeColumns(int offset, int amount) {
                getWidget().removeColumns(offset, amount);
            }

            @Override
            public void insertColumns(int offset, int amount) {
                getWidget().insertColumns(offset, amount);
            }

            @Override
            public void scrollToRow(int index, String destination, int padding) {
                getWidget().scrollToRow(index, getDestination(destination),
                        padding);
            }

            @Override
            public void scrollToColumn(int index, String destination,
                    int padding) {
                getWidget().scrollToColumn(index, getDestination(destination),
                        padding);
            }

            private ScrollDestination getDestination(String destination) {
                final ScrollDestination d;
                if (destination.equals("start")) {
                    d = ScrollDestination.START;
                } else if (destination.equals("middle")) {
                    d = ScrollDestination.MIDDLE;
                } else if (destination.equals("end")) {
                    d = ScrollDestination.END;
                } else {
                    d = ScrollDestination.ANY;
                }
                return d;
            }

            @Override
            public void setFrozenColumns(int frozenColumns) {
                getWidget().getColumnConfiguration().setFrozenColumnCount(
                        frozenColumns);
            }

            @Override
            public void insertHeaders(int index, int amount) {
                getWidget().getHeader().insertRows(index, amount);
            }

            @Override
            public void removeHeaders(int index, int amount) {
                getWidget().getHeader().removeRows(index, amount);
            }

            @Override
            public void insertFooters(int index, int amount) {
                getWidget().getFooter().insertRows(index, amount);
            }

            @Override
            public void removeFooters(int index, int amount) {
                getWidget().getFooter().removeRows(index, amount);
            }

            @Override
            public void setColumnWidth(int index, int px) {
                getWidget().getColumnConfiguration().setColumnWidth(index, px);
            }

            @Override
            public void calculateColumnWidths() {
                getWidget().calculateColumnWidths();
            }

            @Override
            public void randomRowHeight() {
                getWidget().getHeader().setDefaultRowHeight(
                        Random.nextInt(20) + 20);
                getWidget().getBody().setDefaultRowHeight(
                        Random.nextInt(20) + 20);
                getWidget().getFooter().setDefaultRowHeight(
                        Random.nextInt(20) + 20);
            }
        });
    }

    @Override
    public VTestGrid getWidget() {
        return (VTestGrid) super.getWidget();
    }

    @Override
    public TestGridState getState() {
        return (TestGridState) super.getState();
    }
}
