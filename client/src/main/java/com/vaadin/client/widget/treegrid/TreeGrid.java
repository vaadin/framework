/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.widget.grid.events.BodyClickHandler;
import com.vaadin.client.widget.treegrid.events.TreeGridClickEvent;
import com.vaadin.client.widgets.Grid;

import elemental.json.JsonObject;

/**
 * An extension of the Grid widget, which supports displaying of hierarchical
 * data.
 *
 * @see Grid
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class TreeGrid extends Grid<JsonObject> {

    /**
     * Method for accessing the private {@link Grid#focusCell(int, int)} method
     * from this package
     */
    public native void focusCell(int rowIndex, int columnIndex)/*-{
        this.@com.vaadin.client.widgets.Grid::focusCell(II)(rowIndex, columnIndex);
    }-*/;

    /**
     * Method for accessing the private
     * {@link Grid#isElementInChildWidget(Element)} method from this package
     */
    public native boolean isElementInChildWidget(Element e)/*-{
        return this.@com.vaadin.client.widgets.Grid::isElementInChildWidget(*)(e);
    }-*/;

    @Override
    public HandlerRegistration addBodyClickHandler(BodyClickHandler handler) {
        return addHandler(handler, TreeGridClickEvent.TYPE);
    }
}
