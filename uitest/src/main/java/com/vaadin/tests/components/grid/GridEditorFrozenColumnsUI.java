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
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Grid;

@Theme("valo")
public class GridEditorFrozenColumnsUI extends GridEditorUI {

    @Override
    protected Grid<Person> createGrid() {
        Grid<Person> grid = super.createGrid();

        grid.setFrozenColumnCount(2);

        grid.setWidth("600px");
        grid.setHeight("100%");

        return grid;
    }

    @Override
    protected Integer getTicketNumber() {
        return 16727;
    }

    @Override
    protected String getTestDescription() {
        return "Frozen columns should also freeze cells in editor.";
    }
}
