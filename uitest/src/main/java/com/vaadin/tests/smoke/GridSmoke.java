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
package com.vaadin.tests.smoke;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionMode;

public class GridSmoke extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final Grid grid = new Grid();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addColumn("firstName");
        grid.addColumn("age", Integer.class);

        grid.addRow("Lorem", Integer.valueOf(1));
        grid.addRow("Ipsum", Integer.valueOf(2));

        addComponent(grid);

        addComponent(new Button("Add new row",
                event -> grid.addRow("Dolor", Integer.valueOf(3))));
    }

}
