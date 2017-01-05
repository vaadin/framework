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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridDetailsDetach extends AbstractTestUI {

    private Grid currentGrid;

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setMargin(false);
        layout.setSizeFull();

        Button button = new Button("Test");
        layout.addComponent(button);
        layout.setExpandRatio(button, 0f);

        currentGrid = generateGrid();
        final VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setSpacing(false);
        gridContainer.setMargin(false);
        gridContainer.addComponent(currentGrid);

        button.addClickListener(event -> gridContainer
                .replaceComponent(currentGrid, new Label("Foo")));

        layout.addComponent(new Button("Reattach Grid", event -> {
            gridContainer.removeAllComponents();
            gridContainer.addComponent(currentGrid);
        }));

        layout.addComponent(gridContainer);
        layout.setExpandRatio(gridContainer, 1f);

        addComponent(layout);
    }

    private Grid<GridExampleBean> generateGrid() {
        List<GridExampleBean> items = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            items.add(new GridExampleBean("Bean " + i, i * i, i / 10d));
        }

        final Grid<GridExampleBean> grid = new Grid<>();
        grid.setItems(items);
        grid.addColumn(GridExampleBean::getName);
        grid.addColumn(GridExampleBean::getAmount, new NumberRenderer());
        grid.addColumn(GridExampleBean::getCount, new NumberRenderer());
        grid.setSizeFull();
        grid.setSelectionMode(SelectionMode.NONE);

        grid.setDetailsGenerator(item -> {
            VerticalLayout layout = new VerticalLayout(
                    new Label("Extra data for " + item.getName()));
            layout.setMargin(true);
            return layout;
        });

        grid.addItemClickListener(event -> {
            GridExampleBean item = event.getItem();
            grid.setDetailsVisible(item, !grid.isDetailsVisible(item));
        });
        return grid;
    }

    public class GridExampleBean {

        private String name;

        private int count;

        private double amount;

        public GridExampleBean() {
        }

        public GridExampleBean(String name, int count, double amount) {
            this.name = name;
            this.count = count;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        public double getAmount() {
            return amount;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

    }

}
