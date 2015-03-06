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
package com.vaadin.tests.components.grid.basicfeatures.server;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;

/**
 * Tests that removing and adding rows doesn't cause an infinite loop in the
 * browser.
 * 
 * @author Vaadin Ltd
 */
@Theme("valo")
public class GridClearContainer extends AbstractTestUIWithLog {

    private IndexedContainer ic;

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        ic = new IndexedContainer();
        ic.addContainerProperty("Col 1", String.class, "default");
        ic.addItem("Row 1");
        ic.addItem("Row 2");
        grid.setContainerDataSource(ic);

        Button b = new Button("Clear and re-add", new ClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void buttonClick(ClickEvent event) {
                ic.removeAllItems();
                ic.addItem("Row 3").getItemProperty("Col 1")
                        .setValue("Updated value 1");
                ic.addItem("Row 4").getItemProperty("Col 1")
                        .setValue("Updated value 2");
            }
        });
        addComponent(b);
        addComponent(grid);
    }

    @Override
    protected String getTestDescription() {
        return "Tests that removing and adding rows doesn't cause an infinite loop in the browser.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16747;
    }
}
