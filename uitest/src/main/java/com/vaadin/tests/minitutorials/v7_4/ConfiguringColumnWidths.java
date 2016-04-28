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
package com.vaadin.tests.minitutorials.v7_4;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;

@Theme("valo")
public class ConfiguringColumnWidths extends UI {

    @Override
    protected void init(VaadinRequest request) {
        Grid grid = new Grid(GridExampleHelper.createContainer());
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setColumnOrder("name", "amount", "count");

        setupCase(grid, 3);

        setContent(grid);
    }

    private void setupCase1(Grid grid) {
        grid.getColumn("name").setExpandRatio(1);
    }

    private void setupCase2(Grid grid) {
        grid.getColumn("name").setExpandRatio(1);
        grid.getColumn("amount").setWidth(100);
        grid.getColumn("count").setWidth(100);
    }

    private void setupCase3(Grid grid) {
        grid.setWidth("400px");
        grid.getColumn("name").setExpandRatio(1);
        grid.getColumn("amount").setWidth(100);
        grid.getColumn("count").setWidth(100);
    }

    private void setupCase4(Grid grid) {
        grid.setWidth("400px");
        grid.getColumn("name").setMinimumWidth(250);
        grid.getColumn("amount").setWidth(100);
        grid.getColumn("count").setWidth(100);
    }

    private void setupCase5(Grid grid) {
        grid.setWidth("400px");
        grid.setFrozenColumnCount(1);
        grid.getColumn("name").setMinimumWidth(250);
        grid.getColumn("amount").setWidth(100);
        grid.getColumn("count").setWidth(100);
    }

    private void setupCase6(Grid grid) {
        grid.setWidth("700px");
        grid.setFrozenColumnCount(1);
        grid.getColumn("name").setMinimumWidth(250);
        grid.getColumn("amount").setWidth(100);
        grid.getColumn("count").setWidth(100);
    }

    private void setupCase(Grid grid, int number) {
        if (number == 0) {
            return;
        }
        try {
            getClass().getDeclaredMethod("setupCase" + number, Grid.class)
                    .invoke(this, grid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
