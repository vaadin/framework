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

import java.util.Optional;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

public class GridReplaceContainer extends SimpleGridUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<Person> grid = createGrid();

        grid.setSelectionMode(SelectionMode.SINGLE);

        grid.addSelectionListener(event -> {
            Optional<Person> selected = event.getFirstSelectedItem();
            if (selected.isPresent()) {
                log("Now selected: " + selected.get().getAge());
            } else {
                log("Now selected: null");
            }
        });

        addComponent(grid);
        Button b = new Button("Re-set data source",
                event -> grid.setItems(createPersons()));
        addComponent(b);
    }

}
