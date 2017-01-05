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

import java.util.stream.IntStream;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class GridDetailsWidth extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);

        final Grid<String> grid = new Grid<>();

        Column<String, String> column = grid.addColumn(ValueProvider.identity())
                .setCaption("Hello");
        grid.setItems(IntStream.range(0, 3).mapToObj(i -> "Hello " + i));

        column.setWidth(600);
        grid.setWidth(400, Unit.PIXELS);

        grid.setDetailsGenerator(item -> {
            HorizontalLayout myLayout = new HorizontalLayout();
            TextArea textArea1 = new TextArea();
            TextArea textArea2 = new TextArea();
            textArea1.setSizeFull();
            textArea2.setSizeFull();
            myLayout.addComponent(textArea1);
            myLayout.addComponent(textArea2);
            myLayout.setWidth(100, Unit.PERCENTAGE);
            myLayout.setHeight(null);
            myLayout.setMargin(true);
            return myLayout;
        });

        grid.addItemClickListener(event -> grid.setDetailsVisible(
                event.getItem(), !grid.isDetailsVisible(event.getItem())));

        layout.addComponent(grid);

        addComponent(layout);
    }

    @Override
    protected Integer getTicketNumber() {
        return 18223;
    }

    @Override
    protected String getTestDescription() {
        return "Tests that Escalator will not set explicit widths to the TD element in a details row.";
    }

}
