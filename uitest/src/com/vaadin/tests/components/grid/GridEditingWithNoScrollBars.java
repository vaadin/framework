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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;

public class GridEditingWithNoScrollBars extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new Grid();
        grid.addColumn("foo", String.class);
        grid.addColumn("bar", String.class);
        for (int i = 0; i < 10; ++i) {
            grid.addRow("foo", "" + (i % 3 + 1));
        }

        ComboBox stCombo = new ComboBox();
        stCombo.addItem("" + 1);
        stCombo.addItem("" + 2);
        stCombo.addItem("" + 3);
        stCombo.setNullSelectionAllowed(false);
        stCombo.setSizeFull();

        Column stCol = grid.getColumn("bar");
        stCol.setEditorField(stCombo);

        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setEditorEnabled(true);
        grid.setSizeFull();

        addComponent(grid);
    }

}
