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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

public class GridColumnWidthsWithoutData extends AbstractTestUI {

    private Grid grid = createGrid(true);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(grid);

        addComponent(new Button("Recreate without data",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        replaceGrid(createGrid(false));
                    }
                }));

        addComponent(new Button("Recreate with data",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        replaceGrid(createGrid(true));
                    }
                }));

        addComponent(new Button("Add data", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addDataToGrid(grid);
            }
        }));

        addComponent(new Button("Remove data", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                grid.getContainerDataSource().removeAllItems();
            }
        }));

    }

    private void replaceGrid(Grid newGrid) {
        ((VerticalLayout) grid.getParent()).replaceComponent(grid, newGrid);
        grid = newGrid;
    }

    private Grid createGrid(boolean withData) {
        Grid grid = new Grid();
        grid.addColumn("foo");
        grid.addColumn("bar");
        grid.setWidth("300px");

        if (withData) {
            addDataToGrid(grid);
        }

        return grid;
    }

    private void addDataToGrid(Grid grid) {
        grid.addRow("Some", "Data with more data in one col");
    }

}
