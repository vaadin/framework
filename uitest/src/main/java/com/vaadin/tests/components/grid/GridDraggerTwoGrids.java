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

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridDragger;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridDraggerTwoGrids extends AbstractGridDnD {

    @Override
    protected void setup(VaadinRequest request) {
        getUI().setMobileHtml5DndEnabled(true);

        // Drag source Grid
        Grid<Person> left = createGridAndFillWithData(50);

        // Drop target Grid
        Grid<Person> right = createGridAndFillWithData(0);

        GridDragger<Person> gridDragger = new GridDragger<>(left, right);

        CheckBox addItemsToEnd = new CheckBox("Add Items To End",
                gridDragger.isAddItemsToEnd());
        addItemsToEnd.addValueChangeListener(
                event -> gridDragger.setAddItemsToEnd(event.getValue()));
        CheckBox removeItemsFromSource = new CheckBox(
                "Remove items from source grid",
                gridDragger.isRemoveItemsFromSource());
        removeItemsFromSource.addValueChangeListener(event -> gridDragger
                .setRemoveItemsFromSource(event.getValue()));

        controls.addComponents(addItemsToEnd, removeItemsFromSource);

        initializeTestFor(gridDragger);
    }

}
