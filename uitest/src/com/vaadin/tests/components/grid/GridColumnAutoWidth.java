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
package com.vaadin.tests.components.grid;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.HtmlRenderer;

public class GridColumnAutoWidth extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new Grid(createContainer());
        grid.getColumn("fixed width narrow").setWidth(50);
        grid.getColumn("fixed width wide").setWidth(200);

        for (Object propertyId : grid.getContainerDataSource()
                .getContainerPropertyIds()) {
            Column column = grid.getColumn(propertyId);
            column.setExpandRatio(0);
            column.setRenderer(new HtmlRenderer());
            grid.getHeaderRow(0).getCell(propertyId)
                    .setHtml("<span>" + column.getHeaderCaption() + "</span>");
        }

        grid.setSelectionMode(SelectionMode.NONE);
        grid.setWidth("700px");
        addComponent(grid);
    }

    private static Container.Indexed createContainer() {
        IndexedContainer c = new IndexedContainer();
        c.addContainerProperty("equal width", String.class,
                "<span>equal width</span>");
        c.addContainerProperty("short", String.class,
                "<span>a very long cell content</span>");
        c.addContainerProperty("a very long header content", String.class,
                "<span>short</span>");
        c.addContainerProperty("fixed width narrow", String.class,
                "<span>fixed width narrow</span>");
        c.addContainerProperty("fixed width wide", String.class,
                "<span>fixed width wide</span>");
        c.addItem();
        return c;
    }
}
