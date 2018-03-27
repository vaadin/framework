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
package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;

public class GridWidthIncrease extends AbstractTestUI {

    public static int COLUMN_COUNT = 5;

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        Object[] rowData = new String[COLUMN_COUNT];
        for (int i = 0; i < COLUMN_COUNT; ++i) {
            grid.addColumn("Column " + i, String.class);
            rowData[i] = "Foo (0, " + i + ")";
        }
        grid.addRow(rowData);
        grid.setWidth(400 + "px");
        addComponent(grid);
        addComponent(
                new Button("Increase Grid Width", new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.setWidth((grid.getWidth() + 50) + "px");
                    }
                }));
    }
}
