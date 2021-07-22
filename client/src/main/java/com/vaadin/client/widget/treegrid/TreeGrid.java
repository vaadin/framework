/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.client.widget.treegrid;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.widget.escalator.EscalatorUpdater;
import com.vaadin.client.widget.escalator.Row;
import com.vaadin.client.widget.grid.events.BodyClickHandler;
import com.vaadin.client.widget.grid.events.BodyDoubleClickHandler;
import com.vaadin.client.widget.treegrid.events.TreeGridClickEvent;
import com.vaadin.client.widget.treegrid.events.TreeGridDoubleClickEvent;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.data.HierarchicalDataCommunicatorConstants;

import elemental.json.JsonObject;

/**
 * An extension of the Grid widget, which supports displaying of hierarchical
 * data.
 *
 * @author Vaadin Ltd
 * @see Grid
 * @since 8.1
 */
public class TreeGrid extends Grid<JsonObject> {

    /**
     * Style name prefix for the row's depth in the hierarchy
     */
    private String depthStyleNamePrefix;

    /**
     * Creates a new instance.
     */
    public TreeGrid() {
        setAriaRole("treegrid");
    }

    /**
     * Body updater that adds additional style to each row containing depth
     * information inside the hierarchy.
     */
    protected class BodyUpdater extends Grid<JsonObject>.BodyUpdater {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public void update(Row row, Iterable cellsToUpdate) {
            super.update(row, cellsToUpdate);

            int rowIndex = row.getRow();
            TableRowElement rowElement = row.getElement();

            JsonObject rowData = getDataSource().getRow(rowIndex);
            if (rowData != null) {
                int depth = (int) rowData.getObject(
                        HierarchicalDataCommunicatorConstants.ROW_HIERARCHY_DESCRIPTION)
                        .getNumber(
                                HierarchicalDataCommunicatorConstants.ROW_DEPTH);

                // Add or replace style name containing depth information
                String styleToBeReplaced = getFullClassName(
                        depthStyleNamePrefix, rowElement.getClassName());
                if (styleToBeReplaced == null) {
                    rowElement.addClassName(depthStyleNamePrefix + depth);
                } else {
                    rowElement.replaceClassName(styleToBeReplaced,
                            depthStyleNamePrefix + depth);
                }
            }
        }

        private String getFullClassName(String prefix, String classNameList) {
            int start = classNameList.indexOf(prefix);
            int end = start + prefix.length();
            if (start > -1) {
                while (end < classNameList.length()
                        && classNameList.charAt(end) != ' ') {
                    end++;
                }
                return classNameList.substring(start, end);
            }
            return null;
        }
    }

    /**
     * Method for accessing the private {@link Grid#focusCell(int, int)} method
     * from this package.
     *
     * @param rowIndex
     *            index of row to focus
     * @param columnIndex
     *            index (excluding hidden columns) of cell to focus
     */
    public native void focusCell(int rowIndex, int columnIndex)
    /*-{
        this.@com.vaadin.client.widgets.Grid::focusCell(II)(rowIndex, columnIndex);
    }-*/;

    /**
     * Method for accessing the private
     * {@link Grid#isElementInChildWidget(Element)} method from this package.
     *
     * @param e
     *            the element to check
     * @return {@code true} if the element is located within a child widget of
     *         this TreeGrid, {@code false} otherwise.
     */
    public native boolean isElementInChildWidget(Element e)
    /*-{
        return this.@com.vaadin.client.widgets.Grid::isElementInChildWidget(*)(e);
    }-*/;

    @Override
    public HandlerRegistration addBodyClickHandler(BodyClickHandler handler) {
        return addHandler(handler, TreeGridClickEvent.TYPE);
    }

    @Override
    public HandlerRegistration addBodyDoubleClickHandler(
            BodyDoubleClickHandler handler) {
        return addHandler(handler, TreeGridDoubleClickEvent.TYPE);
    }

    @Override
    protected EscalatorUpdater createBodyUpdater() {
        return new BodyUpdater();
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);

        depthStyleNamePrefix = getStylePrimaryName() + "-row-depth-";
    }
}
