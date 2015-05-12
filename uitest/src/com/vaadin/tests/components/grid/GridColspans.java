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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridColspans extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Indexed dataSource = new IndexedContainer();
        final Grid grid;

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
        grid.setSelectionMode(SelectionMode.MULTI);
        addComponent(grid);

        HeaderRow row = grid.prependHeaderRow();
        row.join("firstName", "lastName").setText("Full Name");
        row.join("streetAddress", "zipCode", "city").setText("Address");
        grid.prependHeaderRow()
                .join(dataSource.getContainerPropertyIds().toArray())
                .setText("All the stuff");

        FooterRow footerRow = grid.appendFooterRow();
        footerRow.join("firstName", "lastName").setText("Full Name");
        footerRow.join("streetAddress", "zipCode", "city").setText("Address");
        grid.appendFooterRow()
                .join(dataSource.getContainerPropertyIds().toArray())
                .setText("All the stuff");

        addComponent(new Button("Show/Hide firstName",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (grid.getColumn("firstName") != null) {
                            grid.removeColumn("firstName");
                        } else {
                            grid.addColumn("firstName");
                        }
                    }
                }));

        addComponent(new Button("Change column order",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.setColumnOrder("zipCode", "firstName");
                    }
                }));
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
