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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Grid;

@SuppressWarnings("serial")
public class GridResizeHiddenColumn extends GridEditorUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = createGrid();
        grid.setItems(createTestData());
        addComponent(grid);

        grid.setColumns("firstName", "phone", "lastName", "zip");
        grid.getColumn("firstName").setHidable(true);
        grid.getColumn("phone").setHidable(true).setHidden(true);
        grid.getColumn("lastName").setHidable(true).setHidden(true);
        grid.getColumn("zip").setHidable(true);

        addComponent(grid);

        grid.addColumnResizeListener(event -> {
            log(String.format("Column resized: id=%s, width=%s",
                    event.getColumn().getId(), event.getColumn().getWidth()));
        });
    }

    @Override
    protected String getTestDescription() {
        return "Resize columns and then make hidden column visible. The originally hidden column should have an extended width.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19826;
    }
}
