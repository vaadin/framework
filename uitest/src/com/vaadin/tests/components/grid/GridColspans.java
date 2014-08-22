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

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.components.grid.Grid;
import com.vaadin.ui.components.grid.GridFooter;
import com.vaadin.ui.components.grid.GridFooter.FooterRow;
import com.vaadin.ui.components.grid.GridHeader;
import com.vaadin.ui.components.grid.GridHeader.HeaderRow;
import com.vaadin.ui.components.grid.renderers.NumberRenderer;

public class GridColspans extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Indexed dataSource = new IndexedContainer();
        Grid grid;

        dataSource.addContainerProperty("firstName", String.class, "");
        dataSource.addContainerProperty("lastName", String.class, "");
        dataSource.addContainerProperty("streetAddress", String.class, "");
        dataSource.addContainerProperty("zipCode", Integer.class, null);
        dataSource.addContainerProperty("city", String.class, "");
        Item i = dataSource.addItem(0);
        i.getItemProperty("firstName").setValue("Rudolph");
        i.getItemProperty("lastName").setValue("Reindeer");
        i.getItemProperty("streetAddress").setValue("Ruukinkatu 2-4");
        i.getItemProperty("zipCode").setValue(20540);
        i.getItemProperty("city").setValue("Turku");
        grid = new Grid(dataSource);
        grid.setWidth("600px");
        grid.getColumn("zipCode").setRenderer(new NumberRenderer());
        addComponent(grid);

        GridHeader header = grid.getHeader();
        HeaderRow row = header.prependRow();
        row.join("firstName", "lastName").setText("Full Name");
        row.join("streetAddress", "zipCode", "city").setText("Address");
        header.prependRow()
                .join(dataSource.getContainerPropertyIds().toArray())
                .setText("All the stuff");

        GridFooter footer = grid.getFooter();
        FooterRow footerRow = footer.appendRow();
        footerRow.join("firstName", "lastName").setText("Full Name");
        footerRow.join("streetAddress", "zipCode", "city").setText("Address");
        footer.appendRow().join(dataSource.getContainerPropertyIds().toArray())
                .setText("All the stuff");

        footer.setVisible(true);
    }

    @Override
    protected String getTestDescription() {
        return "Grid header and footer colspans";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13334;
    }

}
