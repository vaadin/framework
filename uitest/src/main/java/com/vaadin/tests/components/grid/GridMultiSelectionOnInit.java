/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import com.vaadin.ui.Grid.MultiSelectionModel;
import com.vaadin.ui.Grid.SelectionMode;

public class GridMultiSelectionOnInit extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.addColumn("foo", String.class);
        grid.addRow("Foo 1");
        grid.addRow("Foo 2");
        grid.setSelectionMode(SelectionMode.MULTI);
        addComponent(grid);

        addComponent(new Button("Select rows", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                ((MultiSelectionModel) grid.getSelectionModel()).setSelected(
                        grid.getContainerDataSource().getItemIds());
            }
        }));
    }
}
