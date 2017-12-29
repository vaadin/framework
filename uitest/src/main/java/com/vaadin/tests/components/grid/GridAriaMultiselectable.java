/*
 * Copyright 2000-2017 Vaadin Ltd.
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
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

/**
 * @author Vaadin Ltd
 *
 */
public class GridAriaMultiselectable extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(ValueProvider.identity());
        grid.setItems("a", "b");
        grid.setSelectionMode(SelectionMode.NONE);

        addComponent(grid);

        Button singleSelectBtn = new Button("SingleSelect",
                event -> grid.setSelectionMode(SelectionMode.SINGLE));
        addComponent(singleSelectBtn);

        Button multiSelectBtn = new Button("MultiSelect",
                event -> grid.setSelectionMode(SelectionMode.MULTI));
        addComponent(multiSelectBtn);
    }
}
