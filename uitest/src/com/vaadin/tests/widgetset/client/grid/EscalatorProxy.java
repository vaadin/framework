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

import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.vaadin.client.widget.escalator.Cell;
import com.vaadin.client.widget.escalator.ColumnConfiguration;
import com.vaadin.client.widget.escalator.EscalatorUpdater;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.escalator.RowContainer.BodyRowContainer;
import com.vaadin.client.widget.escalator.SpacerUpdater;
import com.vaadin.client.widgets.Escalator;
import com.vaadin.tests.widgetset.client.grid.EscalatorBasicClientFeaturesWidget.LogWidget;

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
            logWidget.log("removeColumns " + index + ", " + numberOfColumns);
            logWidget.updateDebugLabel();
        }

        @Override
        public void insertColumns(int index, int numberOfColumns)
                throws IndexOutOfBoundsException, IllegalArgumentException {
            columnConfiguration.insertColumns(index, numberOfColumns);
            logWidget.log("insertColumns " + index + ", " + numberOfColumns);
            logWidget.updateDebugLabel();
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
        public void setColumnWidth(int index, double px)
                throws IllegalArgumentException {
            columnConfiguration.setColumnWidth(index, px);
        }

        @Override
        public double getColumnWidth(int index) throws IllegalArgumentException {
            return columnConfiguration.getColumnWidth(index);
        }

        @Override
        public double getColumnWidthActual(int index)
                throws IllegalArgumentException {
            return columnConfiguration.getColumnWidthActual(index);
        }

        @Override
        public void refreshColumns(int index, int numberOfColumns)
                throws IndexOutOfBoundsException, IllegalArgumentException {
            columnConfiguration.refreshColumns(index, numberOfColumns);
        }

        @Override
        public void setColumnWidths(Map<Integer, Double> indexWidthMap)
                throws IllegalArgumentException {
            columnConfiguration.setColumnWidths(indexWidthMap);
        }
    }

    private class BodyRowContainerProxy extends RowContainerProxy implements
            BodyRowContainer {
        private BodyRowContainer rowContainer;

        public BodyRowContainerProxy(BodyRowContainer rowContainer) {
            super(rowContainer);
            this.rowContainer = rowContainer;
        }

        @Override
        public void setSpacer(int rowIndex, double height)
                throws IllegalArgumentException {
            rowContainer.setSpacer(rowIndex, height);
        }

        @Override
        public void setSpacerUpdater(SpacerUpdater spacerUpdater)
                throws IllegalArgumentException {
            rowContainer.setSpacerUpdater(spacerUpdater);
        }

        @Override
        public SpacerUpdater getSpacerUpdater() {
            return rowContainer.getSpacerUpdater();
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
            logWidget.log(rowContainer.getClass().getSimpleName()
                    + " removeRows " + index + ", " + numberOfRows);
            logWidget.updateDebugLabel();
        }

        @Override
        public void insertRows(int index, int numberOfRows)
                throws IndexOutOfBoundsException, IllegalArgumentException {
            rowContainer.insertRows(index, numberOfRows);
            logWidget.log(rowContainer.getClass().getSimpleName()
                    + " insertRows " + index + ", " + numberOfRows);
            logWidget.updateDebugLabel();
        }

        @Override
        public void refreshRows(int index, int numberOfRows)
                throws IndexOutOfBoundsException, IllegalArgumentException {
            rowContainer.refreshRows(index, numberOfRows);
            logWidget.log(rowContainer.getClass().getSimpleName()
                    + " refreshRows " + index + ", " + numberOfRows);
        }

        @Override
        public int getRowCount() {
            return rowContainer.getRowCount();
        }

        @Override
        public void setDefaultRowHeight(double px)
                throws IllegalArgumentException {
            rowContainer.setDefaultRowHeight(px);
        }

        @Override
        public double getDefaultRowHeight() {
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
        public TableSectionElement getElement() {
            return rowContainer.getElement();
        }

    }

    private RowContainer headerProxy = null;
    private BodyRowContainer bodyProxy = null;
    private RowContainer footerProxy = null;
    private ColumnConfiguration columnProxy = null;
    private LogWidget logWidget;

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
    public BodyRowContainer getBody() {
        if (bodyProxy == null) {
            bodyProxy = new BodyRowContainerProxy(super.getBody());
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

    public void setLogWidget(LogWidget logWidget) {
        this.logWidget = logWidget;
        logWidget.updateDebugLabel();
    }

    @Override
    public void setScrollTop(double scrollTop) {
        logWidget.log("setScrollTop " + scrollTop);
        logWidget.updateDebugLabel();
        super.setScrollTop(scrollTop);
    }
}
