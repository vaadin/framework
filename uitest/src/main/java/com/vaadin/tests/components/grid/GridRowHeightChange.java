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
import java.util.List;
import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class GridRowHeightChange extends AbstractReindeerTestUI {

    private final List<String> themes = Arrays.asList("valo", "reindeer",
            "runo", "chameleon", "base");

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Integer> grid = new Grid<>();

        // create column and fill rows
        grid.addColumn(item -> "row_" + item).setCaption("Header");

        grid.setItems(IntStream.range(1, 11).boxed());

        // set height mode and height
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(10);

        // create a tabsheet with one tab and place grid inside
        VerticalLayout tab = new VerticalLayout();
        tab.setSpacing(false);
        tab.setMargin(false);
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthUndefined();
        tabSheet.addTab(tab, "Tab");
        tab.addComponent(grid);

        // Theme selector
        NativeSelect<String> themeSelector = new NativeSelect<>(
                "Theme selector", themes);
        themeSelector.setSelectedItem("reindeer");
        themeSelector.setEmptySelectionAllowed(false);
        themeSelector.addValueChangeListener(event -> {
            setTheme(event.getValue());
        });

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setSizeUndefined();

        layout.addComponent(themeSelector);
        layout.addComponent(tabSheet);

        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Test if Grid's height is adjusted when HeightMode.ROW and row height is recalculated.<br>"
                + "When loading is complete, all 10 rows should be visible with all themes.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 20104;
    }
}
