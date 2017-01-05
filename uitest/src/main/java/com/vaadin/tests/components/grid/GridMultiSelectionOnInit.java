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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl.SelectAllCheckBoxVisibility;

public class GridMultiSelectionOnInit extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<String> grid = new Grid<>();
        grid.setItems("Foo 1", "Foo 2");
        grid.addColumn(item -> item);
        MultiSelectionModelImpl<String> selectionModel = (MultiSelectionModelImpl<String>) grid
                .setSelectionMode(SelectionMode.MULTI);

        addComponent(grid);

        addComponent(new Button("Select rows",
                event -> grid.getSelectionModel().select("Foo 1")));
        if (request.getParameter("initialSelection") != null) {
            grid.getSelectionModel().select("Foo 2");
        }

        RadioButtonGroup<SelectAllCheckBoxVisibility> rbg = new RadioButtonGroup<>(
                "Select All Visible",
                Arrays.asList(SelectAllCheckBoxVisibility.VISIBLE,
                        SelectAllCheckBoxVisibility.HIDDEN,
                        SelectAllCheckBoxVisibility.DEFAULT));
        rbg.setValue(selectionModel.getSelectAllCheckBoxVisibility());
        rbg.addValueChangeListener(event -> selectionModel
                .setSelectAllCheckBoxVisibility(event.getValue()));
        addComponent(rbg);
    }
}
