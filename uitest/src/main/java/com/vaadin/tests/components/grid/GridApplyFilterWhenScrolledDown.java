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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public class GridApplyFilterWhenScrolledDown extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();

        grid.addColumn(ValueProvider.identity()).setId("Name")
                .setCaption("Name");

        List<String> data = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            data.add("Name " + i);
        }

        data.add("Test");
        grid.setItems(data);

        addComponent(grid);
        Button button = new Button("Filter Test item",
                event -> filter(grid.getDataProvider(), data));
        addComponent(button);
    }

    private void filter(DataProvider<String, ?> dataProvider,
            List<String> data) {
        String last = data.get(data.size() - 1);
        data.clear();
        data.add(last);
        dataProvider.refreshAll();
    }

}
