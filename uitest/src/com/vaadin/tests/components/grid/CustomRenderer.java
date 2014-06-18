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
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.components.grid.Grid;

@Widgetset(TestingWidgetSet.NAME)
public class CustomRenderer extends AbstractTestUI {

    private static final Object INT_ARRAY_PROPERTY = "int array";

    @Override
    protected void setup(VaadinRequest request) {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(INT_ARRAY_PROPERTY, int[].class,
                new int[] {});

        Object itemId = new Object();
        Item item = container.addItem(itemId);
        @SuppressWarnings("unchecked")
        Property<int[]> property = item.getItemProperty(INT_ARRAY_PROPERTY);
        property.setValue(new int[] { 1, 1, 2, 3, 5, 8, 13 });

        Grid grid = new Grid(container);
        grid.getColumn(INT_ARRAY_PROPERTY).setRenderer(new IntArrayRenderer());
        addComponent(grid);
    }

    @Override
    protected String getTestDescription() {
        return "Verifies that renderers operating on other data than "
                + "just Strings also work ";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(13334);
    }

}
