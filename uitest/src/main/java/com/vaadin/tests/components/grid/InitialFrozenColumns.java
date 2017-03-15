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
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

public class InitialFrozenColumns extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>(Person.class);
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setColumns();
        grid.addColumn("firstName").setWidth(200);
        grid.addColumn("lastName").setWidth(200);
        grid.addColumn("email").setWidth(200);

        grid.setItems(
                new Person("First", "last", "email", 242, Sex.UNKNOWN, null));

        int frozen = 2;
        if (request.getParameter("frozen") != null) {
            frozen = Integer.parseInt(request.getParameter("frozen"));
        }
        grid.setFrozenColumnCount(frozen);

        addComponent(grid);
    }

}
