/*
 * Copyright 2000-2014 Vaadin Ltd.
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
import java.util.List;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

public class GridThemeChange extends AbstractTestUI {
    private final List<String> themes = Arrays.asList("valo", "reindeer",
            "runo", "chameleon", "base");

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.setSelectionMode(SelectionMode.SINGLE);

        grid.addColumn("Theme");
        for (String theme : themes) {
            Object itemId = grid.addRow(theme);
            if (theme.equals(getTheme())) {
                grid.select(itemId);
            }
        }

        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                Object selectedItemId = grid.getSelectedRow();
                Object theme = grid.getContainerDataSource()
                        .getItem(selectedItemId).getItemProperty("Theme")
                        .getValue();
                setTheme(String.valueOf(theme));
            }
        });

        addComponent(grid);

    }
}
