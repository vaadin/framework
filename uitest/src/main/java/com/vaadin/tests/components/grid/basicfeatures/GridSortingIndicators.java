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
package com.vaadin.tests.components.grid.basicfeatures;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;

public class GridSortingIndicators extends AbstractTestUI {

    private static int FOO_MIN = 4;
    private static int BAR_MULTIPLIER = 3;
    private static int BAZ_MAX = 132;

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid(createContainer());
        addComponent(grid);
        grid.sort(Sort.by("foo").then("bar", SortDirection.DESCENDING)
                .then("baz"));

        addComponent(new Button("Reverse sorting", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                grid.sort(Sort.by("baz", SortDirection.DESCENDING).then("bar")
                        .then("foo", SortDirection.DESCENDING));
            }
        }));
    }

    private Container.Indexed createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("foo", Integer.class, 0);
        container.addContainerProperty("bar", Integer.class, 0);
        container.addContainerProperty("baz", Integer.class, 0);
        for (int i = 0; i < 10; ++i) {
            Item item = container.getItem(container.addItem());
            item.getItemProperty("foo").setValue(FOO_MIN + i);
            item.getItemProperty("baz").setValue(BAZ_MAX - i);
            item.getItemProperty("bar").setValue(BAR_MULTIPLIER * i);
        }
        return container;
    }
}
