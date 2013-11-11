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

import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.grid.ScrollDestination;
import com.vaadin.shared.ui.Connect;
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
                getWidget().getBody().insertRows(offset, amount);
            }

            @Override
            public void removeRows(int offset, int amount) {
                getWidget().getBody().removeRows(offset, amount);
            }

            @Override
            public void removeColumns(int offset, int amount) {
                getWidget().getColumnConfiguration().removeColumns(offset,
                        amount);
            }

            @Override
            public void insertColumns(int offset, int amount) {
                getWidget().getColumnConfiguration().insertColumns(offset,
                        amount);
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
