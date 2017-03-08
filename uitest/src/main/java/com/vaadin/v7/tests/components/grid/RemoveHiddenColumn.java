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
package com.vaadin.v7.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;

public class RemoveHiddenColumn extends AbstractTestUIWithLog {

    private final Grid testGrid = new Grid();
    private final Button testBtn = new Button("updateGrid");
    private final Button testBtn2 = new Button("show/hide Grid");
    private final HorizontalLayout buttonBar = new HorizontalLayout(testBtn,
            testBtn2);
    private final VerticalLayout mainLayout = new VerticalLayout(testGrid,
            buttonBar);

    @Override
    protected void setup(final VaadinRequest request) {
        final Grid grid = new Grid();
        final BeanItemContainer<Person> bic = new BeanItemContainer<Person>(
                Person.class);
        grid.setContainerDataSource(bic);
        grid.setColumns("firstName", "lastName", "email", "age");

        grid.getColumn("firstName").setHidden(true);
        grid.getColumn("email").setHidden(true);

        Button addRow = new Button("Add data row", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent e) {
                bic.addBean(new Person("first", "last", "email", 42, Sex.FEMALE,
                        null));

            }
        });
        addRow.setId("add");
        Button removeColumn = new Button("Remove first column",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent e) {
                        Column column = grid.getColumns().get(0);
                        log("Removed column '" + column.getHeaderCaption() + "'"
                                + (column.isHidden() ? " (hidden)" : ""));
                        grid.removeColumn(column.getPropertyId());
                    }
                });
        removeColumn.setId("remove");

        addComponents(grid, addRow, removeColumn);
    }
}
