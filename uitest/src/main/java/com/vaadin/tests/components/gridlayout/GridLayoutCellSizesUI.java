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
package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class GridLayoutCellSizesUI extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        // Create a 4 by 4 grid layout
        final GridLayout grid = new GridLayout(4, 4);

        // Fill out the first row using the cursor
        grid.addComponent(new Button("R/C 1"));
        for (int i = 0; i < 3; i++) {
            grid.addComponent(new Button("Col " + (grid.getCursorX() + 1)));
        }

        // Fill out the first column using coordinates
        for (int i = 1; i < 4; i++) {
            grid.addComponent(new Button("Row " + i), 0, i);
        }

        // Add some components of various shapes.
        grid.addComponent(new Button("3x1 button"), 1, 1, 3, 1);
        grid.addComponent(new Label("1x2 cell"), 1, 2, 1, 3);
        final InlineDateField date = new InlineDateField("A 2x2 date field");
        date.setResolution(DateResolution.DAY);
        grid.addComponent(date, 2, 2, 3, 3);

        grid.setMargin(true);
        grid.setSizeUndefined();

        addComponent(grid);
    }

    @Override
    protected Integer getTicketNumber() {
        return 17039;
    }

    @Override
    protected String getTestDescription() {
        return "Grid cells should be full size when adding borders around the cells";
    }
}
