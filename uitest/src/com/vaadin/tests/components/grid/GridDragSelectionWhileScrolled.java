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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

public class GridDragSelectionWhileScrolled extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Layout layout = new VerticalLayout();

        HorizontalLayout spacer = new HorizontalLayout();
        spacer.setHeight("1000px");
        layout.addComponent(spacer);

        PersonTestGrid grid = new PersonTestGrid(100);
        grid.setSelectionMode(SelectionMode.MULTI);
        layout.addComponent(grid);

        addComponent(layout);
    }

    @Override
    protected Integer getTicketNumber() {
        return 17895;
    }

    @Override
    protected String getTestDescription() {
        return "Drag selecting rows in Grid malfunctions if page is scrolled";
    }
}
