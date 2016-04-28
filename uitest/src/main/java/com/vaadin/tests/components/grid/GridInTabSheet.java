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
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class GridInTabSheet extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet sheet = new TabSheet();
        final Grid grid = new Grid();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addColumn("count", Integer.class);
        for (Integer i = 0; i < 3; ++i) {
            grid.addRow(i);
        }

        sheet.addTab(grid, "Grid");
        sheet.addTab(new Label("Hidden"), "Label");

        addComponent(sheet);
        addComponent(new Button("Add row to Grid", new Button.ClickListener() {

            private Integer k = 0;

            @Override
            public void buttonClick(ClickEvent event) {
                grid.addRow(100 + (k++));
            }
        }));
        addComponent(new Button("Remove row from Grid",
                new Button.ClickListener() {

                    private Integer k = 0;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Object firstItemId = grid.getContainerDataSource()
                                .firstItemId();
                        if (firstItemId != null) {
                            grid.getContainerDataSource().removeItem(
                                    firstItemId);
                        }
                    }
                }));
        addComponent(new Button("Add CellStyleGenerator",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.setCellStyleGenerator(new CellStyleGenerator() {
                            @Override
                            public String getStyle(CellReference cellReference) {
                                int rowIndex = ((Integer) cellReference
                                        .getItemId()).intValue();
                                Object propertyId = cellReference
                                        .getPropertyId();
                                if (rowIndex % 4 == 1) {
                                    return null;
                                } else if (rowIndex % 4 == 3
                                        && "Column 1".equals(propertyId)) {
                                    return null;
                                }
                                return propertyId.toString().replace(' ', '_');
                            }
                        });
                    }
                }));
    }
}
