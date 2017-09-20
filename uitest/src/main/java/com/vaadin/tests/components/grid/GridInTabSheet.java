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

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridInTabSheet extends AbstractReindeerTestUI {

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet sheet = new TabSheet();
        final Grid<Integer> grid = new Grid<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addColumn(ValueProvider.identity(), new NumberRenderer())
                .setId("count");

        LinkedList<Integer> items = IntStream.range(0, 3).boxed()
                .collect(Collectors.toCollection(LinkedList::new));
        ListDataProvider<Integer> provider = DataProvider.ofCollection(items);
        grid.setDataProvider(provider);

        sheet.addTab(grid, "Grid");
        sheet.addTab(new Label("Hidden"), "Label");

        addComponent(sheet);
        addComponent(new Button("Add row to Grid", event -> {
            items.add(100 + index.getAndIncrement());
            provider.refreshAll();
        }));

        Button remove = new Button("Remove row from Grid", event -> {
            Object firstItemId = items.get(0);
            if (firstItemId != null) {
                items.remove(0);
                provider.refreshAll();
            }
        });
        addComponent(remove);
        Button addGenerator = new Button("Add CellStyleGenerator", event -> {
            grid.setStyleGenerator(item -> {
                if (item % 2 == 1) {
                    return null;
                }
                return "count";
            });
        });

        addComponent(addGenerator);
    }
}
