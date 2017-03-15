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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.VerticalSplitPanel;

public class GridScrollToLineWhileResizing extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final VerticalSplitPanel vsp = new VerticalSplitPanel();
        vsp.setWidth(500, Unit.PIXELS);
        vsp.setHeight(500, Unit.PIXELS);
        vsp.setSplitPosition(100, Unit.PERCENTAGE);
        addComponent(vsp);

        Grid<Integer> grid = new Grid<>();
        grid.addColumn(item -> "cell" + item);
        grid.setItems(IntStream.range(0, 100).boxed());
        grid.setSizeFull();

        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.addSelectionListener(event -> {
            vsp.setSplitPosition(50, Unit.PERCENTAGE);
            grid.scrollTo(event.getFirstSelectedItem().get());
        });

        vsp.setFirstComponent(grid);

    }

    @Override
    protected String getTestDescription() {
        return "Tests scrollToLine while moving SplitPanel split position to resize the Grid on the same round-trip.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
