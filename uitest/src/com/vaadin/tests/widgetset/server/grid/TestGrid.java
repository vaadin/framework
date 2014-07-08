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
package com.vaadin.tests.widgetset.server.grid;

import com.vaadin.tests.widgetset.client.grid.TestGridClientRpc;
import com.vaadin.tests.widgetset.client.grid.TestGridState;
import com.vaadin.ui.AbstractComponent;

/**
 * @since
 * @author Vaadin Ltd
 */
public class TestGrid extends AbstractComponent {
    public TestGrid() {
        setWidth(TestGridState.DEFAULT_WIDTH);
        setHeight(TestGridState.DEFAULT_HEIGHT);
    }

    @Override
    protected TestGridState getState() {
        return (TestGridState) super.getState();
    }

    public void insertRows(int offset, int amount) {
        rpc().insertRows(offset, amount);
    }

    public void removeRows(int offset, int amount) {
        rpc().removeRows(offset, amount);
    }

    public void insertColumns(int offset, int amount) {
        rpc().insertColumns(offset, amount);
    }

    public void removeColumns(int offset, int amount) {
        rpc().removeColumns(offset, amount);
    }

    private TestGridClientRpc rpc() {
        return getRpcProxy(TestGridClientRpc.class);
    }

    public void scrollToRow(int index, String destination, int padding) {
        rpc().scrollToRow(index, destination, padding);
    }

    public void scrollToColumn(int index, String destination, int padding) {
        rpc().scrollToColumn(index, destination, padding);
    }

    public void setFrozenColumns(int frozenColumns) {
        rpc().setFrozenColumns(frozenColumns);
    }

    public void insertHeaders(int index, int amount) {
        rpc().insertHeaders(index, amount);
    }

    public void removeHeaders(int index, int amount) {
        rpc().removeHeaders(index, amount);
    }

    public void insertFooters(int index, int amount) {
        rpc().insertFooters(index, amount);
    }

    public void removeFooters(int index, int amount) {
        rpc().removeFooters(index, amount);
    }

    public void setColumnWidth(int index, int px) {
        rpc().setColumnWidth(index, px);
    }

    public void calculateColumnWidths() {
        rpc().calculateColumnWidths();
    }

    public void randomizeDefaultRowHeight() {
        rpc().randomRowHeight();
    }
}
