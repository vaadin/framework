/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.client.widget.treegrid.events;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.renderers.HierarchyRenderer;
import com.vaadin.client.widget.grid.events.AbstractGridMouseEventHandler.GridClickHandler;
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.client.widget.treegrid.TreeGrid;
import com.vaadin.client.widgets.Grid;

/**
 * Represents native mouse click event in TreeGrid.
 * <p>
 * Differs from {@link GridClickEvent} only in allowing events to originate form
 * hierarchy widget.
 *
 * @since 8.1
 * @author Vaadin Ltd
 */
public class TreeGridClickEvent extends GridClickEvent {

    public static final Type<GridClickHandler> TYPE = new Type<GridClickHandler>(
            BrowserEvents.CLICK, new TreeGridClickEvent());

    @Override
    public Type<GridClickHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    public TreeGrid getGrid() {
        EventTarget target = getNativeEvent().getEventTarget();
        if (!Element.is(target)) {
            return null;
        }
        return WidgetUtil.findWidget(Element.as(target), TreeGrid.class, false);
    }

    @Override
    protected boolean ignoreEventFromTarget(Grid<?> grid,
            Element targetElement) {
        // Do not ignore when element is in hierarchy renderer
        return super.ignoreEventFromTarget(grid, targetElement)
                && !HierarchyRenderer.isElementInHierarchyWidget(targetElement);
    }
}
