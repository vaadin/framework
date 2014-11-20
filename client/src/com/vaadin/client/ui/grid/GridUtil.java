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
package com.vaadin.client.ui.grid;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.Util;

/**
 * Utilities for working with Grid.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridUtil {

    /**
     * Returns the cell the given element belongs to.
     *
     * @param grid
     *            the grid instance that is queried
     * @param e
     *            a cell element or the descendant of one
     * @return the cell or null if the element is not a grid cell or a
     *         descendant of one
     */
    public static Cell findCell(Grid<?> grid, Element e) {
        RowContainer container = grid.getEscalator().findRowContainer(e);
        return container != null ? container.getCell(e) : null;
    }

    /**
     * Returns the Grid instance containing the given element, if any.
     * <p>
     * <strong>Note:</strong> This method may not work reliably if the grid in
     * question is wrapped in a {@link Composite} <em>unless</em> the element is
     * inside another widget that is a child of the wrapped grid; please refer
     * to the note in {@link Util#findWidget(Element, Class) Util.findWidget}
     * for details.
     * 
     * @param e
     *            the element whose parent grid to find
     * @return the parent grid or null if none found.
     */
    public static Grid<?> findClosestParentGrid(Element e) {
        Widget w = Util.findWidget(e, null);

        while (w != null && !(w instanceof Grid)) {
            w = w.getParent();
        }
        return (Grid<?>) w;
    }

    /**
     * Accesses the package private method Widget#setParent()
     * 
     * @param widget
     *            The widget to access
     * @param parent
     *            The parent to set
     */
    static native final void setParent(Widget widget, Grid<?> parent)
    /*-{
        widget.@com.google.gwt.user.client.ui.Widget::setParent(Lcom/google/gwt/user/client/ui/Widget;)(parent);
    }-*/;

}
