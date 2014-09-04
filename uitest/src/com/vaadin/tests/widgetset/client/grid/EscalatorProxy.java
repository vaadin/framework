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
package com.vaadin.tests.widgetset.client.grid;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.ColumnConfiguration;
import com.vaadin.client.ui.grid.Escalator;
import com.vaadin.client.ui.grid.EscalatorUpdater;
import com.vaadin.client.ui.grid.RowContainer;

public class EscalatorProxy extends Escalator {
    private class ColumnConfigurationProxy implements ColumnConfiguration {
        private ColumnConfiguration columnConfiguration;

        public ColumnConfigurationProxy(ColumnConfiguration columnConfiguration) {
            this.columnConfiguration = columnConfiguration;
        }

        @Override
        public void removeColumns(int index, int numberOfColumns)
                throws IndexOutOfBoundsException, IllegalArgumentException {
            columnConfiguration.removeColumns(index, numberOfColumns);
            log("removeColumns " + index + ", " + numberOfColumns);
            updateDebugLabel();
        }

        @Override
        public void insertColumns(int index, int numberOfColumns)
                throws IndexOutOfBoundsException, IllegalArgumentException {
            columnConfiguration.insertColumns(index, numberOfColumns);
            log("insertColumns " + index + ", " + numberOfColumns);
            updateDebugLabel();
        }

        @Override
        public int getColumnCount() {
            return columnConfiguration.getColumnCount();
        }

        @Override
        public void setFrozenColumnCount(int count)
                throws IllegalArgumentException {
            columnConfiguration.setFrozenColumnCount(count);
        }

        @Override
        public int getFrozenColumnCount() {
            return columnConfiguration.getFrozenColumnCount();
        }

        @Override
        public void setColumnWidth(int index, int px)
                throws IllegalArgumentException {
            columnConfiguration.setColumnWidth(index, px);
        }

        @Override
        public int getColumnWidth(int index) throws IllegalArgumentException {
            return columnConfiguration.getColumnWidth(index);
        }

        @Override
        public int getColumnWidthActual(int index)
                throws IllegalArgumentException {
            return columnConfiguration.getColumnWidthActual(index);
        }
    }

    private class RowContainerProxy implements RowContainer {
        private final RowContainer rowContainer;

        public RowContainerProxy(RowContainer rowContainer) {
            this.rowContainer = rowContainer;
        }

        @Override
        public EscalatorUpdater getEscalatorUpdater() {
            return rowContainer.getEscalatorUpdater();
        }

        @Override
        public void setEscalatorUpdater(EscalatorUpdater escalatorUpdater)
                throws IllegalArgumentException {
            rowContainer.setEscalatorUpdater(escalatorUpdater);
        }

        @Override
        public void removeRows(int index, int numberOfRows)
                throws IndexOutOfBoundsException, IllegalArgumentException {
            rowContainer.removeRows(index, numberOfRows);
            log(rowContainer.getClass().getSimpleName() + " removeRows "
                    + index + ", " + numberOfRows);
            updateDebugLabel();
        }

        @Override
        public void insertRows(int index, int numberOfRows)
                throws IndexOutOfBoundsException, IllegalArgumentException {
            rowContainer.insertRows(index, numberOfRows);
            log(rowContainer.getClass().getSimpleName() + " insertRows "
                    + index + ", " + numberOfRows);
            updateDebugLabel();
        }

        @Override
        public void refreshRows(int index, int numberOfRows)
                throws IndexOutOfBoundsException, IllegalArgumentException {
            rowContainer.refreshRows(index, numberOfRows);
            log(rowContainer.getClass().getSimpleName() + " refreshRows "
                    + index + ", " + numberOfRows);
        }

        @Override
        public int getRowCount() {
            return rowContainer.getRowCount();
        }

        @Override
        public void setDefaultRowHeight(int px) throws IllegalArgumentException {
            rowContainer.setDefaultRowHeight(px);
        }

        @Override
        public int getDefaultRowHeight() {
            return rowContainer.getDefaultRowHeight();
        }

        @Override
        public Cell getCell(Element element) {
            return rowContainer.getCell(element);
        }

        @Override
        public TableRowElement getRowElement(int index)
                throws IndexOutOfBoundsException, IllegalStateException {
            return rowContainer.getRowElement(index);
        }

        @Override
        public Element getElement() {
            return rowContainer.getElement();
        }

    }

    private static final int MAX_LOG = 9;

    private RowContainer headerProxy = null;
    private RowContainer bodyProxy = null;
    private RowContainer footerProxy = null;
    private ColumnConfiguration columnProxy = null;
    private HTML debugLabel;
    private List<String> logs = new ArrayList<String>();

    @Override
    public RowContainer getHeader() {
        if (headerProxy == null) {
            headerProxy = new RowContainerProxy(super.getHeader());
        }
        return headerProxy;
    }

    @Override
    public RowContainer getFooter() {
        if (footerProxy == null) {
            footerProxy = new RowContainerProxy(super.getFooter());
        }
        return footerProxy;
    }

    @Override
    public RowContainer getBody() {
        if (bodyProxy == null) {
            bodyProxy = new RowContainerProxy(super.getBody());
        }
        return bodyProxy;
    }

    @Override
    public ColumnConfiguration getColumnConfiguration() {
        if (columnProxy == null) {
            columnProxy = new ColumnConfigurationProxy(
                    super.getColumnConfiguration());
        }
        return columnProxy;
    }

    public void setDebugLabel(HTML debugLabel) {
        this.debugLabel = debugLabel;
        updateDebugLabel();
    }

    public void updateDebugLabel() {
        int headers = super.getHeader().getRowCount();
        int bodys = super.getBody().getRowCount();
        int footers = super.getFooter().getRowCount();
        int columns = super.getColumnConfiguration().getColumnCount();

        while (logs.size() > MAX_LOG) {
            logs.remove(0);
        }

        String logString = "<hr>";
        for (String log : logs) {
            logString += log + "<br>";
        }

        debugLabel.setHTML( //
                "Columns: " + columns + "<br>" + //
                        "Header rows: " + headers + "<br>" + //
                        "Body rows: " + bodys + "<br>" + //
                        "Footer rows: " + footers + "<br>" + //
                        logString);
    }

    public void log(String string) {
        logs.add((Duration.currentTimeMillis() % 10000) + ": " + string);
    }
}
