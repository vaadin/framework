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

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

public class GridColumnAutoExpand extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        addComponent(layout);

        Grid<String> grid = new Grid<>();
        grid.setCaption("Broken Grid with Caption");
        grid.setWidth("100%");
        grid.setHeight("100px");

        grid.addColumn(ValueProvider.identity()).setCaption("Col1")
                .setWidth(100);
        grid.addColumn(ValueProvider.identity()).setCaption("Col2")
                .setMinimumWidth(100).setExpandRatio(1);

        layout.addComponent(grid);
    }
}
