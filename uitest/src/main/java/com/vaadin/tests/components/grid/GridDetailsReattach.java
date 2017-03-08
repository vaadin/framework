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
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GridDetailsReattach extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout verticalMain = new VerticalLayout();

        final VerticalLayout layoutWithGrid = new VerticalLayout();

        Grid<String> grid = new Grid<>("Grid");
        grid.addColumn(String::toString).setCaption("Foo");
        grid.setHeight("150px");
        grid.setItems("Foo");
        grid.setDetailsGenerator(str -> new Label("AnyDetails"));
        grid.setDetailsVisible("Foo", true);
        layoutWithGrid.addComponent(grid);

        Button addCaptionToLayoutWithGridButton = new Button(
                "Add caption to 'layoutWithGrid' layout");
        addCaptionToLayoutWithGridButton.addClickListener(e -> layoutWithGrid
                .setCaption("Caption added to 'layoutWithGrid' layout"));
        layoutWithGrid.addComponent(addCaptionToLayoutWithGridButton);

        verticalMain.addComponent(layoutWithGrid);

        addComponent(verticalMain);

    }
}
