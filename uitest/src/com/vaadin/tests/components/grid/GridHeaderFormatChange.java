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

import java.io.Serializable;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridHeaderFormatChange extends AbstractTestUI {

    private static final long serialVersionUID = -2787771187365766027L;

    private HeaderRow row;

    public class Person implements Serializable {
        private static final long serialVersionUID = -7995927620756317000L;

        String firstName;
        String lastName;
        String streetAddress;
        Integer zipCode;
        String city;

        public Person(String firstName, String lastName, String streetAddress,
                Integer zipCode, String city) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.streetAddress = streetAddress;
            this.zipCode = zipCode;
            this.city = city;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getStreetAddress() {
            return streetAddress;
        }

        public void setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
        }

        public Integer getZipCode() {
            return zipCode;
        }

        public void setZipCode(Integer zipCode) {
            this.zipCode = zipCode;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        BeanItemContainer<Person> datasource = new BeanItemContainer<Person>(
                Person.class);
        final Grid grid;

        datasource.addItem(new Person("Rudolph", "Reindeer", "Ruukinkatu 2-4",
                20540, "Turku"));

        grid = new Grid(datasource);
        grid.setWidth("600px");
        grid.getColumn("zipCode").setRenderer(new NumberRenderer());
        grid.setColumnOrder("firstName", "lastName", "streetAddress",
                "zipCode", "city");
        grid.setSelectionMode(SelectionMode.SINGLE);
        addComponent(grid);

        Button showHide = new Button("Hide firstName",
                new Button.ClickListener() {
                    private static final long serialVersionUID = 8107530972693788705L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (grid.getColumn("firstName") != null) {
                            grid.removeColumn("firstName");
                            event.getButton().setCaption("Show firstName");
                        } else {
                            grid.addColumn("firstName");
                            grid.setColumnOrder("firstName", "lastName",
                                    "streetAddress", "zipCode", "city");

                            event.getButton().setCaption("Hide firstName");
                        }
                    }
                });
        showHide.setId("show_hide");

        Button selectionMode = new Button("Set multiselect",
                new Button.ClickListener() {
                    private static final long serialVersionUID = 8107530972693788705L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (grid.getSelectionModel() instanceof SelectionModel.Single) {
                            grid.setSelectionMode(SelectionMode.MULTI);
                        } else {
                            grid.setSelectionMode(SelectionMode.SINGLE);
                        }
                    }
                });
        selectionMode.setId("selection_mode");

        Button join = new Button("Add Join header column",
                new Button.ClickListener() {
                    private static final long serialVersionUID = -5330801275551280623L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (row == null) {
                            row = grid.prependHeaderRow();
                            if (grid.getColumn("firstName") != null) {
                                row.join("firstName", "lastName").setText(
                                        "Full Name");
                            }
                            row.join("streetAddress", "zipCode", "city")
                                    .setText("Address");
                        } else {
                            grid.removeHeaderRow(row);
                            row = null;
                        }
                    }
                });
        join.setId("join");
        addComponent(new HorizontalLayout(showHide, selectionMode, join));
    }

    @Override
    protected String getTestDescription() {
        return "Grid for testing header re-rendering.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17131;
    }
}
