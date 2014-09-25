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
package com.vaadin.client.ui.grid.renderers;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.Util;
import com.vaadin.client.ui.grid.FlyweightCell;

/**
 * A renderer for rendering widgets into cells.
 * 
 * @since
 * @author Vaadin Ltd
 * @param <T>
 *            the row data type
 * @param <W>
 *            the Widget type
 */
public abstract class WidgetRenderer<T, W extends Widget> extends
        ComplexRenderer<T> {

    @Override
    public void init(FlyweightCell cell) {
        // Implement if needed
    }

    /**
     * Creates a widget to attach to a cell. The widgets will be attached to the
     * cell after the cell element has been attached to DOM.
     * 
     * @return widget to attach to a cell. All returned instances should be new
     *         widget instances without a parent.
     */
    public abstract W createWidget();

    @Override
    public void render(FlyweightCell cell, T data) {
        W w = Util.findWidget(cell.getElement().getFirstChildElement(), null);
        assert w != null : "Widget not found in cell (" + cell.getColumn()
                + "," + cell.getRow() + ")";
        render(cell, data, w);
    }

    /**
     * Renders a cell with a widget. This provides a way to update any
     * information in the widget that is cell specific. Do not detach the Widget
     * here, it will be done automatically by the Grid when the widget is no
     * longer needed.
     * 
     * @param cell
     *            the cell to render
     * @param data
     *            the data of the cell
     * @param widget
     *            the widget embedded in the cell
     */
    public abstract void render(FlyweightCell cell, T data, W widget);

}
