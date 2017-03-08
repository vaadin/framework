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

import java.util.Arrays;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.NumberRenderer;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridColumnResizing extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TextField input = new TextField();
        Label isResizedLabel = new Label("not resized");
        Grid<Person> grid = new Grid<>();
        Column<Person, String> nameColumn = grid.addColumn(Person::getFirstName)
                .setCaption("Name");
        Column<Person, Integer> ageColumn = grid
                .addColumn(Person::getAge, new NumberRenderer())
                .setCaption("Age");
        grid.addColumnResizeListener(event -> {
            if (event.isUserOriginated()) {
                isResizedLabel.setValue("client resized");
            } else {
                isResizedLabel.setValue("server resized");
            }
        });
        grid.setItems(Arrays.asList(Person.createTestPerson1(),
                Person.createTestPerson2()));

        addComponent(input);
        addButton("set width", event -> nameColumn
                .setWidth(Double.parseDouble(input.getValue())));
        addButton("set expand ratio", event -> {
            nameColumn.setExpandRatio(4);
            ageColumn.setExpandRatio(1);
        });
        addButton("set min width", event -> nameColumn
                .setMinimumWidth(Double.parseDouble(input.getValue())));
        addButton("set max width", event -> nameColumn
                .setMaximumWidth(Double.parseDouble(input.getValue())));
        addButton("toggle resizable",
                event -> nameColumn.setResizable(!nameColumn.isResizable()));

        addComponents(grid, isResizedLabel);
    }

}
