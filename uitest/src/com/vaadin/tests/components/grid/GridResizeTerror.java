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

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.ResizeTerrorizer;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;

@Widgetset(TestingWidgetSet.NAME)
public class GridResizeTerror extends UI {
    @Override
    protected void init(VaadinRequest request) {
        Grid grid = new Grid();

        int cols = 10;
        Object[] data = new Object[cols];

        for (int i = 0; i < cols; i++) {
            grid.addColumn("Col " + i);
            data[i] = "Data " + i;
        }

        for (int i = 0; i < 500; i++) {
            grid.addRow(data);
        }

        ResizeTerrorizer terrorizer = new ResizeTerrorizer(grid);
        setContent(terrorizer);
    }
}
