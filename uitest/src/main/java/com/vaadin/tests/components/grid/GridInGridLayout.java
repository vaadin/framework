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
package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

public class GridInGridLayout extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("100%");
        layout.setHeight("320px");
        layout.setMargin(true);
        addComponent(cssLayout);
        cssLayout.addComponent(layout);

        final Grid grid = new Grid();
        grid.setSizeFull();
        for (int i = 0; i < 20; i++) {
            Grid.Column column = grid.addColumn("" + i);
            column.setHidable(true);
            column.setEditable(true);
        }
        grid.setEditorEnabled(true);
        grid.setColumnReorderingAllowed(true);
        for (int i = 0; i < 300; i++) {
            grid.addRow("Foo", "Bar", "far", "bar", "bar", "Foo", "Bar", "Bar",
                    "bar", "bar", "Foo", "Bar", "Bar", "bar", "bar", "Foo",
                    "Bar", "Bar", "bar", "bar");

        }
        layout.addComponent(grid);
        grid.setHeight("300px");
        grid.setWidth("400px");
    }

    @Override
    protected Integer getTicketNumber() {
        return 18698;
    }
}
