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
import com.vaadin.ui.Grid;

/**
 * @author Vaadin Ltd
 *
 */
public class HorizontalScrollAfterResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.setWidth("100%");
        grid.setHeight("350px");
        grid.setCaption("My Grid");

        for (int i = 0; i < 10; i++) {
            char ch = (char) ('a' + i);
            grid.addColumn(ch, String.class);
        }
        for (int i = 0; i < 100; i++) {
            String[] row = new String[10];
            Arrays.fill(row, "test");
            grid.addRow(row);
        }

        addComponents(grid);
    }

    @Override
    protected String getTestDescription() {
        return "Don't add more than one scroll handler";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19189; // also 20254, 19622
    }

}
